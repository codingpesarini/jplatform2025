package com.studiodomino.jplatform.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * FileSystemPosition DTO - Rappresenta un elemento nel breadcrumb del file system
 * Usato per tracciare la navigazione nelle cartelle
 *
 * @author Raffaele Pesarini
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileSystemPosition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID del folder
     */
    @Builder.Default
    private String id = "";

    /**
     * Nome del folder
     */
    @Builder.Default
    private String nome = "";

    // ========== BUSINESS METHODS ==========

    /**
     * Verifica se è root
     */
    public boolean isRoot() {
        return "0".equals(id) || "1".equals(id) || "-1".equals(id);
    }

    /**
     * Verifica se ha ID valido
     */
    public boolean hasValidId() {
        return id != null && !id.isEmpty() && !"-1".equals(id);
    }

    /**
     * Verifica se ha nome
     */
    public boolean hasNome() {
        return nome != null && !nome.isEmpty();
    }

    /**
     * Ottieni ID numerico
     */
    public Integer getIdNumerico() {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Ottieni label per display (nome o ID)
     */
    public String getLabel() {
        if (hasNome()) {
            return nome;
        }
        if (isRoot()) {
            return "Home";
        }
        return id;
    }

    /**
     * Ottieni URL per navigazione
     */
    public String getUrl() {
        return "/filesystem?folder=" + id;
    }

    /**
     * Ottieni nome abbreviato (max 30 caratteri)
     */
    public String getNomeAbbreviato() {
        return getNomeAbbreviato(30);
    }

    /**
     * Ottieni nome abbreviato con lunghezza custom
     */
    public String getNomeAbbreviato(int maxLength) {
        if (nome == null || nome.length() <= maxLength) {
            return nome;
        }
        return nome.substring(0, maxLength - 3) + "...";
    }

    /**
     * Crea posizione root
     */
    public static FileSystemPosition root() {
        return FileSystemPosition.builder()
                .id("1")
                .nome("Home")
                .build();
    }

    /**
     * Crea posizione da ID e nome
     */
    public static FileSystemPosition of(String id, String nome) {
        return new FileSystemPosition(id, nome);
    }

    /**
     * Crea posizione da folder entity
     */
    public static FileSystemPosition fromFolder(com.studiodomino.jplatform.shared.entity.Folder folder) {
        if (folder == null) {
            return root();
        }
        return FileSystemPosition.builder()
                .id(folder.getId().toString())
                .nome(folder.getNome())
                .build();
    }
}