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

    // ========================================
    // METODI PUBBLICI - GESTIONE ITEMS
    // ========================================

    /**
     * Aggiungi sezione al breadcrumb
     */
    public void add(Section section) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(section);
    }

    /**
     * Aggiungi elemento al breadcrumb (crea Section al volo)
     */
    public void add(String label, String url) {
        Section section = new Section();
        section.setTitolo(label);
        section.setUrlRW(url);
        add(section);
    }

    /**
     * Aggiungi elemento con ID
     */
    public void add(String label, String url, String id) {
        Section section = new Section();
        section.setTitolo(label);
        section.setUrlRW(url);
        try {
            section.setId(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            // Ignora se ID non valido
        }
        add(section);
    }

    /**
     * Rimuovi ultimo elemento
     */
    public void removeLast() {
        if (items != null && !items.isEmpty()) {
            items.remove(items.size() - 1);
        }
    }

    /**
     * Rimuovi elemento per indice
     */
    public void remove(int index) {
        if (items != null && index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    /**
     * Pulisci tutti gli elementi
     */
    public void clear() {
        if (items != null) {
            items.clear();
        }
        itemAttuale = null;
        itemIdAttuale = null;
        urlIdAttuale = null;
    }

    /**
     * Ottieni numero elementi
     */
    public int size() {
        return items != null ? items.size() : 0;
    }

    /**
     * Ottieni elemento per indice
     */
    public Section get(int index) {
        if (items != null && index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return null;
    }

    /**
     * Ottieni ultimo elemento
     */
    public Section getLast() {
        if (items != null && !items.isEmpty()) {
            return items.get(items.size() - 1);
        }
        return null;
    }

    /**
     * Ottieni primo elemento
     */
    public Section getFirst() {
        if (items != null && !items.isEmpty()) {
            return items.get(0);
        }
        return null;
    }

    // ========================================
    // METODI - ITEM CORRENTE
    // ========================================

    /**
     * Imposta item corrente
     */
    public void setCurrentItem(String label, String url, String id) {
        this.itemAttuale = label;
        this.urlIdAttuale = url;
        this.itemIdAttuale = id;
    }

    /**
     * Verifica se ha item corrente
     */
    public boolean hasCurrentItem() {
        return itemAttuale != null && !itemAttuale.isEmpty();
    }

    // ========================================
    // METODI - BREADCRUMB BACK
    // ========================================

    /**
     * Aggiungi sezione al breadcrumb back
     */
    public void addBack(Section section) {
        if (itemsBack == null) {
            itemsBack = new ArrayList<>();
        }
        itemsBack.add(section);
    }

    /**
     * Aggiungi elemento al breadcrumb back
     */
    public void addBack(String label, String url) {
        Section section = new Section();
        section.setTitolo(label);
        section.setUrlRW(url);
        addBack(section);
    }

    /**
     * Imposta item corrente back
     */
    public void setCurrentItemBack(String label, String url, String id) {
        this.itemAttualeBack = label;
        this.urlIdAttualeBack = url;
        this.itemIdAttualeBack = id;
    }

    /**
     * Pulisci breadcrumb back
     */
    public void clearBack() {
        if (itemsBack != null) {
            itemsBack.clear();
        }
        itemAttualeBack = null;
        itemIdAttualeBack = null;
        urlIdAttualeBack = null;
    }

    /**
     * Verifica se ha breadcrumb back
     */
    public boolean hasBack() {
        return (itemsBack != null && !itemsBack.isEmpty()) ||
                (itemAttualeBack != null && !itemAttualeBack.isEmpty());
    }

    // ========================================
    // METODI UTILITY
    // ========================================

    /**
     * Verifica se breadcrumb è vuoto
     */
    public boolean isEmpty() {
        return (items == null || items.isEmpty())
                && (itemAttuale == null || itemAttuale.isEmpty());
    }

    /**
     * Ottieni percorso completo come stringa
     * Es: "Home / Servizi / Borse di Studio"
     */
    public String getPathAsString() {
        return getPathAsString(" / ");
    }

    /**
     * Ottieni percorso completo con separatore custom
     */
    public String getPathAsString(String separator) {
        if (items == null || items.isEmpty()) {
            return itemAttuale != null ? itemAttuale : "";
        }

        StringBuilder path = new StringBuilder();
        for (Section section : items) {
            if (path.length() > 0) {
                path.append(separator);
            }
            path.append(section.getTitolo());
        }

        if (itemAttuale != null && !itemAttuale.isEmpty()) {
            path.append(separator).append(itemAttuale);
        }

        return path.toString();
    }

    /**
     * Genera HTML breadcrumb (Bootstrap 5 style)
     */
    public String toHtml() {
        StringBuilder html = new StringBuilder();
        html.append("<nav aria-label=\"breadcrumb\">");
        html.append("<ol class=\"breadcrumb\">");

        // Items intermedi
        if (items != null) {
            for (Section section : items) {
                html.append("<li class=\"breadcrumb-item\">");
                html.append("<a href=\"").append(section.getUrlRW()).append("\">");
                html.append(section.getTitolo());
                html.append("</a>");
                html.append("</li>");
            }
        }

        // Item corrente (non linkato)
        if (itemAttuale != null && !itemAttuale.isEmpty()) {
            html.append("<li class=\"breadcrumb-item active\" aria-current=\"page\">");
            html.append(itemAttuale);
            html.append("</li>");
        }

        html.append("</ol>");
        html.append("</nav>");

        return html.toString();
    }

    /**
     * Genera breadcrumb JSON (per API)
     */
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\"breadcrumb\":[");

        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                Section section = items.get(i);
                if (i > 0) json.append(",");
                json.append("{")
                        .append("\"label\":\"").append(escapeJson(section.getTitolo())).append("\",")
                        .append("\"url\":\"").append(escapeJson(section.getUrlRW())).append("\",")
                        .append("\"id\":").append(section.getId())
                        .append("}");
            }
        }

        // Item corrente
        if (itemAttuale != null && !itemAttuale.isEmpty()) {
            if (items != null && !items.isEmpty()) json.append(",");
            json.append("{")
                    .append("\"label\":\"").append(escapeJson(itemAttuale)).append("\",")
                    .append("\"url\":\"").append(escapeJson(urlIdAttuale)).append("\",")
                    .append("\"current\":true")
                    .append("}");
        }

        json.append("]}");
        return json.toString();
    }

    /**
     * Escape JSON string
     */
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    /**
     * Clona breadcrumb
     */
    public Breadcrumb clone() {
        Breadcrumb cloned = new Breadcrumb();

        if (items != null) {
            cloned.items = new ArrayList<>(items);
        }

        cloned.itemAttuale = this.itemAttuale;
        cloned.itemIdAttuale = this.itemIdAttuale;
        cloned.urlIdAttuale = this.urlIdAttuale;

        if (itemsBack != null) {
            cloned.itemsBack = new ArrayList<>(itemsBack);
        }

        cloned.itemAttualeBack = this.itemAttualeBack;
        cloned.itemIdAttualeBack = this.itemIdAttualeBack;
        cloned.urlIdAttualeBack = this.urlIdAttualeBack;

        return cloned;
    }
}