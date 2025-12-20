package com.studiodomino.jplatform.cms.front.dto;

import com.studiodomino.jplatform.cms.entity.DatiBase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Ricerca DTO - Gestisce ricerche avanzate multi-campo
 * Supporta fino a 15 campi di ricerca con mappatura e operatori
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ricerca implements Serializable {

    private static final long serialVersionUID = 1L;

    // ===== CAMPI DI RICERCA (1-15) =====

    @Builder.Default
    private String campo1 = "";
    @Builder.Default
    private String campo2 = "";
    @Builder.Default
    private String campo3 = "";
    @Builder.Default
    private String campo4 = "";
    @Builder.Default
    private String campo5 = "";
    @Builder.Default
    private String campo6 = "";
    @Builder.Default
    private String campo7 = "";
    @Builder.Default
    private String campo8 = "";
    @Builder.Default
    private String campo9 = "";
    @Builder.Default
    private String campo10 = "";
    @Builder.Default
    private String campo11 = "";
    @Builder.Default
    private String campo12 = "";
    @Builder.Default
    private String campo13 = "";
    @Builder.Default
    private String campo14 = "";
    @Builder.Default
    private String campo15 = "";

    // ===== MAPPATURA CAMPI (1-15) =====
    // Indica quale campo DB/entity mappare

    @Builder.Default
    private String mappaCampo1 = "";
    @Builder.Default
    private String mappaCampo2 = "";
    @Builder.Default
    private String mappaCampo3 = "";
    @Builder.Default
    private String mappaCampo4 = "";
    @Builder.Default
    private String mappaCampo5 = "";
    @Builder.Default
    private String mappaCampo6 = "";
    @Builder.Default
    private String mappaCampo7 = "";
    @Builder.Default
    private String mappaCampo8 = "";
    @Builder.Default
    private String mappaCampo9 = "";
    @Builder.Default
    private String mappaCampo10 = "";
    @Builder.Default
    private String mappaCampo11 = "";
    @Builder.Default
    private String mappaCampo12 = "";
    @Builder.Default
    private String mappaCampo13 = "";
    @Builder.Default
    private String mappaCampo14 = "";
    @Builder.Default
    private String mappaCampo15 = "";

    // ===== OPERATORI (1-15) =====
    // Es: "=", "LIKE", ">", "<", "IN", ecc.

    @Builder.Default
    private String operatoreCampo1 = "";
    @Builder.Default
    private String operatoreCampo2 = "";
    @Builder.Default
    private String operatoreCampo3 = "";
    @Builder.Default
    private String operatoreCampo4 = "";
    @Builder.Default
    private String operatoreCampo5 = "";
    @Builder.Default
    private String operatoreCampo6 = "";
    @Builder.Default
    private String operatoreCampo7 = "";
    @Builder.Default
    private String operatoreCampo8 = "";
    @Builder.Default
    private String operatoreCampo9 = "";
    @Builder.Default
    private String operatoreCampo10 = "";
    @Builder.Default
    private String operatoreCampo11 = "";
    @Builder.Default
    private String operatoreCampo12 = "";
    @Builder.Default
    private String operatoreCampo13 = "";
    @Builder.Default
    private String operatoreCampo14 = "";
    @Builder.Default
    private String operatoreCampo15 = "";

    // ===== PARAMETRI RICERCA =====

    @Builder.Default
    private String site = "";

    @Builder.Default
    private String ordine = "";

    @Builder.Default
    private String limite = "";

    @Builder.Default
    private String archivio = "0";

    // ===== CRITERI E RISULTATI =====

    @Builder.Default
    private List<String> criteri = new ArrayList<>();

    @Builder.Default
    private List<DatiBase> risultatoRicerca = new ArrayList<>();

    // ===== BUSINESS METHODS =====

    /**
     * Ottieni numero risultati trovati
     */
    public int getRisultatiTrovati() {
        return risultatoRicerca != null ? risultatoRicerca.size() : 0;
    }

    /**
     * Verifica se ci sono risultati
     */
    public boolean hasRisultati() {
        return risultatoRicerca != null && !risultatoRicerca.isEmpty();
    }

    /**
     * Ottieni campo per numero (1-15)
     */
    public String getCampo(int numero) {
        return switch (numero) {
            case 1 -> campo1;
            case 2 -> campo2;
            case 3 -> campo3;
            case 4 -> campo4;
            case 5 -> campo5;
            case 6 -> campo6;
            case 7 -> campo7;
            case 8 -> campo8;
            case 9 -> campo9;
            case 10 -> campo10;
            case 11 -> campo11;
            case 12 -> campo12;
            case 13 -> campo13;
            case 14 -> campo14;
            case 15 -> campo15;
            default -> "";
        };
    }

    /**
     * Imposta campo per numero (1-15)
     */
    public void setCampo(int numero, String valore) {
        switch (numero) {
            case 1 -> campo1 = valore;
            case 2 -> campo2 = valore;
            case 3 -> campo3 = valore;
            case 4 -> campo4 = valore;
            case 5 -> campo5 = valore;
            case 6 -> campo6 = valore;
            case 7 -> campo7 = valore;
            case 8 -> campo8 = valore;
            case 9 -> campo9 = valore;
            case 10 -> campo10 = valore;
            case 11 -> campo11 = valore;
            case 12 -> campo12 = valore;
            case 13 -> campo13 = valore;
            case 14 -> campo14 = valore;
            case 15 -> campo15 = valore;
        }
    }

    /**
     * Ottieni mappa campo per numero (1-15)
     */
    public String getMappaCampo(int numero) {
        return switch (numero) {
            case 1 -> mappaCampo1;
            case 2 -> mappaCampo2;
            case 3 -> mappaCampo3;
            case 4 -> mappaCampo4;
            case 5 -> mappaCampo5;
            case 6 -> mappaCampo6;
            case 7 -> mappaCampo7;
            case 8 -> mappaCampo8;
            case 9 -> mappaCampo9;
            case 10 -> mappaCampo10;
            case 11 -> mappaCampo11;
            case 12 -> mappaCampo12;
            case 13 -> mappaCampo13;
            case 14 -> mappaCampo14;
            case 15 -> mappaCampo15;
            default -> "";
        };
    }

    /**
     * Ottieni operatore campo per numero (1-15)
     */
    public String getOperatoreCampo(int numero) {
        return switch (numero) {
            case 1 -> operatoreCampo1;
            case 2 -> operatoreCampo2;
            case 3 -> operatoreCampo3;
            case 4 -> operatoreCampo4;
            case 5 -> operatoreCampo5;
            case 6 -> operatoreCampo6;
            case 7 -> operatoreCampo7;
            case 8 -> operatoreCampo8;
            case 9 -> operatoreCampo9;
            case 10 -> operatoreCampo10;
            case 11 -> operatoreCampo11;
            case 12 -> operatoreCampo12;
            case 13 -> operatoreCampo13;
            case 14 -> operatoreCampo14;
            case 15 -> operatoreCampo15;
            default -> "";
        };
    }

    /**
     * Verifica se campo è valorizzato
     */
    public boolean isCampoValorizzato(int numero) {
        String campo = getCampo(numero);
        return campo != null && !campo.isEmpty();
    }

    /**
     * Conta campi valorizzati
     */
    public int countCampiValorizzati() {
        int count = 0;
        for (int i = 1; i <= 15; i++) {
            if (isCampoValorizzato(i)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Verifica se ricerca è vuota
     */
    public boolean isEmpty() {
        return countCampiValorizzati() == 0;
    }

    /**
     * Reset tutti i campi
     */
    public void resetAll() {
        // Campi
        campo1 = campo2 = campo3 = campo4 = campo5 = "";
        campo6 = campo7 = campo8 = campo9 = campo10 = "";
        campo11 = campo12 = campo13 = campo14 = campo15 = "";

        // Mappe
        mappaCampo1 = mappaCampo2 = mappaCampo3 = mappaCampo4 = mappaCampo5 = "";
        mappaCampo6 = mappaCampo7 = mappaCampo8 = mappaCampo9 = mappaCampo10 = "";
        mappaCampo11 = mappaCampo12 = mappaCampo13 = mappaCampo14 = mappaCampo15 = "";

        // Operatori
        operatoreCampo1 = operatoreCampo2 = operatoreCampo3 = operatoreCampo4 = operatoreCampo5 = "";
        operatoreCampo6 = operatoreCampo7 = operatoreCampo8 = operatoreCampo9 = operatoreCampo10 = "";
        operatoreCampo11 = operatoreCampo12 = operatoreCampo13 = operatoreCampo14 = operatoreCampo15 = "";

        // Parametri
        limite = "";
        ordine = "";
        archivio = "0";

        // Risultati
        if (risultatoRicerca != null) {
            risultatoRicerca.clear();
        }
    }

    /**
     * Reset solo archivio (legacy compatibility)
     */
    public void reset() {
        archivio = "0";
    }

    /**
     * Verifica se criterio è selezionato
     */
    public boolean hasCriterio(String key) {
        return criteri != null && criteri.contains(key);
    }

    /**
     * Ottieni valore per checkbox (legacy compatibility)
     */
    public String getValoreKey(String key) {
        return hasCriterio(key) ? "checked" : "";
    }

    /**
     * Aggiungi criterio
     */
    public void addCriterio(String criterio) {
        if (criteri == null) {
            criteri = new ArrayList<>();
        }
        if (!criteri.contains(criterio)) {
            criteri.add(criterio);
        }
    }

    /**
     * Rimuovi criterio
     */
    public void removeCriterio(String criterio) {
        if (criteri != null) {
            criteri.remove(criterio);
        }
    }

    /**
     * Verifica se ha criteri
     */
    public boolean hasCriteri() {
        return criteri != null && !criteri.isEmpty();
    }

    /**
     * Ottieni descrizione ricerca per log/debug
     */
    public String getDescrizioneRicerca() {
        StringBuilder sb = new StringBuilder("Ricerca[");
        int count = 0;
        for (int i = 1; i <= 15; i++) {
            if (isCampoValorizzato(i)) {
                if (count > 0) sb.append(", ");
                sb.append(getMappaCampo(i))
                        .append(" ")
                        .append(getOperatoreCampo(i))
                        .append(" '")
                        .append(getCampo(i))
                        .append("'");
                count++;
            }
        }
        sb.append("]");
        return sb.toString();
    }
}