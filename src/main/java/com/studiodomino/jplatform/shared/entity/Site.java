package com.studiodomino.jplatform.shared.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

/**
 * Site Entity - Configurazione siti multi-tenant
 * Gestisce configurazione, email, SMS, PEC, front-end slots
 */
@Entity
@Table(name = "site")
@Data
public class Site implements Serializable {

    private static final long serialVersionUID = 1L;

    // ========== IDENTIFICAZIONE ==========

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "type")
    private String type;

    private String status;
    private String data;
    private String descrizione;

    @Column(name = "accesso")
    private Integer accesso = 1;  // Campo chiave per routing

    @Column(name = "check")
    private String check = "0";

    // ========== KEYWORDS E CSS ==========

    @Column(columnDefinition = "TEXT")
    private String keywords;

    @Column(columnDefinition = "TEXT")
    private String css;

    // ========== EMAIL CONFIGURATION ==========

    private String emailpop;
    private String emailsmtp;
    private String emailsmtpauth;
    private String emailintestazione;
    private String emailrisposta;
    private String newsletteremail;
    private String newsletteruser;
    private String newsletterpassword;

    @Column(name = "idaccountemail")
    private String idAccountEmail;

    // ========== SMS CONFIGURATION ==========

    @Column(name = "sms_status")
    private String smsStatus = "";

    @Column(name = "sms_url")
    private String smsUrl;

    @Column(name = "sms_login")
    private String smsLogin;

    @Column(name = "sms_password")
    private String smsPassword;

    @Column(name = "sms_sender")
    private String smsSender;

    // ========== PEC CONFIGURATION ==========

    @Column(name = "pecEmail")
    private String pecEmail = "";

    @Column(name = "pecIntestazione")
    private String pecIntestazione = "";

    @Column(name = "pecUsername")
    private String pecUsername = "";

    @Column(name = "pecPassword")
    private String pecPassword = "";

    @Column(name = "pecPop3")
    private String pecPop3 = "";

    @Column(name = "pecPopPort")
    private String pecPopPort = "";

    @Column(name = "pecSmtp")
    private String pecSmtp = "";

    @Column(name = "pecSmtpPort")
    private String pecSmtpPort = "";

    @Column(name = "pecHtml1", columnDefinition = "TEXT")
    private String pecHtml1;

    @Column(name = "pecHtml2", columnDefinition = "TEXT")
    private String pecHtml2;

    @Column(name = "pecHtml3", columnDefinition = "TEXT")
    private String pecHtml3;

    @Column(name = "pecHtml4", columnDefinition = "TEXT")
    private String pecHtml4;

    @Column(name = "pecHtml5", columnDefinition = "TEXT")
    private String pecHtml5;

    @Column(name = "idaccountpec")
    private String idAccountPec;

    // ========== PROXY CONFIGURATION ==========

    private String proxy = "0";
    private String proxyip = "0";
    private String proxyport = "0";
    private String proxyuser = "0";
    private String proxypassword = "0";

    // ========== SERVICES CONFIGURATION ==========

    private String servicesserver;
    private String servicesuser;
    private String servicespassword;

    // ========== SERVIZI (1-10) ==========

    private String servizi1 = "0";
    private String servizi2 = "0";
    private String servizi3 = "0";
    private String servizi4 = "0";
    private String servizi5 = "0";
    private String servizi6 = "0";
    private String servizi7 = "0";
    private String servizi8 = "0";
    private String servizi9 = "0";
    private String servizi10 = "0";

    // ========== CAMPI LIBERI (1-10) ==========

    private String libero1 = "0";
    private String libero2 = "0";
    private String libero3 = "0";
    private String libero4 = "0";
    private String libero5 = "0";
    private String libero6 = "0";
    private String libero7 = "0";
    private String libero8 = "0";
    private String libero9 = "0";
    private String libero10 = "0";

    // ========== ANALYTICS & TRACKING ==========

    @Column(columnDefinition = "TEXT")
    private String stat;

    @Column(columnDefinition = "TEXT")
    private String analytics;

    @Column(columnDefinition = "TEXT")
    private String adsense;

    // ========== HTML BLOCKS (1-5) ==========

    @Column(columnDefinition = "TEXT")
    private String html1;

    @Column(columnDefinition = "TEXT")
    private String html2;

    @Column(columnDefinition = "TEXT")
    private String html3;

    @Column(columnDefinition = "TEXT")
    private String html4;

    @Column(columnDefinition = "TEXT")
    private String html5;

    // ========== TEXT BLOCKS (1-5) ==========

    @Column(columnDefinition = "TEXT")
    private String text1;

    @Column(columnDefinition = "TEXT")
    private String text2;

    @Column(columnDefinition = "TEXT")
    private String text3;

    @Column(columnDefinition = "TEXT")
    private String text4;

    @Column(columnDefinition = "TEXT")
    private String text5;

    // ========== PATHS ==========

    @Column(name = "pathRepository", columnDefinition = "TEXT")
    private String pathRepository;

    @Column(name = "pathWeb", columnDefinition = "TEXT")
    private String pathWeb;

    @Column(columnDefinition = "TEXT")
    private String path1;

    @Column(columnDefinition = "TEXT")
    private String path2;

    @Column(columnDefinition = "TEXT")
    private String path3;

    @Column(columnDefinition = "TEXT")
    private String path4;

    @Column(columnDefinition = "TEXT")
    private String path5;

    // ========== CMS FRONT CONFIGURATION (1-10) ==========

    // Slot 01
    @Column(name = "contenutiFront1")
    private String contenutiFront01;

    @Column(name = "contenutiOrdineFront1")
    private String contenutiOrdineFront01;

    @Column(name = "maxContenutiFront1")
    private String maxContenutiFront01;

    // Slot 02
    @Column(name = "contenutiFront2")
    private String contenutiFront02;

    @Column(name = "contenutiOrdineFront2")
    private String contenutiOrdineFront02;

    @Column(name = "maxContenutiFront2")
    private String maxContenutiFront02;

    // Slot 03
    @Column(name = "contenutiFront3")
    private String contenutiFront03;

    @Column(name = "contenutiOrdineFront3")
    private String contenutiOrdineFront03;

    @Column(name = "maxContenutiFront3")
    private String maxContenutiFront03;

    // Slot 04
    @Column(name = "contenutiFront4")
    private String contenutiFront04;

    @Column(name = "contenutiOrdineFront4")
    private String contenutiOrdineFront04;

    @Column(name = "maxContenutiFront4")
    private String maxContenutiFront04;

    // Slot 05
    @Column(name = "contenutiFront5")
    private String contenutiFront05;

    @Column(name = "contenutiOrdineFront5")
    private String contenutiOrdineFront05;

    @Column(name = "maxContenutiFront5")
    private String maxContenutiFront05;

    // Slot 06
    @Column(name = "contenutiFront6")
    private String contenutiFront06;

    @Column(name = "contenutiOrdineFront6")
    private String contenutiOrdineFront06;

    @Column(name = "maxContenutiFront6")
    private String maxContenutiFront06;

    // Slot 07
    @Column(name = "contenutiFront7")
    private String contenutiFront07;

    @Column(name = "contenutiOrdineFront7")
    private String contenutiOrdineFront07;

    @Column(name = "maxContenutiFront7")
    private String maxContenutiFront07;

    // Slot 08
    @Column(name = "contenutiFront8")
    private String contenutiFront08;

    @Column(name = "contenutiOrdineFront8")
    private String contenutiOrdineFront08;

    @Column(name = "maxContenutiFront8")
    private String maxContenutiFront08;

    // Slot 09
    @Column(name = "contenutiFront9")
    private String contenutiFront09;

    @Column(name = "contenutiOrdineFront9")
    private String contenutiOrdineFront09;

    @Column(name = "maxContenutiFront9")
    private String maxContenutiFront09;

    // Slot 10
    @Column(name = "contenutiFront10")
    private String contenutiFront10;

    @Column(name = "contenutiOrdineFront10")
    private String contenutiOrdineFront10;

    @Column(name = "maxContenutiFront10")
    private String maxContenutiFront10;

    // ========== LEGACY FIELDS ==========

    private String idnewsprima;
    private String idarticoloprima;
    private String numeroarticoliprima;

    // ========== LANGUAGE ==========

    private String lang;

    private String ok;

    // ========================================
    // CAMPI TRANSIENT (non salvati nel DB)
    // ========================================

    @Transient
    private String verificaCredito;

    @Transient
    private Integer counter = 0;

    // ========================================
    // METODI HELPER
    // ========================================

    /**
     * Verifica se SMS è abilitato
     */
    @Transient
    public boolean isSmsEnabled() {
        return "1".equals(smsStatus);
    }

    /**
     * Verifica se proxy è abilitato
     */
    @Transient
    public boolean isProxyEnabled() {
        return "1".equals(proxy);
    }

    /**
     * Ottieni descrizione tipo accesso
     */
    @Transient
    public String getAccessoDescrizione() {
        if (accesso == null) return "Non assegnato";

        return switch (accesso) {
            case 0 -> "Non assegnato";
            case 1 -> "Pannello amministrazione base";
            case 2 -> "Interfaccia portale web";
            case 3 -> "Gestione Protocollo";
            case 4 -> "Borse di studio";
            case 5 -> "Servizio mensa";
            case 6 -> "Alloggi";
            case 7 -> "Workflow";
            case 8 -> "CRM";
            default -> "Gestionale " + (accesso - 3);
        };
    }

    /**
     * Verifica se il sito è pubblico (accesso = 2)
     */
    @Transient
    public boolean isPublic() {
        return accesso != null && accesso == 2;
    }

    /**
     * Verifica se il sito richiede autenticazione (accesso = 1)
     */
    @Transient
    public boolean requiresAuth() {
        return accesso != null && accesso == 1;
    }

    /**
     * Ottiene il template folder per questo sito pubblico
     * Usa path2 se valorizzato, altrimenti default "site01"
     */
    @Transient
    public String getPublicTemplateFolder() {
        if (path2 != null && !path2.isEmpty()) {
            return path2;
        }
        return "site01"; // Default
    }

    /**
     * Ottiene il codice del sito (usa type)
     */
    @Transient
    public String getCodice() {
        return type != null ? type : "default";
    }

    /**
     * Ottieni contenutiFront per numero slot (1-10)
     */
    @Transient
    public String getContenutiFrontBySlot(int slot) {
        return switch (slot) {
            case 1 -> contenutiFront01;
            case 2 -> contenutiFront02;
            case 3 -> contenutiFront03;
            case 4 -> contenutiFront04;
            case 5 -> contenutiFront05;
            case 6 -> contenutiFront06;
            case 7 -> contenutiFront07;
            case 8 -> contenutiFront08;
            case 9 -> contenutiFront09;
            case 10 -> contenutiFront10;
            default -> null;
        };
    }

    /**
     * Ottieni contenutiOrdineFront per numero slot (1-10)
     */
    @Transient
    public String getContenutiOrdineFrontBySlot(int slot) {
        return switch (slot) {
            case 1 -> contenutiOrdineFront01;
            case 2 -> contenutiOrdineFront02;
            case 3 -> contenutiOrdineFront03;
            case 4 -> contenutiOrdineFront04;
            case 5 -> contenutiOrdineFront05;
            case 6 -> contenutiOrdineFront06;
            case 7 -> contenutiOrdineFront07;
            case 8 -> contenutiOrdineFront08;
            case 9 -> contenutiOrdineFront09;
            case 10 -> contenutiOrdineFront10;
            default -> null;
        };
    }

    /**
     * Ottieni maxContenutiFront per numero slot (1-10)
     */
    @Transient
    public String getMaxContenutiFrontBySlot(int slot) {
        return switch (slot) {
            case 1 -> maxContenutiFront01;
            case 2 -> maxContenutiFront02;
            case 3 -> maxContenutiFront03;
            case 4 -> maxContenutiFront04;
            case 5 -> maxContenutiFront05;
            case 6 -> maxContenutiFront06;
            case 7 -> maxContenutiFront07;
            case 8 -> maxContenutiFront08;
            case 9 -> maxContenutiFront09;
            case 10 -> maxContenutiFront10;
            default -> null;
        };
    }

    /**
     * Ottieni libero per numero (1-10)
     */
    @Transient
    public String getLibero(int numero) {
        return switch (numero) {
            case 1 -> libero1;
            case 2 -> libero2;
            case 3 -> libero3;
            case 4 -> libero4;
            case 5 -> libero5;
            case 6 -> libero6;
            case 7 -> libero7;
            case 8 -> libero8;
            case 9 -> libero9;
            case 10 -> libero10;
            default -> "0";
        };
    }

    /**
     * Ottieni servizio per numero (1-10)
     */
    @Transient
    public String getServizio(int numero) {
        return switch (numero) {
            case 1 -> servizi1;
            case 2 -> servizi2;
            case 3 -> servizi3;
            case 4 -> servizi4;
            case 5 -> servizi5;
            case 6 -> servizi6;
            case 7 -> servizi7;
            case 8 -> servizi8;
            case 9 -> servizi9;
            case 10 -> servizi10;
            default -> "0";
        };
    }

    /**
     * Verifica se servizio è abilitato
     */
    @Transient
    public boolean isServizioAbilitato(int numero) {
        String servizio = getServizio(numero);
        return "1".equals(servizio);
    }
}