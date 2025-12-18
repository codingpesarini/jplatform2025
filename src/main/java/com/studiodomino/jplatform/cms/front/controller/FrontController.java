package com.studiodomino.jplatform.cms.front.controller;


import com.studiodomino.jplatform.cms.entity.Configurazione;
import com.studiodomino.jplatform.cms.entity.DatiBase;
import com.studiodomino.jplatform.cms.entity.Section;
import com.studiodomino.jplatform.cms.front.service.*;
import com.studiodomino.jplatform.shared.config.ConfigurazioneCore;
import com.studiodomino.jplatform.shared.entity.Site;
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
 * Gestisce navigazione sezioni e documenti
 */
@Controller
@RequestMapping("/front")
@SessionAttributes("configCore")
@RequiredArgsConstructor
@Slf4j
public class FrontController {

    private final PortalConfigurationService portalConfigService;
    private final FrontDispatchService dispatchService;
    private final CookieNavigationService cookieService;

    /**
     * Entry point unico front-end
     * Equivalente a Dispatch.unspecified()
     */
    @GetMapping
    public String dispatch(
            // Parametri navigazione
            @RequestParam(required = false) String pid,
            @RequestParam(required = false) String ordinamento,

            // Parametri filtro
            @RequestParam(required = false) String stato,
            @RequestParam(required = false) String privato,
            @RequestParam(required = false) String my,
            @RequestParam(required = false) String sqlContenuto,

            // Click tracking
            @RequestParam(required = false) String frompid,

            // Override return
            @RequestParam(required = false, name = "return") String returnParam,

            // Sessione e request
            @SessionAttribute ConfigurazioneCore configCore,
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session,
            Model model) {

        log.debug("=== FRONT DISPATCH === pid: {}", pid);

        String returnAction = "homePortal";
        String statoArchivio = "1"; // Default: pubblicato

        try {
            // ===== 1. INIZIALIZZA PORTALE =====
            // Carica menu, home, contenuti comuni, tag cloud
            Configurazione configPortal = portalConfigService.initializePortal(
                    request, response, configCore
            );

            // ===== 2. ORDINAMENTO =====
            String orderBy = resolveOrdinamento(ordinamento, configCore.getSito());

            // ===== 3. CLICK TRACKING (Newsletter) =====
            if (frompid != null && !frompid.isEmpty()) {
                dispatchService.trackClick(frompid);
            }

            // ===== 4. SE NESSUN PID → HOME =====
            if (pid == null || pid.isEmpty()) {
                // Paginazione home (se necessario)
                dispatchService.buildPagination(configPortal, configCore, request);

                session.setAttribute("configCore", configCore);
                model.addAttribute("configPortal", configPortal);

                return getTemplatePath(configCore, returnAction);
            }

            // ===== 5. ELABORAZIONE PID =====

            // 5a. Carica oggetto base
            DatiBase base = dispatchService.getOggettoBase(pid);

            if (base == null) {
                log.warn("Oggetto non trovato per pid: {}", pid);
                session.setAttribute("configCore", configCore);
                model.addAttribute("configPortal", configPortal);
                return getTemplatePath(configCore, "homePortal");
            }

            // 5b. Aggiorna breadcrumb
            configCore.setBreadcrumb(dispatchService.getBreadcrumbs(pid));

            // 5c. Aggiorna profilo navigazione (cookie ultimi 12 contenuti)
            cookieService.updateNavigationProfile(request, response, pid, configCore);

            // 5d. Verifica stato pubblicazione
            if (!isContenutoAccessibile(base, stato)) {
                log.warn("Contenuto non accessibile: pid={}, stato={}", pid, base.getStato());
                session.setAttribute("configCore", configCore);
                model.addAttribute("configPortal", configPortal);
                return getTemplatePath(configCore, "homePortal");
            }

            // ===== 6. ROUTING: SEZIONE vs DOCUMENTO =====

            if ("-1".equals(base.getIdRoot())) {
                // È una SEZIONE
                returnAction = handleSection(
                        pid, orderBy, stato, privato, my, sqlContenuto,
                        base, configPortal, configCore, request
                );
                statoArchivio = determineStatoArchivio(stato);

            } else {
                // È un DOCUMENTO
                returnAction = handleDocument(
                        pid, orderBy, base, configPortal, configCore, request
                );
            }

            // ===== 7. PAGINAZIONE =====
            dispatchService.buildPagination(configPortal, configCore, request);

            // ===== 8. OVERRIDE RETURN =====
            if (returnParam != null && !returnParam.isEmpty()) {
                returnAction = returnParam;
            }

            // ===== 9. SALVA SESSIONE/REQUEST =====
            session.setAttribute("configCore", configCore);
            model.addAttribute("configPortal", configPortal);

            return getTemplatePath(configCore, returnAction);

        } catch (Exception e) {
            log.error("Errore in front dispatch", e);
            return "redirect:/error";
        }
    }

    // ========================================
    // GESTIONE SEZIONE
    // ========================================

    /**
     * Gestisce visualizzazione SEZIONE
     */
    private String handleSection(
            String pid,
            String orderBy,
            String stato,
            String privato,
            String my,
            String sqlContenuto,
            DatiBase base,
            Configurazione configPortal,
            ConfigurazioneCore configCore,
            HttpServletRequest request) {

        log.debug("Handling SECTION: pid={}", pid);

        // Reset ricerca
        configPortal.setRicerca(null);

        // ===== COSTRUZIONE FILTRI SQL =====

        String contSql = buildContentSql(stato, privato, configCore);
        String contSql2 = buildMySql(my, configCore);
        String myFlag = (my != null && "true".equals(my)) ? "true" : "false";

        // ===== CARICAMENTO SEZIONE =====

        Section section;

        if (sqlContenuto != null && !sqlContenuto.isEmpty()) {
            // MODALITÀ 1: SQL CUSTOM
            section = dispatchService.loadSectionWithCustomSql(
                    pid, orderBy, contSql, sqlContenuto, myFlag,
                    configCore.getImagesRepositoryWeb()
            );

        } else {
            // MODALITÀ 2: NORMALE
            section = dispatchService.loadSection(
                    pid, orderBy, contSql, contSql2, myFlag,
                    configCore.getImagesRepositoryWeb()
            );
        }

        configPortal.setActualSection(section);

        // ===== EXTRATAG (contenuti correlati) =====

        if ("1".equals(base.getRegolaExtraTag1())) {
            dispatchService.loadExtraTags(
                    base, base, configPortal, configCore
            );
        }

        return "sectionDetail";
    }

    // ========================================
    // GESTIONE DOCUMENTO
    // ========================================

    /**
     * Gestisce visualizzazione DOCUMENTO
     */
    private String handleDocument(
            String pid,
            String orderBy,
            DatiBase base,
            Configurazione configPortal,
            ConfigurazioneCore configCore,
            HttpServletRequest request) {

        log.debug("Handling DOCUMENT: pid={}, parent={}", pid, base.getId_root());

        // ===== CARICA SEZIONE PARENT =====
        // (senza caricare i contenuti figli)

        Section parentSection = dispatchService.loadSectionSimple(
                base.getId_root(), orderBy, configCore.getImagesRepositoryWeb()
        );
        configPortal.setActualSection(parentSection);

        // ===== CARICA DOCUMENTO =====

        DatiBase document = dispatchService.loadDocument(
                pid, configCore.getImagesRepositoryWeb()
        );
        configPortal.setActualDocument(document);

        // ===== EXTRATAG =====
        // Priorità: 1) Sezione (se doc non ha), 2) Documento

        if ("1".equals(parentSection.getRegolaExtraTag1())
                && "0".equals(base.getRegolaExtraTag1())) {
            // Usa ExtraTag della SEZIONE
            dispatchService.loadExtraTags(
                    parentSection, parentSection, configPortal, configCore
            );

        } else if ("1".equals(base.getRegolaExtraTag1())) {
            // Usa ExtraTag del DOCUMENTO
            dispatchService.loadExtraTags(
                    parentSection, base, configPortal, configCore
            );
        }

        return "documentDetail";
    }

    // ========================================
    // HELPER METHODS
    // ========================================

    /**
     * Risolve ordinamento: parametro request o default sito
     */
    private String resolveOrdinamento(String ordinamento, Site site) {
        if (ordinamento != null && !ordinamento.isEmpty()) {
            return " order by " + ordinamento;
        }
        // Default dal sito (campo libero7)
        return " order by " + (site.getLibero7() != null ? site.getLibero7() : "id desc");
    }

    /**
     * Costruisce SQL filtro contenuti (stato/privato)
     */
    private String buildContentSql(String stato, String privato, ConfigurazioneCore configCore) {
        StringBuilder sql = new StringBuilder();

        // Filtro per STATO
        if (stato != null && !"-1".equals(stato)) {
            sql.append(" and (stato='").append(stato).append("')");
            sql.append(" and privato='0'");
            return sql.toString();
        }

        // Filtro per PRIVATO (gruppi utente)
        if ("true".equals(privato) && configCore.getUtente() != null) {
            sql.append(" and (stato='1') and privato!='0'");
            sql.append(configCore.getUtente().GruppiSqlCond());
            return sql.toString();
        }

        return sql.toString();
    }

    /**
     * Costruisce SQL filtro "miei contenuti"
     */
    private String buildMySql(String my, ConfigurazioneCore configCore) {
        if ("true".equals(my) && configCore.getUtente() != null) {
            return " and apertoda='" + configCore.getUtente().getId() + "'";
        }
        return null;
    }

    /**
     * Verifica se contenuto è accessibile
     */
    private boolean isContenutoAccessibile(DatiBase base, String statoParam) {
        // Se non specificato stato, contenuto deve essere pubblicato
        if (statoParam == null) {
            String stato = base.getStato();
            return !"0".equals(stato) && !"4".equals(stato);
        }

        // Se specificato stato, verifica che base sia in quello stato
        // NOTA: Logica legacy permette accesso se stato richiesto != 0,4
        if (!"0".equals(statoParam) && !"4".equals(statoParam)) {
            String stato = base.getStato();
            return !"0".equals(stato) && !"4".equals(stato);
        }

        return true;
    }

    /**
     * Determina stato archivio da parametro stato
     */
    private String determineStatoArchivio(String stato) {
        if (stato != null && !"-1".equals(stato)) {
            return stato;
        }
        return "1"; // Default: pubblicato
    }

    /**
     * Determina path template dal Site
     */
    private String getTemplatePath(ConfigurazioneCore configCore, String viewName) {
        String templateFolder = determineTemplateFolder(configCore.getSito());
        return templateFolder + "/" + viewName;
    }

    /**
     * Estrae template folder dal Site
     * TODO: Implementare strategia corretta
     */
    private String determineTemplateFolder(Site site) {
        // OPZIONI:
        // 1. Campo dedicato: site.getTemplateFolder()
        // 2. Convenzione: "site" + padLeft(site.getId(), 2, '0')
        // 3. Campo type: site.getType()

        // Per ora uso convenzione site + id
        return String.format("site%02d", site.getId());
    }
}