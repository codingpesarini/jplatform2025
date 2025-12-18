package com.studiodomino.jplatform.cms.front.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Archivio - DTO per navigazione archivio contenuti per data
 * Struttura gerarchica: Anno → Mesi → Contenuti
 * Non mappato su DB, generato dinamicamente dai contenuti
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Archivio implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID archivio (sezione/root contenuti)
     */
    @Builder.Default
    private String idArchivio = "0";

    /**
     * Stato archivio (filtro stato pubblicazione)
     * "0" = bozza, "1" = pubblicato, "3" = evidenza
     */
    @Builder.Default
    private String statoArchivio = "0";

    /**
     * Anno (es: "2024")
     */
    @Builder.Default
    private String anno = "2012";

    /**
     * Mese (es: "Gennaio", "Febbraio", ...)
     */
    @Builder.Default
    private String mese = "Gennaio";

    /**
     * Numero contenuti per anno
     */
    @Builder.Default
    private String numeroAnni = "0";

    /**
     * Numero contenuti per mese
     */
    @Builder.Default
    private String numeroMesi = "0";

    /**
     * Sotto-archivio gerarchico
     * Per anno: contiene lista mesi
     * Per mese: vuoto
     */
    @Builder.Default
    private List<Archivio> subArchivio = new ArrayList<>();

    /**
     * Flag: indica se anno/mese ha contenuti (mostra link)
     */
    @Builder.Default
    private boolean link = false;

    // ===== BUSINESS METHODS =====

    /**
     * Genera URL rewrite per archivio
     * Formato: pageArchivio/{idArchivio}/{statoArchivio}/{anno}/{mese}
     */
    public String getUrlRW() {
        return String.format("pageArchivio/%s/%s/%s/%s",
                idArchivio, statoArchivio, anno, mese);
    }

    /**
     * Verifica se è archivio anno (ha mesi sotto)
     */
    public boolean isAnno() {
        return subArchivio != null && !subArchivio.isEmpty();
    }

    /**
     * Verifica se è archivio mese (foglia)
     */
    public boolean isMese() {
        return !isAnno();
    }

    /**
     * Verifica se ha contenuti
     */
    public boolean hasContenuti() {
        return link;
    }

    /**
     * Ottieni numero contenuti come intero
     */
    public int getNumeroContenutiAnno() {
        try {
            return Integer.parseInt(numeroAnni);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Ottieni numero contenuti mese come intero
     */
    public int getNumeroContenutiMese() {
        try {
            return Integer.parseInt(numeroMesi);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Aggiungi mese al sotto-archivio anno
     */
    public void addMese(Archivio mese) {
        if (subArchivio == null) {
            subArchivio = new ArrayList<>();
        }
        subArchivio.add(mese);
    }

    /**
     * Ottieni label mese con conteggio
     * Es: "Gennaio (5)"
     */
    public String getMeseLabel() {
        if (getNumeroContenutiMese() > 0) {
            return mese + " (" + numeroMesi + ")";
        }
        return mese;
    }

    /**
     * Ottieni label anno con conteggio
     * Es: "2024 (12)"
     */
    public String getAnnoLabel() {
        if (getNumeroContenutiAnno() > 0) {
            return anno + " (" + numeroAnni + ")";
        }
        return anno;
    }

    /**
     * Numero mese (1-12) da nome italiano
     */
    public int getMeseNumerico() {
        return switch (mese.toLowerCase()) {
            case "gennaio" -> 1;
            case "febbraio" -> 2;
            case "marzo" -> 3;
            case "aprile" -> 4;
            case "maggio" -> 5;
            case "giugno" -> 6;
            case "luglio" -> 7;
            case "agosto" -> 8;
            case "settembre" -> 9;
            case "ottobre" -> 10;
            case "novembre" -> 11;
            case "dicembre" -> 12;
            default -> 0;
        };
    }

    /**
     * Ottieni nome mese da numero (1-12)
     */
    public static String getMeseNome(int numeroMese) {
        return switch (numeroMese) {
            case 1 -> "Gennaio";
            case 2 -> "Febbraio";
            case 3 -> "Marzo";
            case 4 -> "Aprile";
            case 5 -> "Maggio";
            case 6 -> "Giugno";
            case 7 -> "Luglio";
            case 8 -> "Agosto";
            case 9 -> "Settembre";
            case 10 -> "Ottobre";
            case 11 -> "Novembre";
            case 12 -> "Dicembre";
            default -> "";
        };
    }

    /**
     * Ottieni nome mese abbreviato
     */
    public String getMeseAbbreviato() {
        return mese.length() >= 3 ? mese.substring(0, 3) : mese;
    }

    /**
     * Verifica se archivio è vuoto
     */
    public boolean isEmpty() {
        return getNumeroContenutiAnno() == 0 && getNumeroContenutiMese() == 0;
    }

    /**
     * Ottieni URL completo con domain (per SEO)
     */
    public String getUrlCompleto(String baseDomain) {
        return baseDomain + "/" + getUrlRW();
    }

    /**
     * Confronta per ordinamento decrescente (più recente prima)
     */
    public int compareTo(Archivio altro) {
        // Prima ordina per anno
        int annoCompare = altro.anno.compareTo(this.anno);
        if (annoCompare != 0) {
            return annoCompare;
        }

        // Poi per mese (numerico)
        return Integer.compare(altro.getMeseNumerico(), this.getMeseNumerico());
    }

    @Override
    public String toString() {
        if (isAnno()) {
            return "Archivio{anno=" + anno + ", contenuti=" + numeroAnni +
                    ", mesi=" + (subArchivio != null ? subArchivio.size() : 0) + "}";
        } else {
            return "Archivio{anno=" + anno + ", mese=" + mese +
                    ", contenuti=" + numeroMesi + "}";
        }
    }
}