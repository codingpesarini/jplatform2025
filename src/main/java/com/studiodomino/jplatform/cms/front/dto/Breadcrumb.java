package com.studiodomino.jplatform.cms.front.dto;

import com.studiodomino.jplatform.cms.entity.Section;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Breadcrumb - gestisce le "briciole di pane" per la navigazione
 * Non mappato su DB, solo DTO di supporto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Breadcrumb implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Lista sezioni nel percorso (path)
     * Es: Home > Servizi > Borse di Studio
     */
    @Builder.Default
    private List<Section> items = new ArrayList<>();

    /**
     * Titolo item corrente (ultimo breadcrumb)
     */
    private String itemAttuale;

    /**
     * ID item corrente
     */
    private String itemIdAttuale;

    /**
     * URL item corrente
     */
    private String urlIdAttuale;

    // ===== Breadcrumb "Back" - per navigazione indietro =====

    /**
     * Lista sezioni percorso back
     */
    @Builder.Default
    private List<Section> itemsBack = new ArrayList<>();

    /**
     * Titolo item back
     */
    private String itemAttualeBack;

    /**
     * ID item back
     */
    private String itemIdAttualeBack;

    /**
     * URL item back
     */
    private String urlIdAttualeBack;

    /**
     * Helper: ottieni percorso completo come stringa
     * Es: "Home / Servizi / Borse di Studio"
     */
    public String getPathAsString() {
        if (items == null || items.isEmpty()) {
            return itemAttuale != null ? itemAttuale : "";
        }

        StringBuilder path = new StringBuilder();
        for (Section section : items) {
            if (path.length() > 0) {
                path.append(" / ");
            }
            path.append(section.getTitolo());
        }

        if (itemAttuale != null && !itemAttuale.isEmpty()) {
            path.append(" / ").append(itemAttuale);
        }

        return path.toString();
    }

    /**
     * Helper: verifica se breadcrumb è vuoto
     */
    public boolean isEmpty() {
        return (items == null || items.isEmpty())
                && (itemAttuale == null || itemAttuale.isEmpty());
    }
}