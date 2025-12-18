package com.studiodomino.jplatform.cms.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Entity che rappresenta una sezione o un contenuto nel CMS.
 * Mappato sulla tabella 'contents' del database.
 *
 * Un Content può essere:
 * - Una SEZIONE (idRoot = -1): contenitore di altre sezioni/contenuti
 * - Un CONTENUTO (idRoot != -1): contenuto foglia appartenente a una sezione
 */
@Entity
@Table(name = "contents")
@Data
public class Content implements Serializable {

    private static final long serialVersionUID = -198850194510371247L;

    // ========== IDENTIFICAZIONE ==========

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "id_site")
    private String idSite;

    /**
     * ID della sezione radice:
     * -1 = questa è una sezione
     * altro valore = questo è un contenuto appartenente alla sezione con questo ID
     */
    @Column(name = "id_root")
    private Integer idRoot;

    @Column(name = "id_type")
    private Integer idType;

    /**
     * ID del parent immediato nella gerarchia
     */
    @Column(name = "idparent")
    private String idParent;

    @Column(name = "label")
    private String label;

    // ========== CONTENUTO BASE ==========

    @Column(name = "titolo")
    private String titolo;

    @Column(name = "riassunto", columnDefinition = "TEXT")
    private String riassunto;

    @Column(name = "testo", columnDefinition = "LONGTEXT")
    private String testo;

    // Versioni EN
    @Column(name = "titoloEN", columnDefinition = "TEXT")
    private String titoloEN;

    @Column(name = "riassuntoEN", columnDefinition = "TEXT")
    private String riassuntoEN;

    @Column(name = "testoEN", columnDefinition = "TEXT")
    private String testoEN;

    // ========== STATO E VISIBILITÀ ==========

    /**
     * Stato: 0=bozza, 1=pubblicato, 3=programmato, 4=annullato
     */
    @Column(name = "stato")
    private String stato;

    /**
     * Privato: 0=pubblico, altro=ID gruppo con accesso
     */
    @Column(name = "privato")
    private String privato;

    @Column(name = "idgruppo")
    private String idGruppo;

    @Column(name = "visualizza")
    private String visualizza;

    // ========== DATE ==========

    @Column(name = "data")
    private String data;

    @Column(name = "dataVisualizzata")
    private String dataVisualizzata;

    @Column(name = "datasql")
    private LocalDate dataSql;

    @Column(name = "anno")
    private Integer anno;

    @Column(name = "mese")
    private String mese;

    @Column(name = "creato")
    private String creato;

    @Column(name = "creatoda")
    private String creatoDa;

    @Column(name = "modificato")
    private String modificato;

    @Column(name = "modificatoda")
    private String modificatoDa;

    @Column(name = "apertoda")
    private String apertoDa;

    // ========== POSIZIONAMENTO E NAVIGAZIONE ==========

    @Column(name = "position")
    private Integer position;

    @Column(name = "first_page")
    private String firstPage;

    @Column(name = "click")
    private Integer click;

    @Column(name = "rating")
    private Integer rating;

    // ========== MEDIA ==========

    @Column(name = "gallery", columnDefinition = "LONGTEXT")
    private String gallery;

    @Column(name = "id_allegato")
    private String idAllegato;

    // ========== TAG ==========

    @Column(name = "tag", columnDefinition = "TEXT")
    private String tag;

    // ========== EXTRA TAG (1-10) ==========

    @Column(name = "extratag1", columnDefinition = "TEXT")
    private String extraTag1;

    @Column(name = "extratag2", columnDefinition = "TEXT")
    private String extraTag2;

    @Column(name = "extratag3", columnDefinition = "TEXT")
    private String extraTag3;

    @Column(name = "extratag4", columnDefinition = "TEXT")
    private String extraTag4;

    @Column(name = "extratag5", columnDefinition = "TEXT")
    private String extraTag5;

    @Column(name = "extratag6", columnDefinition = "TEXT")
    private String extraTag6;

    @Column(name = "extratag7", columnDefinition = "TEXT")
    private String extraTag7;

    @Column(name = "extratag8", columnDefinition = "TEXT")
    private String extraTag8;

    @Column(name = "extratag9", columnDefinition = "TEXT")
    private String extraTag9;

    @Column(name = "extratag10", columnDefinition = "TEXT")
    private String extraTag10;

    // ========== EXTRA TAG REF (1-10) ==========

    @Column(name = "extratagref1")
    private String extraTagRef1;

    @Column(name = "extratagref2")
    private String extraTagRef2;

    @Column(name = "extratagref3")
    private String extraTagRef3;

    @Column(name = "extratagref4")
    private String extraTagRef4;

    @Column(name = "extratagref5")
    private String extraTagRef5;

    @Column(name = "extratagref6")
    private String extraTagRef6;

    @Column(name = "extratagref7")
    private String extraTagRef7;

    @Column(name = "extratagref8")
    private String extraTagRef8;

    @Column(name = "extratagref9")
    private String extraTagRef9;

    @Column(name = "extratagref10")
    private String extraTagRef10;

    @Column(name = "ordineextratag")
    private String ordineExtraTag;

    @Column(name = "maxextratag")
    private String maxExtraTag;

    @Column(name = "regolaextratag1")
    private String regolaExtraTag1;

    @Column(name = "regolaextratag2")
    private String regolaExtraTag2;

    // ========== LIVELLI (l1-l15) ==========

    @Column(name = "l1", columnDefinition = "TEXT")
    private String l1;

    @Column(name = "l2", columnDefinition = "TEXT")
    private String l2;

    @Column(name = "l3", columnDefinition = "TEXT")
    private String l3;

    @Column(name = "l4", columnDefinition = "TEXT")
    private String l4;

    @Column(name = "l5", columnDefinition = "TEXT")
    private String l5;

    @Column(name = "l6", columnDefinition = "TEXT")
    private String l6;

    @Column(name = "l7", columnDefinition = "TEXT")
    private String l7;

    @Column(name = "l8", columnDefinition = "TEXT")
    private String l8;

    @Column(name = "l9", columnDefinition = "TEXT")
    private String l9;

    @Column(name = "l10", columnDefinition = "TEXT")
    private String l10;

    @Column(name = "l11", columnDefinition = "TEXT")
    private String l11;

    @Column(name = "l12", columnDefinition = "TEXT")
    private String l12;

    @Column(name = "l13", columnDefinition = "TEXT")
    private String l13;

    @Column(name = "l14", columnDefinition = "TEXT")
    private String l14;

    @Column(name = "l15", columnDefinition = "TEXT")
    private String l15;

    // ========== INFO (info1-info5) ==========

    @Column(name = "info1")
    private String info1;

    @Column(name = "info2", columnDefinition = "TEXT")
    private String info2;

    @Column(name = "info3")
    private String info3;

    @Column(name = "info4")
    private String info4;

    @Column(name = "info5")
    private String info5;

    // ========== S (s1-s10) ==========

    @Column(name = "s1")
    private String s1;

    @Column(name = "s2")
    private String s2;

    @Column(name = "s3")
    private String s3;

    @Column(name = "s4")
    private String s4;

    @Column(name = "s5")
    private String s5;

    @Column(name = "s6")
    private String s6;

    @Column(name = "s7")
    private String s7;

    @Column(name = "s8")
    private String s8;

    @Column(name = "s9")
    private String s9;

    @Column(name = "s10")
    private String s10;

    // ========== NEWSLETTER (newsletter1-newsletter5) ==========

    @Column(name = "newsletter1")
    private String newsletter1;

    @Column(name = "newsletter2")
    private String newsletter2;

    @Column(name = "newsletter3")
    private String newsletter3;

    @Column(name = "newsletter4")
    private String newsletter4;

    @Column(name = "newsletter5")
    private String newsletter5;

    // ========== SMS (sms1-sms5) ==========

    @Column(name = "sms1")
    private String sms1;

    @Column(name = "sms2")
    private String sms2;

    @Column(name = "sms3")
    private String sms3;

    @Column(name = "sms4")
    private String sms4;

    @Column(name = "sms5")
    private String sms5;

    // ========== TEXT (text1-text10) ==========

    @Column(name = "text1", columnDefinition = "TEXT")
    private String text1;

    @Column(name = "text2", columnDefinition = "TEXT")
    private String text2;

    @Column(name = "text3", columnDefinition = "TEXT")
    private String text3;

    @Column(name = "text4", columnDefinition = "TEXT")
    private String text4;

    @Column(name = "text5", columnDefinition = "TEXT")
    private String text5;

    @Column(name = "text6", columnDefinition = "TEXT")
    private String text6;

    @Column(name = "text7", columnDefinition = "TEXT")
    private String text7;

    @Column(name = "text8", columnDefinition = "TEXT")
    private String text8;

    @Column(name = "text9", columnDefinition = "TEXT")
    private String text9;

    @Column(name = "text10", columnDefinition = "TEXT")
    private String text10;

    // ========== DATE (data1-data10) ==========

    @Column(name = "data1")
    private LocalDate data1;

    @Column(name = "data2")
    private LocalDate data2;

    @Column(name = "data3")
    private LocalDate data3;

    @Column(name = "data4")
    private LocalDate data4;

    @Column(name = "data5")
    private LocalDate data5;

    @Column(name = "data6")
    private LocalDate data6;

    @Column(name = "data7")
    private LocalDate data7;

    @Column(name = "data8")
    private LocalDate data8;

    @Column(name = "data9")
    private LocalDate data9;

    @Column(name = "data10")
    private LocalDate data10;

    // ========== NUMBER (number1-number10) ==========

    @Column(name = "number1")
    private Double number1;

    @Column(name = "number2")
    private Double number2;

    @Column(name = "number3")
    private Double number3;

    @Column(name = "number4")
    private Double number4;

    @Column(name = "number5")
    private Double number5;

    @Column(name = "number6")
    private Double number6;

    @Column(name = "number7")
    private Double number7;

    @Column(name = "number8")
    private Double number8;

    @Column(name = "number9")
    private Double number9;

    @Column(name = "number10")
    private Double number10;

    // ========== VARCHAR (varchar1-varchar10) ==========

    @Column(name = "varchar1", columnDefinition = "TEXT")
    private String varchar1;

    @Column(name = "varchar2", columnDefinition = "TEXT")
    private String varchar2;

    @Column(name = "varchar3", columnDefinition = "TEXT")
    private String varchar3;

    @Column(name = "varchar4", columnDefinition = "TEXT")
    private String varchar4;

    @Column(name = "varchar5", columnDefinition = "TEXT")
    private String varchar5;

    @Column(name = "varchar6", columnDefinition = "TEXT")
    private String varchar6;

    @Column(name = "varchar7", columnDefinition = "TEXT")
    private String varchar7;

    @Column(name = "varchar8", columnDefinition = "TEXT")
    private String varchar8;

    @Column(name = "varchar9", columnDefinition = "TEXT")
    private String varchar9;

    @Column(name = "varchar10", columnDefinition = "TEXT")
    private String varchar10;

    // ========== ARRAY (array1-array5) ==========

    @Column(name = "array1", columnDefinition = "TEXT")
    private String array1;

    @Column(name = "array2", columnDefinition = "TEXT")
    private String array2;

    @Column(name = "array3", columnDefinition = "TEXT")
    private String array3;

    @Column(name = "array4", columnDefinition = "TEXT")
    private String array4;

    @Column(name = "array5", columnDefinition = "TEXT")
    private String array5;

    // ========== LOG (log1-log3) ==========

    @Column(name = "log1", columnDefinition = "TEXT")
    private String log1;

    @Column(name = "log2", columnDefinition = "TEXT")
    private String log2;

    @Column(name = "log3", columnDefinition = "TEXT")
    private String log3;

    // ========== MENU (menu1-menu5) ==========

    @Column(name = "menu1")
    private String menu1;

    @Column(name = "menu2")
    private String menu2;

    @Column(name = "menu3")
    private String menu3;

    @Column(name = "menu4")
    private String menu4;

    @Column(name = "menu5")
    private String menu5;

    // ========== NUMERATORI (numeratore1-numeratore5) ==========

    @Column(name = "numeratore1")
    private Long numeratore1;

    @Column(name = "numeratore2")
    private Long numeratore2;

    @Column(name = "numeratore3")
    private Long numeratore3;

    @Column(name = "numeratore4")
    private Long numeratore4;

    @Column(name = "numeratore5")
    private Long numeratore5;

    // ========== ALTRI CAMPI ==========

    @Column(name = "sondaggio")
    private String sondaggio;

    @Column(name = "utentiAssociati", columnDefinition = "TEXT")
    private String utentiAssociati;

    @Column(name = "check")
    private Integer check;

    // ========== RELAZIONE CON SECTION TYPE ==========

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type", insertable = false, updatable = false)
    private SectionType sectionType;

    // ========================================
    // CAMPI TRANSIENT (runtime, non nel DB)
    // ========================================

    /**
     * Sotto-sezioni figlie
     */
    @Transient
    private List<Content> subsection;

    /**
     * Contenuti figli (quando questa è una sezione)
     */
    @Transient
    private List<DatiBase> contenuti;

    /**
     * Allegati associati
     */
    @Transient
    private List<Allegato> allegati;

    /**
     * Gallery di immagini
     */
    @Transient
    private List<Images> galleryList;

    /**
     * Sezioni parent nella gerarchia
     */
    @Transient
    private List<Content> parentSection;

    /**
     * Commenti
     */
    @Transient
    private List<Commento> commenti;

    /**
     * ExtraTag elaborati
     */
    @Transient
    private ExtraTag extratag;

    /**
     * Stato archivio (per filtri)
     */
    @Transient
    private String statoArchivio;

    /**
     * Anno/mese temporanei per archivio
     */
    @Transient
    private String annoTemp;

    @Transient
    private String meseTemp;

    /**
     * Locale per i18n
     */
    @Transient
    private String locale = "it_IT";

    // ========================================
    // METODI HELPER
    // ========================================

    /**
     * Verifica se questa è una sezione (e non un contenuto)
     */
    @Transient
    public boolean isSection() {
        return this.idRoot != null && this.idRoot == -1;
    }

    /**
     * Verifica se questa è un contenuto (e non una sezione)
     */
    @Transient
    public boolean isContent() {
        return this.idRoot != null && this.idRoot != -1;
    }

    /**
     * Ottiene il titolo considerando il locale
     */
    @Transient
    public String getTitoloLocalized() {
        if (locale != null && !locale.isEmpty() && !"it_IT".equals(locale)) {
            return titoloEN != null ? titoloEN : titolo;
        }
        return titolo;
    }

    /**
     * Ottiene il riassunto considerando il locale
     */
    @Transient
    public String getRiassuntoLocalized() {
        if (locale != null && !locale.isEmpty() && !"it_IT".equals(locale)) {
            return riassuntoEN != null ? riassuntoEN : riassunto;
        }
        return riassunto;
    }

    /**
     * Ottiene il testo considerando il locale
     */
    @Transient
    public String getTestoLocalized() {
        if (locale != null && !locale.isEmpty() && !"it_IT".equals(locale)) {
            return testoEN != null ? testoEN : testo;
        }
        return testo;
    }
}