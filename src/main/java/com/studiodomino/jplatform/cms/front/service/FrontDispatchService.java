package com.studiodomino.jplatform.cms.front.service;

import com.studiodomino.jplatform.cms.entity.*;
import com.studiodomino.jplatform.cms.front.dto.ExtraTag;
import com.studiodomino.jplatform.cms.front.dto.FrontContentFilter;
import com.studiodomino.jplatform.cms.service.AllegatoService;
import com.studiodomino.jplatform.cms.service.CommentoService;
import com.studiodomino.jplatform.cms.service.ContentService;
import com.studiodomino.jplatform.cms.front.dto.Breadcrumb;
import com.studiodomino.jplatform.shared.config.Configurazione;
import com.studiodomino.jplatform.shared.entity.Images;
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

    public String resolveOrdinamento(String ordinamento, Site site) {
        if (ordinamento != null && !ordinamento.isEmpty()) {
            return ordinamento;
        }
        String defaultOrder = site.getLibero(7);
        return (defaultOrder != null && !defaultOrder.isEmpty()) ? defaultOrder : "data desc";
    }

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
    // UTILITY - GALLERY
    // ========================================

    /**
     * Parse stringa gallery — supporta sia virgola che punto e virgola come separatori
     */
    private List<Images> parseGalleryString(String galleryString) {
        if (galleryString == null || galleryString.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(galleryString.split("[,;]"))
                .map(s -> s.trim().replace("(", "").replace(")", ""))
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
     * Popola gallery per una sezione
     */
    private void popolaGallerySezione(Section section) {
        String gs = section.getGalleryString();
        if (gs != null && !gs.isEmpty()) {
            List<Images> gallery = parseGalleryString(gs);
            section.setGallery(gallery);
            section.setGalleryList(gallery);
            log.debug("Gallery caricata per sezione {}: {} immagini", section.getId(), gallery.size());
        }
    }

    // ========================================
    // CARICAMENTO CONTENUTI
    // ========================================

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
            String maxOrdine = section.getMaxOrdineContenuti();
            if (maxOrdine == null || maxOrdine.isEmpty()) {
                maxOrdine = "0";
            }

            // ===== 4. CARICA CONTENUTI =====
            List<DatiBase> contenuti;
            boolean isArgomento =
                    section.getSectionType() != null
                            && section.getSectionType().getType() != null
                            && section.getSectionType().getType().equalsIgnoreCase("Argomento");

            if (isArgomento) {
                String tag = section.getTitolo();
                log.debug("SEZIONE ARGOMENTO: carico contenuti per TAG='{}'", tag);
                contenuti = contentService.findContentsByTag(idSito.toString(), tag);
                if ((contenuti == null || contenuti.isEmpty()) && section.getLabel() != null) {
                    String tag2 = section.getLabel();
                    log.debug("SEZIONE ARGOMENTO: 0 risultati con titolo, riprovo con LABEL='{}'", tag2);
                    contenuti = contentService.findContentsByTag(idSito.toString(), tag2);
                }
                if (contenuti == null) contenuti = new ArrayList<>();
            } else {
                String whereCondition = buildContentWhereCondition(filter, section.getId());
                contenuti = contentService.findContentsBySection(
                        idSito,
                        section.getId(),
                        whereCondition,
                        orderByContenuti,
                        Integer.parseInt(maxOrdine)
                );
            }

            section.setContenuti(contenuti);
            log.debug("Contenuti caricati: {}", contenuti.size());

            // ===== 5. CARICA SOTTOSEZIONI =====
            if (loadSubsections) {
                List<Section> subsections = contentService.findSubsections(
                        idSito.toString(),
                        section.getId().toString()
                );

                // Popola gallery per ogni sottosezione
                for (Section sub : subsections) {
                    popolaGallerySezione(sub);
                }

                section.setSubsection(subsections);
                log.debug("Sottosezioni caricate: {}", subsections.size());

                // Carica sottosezioni parent
                if (section.getIdParent() != null && !section.getIdParent().isEmpty()
                        && !"0".equals(section.getIdParent())) {
                    List<Section> subsectionsParent = contentService.findSubsections(
                            idSito.toString(),
                            section.getIdParent()
                    );
                    // Popola gallery anche per le sottosezioni parent
                    for (Section sub : subsectionsParent) {
                        popolaGallerySezione(sub);
                    }
                    section.setSubsectionParent(subsectionsParent);
                    log.debug("Sottosezioni parent caricate: {}", subsectionsParent.size());
                }
            }

            // ===== 6. CARICA SEZIONE PADRE =====
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

            // ===== 8. CARICA GALLERY SEZIONE CORRENTE =====
            popolaGallerySezione(section);

            // ===== 9. CARICA COMMENTI =====
            List<Commento> commenti = commentoService.getCommentiConThread(
                    section.getId().toString(), true);
            section.setCommenti(commenti);
            long numeroCommenti = commentoService.contaCommentiApprovati(section.getId().toString());
            section.setNumeroCommenti(Long.toString(numeroCommenti));
            log.debug("Commenti caricati: {}", numeroCommenti);

            // ===== 10. UTENTI ASSOCIATI =====
            if (section.getUtentiAssociatiString() != null && !section.getUtentiAssociatiString().isEmpty()) {
                log.debug("Utenti associati: {}", section.getUtentiAssociatiString());
            }

            // ===== 11. CLICK TRACKING =====
            if (trackClick) {
                contentService.incrementClick(section.getId(), idSito.toString());
                Integer newClick = (section.getClick() != null ? section.getClick() : 0) + 1;
                section.setClick(newClick);
                log.debug("Click incrementato: {}", newClick);
            }

            // ===== 12. STATO ARCHIVIO =====
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
            log.error("ID sezione non valido: {}", pid);
            throw new IllegalArgumentException("ID sezione non valido: " + pid);
        }
    }

    private String determineContentOrdering(Section section, String filterOrdering) {
        if (section.getOrdineContenuti() != null && !section.getOrdineContenuti().isEmpty()) {
            return section.getOrdineContenuti();
        }
        if (filterOrdering != null && !filterOrdering.isEmpty()) {
            return filterOrdering;
        }
        return "data desc";
    }

    private List<DatiBase> loadSectionContents(Section section, FrontContentFilter filter, Integer idSito) {
        String whereCondition = buildContentWhereCondition(filter, section.getId());
        String orderBy = filter.getOrdinamento() != null ? filter.getOrdinamento() : "data desc";
        return contentService.findContentsBySection(idSito, section.getId(), whereCondition, orderBy, null);
    }

    public DatiBase loadDocument(String pid, String imagesRepositoryWeb) {
        try {
            Integer id = Integer.parseInt(pid);
            log.debug("Caricamento documento per id: {}", id);

            DatiBase document = contentService.findDatiBaseById(id).orElse(null);

            if (document != null) {
                List<Allegato> allegati = allegatoService.findAllegatiByDocumento(id);
                document.setAllegati(allegati);

                List<Commento> commenti = commentoService.getCommentiConThread(id.toString(), true);
                document.setCommenti(commenti);

                long numeroCommenti = commentoService.contaCommentiApprovati(id.toString());
                document.setNumeroCommenti(Long.toString(numeroCommenti));

                if (document.getGalleryString() != null && !document.getGalleryString().isEmpty()) {
                    List<Images> gallery = parseGalleryString(document.getGalleryString());
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

    public Breadcrumb getBreadcrumbs(String pid) {
        try {
            Integer id = Integer.parseInt(pid);
            Section section = contentService.findSectionById(id).orElse(null);
            if (section != null) return buildSectionBreadcrumb(section);
            DatiBase base = contentService.findDatiBaseById(id).orElse(null);
            if (base != null) return buildContentBreadcrumb(base);
            return new Breadcrumb();
        } catch (NumberFormatException e) {
            log.error("ID non valido per breadcrumb: {}", pid);
            return new Breadcrumb();
        }
    }

    private Breadcrumb buildSectionBreadcrumb(Section section) {
        Breadcrumb breadcrumb = new Breadcrumb();
        breadcrumb.add("Home", "/");
        breadcrumb.add(section.getTitolo(), "/front?pid=" + section.getId());
        return breadcrumb;
    }

    private Breadcrumb buildContentBreadcrumb(DatiBase base) {
        Breadcrumb breadcrumb = new Breadcrumb();
        breadcrumb.add("Home", "/");
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

    public List<DatiBase> getArchivio(String pid, String anno, String mese, Integer idSito) {
        try {
            Integer sectionId = Integer.parseInt(pid);
            StringBuilder where = new StringBuilder();
            where.append(" AND (stato='1' OR stato='3') AND privato='0'");
            if (anno != null && !anno.isEmpty() && !"-1".equals(anno)) {
                where.append(" AND anno='").append(anno).append("'");
            }
            if (mese != null && !mese.isEmpty() && !"-1".equals(mese)) {
                where.append(" AND mese='").append(mese).append("'");
            }
            return contentService.findContentsBySection(idSito, sectionId, where.toString(), "data desc", null);
        } catch (NumberFormatException e) {
            log.error("ID non valido per archivio: {}", pid);
            return List.of();
        }
    }

    // ========================================
    // STATO E PUBBLICAZIONE
    // ========================================

    public boolean isPublished(Content content, String statoParam) {
        if (content == null) return false;
        String stato = content.getStato();
        if ("0".equals(stato) || "4".equals(stato)) return false;
        if (statoParam != null && !statoParam.isEmpty() && !"-1".equals(statoParam)) {
            return statoParam.equals(stato);
        }
        return "1".equals(stato) || "2".equals(stato) || "3".equals(stato);
    }

    public boolean isPublished(DatiBase base, String statoParam) {
        if (base == null) return false;
        String stato = base.getStato();
        if ("0".equals(stato) || "4".equals(stato)) return false;
        if (statoParam != null && !statoParam.isEmpty() && !"-1".equals(statoParam)) {
            return statoParam.equals(stato);
        }
        return "1".equals(stato) || "3".equals(stato);
    }

    public boolean isPublished(Section section) {
        if (section == null) return false;
        String stato = section.getStato();
        return "1".equals(stato) || "3".equals(stato);
    }

    // ========================================
    // EXTRA TAG
    // ========================================

    public ExtraTag loadExtraTagsForSection(Section section, Configurazione configCore) {
        if (section == null) return new ExtraTag();
        return extraTagService.elaboraExtraTagSection(
                section, section.getOrdineExtraTag(), section.getMaxExtraTag(), configCore);
    }

    public ExtraTag loadExtraTagsForContent(DatiBase base, Configurazione configCore) {
        if (base == null) return new ExtraTag();
        return extraTagService.elaboraExtraTagDatiBase(
                base, base.getOrdineExtraTag(), base.getMaxExtraTag(), configCore);
    }

    // ========================================
    // PAGINAZIONE
    // ========================================

    public void buildPagination(Section section, Configurazione configCore, HttpServletRequest request) {
        try {
            if (section == null || section.getContenuti() == null) {
                configCore.setPaginationBar("");
                return;
            }

            String page = request.getParameter("page");
            String mese = request.getParameter("mese");
            String anno = request.getParameter("anno");
            String url = buildPaginationUrl(section, mese, anno);

            String itemsPerPage = configCore.getSito().getLibero(6);
            if (itemsPerPage == null || itemsPerPage.isEmpty()) itemsPerPage = "10";

            configCore.setItemsPage(itemsPerPage);
            configCore.setPageNumber("1");
            configCore.setStartItems("0");
            configCore.setEndItems(itemsPerPage);

            int itemPage = Integer.parseInt(itemsPerPage);
            int totalItems = section.getContenuti().size();
            int pagineIncomplete = (totalItems % itemPage > 0) ? 1 : 0;
            int pagineTotali = (totalItems / itemPage) + pagineIncomplete;
            if (pagineTotali == 0) pagineTotali = 1;

            configCore.setTotalPage(String.valueOf(pagineTotali));
            String paginationHtml = generatePaginationHtml(page, pagineTotali, url, configCore, totalItems, itemPage);
            configCore.setPaginationBar(paginationHtml);

        } catch (Exception e) {
            log.error("Errore in buildPagination", e);
            configCore.setPaginationBar("");
        }
    }

    private String buildPaginationUrl(Section section, String mese, String anno) {
        String baseUrl = "/front?pid=" + section.getId();
        if (mese != null && !"-1".equals(mese)) {
            return baseUrl + "&anno=" + anno + "&mese=" + mese + "&page=";
        }
        return baseUrl + "&page=";
    }

    private String generatePaginationHtml(String page, int pagineTotali, String url,
                                          Configurazione configCore, int totalItems, int itemPage) {

        StringBuilder html = new StringBuilder();
        int currentPage = (page != null) ? Integer.parseInt(page) : 1;
        configCore.setPageNumber(String.valueOf(currentPage));

        int start = (currentPage - 1) * itemPage;
        int end;
        if (currentPage < pagineTotali) {
            end = start + itemPage;
        } else {
            end = start + (totalItems % itemPage);
            if (end == start) end = totalItems;
        }

        configCore.setStartItems(String.valueOf(start));
        configCore.setEndItems(String.valueOf(end));

        if (pagineTotali > 1) {
            html.append("<ul class=\"pagination\">");
            if (currentPage > 1) {
                html.append("<li><a href=\"").append(url).append(currentPage - 1)
                        .append("\"><i class=\"fa fa-chevron-left\"></i></a></li>");
            }
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

    private String buildContentWhereCondition(FrontContentFilter filter, Integer sectionId) {
        StringBuilder where = new StringBuilder();

        if (filter.getAnno() != null && filter.getMese() != null && !"-1".equals(filter.getMese())) {
            where.append(" AND (stato='1' OR stato='3') AND privato='0'");
            where.append(" AND anno='").append(filter.getAnno()).append("'");
            where.append(" AND mese='").append(filter.getMese()).append("'");
            return where.toString();
        }

        if (filter.getStato() != null && !"-1".equals(filter.getStato())) {
            where.append(" AND stato='").append(filter.getStato()).append("'");
            where.append(" AND privato='0'");
            return where.toString();
        }

        if ("true".equals(filter.getMy()) && filter.getUtente() != null) {
            where.append(" AND stato='1'");
            where.append(" AND apertoda='").append(filter.getUtente().getId()).append("'");
            return where.toString();
        }

        if ("true".equals(filter.getPrivato()) && filter.getUtente() != null) {
            where.append(" AND stato='1' AND privato!='0'");
            return where.toString();
        }

        where.append(" AND (stato='1' OR stato='3') AND privato='0'");
        return where.toString();
    }

    // ========================================
    // TRACKING
    // ========================================

    public void trackClick(String frompid) {
        try {
            if (frompid != null && !frompid.isEmpty()) {
                log.debug("Tracking click from newsletter: {}", frompid);
            }
        } catch (Exception e) {
            log.error("Errore in trackClick", e);
        }
    }
}