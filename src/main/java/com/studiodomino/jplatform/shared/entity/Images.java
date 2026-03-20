package com.studiodomino.jplatform.shared.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

/**
 * Entity JPA che rappresenta un'immagine nel sistema CMS.
 * Gestisce upload, thumbnail, gallery e metadati delle immagini.
 */
@Entity
@Table(name = "images")
@Data
public class Images implements Serializable {

    private static final long serialVersionUID = 1L;

    // ========================================
    // IDENTIFICAZIONE
    // ========================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /**
     * ID temporaneo per upload in corso
     */
    @Column(name = "tmpid")
    private String tmpid = "";

    /**
     * ID della cartella contenitore
     */
    @Column(name = "idfolder")
    private String idfolder = "0";

    // ========================================
    // INFORMAZIONI FILE
    // ========================================

    /**
     * Nome del file originale
     */
    @Column(name = "name")
    private String name;

    /**
     * Tipo MIME (es: "image/jpeg", "image/png")
     */
    @Column(name = "type")
    private String type;

    /**
     * Dimensione in bytes
     */
    @Column(name = "size")
    private String size;

    // ========================================
    // PATH E PERCORSI
    // ========================================

    /**
     * Path relativo dell'immagine originale
     */
    @Column(name = "pathname")
    private String pathname;

    /**
     * Path del thumbnail
     */
    @Column(name = "paththumb")
    private String paththumb;

    /**
     * Path completo dell'immagine
     */
    @Column(name = "fullpath")
    private String fullpath;

    // ========================================
    // METADATI
    // ========================================

    /**
     * Didascalia/caption dell'immagine
     */
    @Column(name = "didascalia", columnDefinition = "TEXT")
    private String didascalia;

    /**
     * Flag privato (0=pubblico, 1=privato)
     */
    @Column(name = "privato")
    private String privato = "0";

    // ========================================
    // CAMPI CUSTOM (L1-L5)
    // ========================================

    /**
     * Campo custom L1 (es: alt text, credits, etc.)
     */
    @Column(name = "l1")
    private String l1;

    /**
     * Campo custom L2
     */
    @Column(name = "l2")
    private String l2;

    /**
     * Campo custom L3
     */
    @Column(name = "l3")
    private String l3;

    /**
     * Campo custom L4
     */
    @Column(name = "l4")
    private String l4;

    /**
     * Campo custom L5
     */
    @Column(name = "l5")
    private String l5;

    // ========================================
    // CAMPI TRANSIENT (non salvati in DB)
    // ========================================

    /**
     * Path web per rendering (non in DB)
     */
    @Transient
    private String pathimageweb;

    /**
     * Path immagine (non in DB)
     */
    @Transient
    private String pathimage;

    /**
     * Versione (per cache busting)
     */
    @Transient
    private String version;

    /**
     * Operazione corrente (upload, delete, etc.)
     */
    @Transient
    private String operazione = "1";

    // ========================================
    // COSTRUTTORI
    // ========================================

    public Images() {
        super();
    }

    public Images(String name, String type, String size, String pathname,
                  String fullpath) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.pathname = pathname;
        this.fullpath = fullpath;
    }

    // ========================================
    // METODI HELPER - DIMENSIONI
    // ========================================

    /**
     * Ottieni dimensione in KB
     */
    public String getKSize() {
        try {
            double bytes = Double.parseDouble(this.size);
            return String.format("%.2f", bytes / 1000);
        } catch (NumberFormatException e) {
            return "-1";
        }
    }

    /**
     * Ottieni dimensione in MB
     */
    public String getMSize() {
        try {
            double bytes = Double.parseDouble(this.size);
            return String.format("%.2f", bytes / 1000000);
        } catch (NumberFormatException e) {
            return "-1";
        }
    }

    /**
     * Ottieni dimensione formattata automaticamente
     */
    public String getFormattedSize() {
        try {
            double bytes = Double.parseDouble(this.size);

            if (bytes >= 1000000) {
                return String.format("%.2f MB", bytes / 1000000);
            } else if (bytes >= 1000) {
                return String.format("%.2f KB", bytes / 1000);
            } else {
                return bytes + " B";
            }
        } catch (NumberFormatException e) {
            return "N/A";
        }
    }

    // ========================================
    // METODI HELPER - URL E PATH
    // ========================================

    /**
     * Ottieni URL con cache busting
     */
    public String getUrl() {
        if (fullpath == null || fullpath.isEmpty()) {
            return "nofoto.jpg";
        }
        return fullpath + "?v=" + (int) (Math.random() * 10000);
    }

    /**
     * Ottieni URL thumbnail con cache busting
     */
    public String getThumbUrl() {
        if (paththumb == null || paththumb.isEmpty()) {
            return fullpath != null ? fullpath : "nofoto.jpg";
        }
        return paththumb + "?v=" + (int) (Math.random() * 10000);
    }

    /**
     * Ottieni path completo per rendering
     */
    public String getFullPathWeb() {
        if (pathimageweb != null && !pathimageweb.isEmpty()) {
            return pathimageweb + "/" + fullpath;
        }
        return fullpath;
    }

    // ========================================
    // METODI HELPER - VALIDAZIONE
    // ========================================

    /**
     * Verifica se l'immagine è valida
     */
    public boolean isValid() {
        return id != null && id > 0 &&
                fullpath != null && !fullpath.isEmpty();
    }

    /**
     * Verifica se ha thumbnail
     */
    public boolean hasThumb() {
        return paththumb != null && !paththumb.isEmpty();
    }

    /**
     * Verifica se è un'immagine (non altro tipo file)
     */
    public boolean isImage() {
        if (type == null) return false;
        return type.startsWith("image/");
    }

    /**
     * Ottieni estensione file
     */
    public String getExtension() {
        if (name == null || !name.contains(".")) {
            return "";
        }
        return name.substring(name.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * Verifica se è privata
     */
    public boolean isPrivata() {
        return "1".equals(privato);
    }

    // ========================================
    // METODI HELPER - CAMPI L
    // ========================================

    /**
     * Ottieni campo L per numero (1-5)
     */
    public String getL(int numero) {
        return switch (numero) {
            case 1 -> l1;
            case 2 -> l2;
            case 3 -> l3;
            case 4 -> l4;
            case 5 -> l5;
            default -> null;
        };
    }

    /**
     * Imposta campo L per numero (1-5)
     */
    public void setL(int numero, String valore) {
        switch (numero) {
            case 1 -> l1 = valore;
            case 2 -> l2 = valore;
            case 3 -> l3 = valore;
            case 4 -> l4 = valore;
            case 5 -> l5 = valore;
        }
    }

    // ========================================
    // METODI HELPER - RENDERING HTML
    // ========================================

    /**
     * Genera tag <img> completo
     */
    public String toImgTag() {
        StringBuilder tag = new StringBuilder("<img src=\"");
        tag.append(getUrl()).append("\"");

        if (name != null) {
            tag.append(" alt=\"").append(name).append("\"");
        }

        if (didascalia != null && !didascalia.isEmpty()) {
            tag.append(" title=\"").append(didascalia).append("\"");
        }

        tag.append(" />");
        return tag.toString();
    }

    /**
     * Genera tag <img> thumbnail
     */
    public String toThumbTag() {
        StringBuilder tag = new StringBuilder("<img src=\"");
        tag.append(getThumbUrl()).append("\"");

        if (name != null) {
            tag.append(" alt=\"").append(name).append("\"");
        }

        tag.append(" class=\"thumbnail\" />");
        return tag.toString();
    }

    // ========================================
    // METODI HELPER - METADATA
    // ========================================

    /**
     * Ottieni nome file senza estensione
     */
    public String getNameWithoutExtension() {
        if (name == null || !name.contains(".")) {
            return name;
        }
        return name.substring(0, name.lastIndexOf("."));
    }

    /**
     * Verifica se ha didascalia
     */
    public boolean hasDidascalia() {
        return didascalia != null && !didascalia.isEmpty();
    }
}