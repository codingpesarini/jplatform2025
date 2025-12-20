package com.studiodomino.jplatform.shared.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

/**
 * Images Entity - Gestione immagini nel file system virtuale
 * Tabella: images (MyISAM)
 */
@Entity
@Table(name = "images")
@Data
public class Images implements Serializable {

    private static final long serialVersionUID = 1L;

    // ========== IDENTIFICAZIONE ==========

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * ID temporaneo (per upload in progress)
     */
    @Column(name = "tmpid", length = 200, nullable = false)
    private String tmpid = "0";

    /**
     * ID folder contenitore
     */
    @Column(name = "idfolder", length = 250)
    private String idfolder = "0";

    // ========== FILE INFO ==========

    /**
     * Nome file originale
     */
    @Column(name = "name", length = 100)
    private String name = "";

    /**
     * MIME type (es: image/jpeg, image/png)
     */
    @Column(name = "type", length = 100)
    private String type = "";

    /**
     * Dimensione file in bytes
     */
    @Column(name = "size", length = 100)
    private String size = "0";

    // ========== PATHS ==========

    /**
     * Path relativo immagine originale
     */
    @Column(name = "pathname", length = 100)
    private String pathname = "";

    /**
     * Path relativo thumbnail
     */
    @Column(name = "paththumb", length = 100)
    private String paththumb = "";

    /**
     * Path completo web-accessible
     */
    @Column(name = "fullpath", length = 200)
    private String fullpath = "nofoto.jpg";

    // ========== METADATA ==========

    /**
     * Didascalia/caption immagine
     */
    @Column(name = "didascalia", columnDefinition = "TEXT")
    private String didascalia;

    /**
     * Flag privato ("0"=pubblico, "1"=privato)
     */
    @Column(name = "privato", length = 100)
    private String privato = "";

    // ========== CAMPI LIBERI (1-5) ==========

    @Column(name = "l1", length = 100)
    private String l1 = "";

    @Column(name = "l2", length = 100)
    private String l2 = "";

    @Column(name = "l3", length = 100)
    private String l3 = "";

    @Column(name = "l4", length = 100)
    private String l4 = "";

    @Column(name = "l5", length = 100)
    private String l5 = "";

    // ========================================
    // CAMPI TRANSIENT (non salvati nel DB)
    // ========================================

    /**
     * Path immagine (filesystem)
     */
    @Transient
    private String pathimage = "";

    /**
     * Path immagine web
     */
    @Transient
    private String pathimageweb = "";

    /**
     * Versione (per cache busting)
     */
    @Transient
    private String version = "";

    /**
     * Operazione in corso
     */
    @Transient
    private String operazione = "1";

    // ========================================
    // METODI HELPER
    // ========================================

    /**
     * Ottieni dimensione in KB
     */
    @Transient
    public String getKSize() {
        try {
            double bytes = Double.parseDouble(this.size);
            return String.format("%.2f", bytes / 1024);
        } catch (NumberFormatException e) {
            return "-1";
        }
    }

    /**
     * Ottieni dimensione in MB
     */
    @Transient
    public String getMSize() {
        try {
            double bytes = Double.parseDouble(this.size);
            return String.format("%.2f", bytes / (1024 * 1024));
        } catch (NumberFormatException e) {
            return "-1";
        }
    }

    /**
     * Ottieni size formattata human-readable
     */
    @Transient
    public String getFormattedSize() {
        try {
            double bytes = Double.parseDouble(this.size);
            if (bytes < 1024) {
                return bytes + " B";
            } else if (bytes < 1024 * 1024) {
                return String.format("%.2f KB", bytes / 1024);
            } else {
                return String.format("%.2f MB", bytes / (1024 * 1024));
            }
        } catch (NumberFormatException e) {
            return "N/A";
        }
    }

    /**
     * Ottieni URL con cache busting
     */
    @Transient
    public String getUrl() {
        return fullpath + "?" + (int) (Math.random() * 10);
    }

    /**
     * Ottieni URL senza cache busting
     */
    @Transient
    public String getCleanUrl() {
        return fullpath;
    }

    /**
     * Verifica se è privata
     */
    @Transient
    public boolean isPrivato() {
        return "1".equals(privato);
    }

    /**
     * Verifica se è pubblica
     */
    @Transient
    public boolean isPubblico() {
        return !"1".equals(privato);
    }

    /**
     * Verifica se ha thumbnail
     */
    @Transient
    public boolean hasThumbnail() {
        return paththumb != null && !paththumb.isEmpty() && !"".equals(paththumb);
    }

    /**
     * Verifica se ha didascalia
     */
    @Transient
    public boolean hasDidascalia() {
        return didascalia != null && !didascalia.isEmpty();
    }

    /**
     * Ottieni estensione file
     */
    @Transient
    public String getExtension() {
        if (name == null || !name.contains(".")) {
            return "";
        }
        return name.substring(name.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * Verifica se è immagine JPEG
     */
    @Transient
    public boolean isJpeg() {
        String ext = getExtension();
        return "jpg".equals(ext) || "jpeg".equals(ext);
    }

    /**
     * Verifica se è immagine PNG
     */
    @Transient
    public boolean isPng() {
        return "png".equals(getExtension());
    }

    /**
     * Verifica se è immagine GIF
     */
    @Transient
    public boolean isGif() {
        return "gif".equals(getExtension());
    }

    /**
     * Verifica se è immagine WebP
     */
    @Transient
    public boolean isWebp() {
        return "webp".equals(getExtension());
    }

    /**
     * Ottieni icona Font Awesome per tipo
     */
    @Transient
    public String getIconClass() {
        return switch (getExtension()) {
            case "jpg", "jpeg" -> "fa fa-file-image text-primary";
            case "png" -> "fa fa-file-image text-success";
            case "gif" -> "fa fa-file-image text-warning";
            case "webp" -> "fa fa-file-image text-info";
            default -> "fa fa-file text-secondary";
        };
    }

    /**
     * Ottieni campo l per numero (1-5)
     */
    @Transient
    public String getL(int numero) {
        return switch (numero) {
            case 1 -> l1;
            case 2 -> l2;
            case 3 -> l3;
            case 4 -> l4;
            case 5 -> l5;
            default -> "";
        };
    }

    /**
     * Imposta campo l per numero (1-5)
     */
    @Transient
    public void setL(int numero, String valore) {
        switch (numero) {
            case 1 -> l1 = valore;
            case 2 -> l2 = valore;
            case 3 -> l3 = valore;
            case 4 -> l4 = valore;
            case 5 -> l5 = valore;
        }
    }

    /**
     * Reset tutti i campi (legacy compatibility)
     */
    public void reset() {
        this.tmpid = "";
        this.id = null;
        this.name = "";
        this.type = "";
        this.size = "";
        this.pathname = "";
        this.paththumb = "";
        this.fullpath = "nofoto.jpg";
        this.privato = "";
        this.l1 = this.l2 = this.l3 = this.l4 = this.l5 = "";
        this.didascalia = "";
        this.idfolder = "-1";
        this.pathimageweb = "";
        this.version = "";
        this.operazione = "";
        this.pathimage = "";
    }
}