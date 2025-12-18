package com.studiodomino.jplatform.cms.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO che rappresenta una SEZIONE nel CMS (idRoot = -1).
 *
 * Questa classe è un POJO (non una JPA Entity) che rappresenta
 * la vista "business logic" di una sezione, mappata dalla entity Content.
 *
 * Una Section è un contenitore che può avere:
 * - Sotto-sezioni figlie (subsection)
 * - Contenuti foglia (contenuti - lista di DatiBase)
 */
@Data
public class Section implements Serializable {

    private static final long serialVersionUID = 1L;

    // ========== IDENTIFICAZIONE ==========

    private Integer id;
    private String idSite;
    private Integer idRoot = -1;  // Sempre -1 per le sezioni
    private Integer idType;
    private String idParent;
    private String label;

    // ========== CONTENUTO BASE ==========

    private String titolo;
    private String riassunto;
    private String testo;

    // Versioni EN
    private String titoloEN;
    private String riassuntoEN;
    private String testoEN;

    // ========== STATO E VISIBILITÀ ==========

    private String stato;
    private String privato;
    private String idGruppo;
    private String visualizza;

    // ========== POSIZIONAMENTO ==========

    private Integer position;
    private String firstPage;
    private Integer click;

    // ========== MEDIA ==========

    private String gallery;
    private String idAllegato;

    // ========== TAG ==========

    private String tag;

    // ========== MENU ==========

    private String menu1;
    private String menu2;
    private String menu3;
    private String menu4;
    private String menu5;

    // ========== CAMPI S (per configurazioni) ==========

    private String s1;
    private String s2;
    private String s3;
    private String s4;
    private String s5;
    private String s6;
    private String s7;
    private String s8;
    private String s9;
    private String s10;

    // ========== DATE ==========

    private String data;
    private String dataVisualizzata;
    private LocalDate dataSql;
    private String creato;
    private String creatoDa;
    private String modificato;
    private String modificatoDa;

    // ========== RELAZIONI ==========

    /**
     * Tipo di sezione (news, eventi, etc.)
     */
    private SectionType sectionType;

    /**
     * Sotto-sezioni figlie (navigazione gerarchica)
     */
    private List<Section> subsection;

    /**
     * Contenuti appartenenti a questa sezione
     */
    private List<DatiBase> contenuti;

    /**
     * Sezioni parent nella gerarchia (per breadcrumb)
     */
    private List<Section> parentSection;

    /**
     * Allegati associati
     */
    private List<Allegato> allegati;

    /**
     * Gallery di immagini
     */
    private List<Images> galleryList;

    // ========== i18n ==========

    private String locale = "it_IT";

    // ========================================
    // METODI HELPER
    // ========================================

    /**
     * Ottiene il titolo considerando il locale
     */
    public String getTitolo() {
        if (locale != null && !locale.isEmpty() && !"it_IT".equals(locale)) {
            return titoloEN != null && !titoloEN.isEmpty() ? titoloEN : titolo;
        }
        return titolo;
    }

    /**
     * Ottiene il riassunto considerando il locale
     */
    public String getRiassunto() {
        if (locale != null && !locale.isEmpty() && !"it_IT".equals(locale)) {
            return riassuntoEN != null && !riassuntoEN.isEmpty() ? riassuntoEN : riassunto;
        }
        return riassunto;
    }

    /**
     * Ottiene il testo considerando il locale
     */
    public String getTesto() {
        if (locale != null && !locale.isEmpty() && !"it_IT".equals(locale)) {
            return testoEN != null && !testoEN.isEmpty() ? testoEN : testo;
        }
        return testo;
    }

    /**
     * Label sanitizzata per URL
     */
    public String getTitoloLabel() {
        if (titolo == null || titolo.isEmpty()) {
            return "";
        }
        return titolo
                .toLowerCase()
                .replaceAll("[àáâãäå]", "a")
                .replaceAll("[èéêë]", "e")
                .replaceAll("[ìíîï]", "i")
                .replaceAll("[òóôõö]", "o")
                .replaceAll("[ùúûü]", "u")
                .replaceAll("[^a-z0-9]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    /**
     * Verifica se ha sotto-sezioni
     */
    public boolean hasSubsections() {
        return subsection != null && !subsection.isEmpty();
    }

    /**
     * Verifica se ha contenuti
     */
    public boolean hasContenuti() {
        return contenuti != null && !contenuti.isEmpty();
    }

    /**
     * Verifica se ha allegati
     */
    public boolean hasAllegati() {
        return allegati != null && !allegati.isEmpty();
    }

    /**
     * Verifica se ha gallery
     */
    public boolean hasGallery() {
        return gallery != null && !gallery.isEmpty();
    }

    /**
     * Conta i contenuti pubblicati
     */
    public int countPubblicati() {
        if (contenuti == null) return 0;
        return (int) contenuti.stream()
                .filter(c -> "1".equals(c.getStato()))
                .count();
    }

    /**
     * URL builder per Spring Boot routing
     */
    public String getUrl() {
        if (label != null && !label.isEmpty()) {
            return "/" + label;
        }
        return "/section/" + id;
    }

    /**
     * URL con rewrite
     */
    public String getUrlRW() {
        if (label != null && !label.isEmpty()) {
            return "/" + label + ".html";
        }
        return "/section/" + id + ".html";
    }
}