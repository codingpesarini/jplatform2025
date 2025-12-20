package com.studiodomino.jplatform.cms.entity;

import com.studiodomino.jplatform.cms.front.dto.ExtraTag;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO che rappresenta una SEZIONE nel CMS (idRoot = -1).
 */
@Data
public class Section implements Serializable {

    private static final long serialVersionUID = 1L;

    // ========== IDENTIFICAZIONE ==========

    private Integer id;
    private String idSite;
    private Integer idRoot = -1;
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

    // ========== CAMPI L (configurazioni custom 1-10) ==========

    private String l1;
    private String l2;
    private String l3;
    private String l4;
    private String l5;
    private String l6;
    private String l7;
    private String l8;
    private String l9;
    private String l10;

    // ========== EXTRA TAG (1-10) ==========

    private String extratag1;
    private String extratag2;
    private String extratag3;
    private String extratag4;
    private String extratag5;
    private String extratag6;
    private String extratag7;
    private String extratag8;
    private String extratag9;
    private String extratag10;

    /**
     * Ordinamento contenuti ExtraTag
     */
    private String ordineExtraTag;

    /**
     * Max contenuti ExtraTag
     */
    private String maxExtraTag;

    // ========== DATE ==========

    private String data;
    private String dataVisualizzata;
    private LocalDate dataSql;
    private String creato;
    private String creatoDa;
    private String modificato;
    private String modificatoDa;

    // ========== ARCHIVIO ==========

    /**
     * Anno temporaneo per archivio
     */
    private String annoTemp;

    /**
     * Mese temporaneo per archivio
     */
    private String meseTemp;

    /**
     * Stato archivio (filtro)
     */
    private String statoArchivio;

    // ========== URL REWRITING ==========

    /**
     * URL base rewriting (con .html)
     * Lombok genera automaticamente getter/setter
     */
    private String urlRW;  // ✅ AGGIUNTO

    /**
     * URL con paginazione
     */
    private String urlRWPages;

    /**
     * URL archivio
     */
    private String urlRWArchivio;

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

    /**
     * ExtraTag - contenuti correlati
     */
    private ExtraTag extratag;

    // ========== i18n ==========

    private String locale = "it_IT";

    // ========================================
    // METODI HELPER - i18n
    // ========================================

    /**
     * Ottiene il titolo considerando il locale
     */
    public String getTitoloLocalized() {
        if (locale != null && !locale.isEmpty() && !"it_IT".equals(locale)) {
            return titoloEN != null && !titoloEN.isEmpty() ? titoloEN : titolo;
        }
        return titolo;
    }

    /**
     * Ottiene il riassunto considerando il locale
     */
    public String getRiassuntoLocalized() {
        if (locale != null && !locale.isEmpty() && !"it_IT".equals(locale)) {
            return riassuntoEN != null && !riassuntoEN.isEmpty() ? riassuntoEN : riassunto;
        }
        return riassunto;
    }

    /**
     * Ottiene il testo considerando il locale
     */
    public String getTestoLocalized() {
        if (locale != null && !locale.isEmpty() && !"it_IT".equals(locale)) {
            return testoEN != null && !testoEN.isEmpty() ? testoEN : testo;
        }
        return testo;
    }

    // ========================================
    // METODI HELPER - EXTRATAG
    // ========================================

    /**
     * Ottieni ExtraTag per numero (1-10)
     */
    public String getExtraTag(int numero) {
        return switch (numero) {
            case 1 -> extratag1;
            case 2 -> extratag2;
            case 3 -> extratag3;
            case 4 -> extratag4;
            case 5 -> extratag5;
            case 6 -> extratag6;
            case 7 -> extratag7;
            case 8 -> extratag8;
            case 9 -> extratag9;
            case 10 -> extratag10;
            default -> null;
        };
    }

    /**
     * Imposta ExtraTag per numero (1-10)
     */
    public void setExtraTag(int numero, String valore) {
        switch (numero) {
            case 1 -> extratag1 = valore;
            case 2 -> extratag2 = valore;
            case 3 -> extratag3 = valore;
            case 4 -> extratag4 = valore;
            case 5 -> extratag5 = valore;
            case 6 -> extratag6 = valore;
            case 7 -> extratag7 = valore;
            case 8 -> extratag8 = valore;
            case 9 -> extratag9 = valore;
            case 10 -> extratag10 = valore;
        }
    }

    /**
     * Verifica se ha ExtraTag configurati
     */
    public boolean hasExtraTagConfig() {
        for (int i = 1; i <= 10; i++) {
            String tag = getExtraTag(i);
            if (tag != null && !tag.isEmpty() && !"0".equals(tag)) {
                return true;
            }
        }
        return false;
    }

    // ========================================
    // METODI HELPER - CAMPI L
    // ========================================

    /**
     * Ottieni campo L per numero (1-10)
     */
    public String getL(int numero) {
        return switch (numero) {
            case 1 -> l1;
            case 2 -> l2;
            case 3 -> l3;
            case 4 -> l4;
            case 5 -> l5;
            case 6 -> l6;
            case 7 -> l7;
            case 8 -> l8;
            case 9 -> l9;
            case 10 -> l10;
            default -> null;
        };
    }

    /**
     * Imposta campo L per numero (1-10)
     */
    public void setL(int numero, String valore) {
        switch (numero) {
            case 1 -> l1 = valore;
            case 2 -> l2 = valore;
            case 3 -> l3 = valore;
            case 4 -> l4 = valore;
            case 5 -> l5 = valore;
            case 6 -> l6 = valore;
            case 7 -> l7 = valore;
            case 8 -> l8 = valore;
            case 9 -> l9 = valore;
            case 10 -> l10 = valore;
        }
    }

    // ========================================
    // METODI HELPER - CAMPI S
    // ========================================

    /**
     * Ottieni campo S per numero (1-10)
     */
    public String getS(int numero) {
        return switch (numero) {
            case 1 -> s1;
            case 2 -> s2;
            case 3 -> s3;
            case 4 -> s4;
            case 5 -> s5;
            case 6 -> s6;
            case 7 -> s7;
            case 8 -> s8;
            case 9 -> s9;
            case 10 -> s10;
            default -> null;
        };
    }

    /**
     * Imposta campo S per numero (1-10)
     */
    public void setS(int numero, String valore) {
        switch (numero) {
            case 1 -> s1 = valore;
            case 2 -> s2 = valore;
            case 3 -> s3 = valore;
            case 4 -> s4 = valore;
            case 5 -> s5 = valore;
            case 6 -> s6 = valore;
            case 7 -> s7 = valore;
            case 8 -> s8 = valore;
            case 9 -> s9 = valore;
            case 10 -> s10 = valore;
        }
    }

    // ========================================
    // METODI HELPER - URL
    // ========================================

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
     * URL builder per Spring Boot routing
     */
    public String getUrl() {
        if (label != null && !label.isEmpty()) {
            return "/" + label;
        }
        return "/section/" + id;
    }

    /**
     * Calcola e imposta URL rewriting base
     */
    public void buildUrlRW() {
        if (label != null && !label.isEmpty()) {
            this.urlRW = "/" + label + ".html";
        } else {
            this.urlRW = "/section/" + id + ".html";
        }
    }

    /**
     * Genera URL per paginazione
     */
    public void buildUrlRWPages() {
        if (this.urlRW == null || this.urlRW.isEmpty()) {
            buildUrlRW();
        }
        this.urlRWPages = this.urlRW.replace(".html", "/page/");
    }

    /**
     * Genera URL per archivio
     */
    public void buildUrlRWArchivio() {
        if (this.urlRW == null || this.urlRW.isEmpty()) {
            buildUrlRW();
        }

        if (annoTemp != null && meseTemp != null) {
            this.urlRWArchivio = this.urlRW.replace(".html",
                    "/" + annoTemp + "/" + meseTemp + "/page/");
        } else {
            this.urlRWArchivio = this.urlRW.replace(".html", "/archivio/page/");
        }
    }

    // ========================================
    // METODI HELPER - STATO
    // ========================================

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
     * Verifica se è pubblicata
     */
    public boolean isPubblicata() {
        return "1".equals(stato) || "3".equals(stato);
    }

    /**
     * Verifica se è privata
     */
    public boolean isPrivata() {
        return "1".equals(privato);
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
     * Conta le sottosezioni
     */
    public int countSubsections() {
        return subsection != null ? subsection.size() : 0;
    }
}