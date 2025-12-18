package com.studiodomino.jplatform.cms.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

/**
 * Entity che rappresenta un tipo di sezione/contenuto.
 * Mappato sulla tabella 'content_type' del database.
 */
@Entity
@Table(name = "content_type")
@Data
public class SectionType implements Serializable {

    private static final long serialVersionUID = 5128662307368591887L;

    // ========== IDENTIFICAZIONE ==========

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "type")
    private String type;

    @Column(name = "description")
    private String description;

    // ========== LIVELLI (l1-l5) ==========

    @Column(name = "l1")
    private String l1;

    @Column(name = "l2")
    private String l2;

    @Column(name = "l3")
    private String l3;

    @Column(name = "l4")
    private String l4;

    @Column(name = "l5")
    private String l5;

    // ========== PAGINE (page1-page5) ==========

    @Column(name = "page1")
    private String page1;

    @Column(name = "page2")
    private String page2;

    @Column(name = "page3")
    private String page3;

    @Column(name = "page4")
    private String page4;

    @Column(name = "page5")
    private String page5;

    // ========== NUMERATORI (numeratore1-numeratore5) ==========

    @Column(name = "numeratore1")
    private String numeratore1;

    @Column(name = "numeratore2")
    private String numeratore2;

    @Column(name = "numeratore3")
    private String numeratore3;

    @Column(name = "numeratore4")
    private String numeratore4;

    @Column(name = "numeratore5")
    private String numeratore5;

    // ========== CAMPI SPECIALI (special1-special10) ==========

    @Column(name = "special1")
    private String special1;

    @Column(name = "special2")
    private String special2;

    @Column(name = "special3")
    private String special3;

    @Column(name = "special4")
    private String special4;

    @Column(name = "special5")
    private String special5;

    @Column(name = "special6", columnDefinition = "TEXT")
    private String special6;

    @Column(name = "special7", columnDefinition = "TEXT")
    private String special7;

    @Column(name = "special8", columnDefinition = "TEXT")
    private String special8;

    @Column(name = "special9", columnDefinition = "TEXT")
    private String special9;

    @Column(name = "special10", columnDefinition = "TEXT")
    private String special10;

    // ========== GETTER CUSTOM CON VALORI DI DEFAULT ==========

    /**
     * Ritorna page1 o il valore di default "dettaglioSezione"
     */
    @Transient
    public String getPage1OrDefault() {
        if (this.page1 != null && !this.page1.isEmpty() && this.page1.length() > 3) {
            return page1;
        }
        return "dettaglioSezione";
    }

    /**
     * Ritorna page2 o il valore di default "dettaglioContenuto"
     */
    @Transient
    public String getPage2OrDefault() {
        if (this.page2 != null && !this.page2.isEmpty() && this.page2.length() > 3) {
            return page2;
        }
        return "dettaglioContenuto";
    }

    /**
     * Ritorna page3 o il valore di default "default"
     */
    @Transient
    public String getPage3OrDefault() {
        if (this.page3 != null && !this.page3.isEmpty() && this.page3.length() > 3) {
            return page3;
        }
        return "default";
    }

    /**
     * Ritorna page4 o il valore di default "default"
     */
    @Transient
    public String getPage4OrDefault() {
        if (this.page4 != null && !this.page4.isEmpty() && this.page4.length() > 3) {
            return page4;
        }
        return "default";
    }

    /**
     * Ritorna page5 o il valore di default "default"
     */
    @Transient
    public String getPage5OrDefault() {
        if (this.page5 != null && !this.page5.isEmpty() && this.page5.length() > 3) {
            return page5;
        }
        return "default";
    }
}