package com.studiodomino.jplatform.cms.front.service;

import com.studiodomino.jplatform.cms.entity.*;
import com.studiodomino.jplatform.cms.front.dto.ExtraTag;
import com.studiodomino.jplatform.cms.front.dto.FrontContentFilter;
import com.studiodomino.jplatform.cms.service.AllegatoService;
import com.studiodomino.jplatform.cms.service.CommentoService;
import com.studiodomino.jplatform.cms.service.ContentService;
import com.studiodomino.jplatform.cms.front.dto.Breadcrumb;
import com.studiodomino.jplatform.shared.config.Configurazione;
import com.studiodomino.jplatform.shared.entity.Site;
import com.studiodomino.jplatform.shared.entity.UtenteEsterno;
import com.studiodomino.jplatform.shared.service.ImagesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FrontDispatchService - Logica di dispatch per controller pubblico
 * Gestisce caricamento contenuti, filtri, paginazione
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FrontDispatchService {

    private final ContentService contentService;
    private final ExtraTagService extraTagService;
    private final AllegatoService allegatoService;
    private final CommentoService commentoService;
    private final ImagesService imagesService;

    // ========================================
    // ORDINAMENTO E FILTRI
    // ========================================

    /**
     * Risolve ordinamento: da parametro o default sito
     */
    public String resolveOrdinamento(String ordinamento, Site site) {
        if (ordinamento != null && !ordinamento.isEmpty()) {
            return ordinamento;
        }
        // Default dal sito (campo libero7)
        String defaultOrder = site.getLibero(7);
        return (defaultOrder != null && !defaultOrder.isEmpty()) ? defaultOrder : "data desc";
    }

    /**
     * Costruisce filtro contenuti
     */
    public FrontContentFilter buildContentFilter(
            String anno, String mese, String stato, String privato,
            String my, String archivio, String orderBy,
            String sqlContenuto, UtenteEsterno utente) {

        return FrontContentFilter.builder()
                .anno(anno)
                .mese(mese)
                .stato(stato)
                .privato(privato)
                .my(my)
                .archivio(archivio)
                .ordinamento(orderBy)
                .sqlContenuto(sqlContenuto)
                .utente(utente)
                .build();
    }

    // ========================================
    // CARICAMENTO CONTENUTI
    // ========================================

    /**
     * Carica Content generico per ID (sia sezione che documento)
     * Ritorna l'entity raw per permettere al controller di fare il routing
     */
    public Content getContentBase(String pid) {
        try {
            Integer id = Integer.parseInt(pid);
            log.debug("Caricamento Content base per id: {}", id);
            return contentService.findContentEntityById(id).orElse(null);
        } catch (NumberFormatException e) {
            log.error("ID non valido: {}", pid);
            return null;
        }
    }

    /**
     * Carica sezione completa con contenuti e tutte le relazioni
     * Equivalente legacy: getSezioneFrontbyId()
     */
    public Section loadSection(
            String pid,
            FrontContentFilter filter,
            String imagesRepositoryWeb,
            Integer idSito,
            boolean loadSubsections,
            boolean trackClick,
            String my) {

        try {
            Integer sectionId = Integer.parseInt(pid);

            // ===== 1. CARICA SEZIONE BASE =====
            Section section = contentService.findSectionById(sectionId)
                    .orElseThrow(() -> new IllegalArgumentException("Sezione non trovata: " + pid));

            log.debug("Sezione base caricata: id={}, titolo={}", section.getId(), section.getTitolo());

            // ===== 2. CARICA SECTION TYPE =====
            if (section.getIdType() != null) {
                section.setSectionType(contentService.getSectionTypeById(section.getIdType()));
                log.debug("SectionType caricato: {}", section.getSectionType() != null ?
                        section.getSectionType().getType() : "null");
            }

            // ===== 3. DETERMINA ORDINAMENTO CONTENUTI =====
            String orderByContenuti = determineContentOrdering(section, filter.getOrdinamento());
            Integer maxContenuti = Integer.parseInt(section.getMaxOrdineContenuti().isEmpty() ?"0":section.getMaxOrdineContenuti());

            // ===== 4. CARICA CONTENUTI =====
            String whereCondition = buildContentWhereCondition(filter, section.getId());
            List<DatiBase> contenuti = contentService.findContentsBySection(
                    idSito,
                    section.getId(),
                    whereCondition,
                    orderByContenuti,
                    maxContenuti
            );
            section.setContenuti(contenuti);
            log.debug("Contenuti caricati: {}", contenuti.size());

            // ===== 5. CARICA SOTTOSEZIONI (se richiesto) =====
            if (loadSubsections) {
                String orderBySottosezioni = section.getOrdineSottosezioni() != null
                        ? section.getOrdineSottosezioni()
                        : "position";

                List<Section> subsections = contentService.findSubsections(
                        idSito.toString(),
                        section.getId().toString()
                );
                section.setSubsection(subsections);
                log.debug("Sottosezioni caricate: {}", subsections.size());

                // Carica anche sottosezioni parent (per navigazione laterale)
                if (section.getIdParent() != null && !section.getIdParent().isEmpty()
                        && !"0".equals(section.getIdParent())) {
                    List<Section> subsectionsParent = contentService.findSubsections(
                            idSito.toString(),
                            section.getIdParent()
                    );
                    section.setSubsectionParent(subsectionsParent);
                    log.debug("Sottosezioni parent caricate: {}", subsectionsParent.size());
                }
            }

            // ===== 6. CARICA SEZIONE PADRE (per breadcrumb) =====
            if (section.getIdParent() != null && !section.getIdParent().isEmpty()
                    && !"0".equals(section.getIdParent())) {
                try {
                    Integer idParent = Integer.parseInt(section.getIdParent());
                    Section parent = contentService.findSectionById(idParent).orElse(null);
                    section.setSezionePadre(parent);
                    log.debug("Sezione padre caricata: {}", parent != null ? parent.getTitolo() : "null");
                } catch (NumberFormatException e) {
                    log.warn("ID parent non valido: {}", section.getIdParent());
                }
            }

            // ===== 7. CARICA ALLEGATI =====
            List<Allegato> allegati = allegatoService.findAllegatiByDocumento(section.getId());
            section.setAllegati(allegati);
            log.debug("Allegati caricati: {}", allegati.size());

            // ===== 8. CARICA GALLERY =====
            if (section.getGalleryString() != null && !section.getGalleryString().isEmpty()) {
                List<com.studiodomino.jplatform.shared.entity.Images> gallery =
                        parseGalleryString(section.getGalleryString());
                section.setGallery(gallery);
                log.debug("Gallery caricata: {} immagini", gallery.size());
            }

            // ===== 9. CARICA COMMENTI =====
            List<Commento> commenti = commentoService.getCommentiConThread(
                    section.getId().toString(),
                    true  // Solo approvati
            );
            section.setCommenti(commenti);

            long numeroCommenti = commentoService.contaCommentiApprovati(section.getId().toString());
            section.setNumeroCommenti(Long.toString(numeroCommenti));
            log.debug("Commenti caricati: {}", numeroCommenti);

            // ===== 10. CARICA UTENTI ASSOCIATI =====
            if (section.getUtentiAssociatiString() != null && !section.getUtentiAssociatiString().isEmpty()) {
                // TODO: Implementare parsing utenti quando disponibile UtenteService
                log.debug("Utenti associati: {}", section.getUtentiAssociatiString());
            }

            // ===== 11. CLICK TRACKING =====
            if (trackClick) {
                contentService.incrementClick(section.getId(), idSito.toString());
                Integer newClick = (section.getClick() != null ? section.getClick() : 0) + 1;
                section.setClick(newClick);
                log.debug("Click incrementato: {}", newClick);
            }

            // ===== 12. IMPOSTA STATO ARCHIVIO =====
            if (filter.getStato() != null && !"-1".equals(filter.getStato())) {
                section.setStatoArchivio(filter.getStato());
            } else {
                section.setStatoArchivio("1");
            }

            log.info("Sezione completa caricata: id={}, contenuti={}, sottosezioni={}, allegati={}",
                    section.getId(),
                    contenuti.size(),
                    section.getSubsection() != null ? section.getSubsection().size() : 0,
                    allegati.size());

            return section;

        } catch (NumberFormatException e) {
            log.error("ID sezione non valido: {} ", pid +" - "+ e);
            throw new IllegalArgumentException("ID sezione non valido: " + pid);
        }
    }

    /**
     * Parse stringa gallery (formato: id1,id2,id3)
     */
    private List<com.studiodomino.jplatform.shared.entity.Images> parseGalleryString(String galleryString) {
        if (galleryString == null || galleryString.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return Arrays.stream(galleryString.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(idStr -> {
                    try {
                        Integer id = Integer.parseInt(idStr);
                        return imagesService.findById(id).orElse(null);
                    } catch (NumberFormatException e) {
                        log.warn("ID immagine non valido in gallery: {}", idStr);
                        return null;
                    }
                })
                .filter(img -> img != null)
                .collect(Collectors.toList());
    }

    /**
     * Determina ordinamento contenuti dalla sezione o dal filtro
     */
    private String determineContentOrdering(Section section, String filterOrdering) {
        // 1. Se sezione ha ordinamento custom, usa quello
        if (section.getOrdineContenuti() != null && !section.getOrdineContenuti().isEmpty()) {
            return section.getOrdineContenuti();
        }

        // 2. Se filtro ha ordinamento, usa quello
        if (filterOrdering != null && !filterOrdering.isEmpty()) {
            return filterOrdering;
        }

        // 3. Default
        return "data desc";
    }

    /**
     * Carica contenuti sezione secondo filtri
     */
    private List<DatiBase> loadSectionContents(
            Section section,
            FrontContentFilter filter,
            Integer idSito) {

        // Costruisci condizioni WHERE
        String whereCondition = buildContentWhereCondition(filter, section.getId());
        String orderBy = filter.getOrdinamento() != null ? filter.getOrdinamento() : "data desc";

        // Carica contenuti
        return contentService.findContentsBySection(
                idSito,
                section.getId(),
                whereCondition,
                orderBy,
                null // Nessun limite per ora (paginazione gestita dopo)
        );
    }

    /**
     * Carica documento singolo
     */
    public DatiBase loadDocument(String pid, String imagesRepositoryWeb) {
        try {
            Integer id = Integer.parseInt(pid);
            log.debug("Caricamento documento per id: {}", id);

            DatiBase document = contentService.findDatiBaseById(id).orElse(null);

            if (document != null) {
                // Carica allegati
                List<Allegato> allegati = allegatoService.findAllegatiByDocumento(id);
                document.setAllegati(allegati);

                // Carica commenti
                List<Commento> commenti = commentoService.getCommentiConThread(
                        id.toString(), true
                );
                document.setCommenti(commenti);

                long numeroCommenti = commentoService.contaCommentiApprovati(id.toString());
                document.setNumeroCommenti(Long.toString(numeroCommenti));

                // Carica gallery se presente
                if (document.getGalleryString() != null && !document.getGalleryString().isEmpty()) {
                    List<com.studiodomino.jplatform.shared.entity.Images> gallery =
                            parseGalleryString(document.getGalleryString());
                    document.setGallery(gallery);
                }
            }

            return document;

        } catch (NumberFormatException e) {
            log.error("ID documento non valido: {}", pid);
            return null;
        }
    }

    // ========================================
    // BREADCRUMB E NAVIGAZIONE
    // ========================================

    /**
     * Carica breadcrumb per oggetto
     */
    public Breadcrumb getBreadcrumbs(String pid) {
        try {
            Integer id = Integer.parseInt(pid);

            // Carica sezione o content
            Section section = contentService.findSectionById(id).orElse(null);
            if (section != null) {
                return buildSectionBreadcrumb(section);
            }

            DatiBase base = contentService.findDatiBaseById(id).orElse(null);
            if (base != null) {
                return buildContentBreadcrumb(base);
            }

            return new Breadcrumb();

        } catch (NumberFormatException e) {
            log.error("ID non valido per breadcrumb: {}", pid);
            return new Breadcrumb();
        }
    }

    /**
     * Costruisce breadcrumb per sezione
     */
    private Breadcrumb buildSectionBreadcrumb(Section section) {
        Breadcrumb breadcrumb = new Breadcrumb();
        breadcrumb.add("Home", "/");

        // TODO: Implementare caricamento gerarchia completa
        breadcrumb.add(section.getTitolo(), "/front?pid=" + section.getId());

        return breadcrumb;
    }

    /**
     * Costruisce breadcrumb per contenuto
     */
    private Breadcrumb buildContentBreadcrumb(DatiBase base) {
        Breadcrumb breadcrumb = new Breadcrumb();
        breadcrumb.add("Home", "/");

        // Carica sezione parent
        if (base.getIdRoot() != null) {
            try {
                Integer idRoot = Integer.parseInt(base.getIdRoot());
                Section section = contentService.findSectionById(idRoot).orElse(null);
                if (section != null) {
                    breadcrumb.add(section.getTitolo(), "/front?pid=" + section.getId());
                }
            } catch (NumberFormatException e) {
                log.warn("ID root non valido: {}", base.getIdRoot());
            }
        }

        breadcrumb.add(base.getTitolo(), "/front?pid=" + base.getId());

        return breadcrumb;
    }

    // ========================================
    // ARCHIVIO
    // ========================================

    /**
     * Carica archivio per anno/mese
     */
    public List<DatiBase> getArchivio(String pid, String anno, String mese, Integer idSito) {
        try {
            Integer sectionId = Integer.parseInt(pid);

            // Costruisci condizione WHERE per archivio
            StringBuilder where = new StringBuilder();
            where.append(" AND (stato='1' OR stato='3') AND privato='0'");

            if (anno != null && !anno.isEmpty() && !"-1".equals(anno)) {
                where.append(" AND anno='").append(anno).append("'");
            }

            if (mese != null && !mese.isEmpty() && !"-1".equals(mese)) {
                where.append(" AND mese='").append(mese).append("'");
            }

            return contentService.findContentsBySection(
                    idSito,
                    sectionId,
                    where.toString(),
                    "data desc",
                    null
            );

        } catch (NumberFormatException e) {
            log.error("ID non valido per archivio: {}", pid);
            return List.of();
        }
    }

    // ========================================
    // STATO E PUBBLICAZIONE
    // ========================================

    /**
     * Verifica se Content è pubblicato
     */
    public boolean isPublished(Content content, String statoParam) {
        if (content == null) return false;

        String stato = content.getStato();

        // Stati NON pubblicati: 0=bozza, 4=eliminato
        if ("0".equals(stato) || "4".equals(stato)) {
            return false;
        }

        // Se specificato stato, verifica match
        if (statoParam != null && !statoParam.isEmpty() && !"-1".equals(statoParam)) {
            return statoParam.equals(stato);
        }

        // Default: pubblicato se stato = 1 o 3
        return "1".equals(stato) || "3".equals(stato);
    }

    /**
     * Verifica se DatiBase è pubblicato
     */
    public boolean isPublished(DatiBase base, String statoParam) {
        if (base == null) return false;

        String stato = base.getStato();

        // Stati NON pubblicati: 0=bozza, 4=eliminato
        if ("0".equals(stato) || "4".equals(stato)) {
            return false;
        }

        // Se specificato stato, verifica match
        if (statoParam != null && !statoParam.isEmpty() && !"-1".equals(statoParam)) {
            return statoParam.equals(stato);
        }

        // Default: pubblicato se stato = 1 o 3
        return "1".equals(stato) || "3".equals(stato);
    }

    /**
     * Verifica se sezione è pubblicata
     */
    public boolean isPublished(Section section) {
        if (section == null) return false;

        String stato = section.getStato();
        return "1".equals(stato) || "3".equals(stato);
    }

    // ========================================
    // EXTRA TAG
    // ========================================

    /**
     * Carica ExtraTag per sezione
     */
    public ExtraTag loadExtraTagsForSection(
            Section section,
            Configurazione configCore) {

        if (section == null) {
            return new ExtraTag();
        }

        return extraTagService.elaboraExtraTagSection(
                section,
                section.getOrdineExtraTag(),
                section.getMaxExtraTag(),
                configCore
        );
    }

    /**
     * Carica ExtraTag per contenuto
     */
    public ExtraTag loadExtraTagsForContent(
            DatiBase base,
            Configurazione configCore) {

        if (base == null) {
            return new ExtraTag();
        }

        return extraTagService.elaboraExtraTagDatiBase(
                base,
                base.getOrdineExtraTag(),
                base.getMaxExtraTag(),
                configCore
        );
    }

    // ========================================
    // PAGINAZIONE
    // ========================================

    /**
     * Genera HTML paginazione
     */
    public void buildPagination(
            Section section,
            Configurazione configCore,
            HttpServletRequest request) {

        try {
            if (section == null || section.getContenuti() == null) {
                configCore.setPaginationBar("");
                return;
            }

            String page = request.getParameter("page");
            String mese = request.getParameter("mese");
            String anno = request.getParameter("anno");

            // Determina URL base
            String url = buildPaginationUrl(section, mese, anno);

            // Configurazione paginazione
            String itemsPerPage = configCore.getSito().getLibero(6);
            if (itemsPerPage == null || itemsPerPage.isEmpty()) {
                itemsPerPage = "10"; // Default
            }

            configCore.setItemsPage(itemsPerPage);
            configCore.setPageNumber("1");
            configCore.setStartItems("0");
            configCore.setEndItems(itemsPerPage);

            // Calcolo pagine totali
            int itemPage = Integer.parseInt(itemsPerPage);
            int totalItems = section.getContenuti().size();

            int pagineIncomplete = (totalItems % itemPage > 0) ? 1 : 0;
            int pagineTotali = (totalItems / itemPage) + pagineIncomplete;

            if (pagineTotali == 0) pagineTotali = 1;

            configCore.setTotalPage(String.valueOf(pagineTotali));

            // Genera HTML paginazione
            String paginationHtml = generatePaginationHtml(
                    page, pagineTotali, url, configCore, totalItems, itemPage
            );

            configCore.setPaginationBar(paginationHtml);

        } catch (Exception e) {
            log.error("Errore in buildPagination", e);
            configCore.setPaginationBar("");
        }
    }

    /**
     * Costruisce URL per paginazione
     */
    private String buildPaginationUrl(Section section, String mese, String anno) {
        String baseUrl = "/front?pid=" + section.getId();

        // Gestione archivio
        if (mese != null && !"-1".equals(mese)) {
            baseUrl += "&anno=" + anno + "&mese=" + mese + "&page=";
            return baseUrl;
        }

        return baseUrl + "&page=";
    }

    /**
     * Genera HTML per barra paginazione
     */
    private String generatePaginationHtml(
            String page, int pagineTotali, String url,
            Configurazione configCore, int totalItems, int itemPage) {

        StringBuilder html = new StringBuilder();
        int currentPage = (page != null) ? Integer.parseInt(page) : 1;

        configCore.setPageNumber(String.valueOf(currentPage));

        // Calcola start/end items
        int start = (currentPage - 1) * itemPage;
        int end;

        if (currentPage < pagineTotali) {
            end = start + itemPage;
        } else {
            end = start + (totalItems % itemPage);
            if (end == start) end = totalItems; // Ultima pagina piena
        }

        configCore.setStartItems(String.valueOf(start));
        configCore.setEndItems(String.valueOf(end));

        // Genera link pagine
        if (pagineTotali > 1) {
            html.append("<ul class=\"pagination\">");

            // Prev
            if (currentPage > 1) {
                html.append("<li><a href=\"").append(url).append(currentPage - 1)
                        .append("\"><i class=\"fa fa-chevron-left\"></i></a></li>");
            }

            // Numeri pagina (max 10 visibili)
            int startPage = Math.max(1, currentPage - 5);
            int endPage = Math.min(pagineTotali, currentPage + 4);

            for (int i = startPage; i <= endPage; i++) {
                if (i == currentPage) {
                    html.append("<li class=\"active\"><a href=\"").append(url)
                            .append(i).append("\">").append(i).append("</a></li>");
                } else {
                    html.append("<li><a href=\"").append(url)
                            .append(i).append("\">").append(i).append("</a></li>");
                }
            }

            // Next
            if (currentPage < pagineTotali) {
                html.append("<li><a href=\"").append(url).append(currentPage + 1)
                        .append("\"><i class=\"fa fa-chevron-right\"></i></a></li>");
            }

            html.append("</ul>");
        }

        return html.toString();
    }

    // ========================================
    // UTILITY - COSTRUZIONE SQL
    // ========================================

    /**
     * Costruisce condizione WHERE per filtro contenuti
     */
    private String buildContentWhereCondition(FrontContentFilter filter, Integer sectionId) {
        StringBuilder where = new StringBuilder();

        // Filtro anno/mese (archivio)
        if (filter.getAnno() != null && filter.getMese() != null
                && !"-1".equals(filter.getMese())) {
            where.append(" AND (stato='1' OR stato='3') AND privato='0'");
            where.append(" AND anno='").append(filter.getAnno()).append("'");
            where.append(" AND mese='").append(filter.getMese()).append("'");
            return where.toString();
        }

        // Filtro stato specifico
        if (filter.getStato() != null && !"-1".equals(filter.getStato())) {
            where.append(" AND stato='").append(filter.getStato()).append("'");
            where.append(" AND privato='0'");
            return where.toString();
        }

        // Filtro "miei contenuti"
        if ("true".equals(filter.getMy()) && filter.getUtente() != null) {
            where.append(" AND stato='1'");
            where.append(" AND apertoda='").append(filter.getUtente().getId()).append("'");
            return where.toString();
        }

        // Filtro contenuti privati (con gruppi utente)
        if ("true".equals(filter.getPrivato()) && filter.getUtente() != null) {
            where.append(" AND stato='1' AND privato!='0'");
            // TODO: Implementare GruppiSqlCond() in UtenteEsterno
            // where.append(filter.getUtente().GruppiSqlCond());
            return where.toString();
        }

        // Default: solo pubblicati pubblici
        where.append(" AND (stato='1' OR stato='3') AND privato='0'");

        return where.toString();
    }

    // ========================================
    // TRACKING (NEWSLETTER)
    // ========================================

    /**
     * Click tracking per newsletter
     */
    public void trackClick(String frompid) {
        try {
            if (frompid != null && !frompid.isEmpty()) {
                log.debug("Tracking click from newsletter: {}", frompid);
                // TODO: Implementare tracking click quando serve
                // Incrementa contatore click in DB
            }
        } catch (Exception e) {
            log.error("Errore in trackClick", e);
        }
    }
}