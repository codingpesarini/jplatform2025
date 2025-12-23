package com.studiodomino.jplatform.cms.front.controller;

import com.studiodomino.jplatform.cms.entity.DatiBase;
import com.studiodomino.jplatform.cms.entity.Section;
import com.studiodomino.jplatform.cms.front.dto.ExtraTag;
import com.studiodomino.jplatform.cms.front.dto.FrontContentFilter;
import com.studiodomino.jplatform.cms.front.service.*;
import com.studiodomino.jplatform.shared.config.Configurazione;
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

    private final PortalConfigurationService portalConfigService;
    private final FrontDispatchService dispatchService;
    private final CookieNavigationService cookieService;

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
     * @param config Configurazione unificata (da sessione)
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

            // Configurazione unificata (SESSIONE + REQUEST)
            @SessionAttribute Configurazione config,
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session,
            Model model) {

        log.debug("=== FRONT DISPATCH === pid: {}, sito: {}",
                pid, config.getSito().getId());

        String returnView = "homePortal";

        try {
            // ===== 1. INIZIALIZZA PORTALE =====
            // Popola parti REQUEST: menu, home, slot contenuti, tag cloud
            portalConfigService.initializePortal(request, response, config);

            // ===== 2. CLICK TRACKING (Newsletter) =====
            if (frompid != null && !frompid.isEmpty()) {
                dispatchService.trackClick(frompid);
            }

            // ===== 3. SE NESSUN PID → MOSTRA HOME =====
            if (pid == null || pid.isEmpty()) {
                log.debug("Nessun PID specificato, mostrando home page");
                model.addAttribute("config", config);
                return resolveTemplate(config, returnView);
            }

            // ===== 4. CARICA OGGETTO BASE =====
            DatiBase base = dispatchService.getOggettoBase(pid);

            if (base == null) {
                log.warn("Oggetto non trovato per pid: {}", pid);
                model.addAttribute("config", config);
                return resolveTemplate(config, "homePortal");
            }

            // ===== 5. AGGIORNA BREADCRUMB =====
            config.setBreadcrumb(dispatchService.getBreadcrumbs(pid));

            // ===== 6. AGGIORNA PROFILO NAVIGAZIONE (cookie) =====
            cookieService.updateNavigationProfile(request, response, pid, config);

            // ===== 7. VERIFICA ACCESSIBILITÀ CONTENUTO =====
            if (!dispatchService.isPublished(base, stato)) {
                log.warn("Contenuto non accessibile: pid={}, stato={}",
                        pid, base.getStato());
                model.addAttribute("config", config);
                return resolveTemplate(config, "homePortal");
            }

            // ===== 8. ROUTING: SEZIONE vs DOCUMENTO =====
            if ("-1".equals(base.getIdRoot())) {
                // È una SEZIONE (contenitore)
                returnView = handleSection(
                        pid, anno, mese, stato, privato, my, archivio,
                        sqlContenuto, ordinamento, base, config, request, model
                );
            } else {
                // È un DOCUMENTO (contenuto foglia)
                returnView = handleDocument(
                        pid, ordinamento, base, config, request, model
                );
            }

            // ===== 9. OVERRIDE RETURN (se specificato) =====
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
            DatiBase base,
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
                    idSito
            );

            if (section == null) {
                log.warn("Sezione non trovata: {}", pid);
                return "homePortal";
            }

            // ===== 3. CARICA EXTRATAG (contenuti correlati) =====
            if ("1".equals(base.getRegolaExtraTag1())) {
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

            model.addAttribute("section", section);
            model.addAttribute("contents", section.getContenuti());

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
            DatiBase base,
            Configurazione config,
            HttpServletRequest request,
            Model model) {

        log.debug("Gestione DOCUMENTO: pid={}, parent={}", pid, base.getIdRoot());

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
                    base, ordinamento, config
            );

            // Imposta parent section in config per breadcrumb
            if (parentSection != null) {
                config.setActualSection(parentSection);
            }

            // ===== 3. CARICA EXTRATAG (contenuti correlati) =====
            loadDocumentExtraTag(document, parentSection, config);

            // ===== 4. IMPOSTA IN CONFIGURAZIONE E MODEL =====
            config.setActualDocument(document);

            model.addAttribute("document", document);
            model.addAttribute("content", document); // Alias per compatibilità template

            if (parentSection != null) {
                model.addAttribute("parentSection", parentSection);
            }

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
            DatiBase base,
            String ordinamento,
            Configurazione config) {

        if (base.getIdRoot() == null || base.getIdRoot().isEmpty()) {
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
                    base.getIdRoot(),
                    emptyFilter,
                    config.getImagesRepositoryWeb(),
                    config.getSito().getId()
            );

        } catch (Exception e) {
            log.warn("Errore caricamento sezione parent: {}", base.getIdRoot(), e);
            return null;
        }
    }

    /**
     * Carica ExtraTag per documento
     * Priorità: 1) ExtraTag del documento, 2) ExtraTag della sezione parent
     */
    private void loadDocumentExtraTag(
            DatiBase document,
            Section parentSection,
            Configurazione config) {

        boolean hasDocumentExtraTag = "1".equals(document.getRegolaExtraTag1());
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

    /**
     * Risolve il path del template in base al sito
     *
     * Esempi:
     * - Sito 1 (path2='site01') + "homePortal" → "site01/homePortal"
     * - Sito 2 (path2='site02') + "sectionDetail" → "site02/sectionDetail"
     *
     * @param config Configurazione con sito corrente
     * @param viewName Nome della view (es: "homePortal", "sectionDetail")
     * @return Path completo del template
     */
    private String resolveTemplate(Configurazione config, String viewName) {
        String templateFolder = config.getPublicTemplateFolder();
        return templateFolder + "/front/" + viewName;
    }
}