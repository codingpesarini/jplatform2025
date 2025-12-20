package com.studiodomino.jplatform.cms.entity;

import com.studiodomino.jplatform.cms.front.dto.Archivio;
import com.studiodomino.jplatform.cms.front.dto.Ricerca;
import com.studiodomino.jplatform.cms.front.dto.Tag;
import com.studiodomino.jplatform.shared.dto.FileSystem;
import com.studiodomino.jplatform.shared.entity.Site;
import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * POJO che contiene la configurazione del portale per una singola richiesta HTTP.
 * Questo oggetto viene creato ad ogni richiesta e contiene tutti gli elementi
 * necessari per renderizzare le pagine del portale.
 *
 * Scope: REQUEST (non viene memorizzato in sessione)
 */
@Data
public class Configurazione implements Serializable {

    private static final long serialVersionUID = 5545802942781497253L;

    // ========================================
    // SITO E PATH
    // ========================================

    private Site sito;

    private String urlBase = "";
    private String filesRepository = "";
    private String filesRepositoryWeb = "";
    private String imagesRepository = "";
    private String imagesRepositoryWeb = "";

    // ========================================
    // NAVIGAZIONE PRINCIPALE
    // ========================================

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
    // SLOT CONTENUTI HOME PAGE (01-10)
    // ========================================

    /**
     * Slot 01 - contenuti configurabili per home page
     */
    private List<DatiBase> contenutiFront01;

    /**
     * Slot 02 - contenuti configurabili per home page
     */
    private List<DatiBase> contenutiFront02;

    /**
     * Slot 03 - contenuti configurabili per home page
     */
    private List<DatiBase> contenutiFront03;

    /**
     * Slot 04 - contenuti configurabili per home page
     */
    private List<DatiBase> contenutiFront04;

    /**
     * Slot 05 - contenuti configurabili per home page
     */
    private List<DatiBase> contenutiFront05;

    /**
     * Slot 06 - contenuti configurabili per home page
     */
    private List<DatiBase> contenutiFront06;

    /**
     * Slot 07 - contenuti configurabili per home page
     */
    private List<DatiBase> contenutiFront07;

    /**
     * Slot 08 - contenuti configurabili per home page
     */
    private List<DatiBase> contenutiFront08;

    /**
     * Slot 09 - contenuti configurabili per home page
     */
    private List<DatiBase> contenutiFront09;

    /**
     * Slot 10 - contenuti configurabili per home page
     */
    private List<DatiBase> contenutiFront10;

    // ========================================
    // EXTRA TAG (01-10)
    // ========================================

    /**
     * Contenuti correlati tramite ExtraTag 01
     */
    private List<DatiBase> contenutiExtraTag01;

    /**
     * Contenuti correlati tramite ExtraTag 02
     */
    private List<DatiBase> contenutiExtraTag02;

    /**
     * Contenuti correlati tramite ExtraTag 03
     */
    private List<DatiBase> contenutiExtraTag03;

    /**
     * Contenuti correlati tramite ExtraTag 04
     */
    private List<DatiBase> contenutiExtraTag04;

    /**
     * Contenuti correlati tramite ExtraTag 05
     */
    private List<DatiBase> contenutiExtraTag05;

    /**
     * Contenuti correlati tramite ExtraTag 06
     */
    private List<DatiBase> contenutiExtraTag06;

    /**
     * Contenuti correlati tramite ExtraTag 07
     */
    private List<DatiBase> contenutiExtraTag07;

    /**
     * Contenuti correlati tramite ExtraTag 08
     */
    private List<DatiBase> contenutiExtraTag08;

    /**
     * Contenuti correlati tramite ExtraTag 09
     */
    private List<DatiBase> contenutiExtraTag09;

    /**
     * Contenuti correlati tramite ExtraTag 10
     */
    private List<DatiBase> contenutiExtraTag10;

    // ========================================
    // TAG E RICERCA
    // ========================================

    /**
     * Tag cloud HTML renderizzato
     */


    private List<Tag> tagCloud = new ArrayList<>();

    /**
     * Contenuti filtrati per tag
     */
    private List<DatiBase> contenutiTag;

    /**
     * Oggetto ricerca corrente
     */
    private Ricerca ricerca;

    // ========================================
    // ARCHIVIO
    // ========================================

    /**
     * Struttura archivio per anno/mese
     */
    private List<Archivio> archivio;

    // ========================================
    // PROFILO NAVIGAZIONE UTENTE
    // ========================================

    /**
     * Contenuti basati sul profilo di navigazione dell'utente
     */
    private List<DatiBase> contenutiProfileID;

    // ========================================
    // FILE SYSTEM
    // ========================================

    /**
     * Cartella del file manager corrente
     */
    private FileSystem fileManagerFolder;

    // ========================================
    // NAVIGAZIONE E RITORNO UTENTE
    // ========================================

    /**
     * URL di ritorno livello 1
     */
    private String pageReturn1 = "";

    /**
     * URL di ritorno livello 2
     */
    private String pageReturn2 = "";

    /**
     * URL di ritorno livello 3
     */
    private String pageReturn3 = "";

    /**
     * URL di ritorno livello 4
     */
    private String pageReturn4 = "";

    /**
     * URL di ritorno livello 5
     */
    private String pageReturn5 = "";

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
        if (sito != null) {
            this.filesRepository = this.sito.getPathRepository()
                    + System.getProperty("file.separator") + "repository"
                    + System.getProperty("file.separator");
        }
        return filesRepository;
    }

    /**
     * Calcola dinamicamente il path web del repository files
     */
    public String getFilesRepositoryWeb() {
        if (sito != null) {
            this.filesRepositoryWeb = (this.sito.getDescrizione()
                    + this.sito.getPathWeb()) + "/repository/";
        }
        return filesRepositoryWeb;
    }

    /**
     * Calcola dinamicamente il path del repository images
     */
    public String getImagesRepository() {
        if (sito != null) {
            this.imagesRepository = this.sito.getPathRepository()
                    + System.getProperty("file.separator") + "images"
                    + System.getProperty("file.separator");
        }
        return imagesRepository;
    }

    /**
     * Calcola dinamicamente il path web del repository images
     */
    public String getImagesRepositoryWeb() {
        if (sito != null) {
            this.imagesRepositoryWeb = (sito.getDescrizione()
                    + sito.getPathWeb()) + "/images/";
        }
        return imagesRepositoryWeb;
    }

    /**
     * Ottiene l'URL base dal sito
     */
    public String getUrlBase() {
        return sito != null ? sito.getDescrizione() : urlBase;
    }

    // ========================================
    // METODI HELPER
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