package com.studiodomino.jplatform.cms.entity;

import com.studiodomino.jplatform.cms.front.dto.ExtraTag;
import com.studiodomino.jplatform.cms.front.dto.Breadcrumb;
import com.studiodomino.jplatform.shared.entity.Images;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.beans.Transient;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;

/**
* Data Transfer Object per la rappresentazione semplificata dei contenuti.
* Utilizzato principalmente nel front-end per visualizzare sezioni e documenti
* senza la complessità completa dell'entità Content.
*
* Questo POJO contiene una versione "flattened" dei dati di Content con campi
* aggiuntivi calcolati (URL, logo, tags formattati) utili per il rendering.
*/
@Data
@NoArgsConstructor
public class DatiBase implements Serializable {

private static final long serialVersionUID = -2831802014362039895L;

// ========================================
// IDENTIFICATORI E RELAZIONI
// ========================================

private String prog;
private String id;
private String idRoot;
private String idType;
private String idParent;
private String idSite;

// ========================================
// CONTENUTO PRINCIPALE
// ========================================

private String titolo;
private String riassunto;
private String testo;

// Versioni EN per i18n
private String titoloEN;
private String riassuntoEN;
private String testoEN;

// ========================================
// DATE E METADATI
// ========================================

private String data;
private String dataVisualizzata;
private LocalDate dataSql;
private String tag;
private String tagsCloud;

private int anno = 2012;
private String mese;
private String giorno = "";
private String mese3 = "";

// ========================================
// STATO E VISIBILITÀ
// ========================================

private String stato;
private String statoText = "";
private String privato;
private String idGruppo = "0";
private String position;

// ========================================
// ALLEGATI E GALLERY
// ========================================

private String vis;
private String repo;
private String repoId = "";
private String repoName;
private String galleryString = "";

private List<Allegato> allegati = new ArrayList<>();
private List<Images> gallery = new ArrayList<>();
private Allegato allegato = new Allegato();

private String logo = "";
private String logo2 = "";
private String logo3 = "";

// ========================================
// LIVELLI (l1-l15)
// ========================================

private String l1 = "";
private String l2 = "";
private String l3 = "";
private String l4 = "";
private String l5 = "";
private String l6 = "";
private String l7 = "";
private String l8 = "";
private String l9 = "";
private String l10 = "";
private String l11 = "";
private String l12 = "";
private String l13 = "";
private String l14 = "";
private String l15 = "";

// ========================================
// CAMPI INFO (1-5)
// ========================================

private String info1 = "";
private String info2 = "";
private String info3 = "";
private String info4 = "";
private String info5 = "";

// ========================================
// CAMPI S (1-10)
// ========================================

private String s1 = "";
private String s2 = "";
private String s3 = "";
private String s4 = "";
private String s5 = "";
private String s6 = "";
private String s7 = "";
private String s8 = "";
private String s9 = "";
private String s10 = "";

// ========================================
// AUDIT E TRACKING
// ========================================

private String apertoDa = "";
private String creato = "";
private String creatoDa = "";
private String modificato = "";
private String modificatoDa = "";
private int click = 0;

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
// ARRAY FIELDS (1-5)
// ========================================

private String[] array1 = {"0"};
private String[] array2 = {"0"};
private String[] array3 = {"0"};
private String[] array4 = {"0"};
private String[] array5 = {"0"};

// ========================================
// CAMPI TEXT (1-10)
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
// CAMPI DATA (1-10)
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
// CAMPI VARCHAR (1-10)
// ========================================

private String varchar1 = "";
private String varchar2 = "";
private String varchar3 = "";
private String varchar4 = "";
private String varchar5 = "";
private String varchar6 = "";
private String varchar7 = "";
private String varchar8 = "";
private String varchar9 = "";
private String varchar10 = "";

// ========================================
// CAMPI NUMBER (1-10)
// ========================================

private double number1 = 0;
private double number2 = 0;
private double number3 = 0;
private double number4 = 0;
private double number5 = 0;
private double number6 = 0;
private double number7 = 0;
private double number8 = 0;
private double number9 = 0;
private double number10 = 0;

// ========================================
// CAMPI LOG (1-3)
// ========================================

private String log1 = "";
private String log2 = "";
private String log3 = "";

// ========================================
// NUMERATORI (1-5)
// ========================================

private Long numeratore1 = 0L;
private Long numeratore2 = 0L;
private Long numeratore3 = 0L;
private Long numeratore4 = 0L;
private Long numeratore5 = 0L;

// ========================================
// EXTRA TAG
// ========================================

private String extraTag1 = "";
private String extraTag2 = "";
private String extraTag3 = "";
private String extraTag4 = "";
private String extraTag5 = "";
private String extraTag6 = "";
private String extraTag7 = "";
private String extraTag8 = "";
private String extraTag9 = "";
private String extraTag10 = "";

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

private String ordineExtraTag = "rand()";
private String maxExtraTag = "5";
private String regolaExtraTag1 = "0";
private String regolaExtraTag2 = "0";

// ========================================
// RELAZIONI - CORRECTED
// ========================================

private Section section;  // ✅ FIXED: era Conten
private DatiBase relazione;
private List<Section> relazioneSezioni = new ArrayList<>();  // ✅ FIXED: era List<Conten>

private List<Commento> commenti = new ArrayList<>();
private String numeroCommenti = "0";

private List<Rating> ratings = new ArrayList<>();
private int rating = 0;

private ExtraTag extratag = new ExtraTag();

private List<DatiBase> docCorrelati1 = new ArrayList<>();
private List<DatiBase> docCorrelati2 = new ArrayList<>();
private List<DatiBase> docCorrelati3 = new ArrayList<>();
private List<DatiBase> docCorrelati4 = new ArrayList<>();
private List<DatiBase> docCorrelati5 = new ArrayList<>();

// ========================================
// CAMPI TEMPORANEI
// ========================================

private String label = "";
private String locale = "";
private String temp1 = "";
private String temp2 = "";
private String temp3 = "";
private String temp4 = "";
private String temp5 = "";

// ========================================
// COSTRUTTORI
// ========================================

/**
 * Costruttore completo
 */
public DatiBase(String id, String idRoot, String idType, String data,
                String dataVisualizzata, String tag, String titolo,
                String riassunto, String testo, List<Allegato> allegati,
                List<Images> gallery, String l1, String l2, String l3,
                String l4, String l5, String l6, String l7, String l8, String l9,
                String l10, String l11, String l12, String l13, String l14,
                String l15, String idParent, String stato, String privato,
                String idSite) {
    this.id = id;
    this.idRoot = idRoot;
    this.idType = idType;
    this.data = data;
    this.dataVisualizzata = dataVisualizzata;
    this.tag = tag;
    this.titolo = titolo;
    this.riassunto = riassunto;
    this.testo = testo;
    this.allegati = allegati != null ? allegati : new ArrayList<>();
    this.gallery = gallery != null ? gallery : new ArrayList<>();
    this.l1 = l1;
    this.l2 = l2;
    this.l3 = l3;
    this.l4 = l4;
    this.l5 = l5;
    this.l6 = l6;
    this.l7 = l7;
    this.l8 = l8;
    this.l9 = l9;
    this.l10 = l10;
    this.l11 = l11;
    this.l12 = l12;
    this.l13 = l13;
    this.l14 = l14;
    this.l15 = l15;
    this.idParent = idParent;
    this.stato = stato;
    this.privato = privato;
    this.idSite = idSite;
}

// ========================================
// METODI GETTER CUSTOMIZZATI
// ========================================

/**
 * Ritorna l'ID, fallback su prog se vuoto
 */
public String getId() {
    if (this.id == null || this.id.equals("")) {
        return prog;
    }
    return id;
}

/**
 * Ritorna il titolo localizzato
 */
public String getTitolo() {
    if (locale != null && !locale.equals("") && !locale.equals("it_IT")) {
        return getTitoloEN();
    }
    return titolo;
}

public String getUrlIdTitolo() {
    return "front/" + this.id + "/" + getTitoloLabel();
}

/**
 * Ritorna il titolo EN con default
 */
public String getTitoloEN() {
    if (titoloEN == null || titoloEN.equals("")) {
        return "Not define";
    }
    return titoloEN;
}

/**
 * Ritorna il titolo formattato per label (senza caratteri speciali)
 */
public String getTitoloLabel() {
    try {
        if (this.titolo != null && !this.titolo.equals("")) {
            return this.titolo
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

/**
 * Ritorna il titolo come testo pulito (senza apici)
 */
public String getTitoloText() {
    if (locale != null && !locale.equals("") && !locale.equals("it_IT")) {
        return getTitoloEN().replaceAll("'", " ").replaceAll("\"", " ");
    }
    return titolo.replaceAll("'", " ").replaceAll("\"", " ");
}

/**
 * Ritorna il riassunto localizzato
 */
public String getRiassunto() {
    if (locale != null && !locale.equals("") && !locale.equals("it_IT")) {
        return getRiassuntoEN();
    }
    return riassunto;
}

/**
 * Ritorna il testo localizzato
 */
public String getTesto() {
    if (locale != null && !locale.equals("") && !locale.equals("it_IT")) {
        return getTestoEN();
    }
    return testo;
}

/**
 * Ritorna il label (titolo pulito)
 */
public String getLabel() {
    if (this.titolo != null) {
        return this.titolo.replace("'", "").replace("\"", "");
    }
    return label;
}

/**
 * Ritorna lo stato formattato come chiave i18n
 */
public String getStatoText() {
    return "stato_" + this.stato;
}

/**
 * Ritorna numero intero da number1
 */
public int getNumberInt1() {
    return (int) number1;
}

public int getNumberInt2() {
    return (int) number2;
}

public int getNumberInt3() {
    return (int) number3;
}

public int getNumberInt4() {
    return (int) number4;
}

public int getNumberInt5() {
    return (int) number5;
}

public int getNumberInt6() {
    return (int) number6;
}

public int getNumberInt7() {
    return (int) number7;
}

public int getNumberInt8() {
    return (int) number8;
}

public int getNumberInt9() {
    return (int) number9;
}

public int getNumberInt10() {
    return (int) number10;
}

// ========================================
// METODI NUMERATORI FORMATTATI
// ========================================

/**
 * Ritorna numeratore1 formattato con 8 cifre (00000000)
 */
public String getNumeratore1() {
    if (numeratore1 == null || numeratore1 == 0L) {
        return id != null ? String.format("%08d", Long.parseLong(id)) : "00000000";
    }
    DecimalFormat formatter = new DecimalFormat("00000000");
    return formatter.format(numeratore1);
}

/**
 * Ritorna numeratore1 base senza formattazione
 */
public String getNumeratore1Base() {
    return numeratore1.toString();
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

// Dopo i metodi getNumeratore1Base() esistenti, aggiungi:

/**
 * Ritorna numeratore1 come Long (raw value)
 */
public Long getNumeratore1Long() {
    return numeratore1;
}

public Long getNumeratore2Long() {
    return numeratore2;
}

public Long getNumeratore3Long() {
    return numeratore3;
}

public Long getNumeratore4Long() {
    return numeratore4;
}

public Long getNumeratore5Long() {
    return numeratore5;
}



// ========================================
// METODI ALLEGATI E GALLERY
// ========================================

/**
 * Controlla se ci sono allegati
 */
public Boolean getAllegatiCheck() {
    return this.allegati != null && this.allegati.size() > 0;
}

/**
 * Controlla se c'è una gallery
 */
public Boolean getGalleryCheck() {
    return this.gallery != null && this.gallery.size() > 0;
}

/**
 * Ritorna il logo principale (prima immagine della gallery o nofoto.jpg)
 */
public String getLogo() {
    String foto = "nofoto.jpg";
    if (getGalleryCheck()) {
        foto = this.gallery.get(0).getFullpath();
    }
    return foto;
}

/**
 * Ritorna il secondo logo
 */
public String getLogo2() {
    String foto = "nofoto.jpg";
    if (getGalleryCheck() && this.gallery.size() >= 2) {
        foto = this.gallery.get(1).getFullpath();
    }
    return foto;
}

/**
 * Ritorna il terzo logo
 */
public String getLogo3() {
    String foto = "nofoto.jpg";
    if (getGalleryCheck() && this.gallery.size() >= 3) {
        foto = this.gallery.get(2).getFullpath();
    }
    return foto;
}

/**
 * Ritorna un'immagine random dalla gallery
 */
public Images getRandomImage() {
    int size = gallery.size();
    if (size == 0) return null;
    int rand = (int) (size * Math.random());
    return gallery.get(rand);
}

// ========================================
// METODI URL BUILDING
// ========================================

/**
 * URL standard per query parameter
 */
public String getUrl() {
    return "/front/" + this.id + "/" + getTitoloLabel();
}

/**
 * URL rewrite-friendly pubblico
 */
public String getUrlRW() {
    return "page/" + this.idSite + "/" + this.id + "/" +
            this.stato + "/" + this.anno + "/" + getTitoloLabel();
}

/**
 * URL rewrite-friendly privato
 */
public String getUrlRWPrivato() {
    return "Privatepage/" + this.idSite + "/" + this.id + "/" +
            this.stato + "/" + this.anno + "/" + getTitoloLabel();
}

/**
 * URL rewrite semplice (senza titolo)
 */
public String getUrlRWS() {
    return "page/" + this.idSite + "/" + this.id + "/" +
            this.stato + "/" + this.anno + "/documento";
}

/**
 * URL archivio
 */
public String getUrlRWArchivio() {
    return "pageArchivio/" + this.idSite + "/" + this.id + "/" +
            this.stato + "/" + this.anno + "/" + this.mese + "/" + getTitoloLabel();
}

/**
 * URL backend (Spring Boot)
 */
public String getUrlBackEnd() {
    if ("-1".equals(this.idRoot)) {
        return "/admin/sezioni";
    } else {
        return "/admin/documenti";
    }
}

// ========================================
// METODI TAG CLOUD E DATE
// ========================================

/**
 * Genera il tag cloud HTML dai tag
 */
public String getTagsCloud() {
    if (tag == null || tag.isEmpty()) {
        return "";
    }

    StringTokenizer st = new StringTokenizer(this.tag, ",");
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
 * Estrae i link dal campo text10
 */
public List<String> getLinks() {
    List<String> linkList = new ArrayList<>();
    if (text10 == null || text10.isEmpty()) {
        return linkList;
    }

    StringTokenizer st = new StringTokenizer(this.text10, ",");
    while (st.hasMoreTokens()) {
        String tags = st.nextToken().trim();
        linkList.add("<a target=\"_blank\" href=\"http://" + tags + "\">" + tags + "</a>");
    }

    return linkList;
}

/**
 * Estrae il giorno dalla dataVisualizzata
 */
public String getGiorno() {
    if (this.dataVisualizzata != null && !this.dataVisualizzata.equals("")) {
        int spaceIndex = this.dataVisualizzata.indexOf(" ");
        if (spaceIndex > 0) {
            this.giorno = this.dataVisualizzata.substring(0, spaceIndex);
            return this.giorno;
        }
    }
    return "";
}

/**
 * Estrae il mese (3 caratteri) dalla dataVisualizzata
 */
public String getMese3() {
    if (this.dataVisualizzata != null && !this.dataVisualizzata.equals("")) {
        int spaceIndex = this.dataVisualizzata.indexOf(" ");
        if (spaceIndex > 0 && this.dataVisualizzata.length() >= spaceIndex + 4) {
            this.mese3 = this.dataVisualizzata.substring(spaceIndex + 1, spaceIndex + 4);
            return this.mese3;
        }
    }
    return "";
}

/**
 * Converte il mese da abbreviazione a numero
 */
public String getMese() {
    Map<String, String> mesi = new HashMap<>();
    mesi.put("Gen", "01");
    mesi.put("Feb", "02");
    mesi.put("Mar", "03");
    mesi.put("Apr", "04");
    mesi.put("Mag", "05");
    mesi.put("Giu", "06");
    mesi.put("Lug", "07");
    mesi.put("Ago", "08");
    mesi.put("Set", "09");
    mesi.put("Ott", "10");
    mesi.put("Nov", "11");
    mesi.put("Dic", "12");

    return mesi.getOrDefault(getMese3(), "01");
}

// ========================================
// METODI COMMENTI
// ========================================

/**
 * Ritorna i commenti solo se presenti
 */
public List<Commento> getCommenti() {
    if (this.commenti == null || this.commenti.size() == 0) {
        return null;
    }
    return commenti;
}

// ========================================
// METODI SETTER CUSTOM
// ========================================

/**
 * Setter per numeratore da String
 */
public void setNumeratore1String(String numeratore1) {
    this.numeratore1 = Long.parseLong(numeratore1);
}
public void setNumeratore2String(String numeratore1) {
    this.numeratore2 = Long.parseLong(numeratore1);
}
public void setNumeratore3String(String numeratore1) {
    this.numeratore3 = Long.parseLong(numeratore1);
}
public void setNumeratore4String(String numeratore1) {
    this.numeratore4 = Long.parseLong(numeratore1);
}
public void setNumeratore5String(String numeratore1) {
    this.numeratore5 = Long.parseLong(numeratore1);
}

/**
 * Aggiunge un allegato alla lista
 */
public void addAllegato(Allegato tmp) {
    if (this.allegati == null) {
        this.allegati = new ArrayList<>();
    }
    this.allegati.add(tmp);
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

// In DatiBase.java e Section.java
@Transient
public String getExtraTag(int numero) {
    return switch (numero) {
        case 1 -> extraTag1;
        case 2 -> extraTag2;
        case 3 -> extraTag3;
        case 4 -> extraTag4;
        case 5 -> extraTag5;
        case 6 -> extraTag6;
        case 7 -> extraTag7;
        case 8 -> extraTag8;
        case 9 -> extraTag9;
        case 10 -> extraTag10;
        default -> null;
    };
}

    @Transient
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

    @Transient
    public void setExtraTag(int numero, String value) {
        switch (numero) {
            case 1 -> this.extraTag1 = value;
            case 2 -> this.extraTag2 = value;
            case 3 -> this.extraTag3 = value;
            case 4 -> this.extraTag4 = value;
            case 5 -> this.extraTag5 = value;
            case 6 -> this.extraTag6 = value;
            case 7 -> this.extraTag7 = value;
            case 8 -> this.extraTag8 = value;
            case 9 -> this.extraTag9 = value;
            case 10 -> this.extraTag10 = value;
            default -> { }
        }
    }

    @Transient
    public void setExtraTagRef(int numero, String value) {
        switch (numero) {
            case 1 -> this.extraTagRef1 = value;
            case 2 -> this.extraTagRef2 = value;
            case 3 -> this.extraTagRef3 = value;
            case 4 -> this.extraTagRef4 = value;
            case 5 -> this.extraTagRef5 = value;
            case 6 -> this.extraTagRef6 = value;
            case 7 -> this.extraTagRef7 = value;
            case 8 -> this.extraTagRef8 = value;
            case 9 -> this.extraTagRef9 = value;
            case 10 -> this.extraTagRef10 = value;
            default -> { }
        }
    }
}