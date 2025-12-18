package com.studiodomino.jplatform.cms.front.service;

import com.studiodomino.jplatform.cms.entity.*;
import com.studiodomino.jplatform.cms.front.dto.FrontContentFilter;
import com.studiodomino.jplatform.shared.config.Breadcrumb;
import com.studiodomino.jplatform.shared.config.ConfigurazioneCore;
import com.studiodomino.jplatform.shared.entity.Site;
import com.studiodomino.jplatform.shared.entity.UtenteEsterno;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FrontDispatchService {

     private final ExtraTagService extraTagService;

    /**
     * Risolve ordinamento: da parametro o default sito
     */
    public String resolveOrdinamento(String ordinamento, Site site) {
        if (ordinamento != null && !ordinamento.isEmpty()) {
            return " order by " + ordinamento;
        }
        // Default dal sito (campo libero7)
        return " order by " + site.getLibero7();
    }

    /**
     * Click tracking per newsletter
     */
    public void trackClick(String frompid) {
        try {
            daoPubblico.clicca(frompid);
        } catch (Exception e) {
            log.error("Errore in trackClick", e);
        }
    }

    /**
     * Carica oggetto base per ID
     */
    public DatiBase getOggettoBase(String pid) {
        return daoPubblico.getOggettoBaseId(pid);
    }

    /**
     * Carica breadcrumb per oggetto
     */
    public Breadcrumb getBreadcrumbs(String pid) {
        return daoPubblico.getBreadcrumbs(pid);
    }

    /**
     * Carica archivio per anno/mese
     */
    public List<Archivio> getArchivio(String pid, String anno, String mese) {
        return daoPubblico.getArchiviobyId(
                pid, "-1", "-1", false, anno, mese
        );
    }

    /**
     * Verifica se contenuto è pubblicato
     */
    public boolean isPublished(DatiBase base, String statoParam) {
        if (statoParam == null) {
            // Se non specificato stato, controlla lo stato del base
            String stato = base.getStato();
            return !"0".equals(stato) && !"4".equals(stato);
        }

        // Se specificato stato, verifica che base sia in quello stato
        String stato = base.getStato();
        return !("0".equals(stato) || "4".equals(stato));
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

    /**
     * Carica sezione completa
     */
    public Section loadSection(
            String pid,
            FrontContentFilter filter,
            String imagesRepositoryWeb) {

        String orderBy = filter.getOrdinamento();
        String contSql = buildContentSql(filter);
        String contSql2 = buildMySql(filter);
        String myFlag = filter.getMy() != null ? filter.getMy() : "false";

        Section section;

        // Gestione archivio
        if (filter.getArchivio() != null) {
            section = daoSection.getSezioneFrontbyId(
                    pid, true, true, orderBy, "", contSql,
                    imagesRepositoryWeb, "true"
            );
        } else if (filter.getSqlContenuto() != null) {
            // SQL personalizzato
            section = daoSection.getSezioneFrontbyIdSqlContenuto(
                    pid, true, true, orderBy, contSql,
                    filter.getSqlContenuto(), imagesRepositoryWeb, myFlag
            );
        } else {
            // Standard
            section = daoSection.getSezioneFrontbyId(
                    pid, true, true, orderBy, contSql, contSql2,
                    imagesRepositoryWeb, myFlag
            );
        }

        // Imposta stato archivio
        if (filter.getStato() != null && !"-1".equals(filter.getStato())) {
            section.setStatoArchivio(filter.getStato());
        } else {
            section.setStatoArchivio("1");
        }

        return section;
    }

    /**
     * Carica documento
     */
    public DatiBase loadDocument(String pid, String imagesRepositoryWeb) {
        return daoDocumenti.getDocumentoFrontbyId(pid, imagesRepositoryWeb);
    }

    /**
     * Carica ExtraTag (delega a ExtraTagService)
     */
    public void loadExtraTags(
            Section section,
            DatiBase base,
            Configurazione configPortal,
            ConfigurazioneCore configCore) {

        extraTagService.elaboraExtraTag(
                section, base, configPortal, configCore
        );
    }

    /**
     * Genera HTML paginazione
     */
    public void buildPagination(
            Configurazione configPortal,
            ConfigurazioneCore configCore,
            HttpServletRequest request) {

        try {
            if (configPortal.getActualSection() == null) return;

            String page = request.getParameter("page");
            String mese = request.getParameter("mese");
            String url = configPortal.getActualSection().getUrlRWPages();

            // Gestione URL archivio
            if (mese != null && !"-1".equals(mese)) {
                String anno = request.getParameter("anno");
                configPortal.getActualSection().setAnnoTemp(anno);
                configPortal.getActualSection().setMeseTemp(mese);
                url = configPortal.getActualSection().getUrlRWArchivio();
            }

            // Configurazione paginazione
            String itemsPerPage = configCore.getSito().getLibero6();
            configCore.setItemsPage(itemsPerPage);
            configCore.setPageNumber("1");
            configCore.setStartItems("0");
            configCore.setEndItems(itemsPerPage);

            // Calcolo pagine totali
            int itemPage = Integer.parseInt(itemsPerPage);
            int totalItems = 0;

            if (configPortal.getActualSection().getContenuti() != null) {
                totalItems = configPortal.getActualSection().getContenuti().size();
            }

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
        }
    }

    /**
     * Genera HTML per barra paginazione
     */
    private String generatePaginationHtml(
            String page, int pagineTotali, String url,
            ConfigurazioneCore configCore, int totalItems, int itemPage) {

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
            // Prev
            if (currentPage > 1) {
                html.append("<li><a href=\"../").append(url).append(currentPage - 1)
                        .append("\"><i class=\"icon icon-chevron-left\"></i></a></li>");
            }

            // Numeri pagina
            for (int i = 1; i <= pagineTotali; i++) {
                if (i == currentPage) {
                    html.append("<li class=\"active\"><a href=\"../")
                            .append(url).append(i).append("\">")
                            .append(i).append("</a></li>");
                } else {
                    html.append("<li><a href=\"../")
                            .append(url).append(i).append("\">")
                            .append(i).append("</a></li>");
                }
            }

            // Next
            if (currentPage < pagineTotali) {
                html.append("<li><a href=\"../").append(url).append(pagineTotali)
                        .append("\"><i class=\"icon icon-chevron-right\"></i></a></li>");
            }
        }

        return html.toString();
    }

    /**
     * Costruisce SQL filtro contenuti
     */
    private String buildContentSql(FrontContentFilter filter) {
        StringBuilder sql = new StringBuilder();

        // Filtro anno/mese
        if (filter.getAnno() != null && filter.getMese() != null
                && !"-1".equals(filter.getMese())) {
            sql.append(" and (stato='1' or stato='3') and privato='0'");
            sql.append(" and anno='").append(filter.getAnno()).append("'");
            sql.append(" and mese='").append(filter.getMese()).append("'");
            return sql.toString();
        }

        // Filtro stato
        if (filter.getStato() != null && !"-1".equals(filter.getStato())) {
            sql.append(" and (stato='").append(filter.getStato()).append("')");
            sql.append(" and privato='0'");
            return sql.toString();
        }

        // Filtro privato (gruppi utente)
        if ("true".equals(filter.getPrivato()) && filter.getUtente() != null) {
            sql.append(" and (stato='1') and privato!='0'");
            sql.append(filter.getUtente().GruppiSqlCond());
            return sql.toString();
        }

        return sql.toString();
    }

    /**
     * Costruisce SQL filtro "miei contenuti"
     */
    private String buildMySql(FrontContentFilter filter) {
        if ("true".equals(filter.getMy()) && filter.getUtente() != null) {
            return " and apertoda='" + filter.getUtente().getId() + "'";
        }
        return null;
    }
}