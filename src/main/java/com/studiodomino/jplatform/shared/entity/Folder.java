package com.studiodomino.jplatform.shared.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Folder Entity - Gestione cartelle file system virtuale
 * Tabella: folder (MyISAM)
 */
@Entity
@Table(name = "folder")
@Data
public class Folder implements Serializable {

    private static final long serialVersionUID = 1L;

    // ========== IDENTIFICAZIONE ==========

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * ID del folder parent (gerarchia)
     */
    @Column(name = "idfolder", length = 100, nullable = false)
    private String idfolder = "";

    /**
     * Nome folder
     */
    @Column(name = "nome", length = 100)
    private String nome = "";

    // ========== DATE ==========

    @Column(name = "datacreazione")
    private LocalDate datacreazione = LocalDate.of(2000, 1, 1);

    // ========== STATI (1-5) ==========

    @Column(name = "stato1")
    private Integer stato1 = 0;

    @Column(name = "stato2")
    private Integer stato2 = 0;

    @Column(name = "stato3")
    private Integer stato3 = 0;

    @Column(name = "stato4")
    private Integer stato4 = 0;

    @Column(name = "stato5")
    private Integer stato5 = 0;

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

    // ========== PERMESSI ==========

    /**
     * ID gruppo associato (permessi)
     */
    @Column(name = "idgruppo", length = 250, nullable = false)
    private String idgruppo = "";

    // ========================================
    // CAMPI TRANSIENT (non salvati nel DB)
    // ========================================

    /**
     * Subfolder contenuti (caricati runtime)
     */
    @Transient
    private List<Folder> subfolder = new ArrayList<>();

    /**
     * Immagini contenute (caricati runtime)
     */
    @Transient
    private List<Object> direttorio = new ArrayList<>(); // TODO: cambiare Object con Images entity

    /**
     * Allegati contenuti (caricati runtime)
     */
    @Transient
    private List<Object> direttorioallegati = new ArrayList<>(); // TODO: cambiare Object con Allegato entity

    /**
     * Breadcrumb posizione
     */
    @Transient
    private List<Object> position = new ArrayList<>(); // TODO: cambiare Object con FileSystemPosition

    /**
     * Flag: ha subfolder ("0"/"1")
     */
    @Transient
    private String subfolders = "0";

    /**
     * Flag: ha files ("0"/"1")
     */
    @Transient
    private String subdirettorio = "0";

    /**
     * Contatori runtime
     */
    @Transient
    private String numeroFolder = "0";

    @Transient
    private String sizeFolder = "0";

    @Transient
    private String numeroAllegati = "0";

    @Transient
    private String numeroImmagini = "0";

    // ========================================
    // METODI HELPER
    // ========================================

    /**
     * Verifica se è root
     */
    @Transient
    public boolean isRoot() {
        return "0".equals(idfolder) || "-1".equals(idfolder) || idfolder == null || idfolder.isEmpty();
    }

    /**
     * Ottieni size folder in KB
     */
    @Transient
    public String getSizeFolderKB() {
        try {
            double bytes = Double.parseDouble(this.sizeFolder);
            return String.format("%.2f", bytes / 1024);
        } catch (NumberFormatException e) {
            return "0.00";
        }
    }

    /**
     * Ottieni size folder in MB
     */
    @Transient
    public String getSizeFolderMB() {
        try {
            double bytes = Double.parseDouble(this.sizeFolder);
            return String.format("%.2f", bytes / (1024 * 1024));
        } catch (NumberFormatException e) {
            return "0.00";
        }
    }

    /**
     * Verifica se ha subfolder
     */
    @Transient
    public boolean hasSubfolders() {
        return "1".equals(subfolders) || (subfolder != null && !subfolder.isEmpty());
    }

    /**
     * Verifica se ha files
     */
    @Transient
    public boolean hasFiles() {
        return "1".equals(subdirettorio) ||
                (direttorio != null && !direttorio.isEmpty()) ||
                (direttorioallegati != null && !direttorioallegati.isEmpty());
    }

    /**
     * Verifica se è vuoto
     */
    @Transient
    public boolean isEmpty() {
        return !hasSubfolders() && !hasFiles();
    }

    /**
     * Conta totale items
     */
    @Transient
    public int getTotalItems() {
        int count = 0;
        try {
            count += Integer.parseInt(numeroFolder);
            count += Integer.parseInt(numeroAllegati);
            count += Integer.parseInt(numeroImmagini);
        } catch (NumberFormatException e) {
            // Ignora
        }
        return count;
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
     * Ottieni stato per numero (1-5)
     */
    @Transient
    public Integer getStato(int numero) {
        return switch (numero) {
            case 1 -> stato1;
            case 2 -> stato2;
            case 3 -> stato3;
            case 4 -> stato4;
            case 5 -> stato5;
            default -> 0;
        };
    }

    /**
     * Imposta stato per numero (1-5)
     */
    @Transient
    public void setStato(int numero, Integer valore) {
        switch (numero) {
            case 1 -> stato1 = valore;
            case 2 -> stato2 = valore;
            case 3 -> stato3 = valore;
            case 4 -> stato4 = valore;
            case 5 -> stato5 = valore;
        }
    }

    /**
     * Reset tutti i campi (legacy compatibility)
     */
    public void reset() {
        this.id = null;
        this.nome = "";
        this.idfolder = "";
        this.datacreazione = LocalDate.of(2000, 1, 1);
        this.stato1 = this.stato2 = this.stato3 = this.stato4 = this.stato5 = 0;
        this.l1 = this.l2 = this.l3 = this.l4 = this.l5 = "";
        this.subfolder = null;
        this.direttorio = null;
        this.direttorioallegati = null;
        this.position = null;
        this.subfolders = "0";
        this.subdirettorio = "0";
        this.idgruppo = "0";
    }
}