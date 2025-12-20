package com.studiodomino.jplatform.cms.entity;

import com.studiodomino.jplatform.shared.entity.UtenteEsterno;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Commento - Sistema commenti per contenuti CMS
 * Supporta threading (reply), moderazione, tipologie multiple
 */
@Entity
@Table(name = "commenti")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commento implements Serializable {

    private static final long serialVersionUID = 1L;

    // ===== IDENTIFICAZIONE =====

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /**
     * ID oggetto commentato (Section/DatiBase)
     */
    @NotBlank(message = "ID oggetto obbligatorio")
    @Size(max = 100)
    @Column(name = "idoggetto", nullable = false, length = 100)
    private String idoggetto;

    /**
     * ID parent per threading
     * "0" = commento principale, altro = risposta
     */
    @Size(max = 250)
    @Column(name = "idparent", length = 250)
    @Builder.Default
    private String idparent = "0";

    /**
     * Tipologia commento
     * "c" = commento standard, altri valori custom
     */
    @Size(max = 10)
    @Column(name = "tipologia", nullable = false, length = 10)
    @Builder.Default
    private String tipologia = "c";

    // ===== DATI AUTORE =====

    /**
     * ID utente (se registrato)
     */
    @Size(max = 100)
    @Column(name = "iduser", nullable = false, length = 100)
    @Builder.Default
    private String iduser = "";

    @NotBlank(message = "Nome obbligatorio")
    @Size(max = 100)
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "Cognome obbligatorio")
    @Size(max = 100)
    @Column(name = "cognome", nullable = false, length = 100)
    private String cognome;

    @Email(message = "Email non valida")
    @NotBlank(message = "Email obbligatoria")
    @Size(max = 100)
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    // ===== CONTENUTO =====

    @NotBlank(message = "Messaggio obbligatorio")
    @Lob
    @Column(name = "messaggio", columnDefinition = "TEXT", nullable = false)
    private String messaggio;

    /**
     * Data commento (formato legacy: "dd-MM-yyyy HH:mm:ss")
     */
    @NotBlank(message = "Data obbligatoria")
    @Size(max = 100)
    @Column(name = "data", nullable = false, length = 100)
    private String data;

    // ===== MODERAZIONE =====

    /**
     * Stato moderazione
     * "0" = in attesa, "1" = approvato, "2" = rifiutato
     */
    @Size(max = 100)
    @Column(name = "stato", nullable = false, length = 100)
    @Builder.Default
    private String stato = "0";

    // ===== CAMPI CONFIGURABILI (l1-l5) =====

    @Size(max = 100)
    @Column(name = "l1", nullable = false, length = 100)
    @Builder.Default
    private String l1 = "0";

    @Size(max = 100)
    @Column(name = "l2", nullable = false, length = 100)
    @Builder.Default
    private String l2 = "0";

    @Size(max = 100)
    @Column(name = "l3", nullable = false, length = 100)
    @Builder.Default
    private String l3 = "0";

    @Size(max = 100)
    @Column(name = "l4", nullable = false, length = 100)
    @Builder.Default
    private String l4 = "0";

    @Size(max = 100)
    @Column(name = "l5", nullable = false, length = 100)
    @Builder.Default
    private String l5 = "0";

    // ===== TRANSIENT FIELDS (non persistiti) =====

    /**
     * Risposte a questo commento (threading)
     */
    @Transient
    @Builder.Default
    private List<Commento> subCommenti = new ArrayList<>();

    /**
     * Utente associato (se registrato)
     */
    @Transient
    private UtenteEsterno utente;

    /**
     * Contenuto commentato (Section o DatiBase)
     */
    @Transient
    private Object contenuto; // può essere Section o DatiBase

    // ===== BUSINESS METHODS =====

    /**
     * Verifica se è commento principale (non reply)
     */
    public boolean isCommentoPrincipale() {
        return "0".equals(idparent) || idparent == null || idparent.isEmpty();
    }

    /**
     * Verifica se è una risposta
     */
    public boolean isRisposta() {
        return !isCommentoPrincipale();
    }

    /**
     * Verifica se commento è approvato
     */
    public boolean isApprovato() {
        return "1".equals(stato);
    }

    /**
     * Verifica se commento è in attesa moderazione
     */
    public boolean isInAttesa() {
        return "0".equals(stato);
    }

    /**
     * Verifica se commento è rifiutato
     */
    public boolean isRifiutato() {
        return "2".equals(stato);
    }

    /**
     * Verifica se è commento standard
     */
    public boolean isCommentoStandard() {
        return "c".equalsIgnoreCase(tipologia);
    }

    /**
     * Approva commento
     */
    public void approva() {
        this.stato = "1";
    }

    /**
     * Rifiuta commento
     */
    public void rifiuta() {
        this.stato = "2";
    }

    /**
     * Metti in attesa
     */
    public void mettiInAttesa() {
        this.stato = "0";
    }

    /**
     * Verifica se utente è registrato
     */
    public boolean isUtenteRegistrato() {
        return iduser != null && !iduser.isEmpty() && !"0".equals(iduser) && !"-1".equals(iduser);
    }

    /**
     * Ottieni nome completo autore
     */
    public String getNomeCompleto() {
        return (nome != null ? nome : "") + " " + (cognome != null ? cognome : "");
    }

    /**
     * Verifica se ha risposte
     */
    public boolean hasRisposte() {
        return subCommenti != null && !subCommenti.isEmpty();
    }

    /**
     * Conta numero risposte
     */
    public int getNumeroRisposte() {
        return subCommenti != null ? subCommenti.size() : 0;
    }

    /**
     * Aggiungi risposta
     */
    public void addRisposta(Commento risposta) {
        if (subCommenti == null) {
            subCommenti = new ArrayList<>();
        }
        subCommenti.add(risposta);
    }

    /**
     * Ottieni estratto messaggio (primi 100 caratteri)
     */
    public String getEstratto() {
        if (messaggio == null) {
            return "";
        }
        if (messaggio.length() > 100) {
            return messaggio.substring(0, 100) + "...";
        }
        return messaggio;
    }

    /**
     * Ottieni livello per numero (1-5)
     */
    public String getL(int numero) {
        return switch (numero) {
            case 1 -> l1;
            case 2 -> l2;
            case 3 -> l3;
            case 4 -> l4;
            case 5 -> l5;
            default -> "0";
        };
    }

    /**
     * Imposta livello per numero (1-5)
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

    /**
     * Ottieni label stato per UI
     */
    public String getStatoLabel() {
        return switch (stato) {
            case "0" -> "In attesa";
            case "1" -> "Approvato";
            case "2" -> "Rifiutato";
            default -> "Sconosciuto";
        };
    }

    /**
     * Ottieni CSS class per stato
     */
    public String getStatoCssClass() {
        return switch (stato) {
            case "0" -> "status-pending";
            case "1" -> "status-approved";
            case "2" -> "status-rejected";
            default -> "status-unknown";
        };
    }

    // ===== EQUALS & HASHCODE =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Commento that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Commento{" +
                "id=" + id +
                ", idoggetto='" + idoggetto + '\'' +
                ", autore='" + getNomeCompleto() + '\'' +
                ", stato='" + getStatoLabel() + '\'' +
                ", data='" + data + '\'' +
                ", risposte=" + getNumeroRisposte() +
                '}';
    }
}