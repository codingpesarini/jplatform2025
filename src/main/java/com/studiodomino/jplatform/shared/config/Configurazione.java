package com.studiodomino.jplatform.shared.config;

import com.studiodomino.jplatform.cms.entity.DatiBase;
import com.studiodomino.jplatform.cms.entity.Section;
import com.studiodomino.jplatform.cms.front.dto.Archivio;
import com.studiodomino.jplatform.cms.front.dto.Breadcrumb;
import com.studiodomino.jplatform.cms.front.dto.Ricerca;
import com.studiodomino.jplatform.cms.front.dto.Tag;
import com.studiodomino.jplatform.shared.dto.FileSystem;
import com.studiodomino.jplatform.shared.entity.*;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Configurazione unificata dell'applicazione.
 * Contiene sia dati di SESSIONE che di REQUEST.
 *
 * SESSIONE: Utente loggato, sito corrente, locale, gruppi/ruoli
 * REQUEST:  Menu, sezioni, contenuti, slot home page, tag cloud
 *
 * Unifica le vecchie classi ConfigurazioneCore + Configurazione
 */
@Data
public class Configurazione implements Serializable {

    private static final long serialVersionUID = 5545802942781497253L;

    // ========================================
    // SITO E LOCALIZZAZIONE (SESSIONE)
    // ========================================

    /**
     * Sito corrente (da database)
     */
    private Site sito;

    /**
     * Locale corrente (es: "it_IT", "en_US")
     */
    private String locale = "it_IT";

    /**
     * URL base del sito
     */
    private String urlBase = "";

    // ========================================
    // REPOSITORY PATHS (SESSIONE)
    // ========================================

    private String filesRepository = "";
    private String filesRepositoryWeb = "";
    private String imagesRepository = "";
    private String imagesRepositoryWeb = "";

    // ========================================
    // COOKIES (SESSIONE)
    // ========================================

    private String langPlatCookieName;
    private String userIDCookieName;
    private String profileIDCookieName;
    private String langPlatCookieValue;
    private String userIDCookieValue;
    private String profileIDCookieValue;

    // ========================================
    // UTENTI (SESSIONE)
    // ========================================

    /**
     * Utente amministratore loggato
     */
    private Utente amministratore;

    /**
     * Utente esterno/pubblico loggato
     */
    private UtenteEsterno utente;

    // ========================================
    // GRUPPI E RUOLI (SESSIONE)
    // ========================================

    private List<Gruppo> gruppiAmministratore = new ArrayList<>();
    private List<Gruppo> gruppi = new ArrayList<>();
    private List<Ruolo> ruoli = new ArrayList<>();

    // ========================================
    // CMS CONFIGURATION (SESSIONE)
    // ========================================

    // Tipologie sezione CMS (es: News, Articoli, Gallery)
    // TODO: Creare entity SectionType quando necessario
    // private List<SectionType> sectionType = new ArrayList<>();

    // Template messaggi email
    // TODO: Creare entity MessaggioEmail quando necessario
    // private List<MessaggioEmail> messaggiEmail = new ArrayList<>();

    // ========================================
    // MAILBOX MANAGEMENT (SESSIONE)
    // ========================================

    private int numeroEmail = 0;
    private String actualMailboxId = "0";
    private String actualMailboxFolderName = "";
    // TODO: Creare entity Account quando necessario
    // private Account actualMailbox;

    // ========================================
    // NAVIGAZIONE (SESSIONE/REQUEST)
    // ========================================

    /**
     * Breadcrumb corrente
     */
    private Breadcrumb breadcrumb;

    /**
     * Breadcrumb di ritorno
     */
    private Breadcrumb breadcrumbBack;

    /**
     * Struttura menu di navigazione (sezioni pubbliche)
     */
    private List<Section> sezioniFront;

    /**
     * Struttura menu privato (sezioni accessibili con autenticazione)
     */
    private List<Section> sezioniFrontPrivate;

    /**
     * Sezione home page
     */
    private Section home;

    /**
     * Sezione correntemente visualizzata
     */
    private Section actualSection;

    /**
     * Documento correntemente visualizzato
     */
    private DatiBase actualDocument;

    /**
     * Contenuti della sezione corrente
     */
    private List<DatiBase> contenutiActualSection;

    // ========================================
    // RICERCA (REQUEST)
    // ========================================

    /**
     * Oggetto ricerca corrente
     */
    private Ricerca ricerca;

    // ========================================
    // PAGINAZIONE (REQUEST)
    // ========================================

    private String pageNumber = "";
    private String startItems = "";
    private String endItems = "";
    private String totalPage = "";
    private String itemsPage = "";
    private String paginationBar = "";

    // ========================================
    // SLOT CONTENUTI HOME PAGE (REQUEST)
    // 01-10
    // ========================================

    private List<DatiBase> contenutiFront01;
    private List<DatiBase> contenutiFront02;
    private List<DatiBase> contenutiFront03;
    private List<DatiBase> contenutiFront04;
    private List<DatiBase> contenutiFront05;
    private List<DatiBase> contenutiFront06;
    private List<DatiBase> contenutiFront07;
    private List<DatiBase> contenutiFront08;
    private List<DatiBase> contenutiFront09;
    private List<DatiBase> contenutiFront10;

    // ========================================
    // EXTRA TAG (REQUEST)
    // 01-10 - Contenuti correlati
    // ========================================

    private List<DatiBase> contenutiExtraTag01;
    private List<DatiBase> contenutiExtraTag02;
    private List<DatiBase> contenutiExtraTag03;
    private List<DatiBase> contenutiExtraTag04;
    private List<DatiBase> contenutiExtraTag05;
    private List<DatiBase> contenutiExtraTag06;
    private List<DatiBase> contenutiExtraTag07;
    private List<DatiBase> contenutiExtraTag08;
    private List<DatiBase> contenutiExtraTag09;
    private List<DatiBase> contenutiExtraTag10;

    // ========================================
    // TAG E RICERCA (REQUEST)
    // ========================================

    /**
     * Tag cloud HTML renderizzato
     */
    private List<Tag> tagCloud = new ArrayList<>();

    /**
     * Contenuti filtrati per tag
     */
    private List<DatiBase> contenutiTag;

    // ========================================
    // ARCHIVIO (REQUEST)
    // ========================================

    /**
     * Struttura archivio per anno/mese
     */
    private List<Archivio> archivio;

    // ========================================
    // PROFILO NAVIGAZIONE UTENTE (REQUEST)
    // ========================================

    /**
     * Contenuti basati sul profilo di navigazione dell'utente
     */
    private List<DatiBase> contenutiProfileID;

    // ========================================
    // FILE SYSTEM (REQUEST)
    // ========================================

    /**
     * Cartella del file manager corrente
     */
    private FileSystem fileManagerFolder;

    // ========================================
    // NAVIGAZIONE TEMPLATE (REQUEST)
    // ========================================

    private String nav01;
    private String nav02;
    private String nav03;
    private String nav04;
    private String nav05;

    // ========================================
    // NAVIGAZIONE E RITORNO UTENTE (REQUEST)
    // ========================================

    private String pageReturn1 = "";
    private String pageReturn2 = "";
    private String pageReturn3 = "";
    private String pageReturn4 = "";
    private String pageReturn5 = "";

    // ========================================
    // ROUTING POST-LOGIN (SESSIONE)
    // ========================================

    /**
     * ID del sito richiesto prima del login (per redirect post-login)
     */
    private Integer requestedSiteId;

    /**
     * Endpoint target dopo il login (calcolato da utente.l2)
     */
    private String postLoginEndpoint;

    // ========================================
    // COSTRUTTORE
    // ========================================

    public Configurazione() {
        super();
        this.sezioniFront = new ArrayList<>();
        this.sezioniFrontPrivate = new ArrayList<>();
    }

    // ========================================
    // METODI DINAMICI PER I PATH
    // ========================================

    /**
     * Calcola dinamicamente il path del repository files
     */
    public String getFilesRepository() {
        if (sito != null && sito.getPathRepository() != null) {
            this.filesRepository = sito.getPathRepository() +
                    System.getProperty("file.separator") +
                    "repository" +
                    System.getProperty("file.separator");
        }
        return filesRepository;
    }

    /**
     * Calcola dinamicamente il path web del repository files
     */
    public String getFilesRepositoryWeb() {
        if (sito != null) {
            this.filesRepositoryWeb = sito.getDescrizione() +
                    sito.getPathWeb() +
                    "/repository/";
        }
        return filesRepositoryWeb;
    }

    /**
     * Calcola dinamicamente il path del repository images
     */
    public String getImagesRepository() {
        if (sito != null && sito.getPathRepository() != null) {
            this.imagesRepository = sito.getPathRepository() +
                    System.getProperty("file.separator") +
                    "images" +
                    System.getProperty("file.separator");
        }
        return imagesRepository;
    }

    /**
     * Calcola dinamicamente il path web del repository images
     */
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

    /**
     * Ottiene l'URL base dal sito
     */
    public String getUrlBase() {
        if (sito != null) {
            this.urlBase = sito.getDescrizione();
        }
        return urlBase;
    }

    /**
     * Ottiene ID sito come String
     */
    public String getIdSito() {
        return sito != null && sito.getId() != null ? sito.getId().toString() : "-1";
    }

    /**
     * Imposta ID sito da String
     */
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

    // ========================================
    // METODI HELPER - UTENTE
    // ========================================

    /**
     * Verifica se c'è un amministratore loggato
     */
    public boolean hasAmministratore() {
        return amministratore != null;
    }

    /**
     * Verifica se c'è un utente esterno loggato
     */
    public boolean hasUtente() {
        return utente != null;
    }

    /**
     * Ottiene l'utente loggato (esterno)
     */
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
        if (amministratore != null) {
            return amministratore.getUsername();
        }
        if (utente != null) {
            return utente.getUsername();
        }
        return null;
    }

    /**
     * Ottiene campo l2 dell'utente loggato
     */
    public String getUserL2() {
        UtenteEsterno u = getUtenteLoggato();
        return u != null ? u.getL2() : null;
    }

    /**
     * Ottiene ID utente loggato
     */
    public Integer getIdUtenteLoggato() {
        if (amministratore != null) {
            return amministratore.getId();
        }
        if (utente != null) {
            return utente.getId();
        }
        return null;
    }

    // ========================================
    // METODI HELPER - RESET
    // ========================================

    /**
     * Resetta tutti i contenuti ExtraTag
     */
    public void restoreExtraTag() {
        this.contenutiExtraTag01 = null;
        this.contenutiExtraTag02 = null;
        this.contenutiExtraTag03 = null;
        this.contenutiExtraTag04 = null;
        this.contenutiExtraTag05 = null;
        this.contenutiExtraTag06 = null;
        this.contenutiExtraTag07 = null;
        this.contenutiExtraTag08 = null;
        this.contenutiExtraTag09 = null;
        this.contenutiExtraTag10 = null;
    }

    /**
     * Resetta tutti gli URL di ritorno
     */
    public void restorePageReturn() {
        this.pageReturn1 = "";
        this.pageReturn2 = "";
        this.pageReturn3 = "";
        this.pageReturn4 = "";
        this.pageReturn5 = "";
    }
}