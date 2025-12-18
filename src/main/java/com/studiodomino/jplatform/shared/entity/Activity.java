package com.studiodomino.jplatform.shared.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;

/**
 * Activity - Attività/Task gestite nel sistema
 * Associata a utente e messaggi, traccia apertura/completamento
 * con urgenza, stato, note e log operativo
 */
@Entity
@Table(name = "activity", indexes = {
        @Index(name = "idx_idutente", columnList = "idutente")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;

    // ===== IDENTIFICAZIONE =====

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /**
     * ID utente assegnato all'attività
     */
    @NotNull(message = "ID utente obbligatorio")
    @Column(name = "idutente", nullable = false)
    private Integer idutente;

    /**
     * ID messaggio utente correlato (se derivato da messaggio)
     */
    @NotNull(message = "ID messaggio obbligatorio")
    @Column(name = "idmsgutente", nullable = false)
    private Integer idmsgutente;

    // ===== TIPO E DATE =====

    /**
     * Tipo attività (es: "ticket", "task", "richiesta", etc.)
     */
    @NotBlank(message = "Tipo attività obbligatorio")
    @Size(max = 100)
    @Column(name = "tipoattivita", nullable = false, length = 100)
    private String tipoattivita;

    /**
     * Data apertura attività (formato legacy: "dd-MM-yyyy HH:mm:ss")
     */
    @NotBlank(message = "Data apertura obbligatoria")
    @Size(max = 100)
    @Column(name = "dataapertura", nullable = false, length = 100)
    private String dataapertura;

    /**
     * Data completamento attività (vuoto se non completata)
     * Formato: "dd-MM-yyyy HH:mm:ss"
     */
    @Size(max = 100)
    @Column(name = "datacompletamento", nullable = false, length = 100)
    @Builder.Default
    private String datacompletamento = "";

    // ===== STATO =====

    /**
     * Stato urgenza
     * Valori tipici: "bassa", "media", "alta", "critica"
     */
    @NotBlank(message = "Stato urgenza obbligatorio")
    @Size(max = 50)
    @Column(name = "statourgenza", nullable = false, length = 50)
    private String statourgenza;

    /**
     * Stato completamento
     * Valori tipici: "aperta", "in_corso", "sospesa", "completata", "annullata"
     */
    @NotBlank(message = "Stato completamento obbligatorio")
    @Size(max = 50)
    @Column(name = "statocompletamento", nullable = false, length = 50)
    private String statocompletamento;

    // ===== CONTENUTO =====

    /**
     * Note descrittive attività
     */
    @Lob
    @Column(name = "note", columnDefinition = "LONGTEXT", nullable = false)
    @Builder.Default
    private String note = "";

    /**
     * Note operative (interne, per operatori)
     */
    @Lob
    @Column(name = "noteoperative", columnDefinition = "LONGTEXT", nullable = false)
    @Builder.Default
    private String noteoperative = "";

    /**
     * Log cronologico delle operazioni
     * Formato: timestamp + descrizione, multi-riga
     */
    @Lob
    @Column(name = "logattivita", columnDefinition = "TEXT", nullable = false)
    @Builder.Default
    private String logattivita = "";

    /**
     * Allegati associati (path o IDs separati da virgola)
     */
    @Size(max = 100)
    @Column(name = "allegati", nullable = false, length = 100)
    @Builder.Default
    private String allegati = "";

    // ===== BUSINESS METHODS =====

    /**
     * Verifica se attività è completata
     */
    public boolean isCompletata() {
        return "completata".equalsIgnoreCase(statocompletamento);
    }

    /**
     * Verifica se attività è aperta
     */
    public boolean isAperta() {
        return "aperta".equalsIgnoreCase(statocompletamento);
    }

    /**
     * Verifica se attività è in corso
     */
    public boolean isInCorso() {
        return "in_corso".equalsIgnoreCase(statocompletamento);
    }

    /**
     * Verifica se attività è sospesa
     */
    public boolean isSospesa() {
        return "sospesa".equalsIgnoreCase(statocompletamento);
    }

    /**
     * Verifica se attività è annullata
     */
    public boolean isAnnullata() {
        return "annullata".equalsIgnoreCase(statocompletamento);
    }

    /**
     * Verifica urgenza alta/critica
     */
    public boolean isUrgente() {
        return "alta".equalsIgnoreCase(statourgenza)
                || "critica".equalsIgnoreCase(statourgenza);
    }

    /**
     * Verifica se ha allegati
     */
    public boolean hasAllegati() {
        return allegati != null && !allegati.isEmpty() && !allegati.equals("0");
    }

    /**
     * Ottieni array allegati (split da CSV)
     */
    public String[] getAllegatiArray() {
        if (!hasAllegati()) {
            return new String[0];
        }
        return allegati.split(",");
    }

    /**
     * Aggiungi entry al log attività
     * @param entry Nuova entry da aggiungere
     */
    public void aggiungiLogEntry(String entry) {
        if (logattivita == null || logattivita.isEmpty()) {
            logattivita = entry;
        } else {
            logattivita += "\n" + entry;
        }
    }

    /**
     * Ottieni log attività come array (split per righe)
     */
    public String[] getLogAttivitaArray() {
        if (logattivita == null || logattivita.isEmpty()) {
            return new String[0];
        }
        return logattivita.split("\n");
    }

    /**
     * Marca attività come completata (ora)
     * @param dataCompletamento Data completamento
     */
    public void completaAttivita(String dataCompletamento) {
        this.statocompletamento = "completata";
        this.datacompletamento = dataCompletamento;
    }

    /**
     * Avvia attività (cambia stato in "in_corso")
     */
    public void avviaAttivita() {
        if (isAperta()) {
            this.statocompletamento = "in_corso";
        }
    }

    /**
     * Sospendi attività
     */
    public void sospendiAttivita() {
        if (!isCompletata() && !isAnnullata()) {
            this.statocompletamento = "sospesa";
        }
    }

    /**
     * Annulla attività
     */
    public void annullaAttivita() {
        if (!isCompletata()) {
            this.statocompletamento = "annullata";
        }
    }

    /**
     * Ottieni descrizione stato completamento localizzata
     */
    public String getStatoCompletamentoLabel() {
        return switch (statocompletamento.toLowerCase()) {
            case "aperta" -> "Aperta";
            case "in_corso" -> "In Corso";
            case "sospesa" -> "Sospesa";
            case "completata" -> "Completata";
            case "annullata" -> "Annullata";
            default -> statocompletamento;
        };
    }

    /**
     * Ottieni descrizione urgenza localizzata
     */
    public String getStatoUrgenzaLabel() {
        return switch (statourgenza.toLowerCase()) {
            case "bassa" -> "Bassa";
            case "media" -> "Media";
            case "alta" -> "Alta";
            case "critica" -> "Critica";
            default -> statourgenza;
        };
    }

    /**
     * Ottieni CSS class per urgenza (per colorazione UI)
     */
    public String getUrgenzaCssClass() {
        return switch (statourgenza.toLowerCase()) {
            case "bassa" -> "urgency-low";
            case "media" -> "urgency-medium";
            case "alta" -> "urgency-high";
            case "critica" -> "urgency-critical";
            default -> "urgency-default";
        };
    }

    /**
     * Ottieni CSS class per stato (per colorazione UI)
     */
    public String getStatoCssClass() {
        return switch (statocompletamento.toLowerCase()) {
            case "aperta" -> "status-open";
            case "in_corso" -> "status-progress";
            case "sospesa" -> "status-suspended";
            case "completata" -> "status-completed";
            case "annullata" -> "status-cancelled";
            default -> "status-default";
        };
    }

    /**
     * Verifica se attività è attiva (non completata né annullata)
     */
    public boolean isAttiva() {
        return !isCompletata() && !isAnnullata();
    }

    /**
     * Conta giorni dall'apertura (richiede parsing data)
     * Metodo helper - implementazione base
     */
    public String getGiorniDallApertura() {
        // TODO: Implementare parsing date e calcolo giorni
        return "N/A";
    }

    // ===== EQUALS & HASHCODE =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Activity that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", tipoattivita='" + tipoattivita + '\'' +
                ", statocompletamento='" + statocompletamento + '\'' +
                ", statourgenza='" + statourgenza + '\'' +
                ", idutente=" + idutente +
                ", dataapertura='" + dataapertura + '\'' +
                '}';
    }
}