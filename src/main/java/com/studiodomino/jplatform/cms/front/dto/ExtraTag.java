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
 * ExtraTag - Container per contenuti correlati
 * Gestisce fino a 10 slot di contenuti correlati basati su tag
 * Non è un'entity - è un DTO runtime
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtraTag implements Serializable {

    private static final long serialVersionUID = 1L;

    // ===== 10 SLOT CONTENUTI CORRELATI =====

    @Builder.Default
    private List<DatiBase> contenutiExtraTag01 = new ArrayList<>();

    @Builder.Default
    private List<DatiBase> contenutiExtraTag02 = new ArrayList<>();

    @Builder.Default
    private List<DatiBase> contenutiExtraTag03 = new ArrayList<>();

    @Builder.Default
    private List<DatiBase> contenutiExtraTag04 = new ArrayList<>();

    @Builder.Default
    private List<DatiBase> contenutiExtraTag05 = new ArrayList<>();

    @Builder.Default
    private List<DatiBase> contenutiExtraTag06 = new ArrayList<>();

    @Builder.Default
    private List<DatiBase> contenutiExtraTag07 = new ArrayList<>();

    @Builder.Default
    private List<DatiBase> contenutiExtraTag08 = new ArrayList<>();

    @Builder.Default
    private List<DatiBase> contenutiExtraTag09 = new ArrayList<>();

    @Builder.Default
    private List<DatiBase> contenutiExtraTag10 = new ArrayList<>();

    // ===== BUSINESS METHODS =====

    /**
     * Ottieni slot ExtraTag per numero (1-10)
     */
    public List<DatiBase> getExtraTagByNumber(int numero) {
        return switch (numero) {
            case 1 -> contenutiExtraTag01;
            case 2 -> contenutiExtraTag02;
            case 3 -> contenutiExtraTag03;
            case 4 -> contenutiExtraTag04;
            case 5 -> contenutiExtraTag05;
            case 6 -> contenutiExtraTag06;
            case 7 -> contenutiExtraTag07;
            case 8 -> contenutiExtraTag08;
            case 9 -> contenutiExtraTag09;
            case 10 -> contenutiExtraTag10;
            default -> new ArrayList<>();
        };
    }

    /**
     * Imposta slot ExtraTag per numero (1-10)
     */
    public void setExtraTagByNumber(int numero, List<DatiBase> contenuti) {
        switch (numero) {
            case 1 -> contenutiExtraTag01 = contenuti;
            case 2 -> contenutiExtraTag02 = contenuti;
            case 3 -> contenutiExtraTag03 = contenuti;
            case 4 -> contenutiExtraTag04 = contenuti;
            case 5 -> contenutiExtraTag05 = contenuti;
            case 6 -> contenutiExtraTag06 = contenuti;
            case 7 -> contenutiExtraTag07 = contenuti;
            case 8 -> contenutiExtraTag08 = contenuti;
            case 9 -> contenutiExtraTag09 = contenuti;
            case 10 -> contenutiExtraTag10 = contenuti;
        }
    }

    /**
     * Verifica se ha contenuti in almeno uno slot
     */
    public boolean hasContenutiExtraTag() {
        return hasContenutiInSlot(1) || hasContenutiInSlot(2) ||
                hasContenutiInSlot(3) || hasContenutiInSlot(4) ||
                hasContenutiInSlot(5) || hasContenutiInSlot(6) ||
                hasContenutiInSlot(7) || hasContenutiInSlot(8) ||
                hasContenutiInSlot(9) || hasContenutiInSlot(10);
    }

    /**
     * Verifica se slot specifico ha contenuti
     */
    public boolean hasContenutiInSlot(int numero) {
        List<DatiBase> contenuti = getExtraTagByNumber(numero);
        return contenuti != null && !contenuti.isEmpty();
    }

    /**
     * Conta contenuti in slot specifico
     */
    public int countContenutiInSlot(int numero) {
        List<DatiBase> contenuti = getExtraTagByNumber(numero);
        return contenuti != null ? contenuti.size() : 0;
    }

    /**
     * Conta totale contenuti in tutti gli slot
     */
    public int countTotaleContenuti() {
        int totale = 0;
        for (int i = 1; i <= 10; i++) {
            totale += countContenutiInSlot(i);
        }
        return totale;
    }

    /**
     * Ottieni tutti gli slot popolati
     */
    public List<Integer> getSlotPopolati() {
        List<Integer> slot = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            if (hasContenutiInSlot(i)) {
                slot.add(i);
            }
        }
        return slot;
    }

    /**
     * Ottieni numero slot popolati
     */
    public int countSlotPopolati() {
        return getSlotPopolati().size();
    }

    /**
     * Reset tutti gli slot (equivalente a RestoreExtraTag legacy)
     */
    public void restoreExtraTag() {
        contenutiExtraTag01 = null;
        contenutiExtraTag02 = null;
        contenutiExtraTag03 = null;
        contenutiExtraTag04 = null;
        contenutiExtraTag05 = null;
        contenutiExtraTag06 = null;
        contenutiExtraTag07 = null;
        contenutiExtraTag08 = null;
        contenutiExtraTag09 = null;
        contenutiExtraTag10 = null;
    }

    /**
     * Clear tutti gli slot (imposta liste vuote invece di null)
     */
    public void clearExtraTag() {
        contenutiExtraTag01 = new ArrayList<>();
        contenutiExtraTag02 = new ArrayList<>();
        contenutiExtraTag03 = new ArrayList<>();
        contenutiExtraTag04 = new ArrayList<>();
        contenutiExtraTag05 = new ArrayList<>();
        contenutiExtraTag06 = new ArrayList<>();
        contenutiExtraTag07 = new ArrayList<>();
        contenutiExtraTag08 = new ArrayList<>();
        contenutiExtraTag09 = new ArrayList<>();
        contenutiExtraTag10 = new ArrayList<>();
    }

    /**
     * Verifica se è vuoto (nessuno slot popolato)
     */
    public boolean isEmpty() {
        return !hasContenutiExtraTag();
    }

    /**
     * Ottieni primo contenuto dal primo slot popolato
     */
    public DatiBase getPrimoContenuto() {
        for (int i = 1; i <= 10; i++) {
            if (hasContenutiInSlot(i)) {
                List<DatiBase> contenuti = getExtraTagByNumber(i);
                if (!contenuti.isEmpty()) {
                    return contenuti.get(0);
                }
            }
        }
        return null;
    }

    /**
     * Aggiungi contenuto a slot specifico
     */
    public void addContenutoToSlot(int numero, DatiBase contenuto) {
        List<DatiBase> slot = getExtraTagByNumber(numero);
        if (slot == null) {
            slot = new ArrayList<>();
            setExtraTagByNumber(numero, slot);
        }
        if (!slot.contains(contenuto)) {
            slot.add(contenuto);
        }
    }

    /**
     * Rimuovi contenuto da slot specifico
     */
    public void removeContenutoFromSlot(int numero, DatiBase contenuto) {
        List<DatiBase> slot = getExtraTagByNumber(numero);
        if (slot != null) {
            slot.remove(contenuto);
        }
    }

    /**
     * Merge ExtraTag da altro ExtraTag
     */
    public void mergeFrom(ExtraTag other) {
        if (other == null) return;

        for (int i = 1; i <= 10; i++) {
            if (other.hasContenutiInSlot(i)) {
                List<DatiBase> otherContenuti = other.getExtraTagByNumber(i);
                List<DatiBase> myContenuti = getExtraTagByNumber(i);

                if (myContenuti == null) {
                    setExtraTagByNumber(i, new ArrayList<>(otherContenuti));
                } else {
                    // Aggiungi solo contenuti non già presenti
                    for (DatiBase contenuto : otherContenuti) {
                        if (!myContenuti.contains(contenuto)) {
                            myContenuti.add(contenuto);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ExtraTag{");
        for (int i = 1; i <= 10; i++) {
            if (hasContenutiInSlot(i)) {
                sb.append("slot").append(i).append("=")
                        .append(countContenutiInSlot(i)).append(" items, ");
            }
        }
        if (sb.length() > 10) {
            sb.setLength(sb.length() - 2); // Rimuovi ultima virgola
        }
        sb.append("}");
        return sb.toString();
    }
}