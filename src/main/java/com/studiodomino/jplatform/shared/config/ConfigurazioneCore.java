package com.studiodomino.jplatform.shared.config;

import com.studiodomino.jplatform.shared.entity.*;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * Configurazione principale dell'applicazione (sessione)
 * Equivalente di ConfigurazioneCore in Struts
 *
 * TUTTO passa attraverso questo oggetto - NIENTE nella sessione diretta!
 */
@Data
public class ConfigurazioneCore implements Serializable {

    private static final long serialVersionUID = 5545802942781497253L;

    // ========== CONFIGURAZIONE SITO ==========
    private Site sito;  // Entity Site dal database

    // ========== LOCALIZZAZIONE ==========
    private String locale = "it_IT";

    // ========== REPOSITORY PATHS ==========
    private String filesRepository = "";
    private String filesRepositoryWeb = "";
    private String imagesRepository = "";
    private String imagesRepositoryWeb = "";

    // ========== COOKIES ==========
    private String langPlatCookieName;
    private String userIDCookieName;
    private String profileIDCookieName;
    private String langPlatCookieValue;
    private String userIDCookieValue;
    private String profileIDCookieValue;

    // ========== UTENTI ==========
    private Utente amministratore;  // Entity Utente loggato (admin)
    private UtenteEsterno utente;          // Entity Utente loggato (normale)

    // ========== GRUPPI E RUOLI ==========
    private List<Gruppo> gruppiAmministratore;
    private List<Gruppo> gruppi;
    private List<Ruolo> ruoli;

    // ========== NAVIGAZIONE ==========
    private Breadcrumb breadcrumb;
    private Breadcrumb breadcrumbBack;

    // ========== RICERCA ==========
    private Ricerca ricerca;

    // ========== PAGINAZIONE ==========
    private String pageNumber = "";
    private String startItems = "";
    private String endItems = "";
    private String totalPage = "";
    private String itemsPage = "";
    private String paginationBar = "";

    // ========== NAVIGAZIONE TEMPLATE ==========
    private String nav01;
    private String nav02;
    private String nav03;
    private String nav04;
    private String nav05;

    // ✅ NUOVI CAMPI PER ROUTING POST-LOGIN
    /**
     * ID del sito richiesto prima del login (per redirect post-login)
     */
    private Integer requestedSiteId;

    /**
     * Endpoint target dopo il login (calcolato da utente.l2)
     */
    private String postLoginEndpoint;

    // ========================================
    // METODI HELPER - PATHS
    // ========================================

    public String getFilesRepository() {
        if (sito != null && sito.getPathRepository() != null) {
            this.filesRepository = sito.getPathRepository() +
                    System.getProperty("file.separator") +
                    "repository" +
                    System.getProperty("file.separator");
        }
        return filesRepository;
    }

    public String getFilesRepositoryWeb() {
        if (sito != null) {
            this.filesRepositoryWeb = sito.getDescrizione() +
                    sito.getPathWeb() +
                    "/repository/";
        }
        return filesRepositoryWeb;
    }

    public String getImagesRepository() {
        if (sito != null && sito.getPathRepository() != null) {
            this.imagesRepository = sito.getPathRepository() +
                    System.getProperty("file.separator") +
                    "images" +
                    System.getProperty("file.separator");
        }
        return imagesRepository;
    }

    public String getImagesRepositoryWeb() {
        if (sito != null) {
            this.imagesRepositoryWeb = sito.getDescrizione() +
                    sito.getPathWeb() +
                    "/images/";
        }
        return imagesRepositoryWeb;
    }

    // ========================================
    // METODI HELPER - SITO
    // ========================================

    public String getUrlBase() {
        return sito != null ? sito.getDescrizione() : "";
    }

    public String getIdSito() {
        return sito != null && sito.getId() != null ? sito.getId().toString() : "-1";
    }

    public void setIdSito(String id) {
        if (sito == null) {
            sito = new Site();
        }
        try {
            sito.setId(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            // Ignora
        }
    }

    // ========================================
    // METODI HELPER - UTENTE
    // ========================================

    public boolean hasAmministratore() {
        return amministratore != null;
    }

    public boolean hasUtente() {
        return utente != null;
    }

    public UtenteEsterno getUtenteLoggato() {
        return utente;
    }

    /**
     * Verifica se c'è un utente loggato (admin o normale)
     */
    public boolean isLogged() {
        return amministratore != null || utente != null;
    }

    /**
     * Ottiene username dell'utente loggato
     */
    public String getUsername() {
        UtenteEsterno u = getUtenteLoggato();
        return u != null ? u.getUsername() : null;
    }

    /**
     * Ottiene campo l2 dell'utente loggato
     */
    public String getUserL2() {
        UtenteEsterno u = getUtenteLoggato();
        return u != null ? u.getL2() : null;
    }

    // ========================================
    // METODI HELPER - ROUTING
    // ========================================

    /**
     * Verifica se il sito corrente è pubblico
     */
    public boolean isSitoPublico() {
        return sito != null && sito.isPublic();
    }

    /**
     * Verifica se il sito corrente richiede autenticazione
     */
    public boolean isSitoProtetto() {
        return sito != null && sito.requiresAuth();
    }

    /**
     * Ottiene il template folder per il sito pubblico
     */
    public String getPublicTemplateFolder() {
        return sito != null ? sito.getPublicTemplateFolder() : "site01";
    }
}