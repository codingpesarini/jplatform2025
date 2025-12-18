package com.studiodomino.jplatform.cms.entity;

import com.studiodomino.jplatform.shared.config.Ricerca;
import com.studiodomino.jplatform.shared.entity.Gruppo;
import com.studiodomino.jplatform.shared.entity.Ruolo;
import com.studiodomino.jplatform.shared.entity.Site;
import com.studiodomino.jplatform.shared.entity.Utente;
import com.studiodomino.jplatform.cms.front.dto.Breadcrumb;
import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * POJO che contiene la configurazione core dell'applicazione.
 * Viene memorizzato in sessione HTTP.
 */
@Data
public class ConfigurazioneCore implements Serializable {

    private static final long serialVersionUID = 5545802942781497253L;

    // ========== CONFIGURAZIONE SITO ==========

    private Site sito;

    private String locale = "it_IT";

    private String urlBase = "";

    // ========== REPOSITORY PATHS ==========

    private String filesRepository = "";
    private String filesRepositoryWeb = "";
    private String imagesRepository = "";
    private String imagesRepositoryWeb = "";

    // ========== COOKIES ==========

    private String langPlatCookieName = null;
    private String userIDCookieName = null;
    private String profileIDCookieName = null;

    private String langPlatCookieValue = null;
    private String userIDCookieValue = null;
    private String profileIDCookieValue = null;

    // ========== UTENTI E PERMESSI ==========

    private Utente amministratore;
    private ArrayList<Gruppo> gruppiAmministratore;
    private UtenteEsterno utente;
    private ArrayList<Gruppo> gruppi;
    private ArrayList<Ruolo> ruoli;

    // ========== CONFIGURAZIONE CMS ==========

    private ArrayList<SectionType> sectionType;
    private ArrayList<MessaggioEmail> messaggiEmail;
    private Ricerca ricerca;

    // ========== GESTIONE MAILBOX ==========

    private int numeroEmail;
    private String actualMailboxId = "0";
    private String actualMailboxFolderName = "";
    private Account actualMailbox;

    // ========== STRUTTURA DI NAVIGAZIONE ==========

    private Breadcrumb breadcrumb;
    private Breadcrumb breadcrumbBack;

    // ========== ELEMENTI PER LA PAGINAZIONE ==========

    private String pageNumber = "";
    private String startItems = "";
    private String endItems = "";
    private String totalPage = "";
    private String itemsPage = "";
    private String paginationBar = "";

    // ========== ELEMENTI PER LA NAVIGAZIONE E I TEMPLATE ==========

    private String nav01;
    private String nav02;
    private String nav03;
    private String nav04;
    private String nav05;

    // ========== COSTRUTTORE ==========

    public ConfigurazioneCore() {
        super();
    }

    // ========== METODI DINAMICI PER I PATH ==========

    /**
     * Calcola dinamicamente il path del repository files
     */
    public String getFilesRepository() {
        this.filesRepository = this.getSito().getPathRepository()
                + System.getProperty("file.separator") + "repository"
                + System.getProperty("file.separator");
        return filesRepository;
    }

    /**
     * Calcola dinamicamente il path web del repository files
     */
    public String getFilesRepositoryWeb() {
        this.filesRepositoryWeb = (this.getSito().getDescrizione()
                + this.getSito().getPathWeb()) + "/repository/";
        return filesRepositoryWeb;
    }

    /**
     * Calcola dinamicamente il path del repository images
     */
    public String getImagesRepository() {
        this.imagesRepository = this.getSito().getPathRepository()
                + System.getProperty("file.separator") + "images"
                + System.getProperty("file.separator");
        return imagesRepository;
    }

    /**
     * Calcola dinamicamente il path web del repository images
     */
    public String getImagesRepositoryWeb() {
        this.imagesRepositoryWeb = (sito.getDescrizione()
                + sito.getPathWeb()) + "/images/";
        return imagesRepositoryWeb;
    }

    /**
     * Ottiene l'URL base dal sito
     */
    public String getUrlBase() {
        return this.sito.getDescrizione();
    }

    /**
     * Ottiene l'ID del sito
     */
    public String getIdSito() {
        return sito.getId();
    }

    /**
     * Imposta l'ID del sito
     */
    public void setIdSito(String id) {
        this.sito.setId(id);
    }
}