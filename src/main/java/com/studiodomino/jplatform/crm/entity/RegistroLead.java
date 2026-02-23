package com.studiodomino.jplatform.crm.entity;

import com.studiodomino.jplatform.shared.entity.Utente;
import com.studiodomino.jplatform.shared.entity.UtenteEsterno;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity JPA per la tabella 'registrolead'.
 * Conversione da RegistroLead.java (Struts ActionForm) a Spring Boot JPA Entity.
 *
 * Campi transient (non salvati su DB):
 *   - utente, amministratore → caricati dal service dopo la query
 *   - messaggioEmail, commento → caricati dal service in base allo store
 */
@Entity
@Table(name = "registrolead")
@Getter
@Setter
@NoArgsConstructor
public class RegistroLead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Direzione: "e" = entrata, "u" = uscita
    @Column(name = "direzione", length = 1)
    private String direzione = "e";

    @Column(name = "idutente")
    private int idutente = 0;

    @Column(name = "idleadstore")
    private int idleadstore = 0;

    @Column(name = "idamministratore")
    private int idamministratore = 0;

    // Store: "commenti", "emailstore", "diretto", "Sms", "Email", ecc.
    @Column(name = "store", length = 100)
    private String store = "";

    // Stato: 0=Non gestito, 1=Da richiamare, 2=Inviare email,
    //        3=In lavorazione, 4=Completato, 5=Annullato
    @Column(name = "stato", length = 10)
    private String stato = "0";

    @Column(name = "notalead", columnDefinition = "TEXT")
    private String notalead = "";

    // Metatag salvato come stringa separata da ";"
    // Vecchio: stringToArray(metatagString, ";")
    @Column(name = "metatag", columnDefinition = "TEXT")
    private String metatagString = "";

    @Column(name = "data", length = 20)
    private String data = "";

    @Column(name = "ora", length = 10)
    private String ora = "";

    // Campi liberi l1..l10
    @Column(name = "l1", length = 255)  private String l1 = "";
    @Column(name = "l2", length = 255)  private String l2 = "";
    @Column(name = "l3", length = 255)  private String l3 = "";
    @Column(name = "l4", length = 255)  private String l4 = "";
    @Column(name = "l5", length = 255)  private String l5 = "";
    @Column(name = "l6", length = 255)  private String l6 = "";
    @Column(name = "l7", length = 255)  private String l7 = "";
    @Column(name = "l8", length = 255)  private String l8 = "";
    @Column(name = "l9", length = 255)  private String l9 = "";
    @Column(name = "l10", length = 255) private String l10 = "";

    // Log HTML delle operazioni sul lead
    @Column(name = "log", columnDefinition = "TEXT")
    private String log = "";

    // =====================================================================
    // CAMPI TRANSIENT — non persistiti, caricati dal service
    // =====================================================================

    /** Utente esterno associato al lead. Caricato dal RegistroLeadService. */
    @Transient
    private UtenteEsterno utente;

    /** Amministratore assegnatario. Caricato dal RegistroLeadService. */
    @Transient
    private Utente amministratore;

    /** Email associata al lead (store='emailstore'). Caricata dal service. */
    @Transient
    private Object messaggioEmail;

    /** Commento associato al lead (store='commenti'). Caricato dal service. */
    @Transient
    private Object commento;

    // =====================================================================
    // UTILITY
    // =====================================================================

    /**
     * Converte il metatagString in array.
     * Vecchio: stringToArray(metatagString, ";")
     */
    public String[] getMetatag() {
        if (metatagString == null || metatagString.isBlank()) return new String[0];
        return metatagString.split(";");
    }

    public void setMetatag(String[] metatag) {
        if (metatag == null) {
            this.metatagString = "";
        } else {
            this.metatagString = String.join(";", metatag);
        }
    }
}