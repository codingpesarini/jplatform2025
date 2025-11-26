package com.studiodomino.jplatform.shared.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

@Entity
@Table(name = "ruolo")
@Data
public class Ruolo implements Serializable {

    private static final long serialVersionUID = -5771382652009502985L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descrizione;

    // ========== LIVELLI (l1-l10) ==========
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

    // ========== SEZIONI (s1-s10) ==========
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

    private String datamodifica;

    // ========================================
    // METODI HELPER
    // ========================================

    /**
     * Verifica se il ruolo ha accesso a un livello specifico
     */
    @Transient
    public boolean hasLivello(int numero) {
        String livello = switch (numero) {
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
            default -> "";
        };
        return livello != null && !livello.isEmpty() && !"0".equals(livello);
    }

    /**
     * Verifica se il ruolo ha accesso a una sezione specifica
     */
    @Transient
    public boolean hasSezione(int numero) {
        String sezione = switch (numero) {
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
            default -> "";
        };
        return sezione != null && !sezione.isEmpty() && !"0".equals(sezione);
    }
}