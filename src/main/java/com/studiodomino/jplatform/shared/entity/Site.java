package com.studiodomino.jplatform.shared.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

@Entity
@Table(name = "site")
@Data
public class Site implements Serializable {

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
    @Column(name = "contenutiFront1")
    private String contenutiFront1;

    @Column(name = "contenutiOrdineFront1")
    private String contenutiOrdineFront1;

    @Column(name = "maxContenutiFront1")
    private String maxContenutiFront1;

    @Column(name = "contenutiFront2")
    private String contenutiFront2;

    @Column(name = "contenutiOrdineFront2")
    private String contenutiOrdineFront2;

    @Column(name = "maxContenutiFront2")
    private String maxContenutiFront2;

    @Column(name = "contenutiFront3")
    private String contenutiFront3;

    @Column(name = "contenutiOrdineFront3")
    private String contenutiOrdineFront3;

    @Column(name = "maxContenutiFront3")
    private String maxContenutiFront3;

    @Column(name = "contenutiFront4")
    private String contenutiFront4;

    @Column(name = "contenutiOrdineFront4")
    private String contenutiOrdineFront4;

    @Column(name = "maxContenutiFront4")
    private String maxContenutiFront4;

    @Column(name = "contenutiFront5")
    private String contenutiFront5;

    @Column(name = "contenutiOrdineFront5")
    private String contenutiOrdineFront5;

    @Column(name = "maxContenutiFront5")
    private String maxContenutiFront5;

    @Column(name = "contenutiFront6")
    private String contenutiFront6;

    @Column(name = "contenutiOrdineFront6")
    private String contenutiOrdineFront6;

    @Column(name = "maxContenutiFront6")
    private String maxContenutiFront6;

    @Column(name = "contenutiFront7")
    private String contenutiFront7;

    @Column(name = "contenutiOrdineFront7")
    private String contenutiOrdineFront7;

    @Column(name = "maxContenutiFront7")
    private String maxContenutiFront7;

    @Column(name = "contenutiFront8")
    private String contenutiFront8;

    @Column(name = "contenutiOrdineFront8")
    private String contenutiOrdineFront8;

    @Column(name = "maxContenutiFront8")
    private String maxContenutiFront8;

    @Column(name = "contenutiFront9")
    private String contenutiFront9;

    @Column(name = "contenutiOrdineFront9")
    private String contenutiOrdineFront9;

    @Column(name = "maxContenutiFront9")
    private String maxContenutiFront9;

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
            default -> "Gestionale " + (accesso );
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
}