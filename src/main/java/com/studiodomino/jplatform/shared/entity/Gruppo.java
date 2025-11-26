package com.studiodomino.jplatform.shared.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gruppo")
@Data
public class Gruppo implements Serializable {

    private static final long serialVersionUID = -5344418710816239968L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;

    // ========== LIVELLI (l1-l10) ==========
    private String l1 = "0";
    private String l2 = "";
    private String l3 = "";
    private String l4 = "";
    private String l5 = "";
    private String l6 = "";
    private String l7 = "";
    private String l8 = "";
    private String l9 = "";
    private String l10 = "";

    private String datamodifica;

    // ========================================
    // CAMPI TRANSIENT (non nel DB)
    // ========================================

    @Transient
    private Integer iscritti = 0;  // Numero utenti nel gruppo

    @Transient
    private List<Gruppo> gruppi = new ArrayList<>();  // Sottogruppi

    // ========================================
    // METODI HELPER
    // ========================================

    /**
     * Verifica se il gruppo ha accesso a un livello specifico
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
}