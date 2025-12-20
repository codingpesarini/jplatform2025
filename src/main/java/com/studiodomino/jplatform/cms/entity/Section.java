package com.studiodomino.jplatform.cms.entity;

import com.studiodomino.jplatform.cms.front.dto.ExtraTag;
import com.studiodomino.jplatform.shared.entity.UtenteEsterno;
import jakarta.persistence.Column;
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


    // Ordinamento contenuti/sottosezioni
    private String ordineContenuti;
    private String ordineSottosezioni;
    private String maxOrdineContenuti;
    private String maxOrdineSottosezioni;

    // ExtraTag References (per correlazioni inverse)
    private String extraTagRef1;
    private String extraTagRef2;
    private String extraTagRef3;
    private String extraTagRef4;
    private String extraTagRef5;
    private String extraTagRef6;
    private String extraTagRef7;
    private String extraTagRef8;
    private String extraTagRef9;
    private String extraTagRef10;

    private String regolaExtraTag1;

    private String regolaExtraTag2;

    // L estesi
    private String l11;
    private String l12;
    private String l13;
    private String l14;
    private String l15;
    // ========================================
// NEWSLETTER E SMS
// ========================================

    private String newsletter1 = "0";
    private String newsletter2 = "0";
    private String newsletter3 = "0";
    private String newsletter4 = "0";
    private String newsletter5 = "0";

    private String sms1 = "";
    private String sms2 = "";
    private String sms3 = "";
    private String sms4 = "";
    private String sms5 = "";

// ========================================
// CAMPI CUSTOM ESTESI - TEXT (1-10)
// ========================================

    private String text1 = "";
    private String text2 = "";
    private String text3 = "";
    private String text4 = "";
    private String text5 = "";
    private String text6 = "";
    private String text7 = "";
    private String text8 = "";
    private String text9 = "";
    private String text10 = "";

// ========================================
// CAMPI CUSTOM ESTESI - DATE (1-10)
// ========================================

    private LocalDate data1;
    private LocalDate data2;
    private LocalDate data3;
    private LocalDate data4;
    private LocalDate data5;
    private LocalDate data6;
    private LocalDate data7;
    private LocalDate data8;
    private LocalDate data9;
    private LocalDate data10;

// ========================================
// CAMPI CUSTOM ESTESI - VARCHAR (1-10)
// ========================================

    private String varchar1;
    private String varchar2;
    private String varchar3;
    private String varchar4;
    private String varchar5;
    private String varchar6;
    private String varchar7;
    private String varchar8;
    private String varchar9;
    private String varchar10;

// ========================================
// CAMPI CUSTOM ESTESI - NUMBER (1-10)
// ========================================

    private Double number1;
    private Double number2;
    private Double number3;
    private Double number4;
    private Double number5;
    private Double number6;
    private Double number7;
    private Double number8;
    private Double number9;
    private Double number10;

// ========================================
// CAMPI CUSTOM ESTESI - ARRAY (1-5)
// ========================================

    private String[] array1 = {"0"};
    private String[] array2 = {"0"};
    private String[] array3 = {"0"};
    private String[] array4 = {"0"};
    private String[] array5 = {"0"};

// ========================================
// NUMERATORI PROGRESSIVI (1-5)
// ========================================

    private Long numeratore1 = 0L;
    private Long numeratore2 = 0L;
    private Long numeratore3 = 0L;
    private Long numeratore4 = 0L;
    private Long numeratore5 = 0L;

// ========================================
// CAMPI INFO (1-5)
// ========================================

    private String info1 = "";
    private String info2 = "";
    private String info3 = "";
    private String info4 = "";
    private String info5 = "";

// ========================================
// LOGO E TEMPORANEI
// ========================================

    private String logo = "";
    private String logo2 = "";
    private String logo3 = "";

    private String log1 = "";
    private String log2 = "";
    private String log3 = "";

    private String temp1 = "";
    private String temp2 = "";
    private String temp3 = "";
    private String temp4 = "";
    private String temp5 = "";

// ========================================
// REPOSITORY
// ========================================

    private String repo = "0";
    private String repoId = "0";
    private String repoName = "";

// ========================================
// NAVIGAZIONE ESTESA
// ========================================

    /**
     * Sottosezioni parent (navigazione verso l'alto)
     */
    private List<Section> subsectionParent;

    /**
     * Sottosezioni private (filtrate per gruppo)
     */
    private List<Section> subsectionPrivate;

    /**
     * Sezioni correlate
     */
    private List<Section> relazione;

    /**
     * Relazioni inverse
     */
    private List<Section> relazioneInversa;

// ========================================
// UTENTI ASSOCIATI
// ========================================

    private List<UtenteEsterno> utentiAssociati;
    private String utentiAssociatiString = "";

// ========================================
// GRUPPI
// ========================================

    private String[] idgruppi;

// ========================================
// COMMENTI E RATING
// ========================================

    /**
     * Lista commenti associati alla sezione
     */
    private List<Commento> commenti;

    /**
     * Lista rating
     */
    //private List<Rating> ratings;

    /**
     * Numero commenti (contatore)
     */
    private String numeroCommenti = "0";

    /**
     * Rating medio
     */
    private int rating = 0;

// ========================================
// SONDAGGI
// ========================================

    private String subsectType;


// ========================================
// SECTION TYPES
// ========================================

    private List<SectionType> sectionTypes;

// ========================================
// ALLEGATO SINGOLO
// ========================================

    /**
     * Allegato singolo (legacy)
     */
    private Allegato allegato;

// ========================================
// ALTRI CAMPI
// ========================================

    private String apertoda = "";
    private String urlBack = "";
    private String vis = "0";

    /**
     * Numero di contenuti (size cache)
     */
    private String sizeContenuti = "0";

    /**
     * Giorno (estratto da dataVisualizzata)
     */
    private String giorno = "";

    /**
     * Mese abbreviato (estratto da dataVisualizzata)
     */
    private String mese3 = "";

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

    // ========================================
// METODI HELPER - CAMPI L ESTESI
// ========================================

    /**
     * Ottieni campo L per numero (1-15) - ESTESO
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
            case 11 -> l11;  // NUOVO
            case 12 -> l12;  // NUOVO
            case 13 -> l13;  // NUOVO
            case 14 -> l14;  // NUOVO
            case 15 -> l15;  // NUOVO
            default -> null;
        };
    }

    /**
     * Imposta campo L per numero (1-15) - ESTESO
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
            case 11 -> l11 = valore;  // NUOVO
            case 12 -> l12 = valore;  // NUOVO
            case 13 -> l13 = valore;  // NUOVO
            case 14 -> l14 = valore;  // NUOVO
            case 15 -> l15 = valore;  // NUOVO
        }
    }

// ========================================
// METODI HELPER - EXTRATAG REF
// ========================================

    /**
     * Ottieni ExtraTagRef per numero (1-10)
     */
    public String getExtraTagRef(int numero) {
        return switch (numero) {
            case 1 -> extraTagRef1;
            case 2 -> extraTagRef2;
            case 3 -> extraTagRef3;
            case 4 -> extraTagRef4;
            case 5 -> extraTagRef5;
            case 6 -> extraTagRef6;
            case 7 -> extraTagRef7;
            case 8 -> extraTagRef8;
            case 9 -> extraTagRef9;
            case 10 -> extraTagRef10;
            default -> null;
        };
    }

    /**
     * Imposta ExtraTagRef per numero (1-10)
     */
    public void setExtraTagRef(int numero, String valore) {
        switch (numero) {
            case 1 -> extraTagRef1 = valore;
            case 2 -> extraTagRef2 = valore;
            case 3 -> extraTagRef3 = valore;
            case 4 -> extraTagRef4 = valore;
            case 5 -> extraTagRef5 = valore;
            case 6 -> extraTagRef6 = valore;
            case 7 -> extraTagRef7 = valore;
            case 8 -> extraTagRef8 = valore;
            case 9 -> extraTagRef9 = valore;
            case 10 -> extraTagRef10 = valore;
        }
    }

// ========================================
// METODI HELPER - NUMERATORE FORMATTATO
// ========================================

    /**
     * Ottieni numeratore formattato con padding (00000000)
     */
    public String getNumeratoreFormatted(int numero) {
        Long value = switch (numero) {
            case 1 -> numeratore1;
            case 2 -> numeratore2;
            case 3 -> numeratore3;
            case 4 -> numeratore4;
            case 5 -> numeratore5;
            default -> 0L;
        };
        return String.format("%08d", value);
    }

// ========================================
// METODI HELPER - LOGO GALLERY
// ========================================

    /**
     * Ottieni logo da gallery position
     */
    public String getLogoFromGallery(int position) {
        if (galleryList != null && galleryList.size() > position) {
            return galleryList.get(position).getFullpath();
        }
        return "nofoto.jpg";
    }

// ========================================
// METODI HELPER - TAG CLOUD
// ========================================

    /**
     * Genera tag cloud HTML da campo tag
     */
    public String getTagsCloudHtml() {
        if (tag == null || tag.isEmpty()) {
            return "";
        }

        StringBuilder cloud = new StringBuilder();
        String[] tags = tag.split(",");

        for (String t : tags) {
            t = t.trim();
            if (!t.isEmpty()) {
                cloud.append("<a class='badge badge-sm badge-pill badge-outline-greendark' ")
                        .append("href=\"tagArgomento/").append(t).append("\">")
                        .append(t).append("</a> ");
            }
        }

        return cloud.toString();
    }

// ========================================
// METODI HELPER - DATA ESTRATTA
// ========================================

    /**
     * Estrae giorno da dataVisualizzata
     */
    public String getGiornoFromData() {
        if (dataVisualizzata != null && !dataVisualizzata.isEmpty() &&
                dataVisualizzata.contains(" ")) {
            return dataVisualizzata.substring(0, dataVisualizzata.indexOf(" "));
        }
        return "";
    }

    /**
     * Estrae mese (3 caratteri) da dataVisualizzata
     */
    public String getMese3FromData() {
        if (dataVisualizzata != null && !dataVisualizzata.isEmpty() &&
                dataVisualizzata.contains(" ")) {
            int start = dataVisualizzata.indexOf(" ") + 1;
            int end = Math.min(start + 3, dataVisualizzata.length());
            return dataVisualizzata.substring(start, end);
        }
        return "";
    }
}