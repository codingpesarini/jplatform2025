    package com.studiodomino.jplatform.cms.front.controller;

    import com.studiodomino.jplatform.cms.entity.Content;
    import com.studiodomino.jplatform.cms.entity.DatiBase;
    import com.studiodomino.jplatform.cms.entity.Section;
    import com.studiodomino.jplatform.cms.front.dto.Breadcrumb;
    import com.studiodomino.jplatform.cms.front.dto.ExtraTag;
    import com.studiodomino.jplatform.cms.front.dto.FrontContentFilter;
    import com.studiodomino.jplatform.cms.front.service.*;
    import com.studiodomino.jplatform.cms.service.ContentService;
    import com.studiodomino.jplatform.shared.config.Configurazione;
    import com.studiodomino.jplatform.shared.service.ConfigurazioneService;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import jakarta.servlet.http.HttpSession;

    /**
     * FrontController - Entry point principale front-end pubblico
     * Gestisce navigazione sezioni e documenti del CMS
     *
     * SCOPE SESSIONE: Configurazione (utente, sito, locale, gruppi/ruoli)
     * SCOPE REQUEST:  Configurazione (menu, sezioni, contenuti, slot, tag cloud)
     */
    @Controller
    @RequestMapping("/front")
    @SessionAttributes("config")
    @RequiredArgsConstructor
    @Slf4j
    public class FrontController {

        private final ConfigurazioneService configurazioneService;
        private final PortalConfigurationService portalConfigService;
        private final FrontDispatchService dispatchService;
        private final CookieNavigationService cookieService;
        private final ContentService contentService;

        /**
         * Entry point unico front-end
         * Equivalente legacy: Dispatch.unspecified()
         *
         * @param pid ID del contenuto da visualizzare (sezione o documento)
         * @param ordinamento Campo di ordinamento custom
         * @param anno Filtro anno (per archivio)
         * @param mese Filtro mese (per archivio)
         * @param stato Filtro stato (0=bozza, 1=pubblicato, 2=evidenza, etc.)
         * @param privato Filtro contenuti privati (true/false)
         * @param my Filtro "miei contenuti" (true/false)
         * @param archivio Modalità archivio (true/false)
         * @param sqlContenuto Query SQL custom per filtrare contenuti
         * @param frompid ID contenuto di provenienza (per tracking click)
         * @param returnParam Override view da restituire
         * @param config Configurazione unificata (da sessione, opzionale)
         */
        @GetMapping
        public String dispatch(
                // Parametri navigazione
                @RequestParam(required = false) String pid,
                @RequestParam(required = false) String ordinamento,

                // Parametri filtro
                @RequestParam(required = false) String anno,
                @RequestParam(required = false) String mese,
                @RequestParam(required = false) String stato,
                @RequestParam(required = false) String privato,
                @RequestParam(required = false) String my,
                @RequestParam(required = false) String archivio,
                @RequestParam(required = false) String sqlContenuto,

                // Click tracking
                @RequestParam(required = false) String frompid,

                // Override return
                @RequestParam(required = false, name = "return") String returnParam,

                // Configurazione unificata (OPZIONALE - può essere null se sessione scaduta)
                @SessionAttribute(required = false) Configurazione config,
                HttpServletRequest request,
                HttpServletResponse response,
                HttpSession session,
                Model model) {

            log.debug("=== FRONT DISPATCH === pid: {}", pid);

            String returnView = "homePortal";

            try {
                // ===== 0. VERIFICA E INIZIALIZZA CONFIGURAZIONE. Se la sessione è scaduta o non c’è config, la ricrea. =====
                if (config == null) {
                    log.info("Sessione scaduta o configurazione assente, inizializzo nuova configurazione");

                    // Usa lo stesso metodo dello StartupController
                    config = configurazioneService.getOrCreateConfiguration(request);

                    if (config == null) {
                        log.error("Impossibile inizializzare la configurazione");
                        model.addAttribute("errorMessage", "Errore di configurazione del sistema");
                        return "error/500";
                    }

                    // Salva in model (per @SessionAttributes) e in sessione (per persistenza)
                    model.addAttribute("config", config);
                    log.debug("Nuova configurazione creata e salvata in sessione");
                }

                log.debug("Configurazione OK - sito: {}", config.getSito().getId());

                // ===== 1. INIZIALIZZA PORTALE. Significa: carica menu, slot, tag cloud, roba della home ecc. nella request/config. =====
                // Popola parti REQUEST: menu, home, slot contenuti, tag cloud
                portalConfigService.initializePortal(request, response, config);

                // ===== 2. CLICK TRACKING (Newsletter) =====
                if (frompid != null && !frompid.isEmpty()) {
                    dispatchService.trackClick(frompid);
                }

                // ===== 3. SE NESSUN PID → MOSTRA HOME =====
                if (pid == null || pid.isEmpty()) {
                    log.debug("Nessun PID specificato, mostrando home page");
                    model.addAttribute("config", config); //passa le configurazioni generali alla pagina(vista)
                    return resolveTemplate(config, returnView); //ritorna il template della Home Page
                }

                // ===== 4. CARICA CONTENT GENERICO: cioè è il modello base del contenuto: Contiene solo: ID, PID, tipo (cioè è una SEZIONE o un DOCUMENT, ecc.), stato (pubblicato, bozza…), idRoot / gerarchia, datebase
                //NON contiene: testo, immagini, campi personalizzati=====
                Content contentBase = dispatchService.getContentBase(pid); //

                if (contentBase == null) {
                    log.warn("Contenuto non trovato per pid: {}", pid);
                    model.addAttribute("config", config);
                    return resolveTemplate(config, "homePortal");
                }

                log.debug("Content caricato: id={}, idRoot={}, tipo={}",
                        contentBase.getId(),
                        contentBase.getIdRoot(),
                        contentBase.isSection() ? "SEZIONE" : "DOCUMENTO");

                // ===== 5. AGGIORNA BREADCRUMB. indica la posizione attuale dell'utente all'interno della gerarchia di un sito web o di un'applicazione, mostrando il percorso dalla home page (es. Home > Categoria > Sottocategoria > Pagina Corrente=====
                config.setBreadcrumb(dispatchService.getBreadcrumbs(pid));

                // ===== 6. AGGIORNA PROFILO NAVIGAZIONE (cookie) =====
                cookieService.updateNavigationProfile(request, response, pid, config);

                // ===== 7. VERIFICA ACCESSIBILITÀ CONTENUTO =====
                if (!dispatchService.isPublished(contentBase, stato)) {
                    log.warn("Contenuto non accessibile: pid={}, stato={}",
                            pid, contentBase.getStato());
                    model.addAttribute("config", config);
                    return resolveTemplate(config, "homePortal");
                }

                // ===== 8. ROUTING: SEZIONE vs DOCUMENTO =====
                if (contentBase.isSection()) {
                    // È una SEZIONE (contenitore)
                    log.debug("Routing → handleSection");
                    returnView = handleSection(
                            pid, anno, mese, stato, privato, my, archivio,
                            sqlContenuto, ordinamento, contentBase, config, request, model
                    );
                } else {
                    // È un DOCUMENTO (contenuto foglia)
                    log.debug("Routing → handleDocument");
                    returnView = handleDocument(
                            pid, ordinamento, contentBase, config, request, model
                    );
                }

                // ===== 9. OVERRIDE RETURN (se specificato). Quindi dice: "Se l'utente mi ha suggerito return nell'URL, usa quella al posto di quella predefinita".=====
                if (returnParam != null && !returnParam.isEmpty()) {
                    returnView = returnParam;
                }

                // ===== 10. FINALIZZA RISPOSTA =====
                model.addAttribute("config", config);
                return resolveTemplate(config, returnView);

            } catch (Exception e) {
                log.error("Errore in front dispatch per pid: {}", pid, e);
                model.addAttribute("config", config);
                model.addAttribute("errorMessage", "Errore durante il caricamento della pagina");
                return "error/500";
            }
        }

        // ========================================
        // GESTIONE SEZIONE
        // ========================================

        /**
         * Gestisce visualizzazione SEZIONE
         *
         * Una sezione è un contenitore che può avere:
         * - Sotto-sezioni (navigazione gerarchica)
         * - Contenuti/documenti (elementi foglia)
         * - ExtraTag (contenuti correlati per tag)
         * - Archivio (filtro anno/mese)
         * - Paginazione
         */
        private String handleSection(
                String pid,
                String anno,
                String mese,
                String stato,
                String privato,
                String my,
                String archivio,
                String sqlContenuto,
                String ordinamento,
                Content contentBase,
                Configurazione config,
                HttpServletRequest request,
                Model model) {

            log.debug("Gestione SEZIONE: pid={}", pid);

            try {
                Integer idSito = config.getSito().getId();

                // ===== 1. COSTRUISCI FILTRO CONTENUTI =====
                String orderBy = dispatchService.resolveOrdinamento(
                        ordinamento, config.getSito()
                );

                FrontContentFilter filter = dispatchService.buildContentFilter(
                        anno, mese, stato, privato, my, archivio,
                        orderBy, sqlContenuto, config.getUtente()
                );

                // ===== 2. CARICA SEZIONE COMPLETA =====
                Section section = dispatchService.loadSection(
                        pid,
                        filter,
                        config.getImagesRepositoryWeb(),
                        idSito,
                        true,
                        true,
                        my
                );

                if (section == null) {
                    log.warn("Sezione non trovata: {}", pid);
                    return "homePortal";
                }

                // ===== 3. CARICA EXTRATAG (contenuti correlati) =====
                if ("1".equals(contentBase.getRegolaextratag1())) {
                    ExtraTag extraTag = dispatchService.loadExtraTagsForSection(
                            section, config
                    );
                    section.setExtratag(extraTag);
                }

                // ===== 4. PAGINAZIONE =====
                dispatchService.buildPagination(section, config, request);

                // ===== 5. IMPOSTA IN CONFIGURAZIONE E MODEL =====
                config.setActualSection(section);
                config.setContenutiActualSection(section.getContenuti());

                model.addAttribute("breadcrumb", contentService.buildBreadcrumbForSection(
                        section,
                        config.getSito().getId().toString()
                ));
                model.addAttribute("section", section);
                model.addAttribute("contents", section.getContenuti());

                log.debug("Sezione caricata: {} contenuti",
                        section.getContenuti() != null ? section.getContenuti().size() : 0);

                return "sectionDetail";

            } catch (Exception e) {
                log.error("Errore caricamento sezione: {}", pid, e);
                return "homePortal";
            }
        }

        // ========================================
        // GESTIONE DOCUMENTO
        // ========================================

        /**
         * Gestisce visualizzazione DOCUMENTO
         *
         * Un documento è un contenuto foglia dentro una sezione.
         * Può avere:
         * - Allegati
         * - Gallery di immagini
         * - ExtraTag (contenuti correlati)
         * - Sezione parent (per breadcrumb e navigazione)
         */
        private String handleDocument(
                String pid,
                String ordinamento,
                Content contentBase,
                Configurazione config,
                HttpServletRequest request,
                Model model) {

            log.debug("Gestione DOCUMENTO: pid={}, parent={}",
                    pid, contentBase.getIdRoot());

            try {
                // ===== 1. CARICA DOCUMENTO COMPLETO =====
                DatiBase document = dispatchService.loadDocument(
                        pid,
                        config.getImagesRepositoryWeb()
                );

                if (document == null) {
                    log.warn("Documento non trovato: {}", pid);
                    return "homePortal";
                }

                // ===== 2. CARICA SEZIONE PARENT (per breadcrumb e context) =====
                Section parentSection = loadParentSection(
                        contentBase, ordinamento, config
                );

                // Imposta parent section in config per breadcrumb
                if (parentSection != null) {
                    config.setActualSection(parentSection);
                }
                document.setSection(parentSection);
                // ===== 3. CARICA EXTRATAG (contenuti correlati) =====
                loadDocumentExtraTag(document, contentBase, parentSection, config);

                // ===== 4. IMPOSTA IN CONFIGURAZIONE E MODEL =====
                config.setActualDocument(document);

                model.addAttribute("post", document);
                model.addAttribute("content", document); // Alias per compatibilità template

                if (parentSection != null) {
                    model.addAttribute("parentSection", parentSection);
                }

                model.addAttribute("breadcrumb", contentService.buildBreadcrumbForContent(
                        document,
                        config.getSito().getId().toString()
                ));

                log.debug("Documento caricato: {}", document.getTitolo());

                return "documentDetail";

            } catch (Exception e) {
                log.error("Errore caricamento documento: {}", pid, e);
                return "homePortal";
            }
        }

        /**
         * Carica la sezione parent di un documento
         */
        private Section loadParentSection(
                Content contentBase,
                String ordinamento,
                Configurazione config) {

            Integer idRoot = contentBase.getIdRoot();
            if (idRoot == null || idRoot == -1) {
                return null;
            }

            try {
                String orderBy = dispatchService.resolveOrdinamento(
                        ordinamento, config.getSito()
                );

                FrontContentFilter emptyFilter = FrontContentFilter.builder()
                        .ordinamento(orderBy)
                        .build();

                return dispatchService.loadSection(
                        idRoot.toString(),
                        emptyFilter,
                        config.getImagesRepositoryWeb(),
                        config.getSito().getId(),
                        true,
                        true,
                        null
                );

            } catch (Exception e) {
                log.warn("Errore caricamento sezione parent: {}", idRoot, e);
                return null;
            }
        }

        /**
         * Carica ExtraTag per documento
         * Priorità: 1) ExtraTag del documento, 2) ExtraTag della sezione parent
         */
        private void loadDocumentExtraTag(
                DatiBase document,
                Content contentBase,
                Section parentSection,
                Configurazione config) {

            boolean hasDocumentExtraTag = "1".equals(contentBase.getRegolaextratag1());
            boolean hasParentExtraTag = parentSection != null &&
                    "1".equals(parentSection.getRegolaExtraTag1());

            try {
                if (hasDocumentExtraTag) {
                    // Usa ExtraTag del documento
                    ExtraTag extraTag = dispatchService.loadExtraTagsForContent(
                            document, config
                    );
                    document.setExtratag(extraTag);

                } else if (hasParentExtraTag) {
                    // Usa ExtraTag della sezione parent
                    ExtraTag extraTag = dispatchService.loadExtraTagsForSection(
                            parentSection, config
                    );
                    document.setExtratag(extraTag);
                }

            } catch (Exception e) {
                log.warn("Errore caricamento ExtraTag per documento: {}", document.getId(), e);
            }
        }

        // ========================================
        // HELPER METHODS
        // ========================================

        @GetMapping("/{pid}/{titolo}")
        public String dispatchSeo(
                @PathVariable String pid,
                @PathVariable String titolo,

                // tieni anche gli altri parametri come querystring (se ti servono)
                @RequestParam(required = false) String ordinamento,
                @RequestParam(required = false) String anno,
                @RequestParam(required = false) String mese,
                @RequestParam(required = false) String stato,
                @RequestParam(required = false) String privato,
                @RequestParam(required = false) String my,
                @RequestParam(required = false) String archivio,
                @RequestParam(required = false) String sqlContenuto,
                @RequestParam(required = false) String frompid,
                @RequestParam(required = false, name = "return") String returnParam,

                @SessionAttribute(required = false) Configurazione config,
                HttpServletRequest request,
                HttpServletResponse response,
                HttpSession session,
                Model model
        ) {
            return dispatch(pid, ordinamento, anno, mese, stato, privato, my, archivio,
                    sqlContenuto, frompid, returnParam, config, request, response, session, model);
        }

        private String resolveTemplate(Configurazione config, String viewName) {
            String templateFolder = config.getPublicTemplateFolder();
            return templateFolder + "/front/" + viewName;
        }
    }