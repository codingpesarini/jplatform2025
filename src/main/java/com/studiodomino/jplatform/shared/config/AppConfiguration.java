package com.studiodomino.jplatform.shared.config;

import com.studiodomino.jplatform.shared.entity.Site;
import com.studiodomino.jplatform.shared.entity.Utente;
import com.studiodomino.jplatform.shared.entity.Gruppo;  // ← Entity ora!
import com.studiodomino.jplatform.shared.entity.Ruolo;   // ← Entity ora!
import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Configurazione principale dell'applicazione (sessione)
 * Equivalente di ConfigurazioneCore in Struts
 */
@Data
public class AppConfiguration implements Serializable {

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
    private Utente utente;          // Entity Utente loggato (normale)

    // ========== GRUPPI E RUOLI (ora Entity!) ==========
    private List<Gruppo> gruppiAmministratore;  // Entity Gruppo
    private List<Gruppo> gruppi;                // Entity Gruppo
    private List<Ruolo> ruoli;                  // Entity Ruolo

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

    // ========================================
    // METODI HELPER (come prima)
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

    public boolean hasAmministratore() {
        return amministratore != null;
    }

    public boolean hasUtente() {
        return utente != null;
    }

    public Utente getUtenteLoggato() {
        return amministratore != null ? amministratore : utente;
    }
}