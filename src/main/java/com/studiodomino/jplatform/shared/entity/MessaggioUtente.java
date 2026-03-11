package com.studiodomino.jplatform.shared.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * MessaggioUtente - Sistema messaggistica interna tra utenti
 * Supporta thread (reply), stati separati mittente/destinatario,
 * e polymorphic sender/receiver (può essere UtenteEsterno o Utente backoffice)
 */
@Entity
@Table(name = "messaggiutente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessaggioUtente implements Serializable {

    private static final long serialVersionUID = 1L;

    // ===== IDENTIFICAZIONE =====

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /**
     * ID messaggio parent (per threading/reply)
     * "0" = messaggio principale, altro = risposta
     */
    @Column(name = "idparent", length = 250, nullable = false)
    @Builder.Default
    private String idparent = "0";

    // ===== DATI MITTENTE (denormalizzati) =====

    /**
     * ID mittente (chiave esterna verso tablename)
     */
    @Column(name = "idmittente", length = 250, nullable = false)
    @Builder.Default
    private String idMittente = "";

    @Column(name = "nomemittente", length = 250, nullable = false)
    @Builder.Default
    private String nomeMittente = "";

    @Column(name = "cognomemittente", length = 250, nullable = false)
    @Builder.Default
    private String cognomeMittente = "";

    @Email(message = "Email mittente non valida")
    @Column(name = "emailmittente", length = 250, nullable = false)
    @Builder.Default
    private String emailMittente = "";

    /**
     * Tabella sorgente mittente
     * Default: "utenteesterno", può essere "utente" per backoffice
     */
    @Column(name = "tablenameMittente", length = 100)
    @Builder.Default
    private String tablenameMittente = "utenteesterno";

    /**
     * Stato lato mittente
     * 0=attivo, 1=archiviato, 2=cancellato
     */
    @Column(name = "statomittente", nullable = false)
    @Builder.Default
    private Integer statoMittente = 0;

    // ===== DATI DESTINATARIO (denormalizzati) =====

    /**
     * ID destinatario (chiave esterna verso tablename)
     */
    @Column(name = "iddestinatario", length = 250, nullable = false)
    @Builder.Default
    private String idDestinatario = "";

    @Column(name = "nomedestinatario", length = 250, nullable = false)
    @Builder.Default
    private String nomeDestinatario = "";

    @Column(name = "cognomedestinatario", length = 250, nullable = false)
    @Builder.Default
    private String cognomeDestinatario = "";

    @Email(message = "Email destinatario non valida")
    @Column(name = "emaildestinatario", length = 250, nullable = false)
    @Builder.Default
    private String emailDestinatario = "";

    /**
     * Tabella sorgente destinatario
     * Default: "utenteesterno", può essere "utente" per backoffice
     */
    @Column(name = "tablenameDestinatario", length = 100)
    @Builder.Default
    private String tablenameDestinatario = "utenteesterno";

    /**
     * Stato lato destinatario
     * 0=non letto, 1=letto, 2=archiviato, 3=cancellato
     */
    @Column(name = "statodestinatario", nullable = false)
    @Builder.Default
    private Integer statoDestinatario = 0;

    // ===== CONTENUTO MESSAGGIO =====

    @NotBlank(message = "Oggetto obbligatorio")
    @Lob
    @Column(name = "oggetto", columnDefinition = "TEXT", nullable = false)
    private String oggetto;

    @NotBlank(message = "Messaggio obbligatorio")
    @Lob
    @Column(name = "messaggio", columnDefinition = "TEXT", nullable = false)
    private String messaggio;

    /**
     * Data invio (formato legacy: "dd-MM-yyyy HH:mm:ss")
     */
    @Column(name = "datainvio", length = 250, nullable = false)
    @Builder.Default
    private String dataInvio = "";

    /**
     * Stato generale messaggio
     * "0"=non letto, "1"=letto, "2"=risposto, etc.
     */
    @Column(name = "stato", length = 10, nullable = false)
    @Builder.Default
    private String stato = "0";

    // ===== CAMPI CONFIGURABILI (l1-l10, s1-s2) =====

    @Column(name = "l1", length = 250, nullable = false)
    @Builder.Default
    private String l1 = "";

    @Column(name = "l2", length = 250, nullable = false)
    @Builder.Default
    private String l2 = "";

    @Column(name = "l3", length = 250, nullable = false)
    @Builder.Default
    private String l3 = "";

    @Column(name = "l4", length = 250, nullable = false)
    @Builder.Default
    private String l4 = "";

    @Column(name = "l5", length = 250, nullable = false)
    @Builder.Default
    private String l5 = "";

    @Column(name = "l6", length = 250, nullable = false)
    @Builder.Default
    private String l6 = "";

    @Column(name = "l7", length = 250, nullable = false)
    @Builder.Default
    private String l7 = "";

    @Column(name = "l8", length = 250, nullable = false)
    @Builder.Default
    private String l8 = "";

    @Column(name = "l9", length = 250, nullable = false)
    @Builder.Default
    private String l9 = "";

    @Column(name = "l10", length = 250, nullable = false)
    @Builder.Default
    private String l10 = "";

    @Column(name = "s1", length = 250, nullable = false)
    @Builder.Default
    private String s1 = "";

    @Column(name = "s2", length = 250, nullable = false)
    @Builder.Default
    private String s2 = "";

    // ===== TRANSIENT FIELDS (non persistiti) =====

    /**
     * Utente associato (mittente o destinatario, caricato runtime)
     */
    @Transient
    private UtenteEsterno utente;

    /**
     * Allegati associati al messaggio/email
     */
    @Transient
    @Builder.Default
    private List<AllegatoEmail> allegati = new ArrayList<>();

    /**
     * Lista risposte a questo messaggio (thread)
     */
    @Transient
    @Builder.Default
    private List<MessaggioUtente> replyMessaggi = new ArrayList<>();

    // ===== BUSINESS METHODS =====

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
     * Verifica se è messaggio principale (non reply)
     */
    public boolean isMessaggioPrincipale() {
        return "0".equals(idparent);
    }

    /**
     * Verifica se è una risposta
     */
    public boolean isRisposta() {
        return !isMessaggioPrincipale();
    }

    /**
     * Verifica se messaggio è stato letto dal destinatario
     */
    public boolean isLettoDaDestinatario() {
        return statoDestinatario != null && statoDestinatario > 0;
    }

    /**
     * Verifica se messaggio è attivo per mittente
     */
    public boolean isAttivoPerMittente() {
        return statoMittente != null && statoMittente < 2;
    }

    /**
     * Verifica se messaggio è attivo per destinatario
     */
    public boolean isAttivoPerDestinatario() {
        return statoDestinatario != null && statoDestinatario < 3;
    }

    /**
     * Ottieni nome completo mittente
     */
    public String getNomeCompletoMittente() {
        return (nomeMittente != null ? nomeMittente : "") + " "
                + (cognomeMittente != null ? cognomeMittente : "");
    }

    /**
     * Ottieni nome completo destinatario
     */
    public String getNomeCompletoDestinatario() {
        return (nomeDestinatario != null ? nomeDestinatario : "") + " "
                + (cognomeDestinatario != null ? cognomeDestinatario : "");
    }

    /**
     * Verifica se mittente è utente esterno
     */
    public boolean isMittenteEsterno() {
        return "utenteesterno".equalsIgnoreCase(tablenameMittente);
    }

    /**
     * Verifica se destinatario è utente esterno
     */
    public boolean isDestinatarioEsterno() {
        return "utenteesterno".equalsIgnoreCase(tablenameDestinatario);
    }

    /**
     * Marca messaggio come letto (dal destinatario)
     */
    public void marcaComeLetto() {
        if (statoDestinatario != null && statoDestinatario == 0) {
            this.statoDestinatario = 1;
            this.stato = "1";
        }
    }

    /**
     * Archivia messaggio per destinatario
     */
    public void archiviaPerDestinatario() {
        this.statoDestinatario = 2;
    }

    /**
     * Archivia messaggio per mittente
     */
    public void archiviaPerMittente() {
        this.statoMittente = 1;
    }

    /**
     * Cancella messaggio per destinatario (soft delete)
     */
    public void cancellaPerDestinatario() {
        this.statoDestinatario = 3;
    }

    /**
     * Cancella messaggio per mittente (soft delete)
     */
    public void cancellaPerMittente() {
        this.statoMittente = 2;
    }

    /**
     * Ottieni livello per numero (1-10)
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
            default -> "";
        };
    }

    /**
     * Ottieni stringa per numero (1-2)
     */
    public String getS(int numero) {
        return switch (numero) {
            case 1 -> s1;
            case 2 -> s2;
            default -> "";
        };
    }

    /**
     * Conta numero risposte nel thread
     */
    public int getNumeroRisposte() {
        return replyMessaggi != null ? replyMessaggi.size() : 0;
    }

    /**
     * Verifica se ha risposte
     */
    public boolean hasRisposte() {
        return getNumeroRisposte() > 0;
    }

    // ===== EQUALS & HASHCODE =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessaggioUtente that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "MessaggioUtente{" +
                "id=" + id +
                ", oggetto='" + oggetto + '\'' +
                ", mittente='" + getNomeCompletoMittente() + '\'' +
                ", destinatario='" + getNomeCompletoDestinatario() + '\'' +
                ", dataInvio='" + dataInvio + '\'' +
                ", letto=" + isLettoDaDestinatario() +
                '}';
    }
}