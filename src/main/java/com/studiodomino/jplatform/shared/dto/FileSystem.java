package com.studiodomino.jplatform.shared.dto;

import com.studiodomino.jplatform.shared.entity.Folder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * FileSystem DTO - Gestisce la navigazione nel file system virtuale
 * Utilizzato per repository files, immagini, documenti
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileSystem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID del folder corrente
     */
    @Builder.Default
    private String actualFolder = "1";

    /**
     * ID del folder padre (superiore)
     */
    @Builder.Default
    private String superactualFolder = "1";

    /**
     * Metodo di visualizzazione:
     * - "imm" = solo immagini
     * - "all" = solo allegati
     * - "tutti" = tutto
     */
    @Builder.Default
    private String metodo = "tutti";

    /**
     * Folder corrente (dettaglio)
     */
    @Builder.Default
    private Folder folder = new Folder();

    /**
     * Lista folders nella directory corrente
     */
    @Builder.Default
    private List<Folder> folders = new ArrayList<>();

    /**
     * Breadcrumb posizione (lista ID folders)
     */
    @Builder.Default
    private List<String> position = new ArrayList<>();

    /**
     * Suffisso tabella DB (per multi-tenancy)
     */
    @Builder.Default
    private String tablePostFix = "";

    // ========== BUSINESS METHODS ==========

    /**
     * Verifica se è nella root
     */
    public boolean isRoot() {
        return "1".equals(actualFolder) || "-1".equals(actualFolder);
    }

    /**
     * Verifica se può andare al parent
     */
    public boolean canGoUp() {
        return !isRoot() && superactualFolder != null && !superactualFolder.isEmpty();
    }

    /**
     * Verifica se visualizza solo immagini
     */
    public boolean isModoImmagini() {
        return "imm".equals(metodo);
    }

    /**
     * Verifica se visualizza solo allegati
     */
    public boolean isModoAllegati() {
        return "all".equals(metodo);
    }

    /**
     * Verifica se visualizza tutto
     */
    public boolean isModoTutti() {
        return "tutti".equals(metodo);
    }

    /**
     * Verifica se ci sono folders
     */
    public boolean hasFolders() {
        return folders != null && !folders.isEmpty();
    }

    /**
     * Conta folders
     */
    public int countFolders() {
        return folders != null ? folders.size() : 0;
    }

    /**
     * Ottieni profondità (livello) attuale
     */
    public int getDepth() {
        return position != null ? position.size() : 0;
    }

    /**
     * Verifica se ha breadcrumb
     */
    public boolean hasBreadcrumb() {
        return position != null && !position.isEmpty();
    }

    /**
     * Aggiungi folder alla posizione corrente
     */
    public void addToPosition(String folderId) {
        if (position == null) {
            position = new ArrayList<>();
        }
        position.add(folderId);
    }

    /**
     * Rimuovi ultimo folder dalla posizione
     */
    public void removeLastFromPosition() {
        if (position != null && !position.isEmpty()) {
            position.remove(position.size() - 1);
        }
    }

    /**
     * Reset posizione
     */
    public void resetPosition() {
        if (position != null) {
            position.clear();
        }
    }

    /**
     * Naviga a folder specifico
     */
    public void navigateTo(String folderId) {
        if (folder != null) {
            this.superactualFolder = this.actualFolder;
        }
        this.actualFolder = folderId;
    }

    /**
     * Torna al parent
     */
    public void navigateUp() {
        if (canGoUp()) {
            this.actualFolder = this.superactualFolder;
            removeLastFromPosition();
        }
    }

    /**
     * Reset a root
     */
    public void navigateToRoot() {
        this.actualFolder = "1";
        this.superactualFolder = "1";
        resetPosition();
    }

    /**
     * Ottieni descrizione metodo
     */
    public String getMetodoDescrizione() {
        return switch (metodo) {
            case "imm" -> "Solo immagini";
            case "all" -> "Solo allegati";
            case "tutti" -> "Tutto";
            default -> "Sconosciuto";
        };
    }
}