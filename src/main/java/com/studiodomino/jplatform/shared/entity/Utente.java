package com.studiodomino.jplatform.shared.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "utente")
@Data
public class Utente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;
    private String password;
    private String nome;
    private String cognome;
    private String email;
    private String pec;
    private String telefono;
    private String telefono2;
    private String incarico;
    private String indirizzo;
    private String idsite;

    // ========== LIVELLI DI ACCESSO (l1-l10) ==========
    private String l1;
    private String l2;
    private String l3;
    private String l4;
    private String l5;
    private String l6;
    private String l7;
    private String l8;
    private String l9;
    private String l10;

    // ========== RUOLI (role1-role20) ==========
    private String role1;
    private String role2;
    private String role3;
    private String role4;
    private String role5;
    private String role6;
    private String role7;
    private String role8;
    private String role9;
    private String role10;
    private String role11;
    private String role12;
    private String role13;
    private String role14;
    private String role15;
    private String role16;
    private String role17;
    private String role18;
    private String role19;
    private String role20;

    // ========== STATO E ACCESSI ==========
    private Integer statoaccesso = 0;
    private String numeroaccessi = "0";
    private String datacreazione;
    private String dataultimoaccesso;
    private String ipultimoaccesso;
    private String idgruppi;

    // ========== DATI ANAGRAFICI ==========
    private String codicefiscale;
    private String datanascita;
    private String genere;
    private String provincia;
    private String comune;
    private String cap;
    private String societa;
    private String partitaiva;

    @Column(name = "stato")
    private String nazione;

    private String personagiuridica;
    private String indirizzospedizione;

    // ========== PROFILO ==========
    @Column(name = "navigationProfile")
    private String navigationProfile;

    @Column(name = "profileImage")
    private Integer profileImage = 0;

    @Column(name = "socialId")
    private String socialId;

    @Column(name = "socialName")
    private String socialName;

    @Column(name = "socialType")
    private String socialType;

    @Column(name = "socialImage")
    private String socialImage;

    private String sottoscrizioni;
    private String extra1;
    private String extra2;
    private String extra3;
    private String extra4;
    private String extra5;

    // ========== ACCOUNT EMAIL/PEC ==========
    private String idaccountpec;
    private String idaccountemail;

    // ========== RUOLI STRING ==========
    @Column(name = "ruoli1String")
    private String ruoli1String;

    @Column(name = "ruoli2String")
    private String ruoli2String;

    @Column(name = "ruoli3String")
    private String ruoli3String;

    @Column(name = "ruoli4String")
    private String ruoli4String;

    @Column(name = "ruoli5String")
    private String ruoli5String;

    // ============================================
    // CAMPI TRANSIENT (non salvati nel DB)
    // ============================================

    @Transient
    private String[] idGruppiArray;

    @Transient
    private String[] ruoliA1;

    @Transient
    private String[] ruoliA2;

    @Transient
    private String[] ruoliA3;

    @Transient
    private String[] ruoliA4;

    @Transient
    private String[] ruoliA5;

    @Transient
    private String avatar;

    @Transient
    private String filePath;

    @Transient
    private int nuoviMessaggi = 0;

    // ============================================
    // METODI HELPER
    // ============================================

    /**
     * Ritorna tutti i ruoli come lista
     */
    @Transient
    public List<String> getRoles() {
        List<String> roles = new ArrayList<>();
        roles.add(role1 != null ? role1 : "0");
        roles.add(role2 != null ? role2 : "0");
        roles.add(role3 != null ? role3 : "0");
        roles.add(role4 != null ? role4 : "0");
        roles.add(role5 != null ? role5 : "0");
        roles.add(role6 != null ? role6 : "0");
        roles.add(role7 != null ? role7 : "0");
        roles.add(role8 != null ? role8 : "0");
        roles.add(role9 != null ? role9 : "0");
        roles.add(role10 != null ? role10 : "0");
        return roles;
    }

    /**
     * Verifica se è amministratore
     */
    @Transient
    public boolean isAmministratore() {
        return "a".equals(role1) || "s".equals(role1);
    }

    /**
     * Verifica se è super admin
     */
    @Transient
    public boolean isSuperAdmin() {
        return "s".equals(role1);
    }

    /**
     * Ottieni nome completo
     */
    @Transient
    public String getNomeCompleto() {
        return (nome != null ? nome : "") + " " + (cognome != null ? cognome : "");
    }

    /**
     * Ritorna il path dell'avatar
     */
    @Transient
    public String getAvatar() {
        if (profileImage == null || profileImage == 0) {
            if (socialImage != null && !socialImage.trim().isEmpty()) {
                return socialImage;
            }
            return (filePath != null ? filePath : "") + "imageProfile/nofoto.png";
        }
        return (filePath != null ? filePath : "") + "imageProfile/usImage" + id + ".jpg";
    }

    /**
     * Verifica se ha accesso a un livello specifico
     */
    @Transient
    public boolean hasLivello(int numero) {
        // Se è admin, ha accesso a tutto
        if (isAmministratore()) {
            return true;
        }

        String livello = switch (numero) {
            case 1 -> l1;
            case 2 -> l2;
            case 3 -> l3;
            case 4 -> l4;
            case 5 -> l5;
            case 6 -> l6;
            case 7 -> l7;
            case 8 -> l8;
            case 9 -> l9;
            case 10 -> l10;
            default -> null;
        };

        return livello != null && !livello.isEmpty() && !"0".equals(livello);
    }

    /**
     * Verifica se ha un ruolo specifico
     */
    @Transient
    public boolean hasRole(int numero) {
        // Se è admin, ha tutti i ruoli
        if (isAmministratore()) {
            return true;
        }

        String role = switch (numero) {
            case 1 -> role1;
            case 2 -> role2;
            case 3 -> role3;
            case 4 -> role4;
            case 5 -> role5;
            case 6 -> role6;
            case 7 -> role7;
            case 8 -> role8;
            case 9 -> role9;
            case 10 -> role10;
            case 11 -> role11;
            case 12 -> role12;
            case 13 -> role13;
            case 14 -> role14;
            case 15 -> role15;
            case 16 -> role16;
            case 17 -> role17;
            case 18 -> role18;
            case 19 -> role19;
            case 20 -> role20;
            default -> null;
        };

        return role != null && !role.isEmpty() && !"0".equals(role) && !"-1".equals(role);
    }
}