package com.studiodomino.jplatform.cms.front.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Tag DTO - Rappresenta un tag del tag cloud front-end
 * Non è un'entity - è generato runtime dalle aggregazioni
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Nome del tag
     */
    private String nome;

    /**
     * Numero di occorrenze (quanti contenuti hanno questo tag)
     */
    @Builder.Default
    private Integer occorrenze = 0;

    /**
     * Peso visivo (1-5, per dimensione font)
     */
    @Builder.Default
    private Integer peso = 1;

    /**
     * CSS class per styling
     */
    private String cssClass;

    /**
     * Colore esadecimale (opzionale)
     */
    private String colore;

    /**
     * URL per ricerca tag
     */
    public String getSearchUrl() {
        return "/front/tag?tag=" + nome;
    }

    /**
     * Verifica se è tag popolare (peso >= 4)
     */
    public boolean isPopolare() {
        return peso != null && peso >= 4;
    }

    /**
     * Verifica se è tag molto usato (occorrenze > 10)
     */
    public boolean isMoltoUsato() {
        return occorrenze != null && occorrenze > 10;
    }

    /**
     * Ottieni font size CSS in base al peso
     */
    public String getFontSize() {
        if (peso == null) return "1em";

        return switch (peso) {
            case 1 -> "0.8em";
            case 2 -> "1em";
            case 3 -> "1.2em";
            case 4 -> "1.5em";
            case 5 -> "2em";
            default -> "1em";
        };
    }

    /**
     * Ottieni font weight CSS in base al peso
     */
    public String getFontWeight() {
        if (peso == null || peso <= 2) return "normal";
        if (peso >= 4) return "bold";
        return "500";
    }

    /**
     * Ottieni opacity CSS in base alle occorrenze
     */
    public String getOpacity() {
        if (occorrenze == null || occorrenze == 0) return "0.5";
        if (occorrenze >= 20) return "1.0";
        if (occorrenze >= 10) return "0.9";
        if (occorrenze >= 5) return "0.8";
        return "0.7";
    }

    /**
     * Genera style inline per tag cloud
     */
    public String getInlineStyle() {
        StringBuilder style = new StringBuilder();
        style.append("font-size: ").append(getFontSize()).append("; ");
        style.append("font-weight: ").append(getFontWeight()).append("; ");
        style.append("opacity: ").append(getOpacity()).append(";");

        if (colore != null && !colore.isEmpty()) {
            style.append(" color: ").append(colore).append(";");
        }

        return style.toString();
    }
}