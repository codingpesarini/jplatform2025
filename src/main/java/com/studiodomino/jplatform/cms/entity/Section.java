package com.studiodomino.jplatform.cms.entity;

import com.studiodomino.jplatform.cms.front.dto.ExtraTag;
import com.studiodomino.jplatform.shared.entity.Images;
import com.studiodomino.jplatform.shared.entity.UtenteEsterno;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * DTO che rappresenta una SEZIONE nel CMS (idRoot = -1).
 * Versione completa con tutti i metodi helper del sistema legacy.
 */
@Data
public class Section implements Serializable {

    private static final long serialVersionUID = 1L; //è un identificatore di versione, Se non lo dichiari il compilatore Java ne genererà uno automaticamente, ma è consigliato definirlo per garantire la portabilità tra diversi compilatori e ambienti

    // ========================================
    // IDENTIFICAZIONE
    // ========================================

    private Integer id;
    private String idSite;
    private Integer idRoot = -1;
    private Integer idType;
    private String idParent;
    private String label;

    // ========================================
    // CONTENUTO BASE
    // ========================================

    private String titolo;
    private String riassunto;
    private String testo;
    private String titoloEN;
    private String riassuntoEN;
    private String testoEN;

    // ========================================
    // STATO E VISIBILITÀ
    // ========================================

    private String stato;
    private String privato;
    private String idGruppo;
    private String visualizza;

    // ========================================
    // POSIZIONAMENTO
    // ========================================

    private Integer position;
    private String firstPage;
    private Integer click;

    // ========================================
    // MEDIA
    // ========================================

    private List<Images> gallery;
    private String galleryString= "";
    private String idAllegato;

    // ========================================
    // TAG
    // ========================================

    private String tag;
    private String tagsCloud;

    // ========================================
    // MENU
    // ========================================

    private String menu1;
    private String menu2;
    private String menu3;
    private String menu4;
    private String menu5;

    // ========================================
    // CAMPI S (1-10)
    // ========================================

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

    // ========================================
    // CAMPI L (1-15)
    // ========================================

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
    private String l11;
    private String l12;
    private String l13;
    private String l14;
    private String l15;

    // ========================================
    // INFO (1-5)
    // ========================================

    private String info1 = "";
    private String info2 = "";
    private String info3 = "";
    private String info4 = "";
    private String info5 = "";

    // ========================================
    // DATE
    // ========================================

    private String data;
    private String dataVisualizzata;
    private LocalDate dataSql;
    private String creato;
    private String creatoDa;
    private String modificato;
    private String modificatoDa;
    private String apertoda = "";

    // Date extraction helpers
    private String giorno = "";
    private String mese = "";
    private String mese3 = "";
    private int anno = 2012;

    // ========================================
    // ARCHIVIO E FILTRI
    // ========================================

    private String annoTemp;
    private String meseTemp;
    private String statoArchivio = "1";

    // ========================================
    // URL REWRITING
    // ========================================

    private String urlRW;
    private String urlRWPages;
    private String urlRWArchivio;
    private String urlBack = "";

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
    // TEXT (1-10)
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
    // DATA (1-10)
    // ========================================

    private String data1;
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
    // VARCHAR (1-10)
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
    // NUMBER (1-10)
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
    // ARRAY (1-5)
    // ========================================

    private String[] array1 = {"0"};
    private String[] array2 = {"0"};
    private String[] array3 = {"0"};
    private String[] array4 = {"0"};
    private String[] array5 = {"0"};

    // ========================================
    // NUMERATORI (1-5)
    // ========================================

    private Long numeratore1 = 0L;
    private Long numeratore2 = 0L;
    private Long numeratore3 = 0L;
    private Long numeratore4 = 0L;
    private Long numeratore5 = 0L;

    // ========================================
    // LOG (1-3)
    // ========================================

    private String log1 = "";
    private String log2 = "";
    private String log3 = "";

    // ========================================
    // TEMP (1-5)
    // ========================================

    private String temp1 = "";
    private String temp2 = "";
    private String temp3 = "";
    private String temp4 = "";
    private String temp5 = "";

    // ========================================
    // LOGO
    // ========================================

    private String logo = "";
    private String logo2 = "";
    private String logo3 = "";

    // ========================================
    // REPOSITORY
    // ========================================

    private String repo = "0";
    private String repoId = "0";
    private String repoName = "";
    private String vis = "0";

    // ========================================
    // EXTRATAG (1-10)
    // ========================================

    private String extratag1 = "";
    private String extratag2 = "";
    private String extratag3 = "";
    private String extratag4 = "";
    private String extratag5 = "";
    private String extratag6 = "";
    private String extratag7 = "";
    private String extratag8 = "";
    private String extratag9 = "";
    private String extratag10 = "";

    // ========================================
    // EXTRATAG REF (1-10)
    // ========================================

    private String extraTagRef1 = "";
    private String extraTagRef2 = "";
    private String extraTagRef3 = "";
    private String extraTagRef4 = "";
    private String extraTagRef5 = "";
    private String extraTagRef6 = "";
    private String extraTagRef7 = "";
    private String extraTagRef8 = "";
    private String extraTagRef9 = "";
    private String extraTagRef10 = "";

    // ========================================
    // REGOLE EXTRATAG
    // ========================================

    private String ordineExtraTag = "rand()";
    private String maxExtraTag = "5";
    private String regolaExtraTag1 = "0";
    private String regolaExtraTag2 = "0";

    // ========================================
    // ORDINAMENTO
    // ========================================

    private String ordineContenuti;
    private String ordineSottosezioni;
    private String maxOrdineContenuti;
    private String maxOrdineSottosezioni;
    private int order = 0;

    // ========================================
    // RELAZIONI
    // ========================================

    private SectionType sectionType;
    private List<SectionType> sectionTypes;
    private List<Section> subsection;
    private List<Section> subsectionParent;
    private List<Section> subsectionPrivate;
    private List<Section> parentSection;
    private List<Section> relazione;
    private List<Section> relazioneInversa;
    private List<DatiBase> contenuti;
    private List<Allegato> allegati;
    private List<Images> galleryList;
    private Allegato allegato;
    private ExtraTag extratag;
    private Section sezionePadre;

    // ========================================
    // UTENTI E GRUPPI
    // ========================================

    private List<UtenteEsterno> utentiAssociati;
    private String utentiAssociatiString = "";
    private String[] idgruppi;

    // ========================================
    // COMMENTI E RATING
    // ========================================

    private List<Commento> commenti;
    private String numeroCommenti = "0";
    private int rating = 0;

    // ========================================
    // STATO E FLAGS
    // ========================================

    private String statoText = "";
    private String sizeContenuti = "0";
    private Boolean emptySubsection;
    private Boolean emptyContenuti;

    // ========================================
    // SONDAGGI (LEGACY)
    // ========================================

    private String sondaggio;
    private String oldsondaggio;
    private String subsectType;

    // ========================================
    // i18n
    // ========================================

    private String locale = "it_IT";

    // ========================================
    // METODI GETTER CUSTOM - TITOLO
    // ========================================

    /**
     * Ritorna il titolo localizzato
     */
    public String getTitolo() {
        if (locale != null && !locale.isEmpty() && !"it_IT".equals(locale)) {
            return getTitoloEN();
        }
        return titolo;
    }

    /**
     * Ritorna il titolo EN con fallback
     */
    public String getTitoloEN() {
        if (titoloEN == null || titoloEN.isEmpty()) {
            return "Not define";
        }
        return titoloEN;
    }

    /**
     * Ritorna il titolo formattato per URL (label sanitizzata)
     */
    public String getTitoloLabel() {
        try {
            if (titolo != null && !titolo.isEmpty()) {
                return titolo
                        .replace(" ", "_")
                        .replace("/", "_")
                        .replace("\\", "_")
                        .replace("\"", "_")
                        .replace("'", "_");
            }
            return titolo;
        } catch (Exception e) {
            return titolo;
        }
    }

    // ========================================
    // METODI GETTER CUSTOM - RIASSUNTO/TESTO
    // ========================================

    /**
     * Ritorna il riassunto localizzato
     */
    public String getRiassunto() {
        if (locale != null && !locale.isEmpty() && !"it_IT".equals(locale)) {
            return getRiassuntoEN();
        }
        return riassunto;
    }

    /**
     * Ritorna il testo localizzato
     */
    public String getTesto() {
        if (locale != null && !locale.isEmpty() && !"it_IT".equals(locale)) {
            return getTestoEN();
        }
        return testo;
    }

    // ========================================
    // METODI HELPER - STATO
    // ========================================

    /**
     * Ritorna lo stato formattato come chiave i18n
     */
    public String getStatoText() {
        return "stato_" + this.stato;
    }

    /**
     * Verifica se ha sotto-sezioni
     */
    public Boolean getEmptySubsection() {
        if (subsection == null) return true;
        return subsection.isEmpty();
    }

    /**
     * Verifica se ha contenuti
     */
    public Boolean getEmptyContenuti() {
        if (contenuti == null) return true;
        return contenuti.isEmpty();
    }

    /**
     * Ritorna il numero di contenuti come stringa
     */
    public String getSizeContenuti() {
        if (contenuti == null) return "0";
        if (contenuti.isEmpty()) return "0";
        return Integer.toString(contenuti.size());
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
     * Controlla se ci sono allegati
     */
    public Boolean getAllegatiCheck() {
        if (allegati == null || allegati.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Verifica se ha gallery
     */
    public boolean hasGallery() {
        return galleryList != null && !galleryList.isEmpty();
    }

    /**
     * Controlla se c'è una gallery
     */
    public Boolean getGalleryCheck() {
        if (galleryList == null || galleryList.isEmpty()) {
            return false;
        }
        return true;
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
    // METODI HELPER - GALLERY E LOGO
    // ========================================

    /**
     * Ottieni immagine dalla gallery per posizione
     */
    public Images getGallery(int pos) {
        if (galleryList != null && galleryList.size() > pos) {
            return galleryList.get(pos);
        }
        return null;
    }

    /**
     * Ritorna il logo principale (prima immagine della gallery)
     */
    public String getLogo() {
        String foto = "nofoto.jpg";
        if (getGalleryCheck() && galleryList.size() > 0) {
            foto = galleryList.get(0).getFullpath();
        }
        return foto;
    }

    /**
     * Ritorna il secondo logo
     */
    public String getLogo1() {
        String foto = "nofoto.jpg";
        if (getGalleryCheck() && galleryList.size() >= 2) {
            foto = galleryList.get(1).getFullpath();
        }
        return foto;
    }

    /**
     * Ritorna il secondo logo (alias)
     */
    public String getLogo2() {
        String foto = "nofoto.jpg";
        if (getGalleryCheck() && galleryList.size() >= 2) {
            foto = galleryList.get(1).getFullpath();
        }
        return foto;
    }

    /**
     * Ritorna il terzo logo
     */
    public String getLogo3() {
        String foto = "nofoto.jpg";
        if (getGalleryCheck() && galleryList.size() >= 3) {
            foto = galleryList.get(2).getFullpath();
        }
        return foto;
    }

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
    // METODI HELPER - URL BUILDING
    // ========================================

    /**
     * URL standard per query parameter
     */
    public String getUrl() {
        return "/front/" + this.id + "/" + getTitoloLabel();
    }

    /**
     * URL rewrite-friendly
     */
    public String getUrlRW() {
        if ("0".equals(privato)) {
            return "page/" + idSite + "/" + id + "/" + stato + "/" + anno + "/" + getTitoloLabel();
        } else {
            return "Privatepage/" + idSite + "/" + id + "/" + stato + "/" + anno + "/" + getTitoloLabel();
        }
    }

    /**
     * URL rewrite base (senza .html)
     */
    public String getUrlRWBase() {
        return idSite + "/" + id + "/" + stato + "/" + anno + "/" + getTitoloLabel();
    }

    /**
     * URL rewrite semplice
     */
    public String getUrlRWS() {
        return "page/" + idSite + "/" + id + "/" + stato + "/" + anno + "/section";
    }

    /**
     * URL per paginazione
     */
    public String getUrlRWPages() {
        return "pages/" + idSite + "/" + id + "/" + stato + "/";
    }

    /**
     * URL paginazione query string
     */
    public String getUrlRWPagination() {
        return "Pager.do?pid=" + id + "&amp;site=" + idSite + "&amp;stato=" + statoArchivio;
    }

    /**
     * URL archivio
     */
    public String getUrlRWArchivio() {
        return "pageArchivio/" + idSite + "/" + id + "/" + statoArchivio + "/" +
                annoTemp + "/" + meseTemp + "/";
    }

    /**
     * URL privato
     */
    public String getUrlPrivate() {
        return "Pager.do?pid=" + id + "&site=" + idSite +
                "&title=" + getTitoloLabel() + "&root=" + idRoot;
    }

    /**
     * URL home
     */
    public String getUrlHome() {
        return "Pager.do?pid=" + id + "&site=" + idSite +
                "&path=home&title=" + getTitoloLabel();
    }

    public String getUrlbreadcrumb() {
        if ("-1".equals(String.valueOf(id))) {
            return "Pager.do?service=" + label + "&title=" + getTitoloLabel();
        }
        return "Pager.do?pid=" + id + "&site=" + idSite + "&title=" + getTitoloLabel();
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
    // METODI HELPER - TAG CLOUD
    // ========================================

    /**
     * Genera il tag cloud HTML dai tag
 */
    public String getTagsCloud() {
        if (tag == null || tag.isEmpty()) {
            return "";
        }

        StringTokenizer st = new StringTokenizer(tag, ",");
        StringBuilder cloud = new StringBuilder();

        while (st.hasMoreTokens()) {
            String tags = st.nextToken().trim();
            cloud.append("<a class='badge badge-sm badge-pill badge-outline-greendark' href=\"tagArgomento/")
                    .append(tags)
                    .append("\">")
                    .append(tags)
                    .append("</a> ");
        }

        return cloud.toString();
    }

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
    // METODI HELPER - DATE
    // ========================================

    /**
     * Estrae il giorno dalla dataVisualizzata
     */
    public String getGiorno() {
        if (dataVisualizzata != null && !dataVisualizzata.isEmpty() &&
                dataVisualizzata.contains(" ")) {
            this.giorno = dataVisualizzata.substring(0, dataVisualizzata.indexOf(" "));
            return this.giorno;
        }
        return "";
    }

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
     * Estrae il mese (3 caratteri) dalla dataVisualizzata
     */
    public String getMese3() {
        if (dataVisualizzata != null && !dataVisualizzata.isEmpty() &&
                dataVisualizzata.contains(" ")) {
            int start = dataVisualizzata.indexOf(" ") + 1;
            int end = Math.min(start + 3, dataVisualizzata.length());
            this.mese3 = dataVisualizzata.substring(start, end);
            return this.mese3;
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

    // ========================================
    // METODI HELPER - LINKS
    // ========================================

    /**
     * Estrae i link dal campo text10
     */
    public List<String> getLinks() {
        List<String> linkList = new ArrayList<>();
        if (text10 == null || text10.isEmpty()) {
            return linkList;
        }

        StringTokenizer st = new StringTokenizer(text10, ",");
        while (st.hasMoreTokens()) {
            String tags = st.nextToken().trim();
            linkList.add("<a target=\"_blank\" href=\"http://" + tags + "\">" + tags + "</a>");
        }

        return linkList;
    }

    // ========================================
    // METODI HELPER - NUMERATORI
    // ========================================

    /**
     * Ritorna numeratore1 formattato con 8 cifre (00000000)
     */
    public String getNumeratore1() {
        if (numeratore1 == null || numeratore1 == 0L) {
            return id != null ? String.format("%08d", id) : "00000000";
        }
        DecimalFormat formatter = new DecimalFormat("00000000");
        return formatter.format(numeratore1);
    }

    public String getNumeratore2() {
        DecimalFormat formatter = new DecimalFormat("00000000");
        return formatter.format(numeratore2);
    }

    public String getNumeratore3() {
        DecimalFormat formatter = new DecimalFormat("00000000");
        return formatter.format(numeratore3);
    }

    public String getNumeratore4() {
        DecimalFormat formatter = new DecimalFormat("00000000");
        return formatter.format(numeratore4);
    }

    public String getNumeratore5() {
        DecimalFormat formatter = new DecimalFormat("00000000");
        return formatter.format(numeratore5);
    }

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
    // METODI HELPER - CAMPI L
    // ========================================

    /**
     * Ottieni campo L per numero (1-15)
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
            case 11 -> l11;
            case 12 -> l12;
            case 13 -> l13;
            case 14 -> l14;
            case 15 -> l15;
            default -> null;
        };
    }

    /**
     * Imposta campo L per numero (1-15)
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
            case 11 -> l11 = valore;
            case 12 -> l12 = valore;
            case 13 -> l13 = valore;
            case 14 -> l14 = valore;
            case 15 -> l15 = valore;
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
    // METODI HELPER STATICI
    // ========================================

    /**
     * Converte una stringa separata in array
     */
    public static String[] stringToArray(String input, String separator) {
        if (input == null || input.length() == 0) {
            return new String[0];
        }

        List<String> wordsList = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(input, separator);

        while (st.hasMoreTokens()) {
            wordsList.add(st.nextToken());
        }

        return wordsList.toArray(new String[0]);
    }
}