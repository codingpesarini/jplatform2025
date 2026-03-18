package com.studiodomino.jplatform.shared.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.studiodomino.jplatform.cms.entity.DatiBase;
import com.studiodomino.jplatform.cms.entity.Section;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * UtenteEsterno - Utente pubblico del portale (non backoffice)
 * Gestisce autenticazione, profilo, gruppi accesso, navigazione
 */
@Entity
@Table(name = "utenteesterno")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtenteEsterno implements Serializable {

    private static final long serialVersionUID = 1L;

    // ===== IDENTIFICAZIONE =====

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank(message = "Username obbligatorio")
    @Size(max = 100)
    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @NotBlank(message = "Password obbligatoria")
    @Size(max = 100)
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    // ===== ANAGRAFICA =====

    @NotBlank(message = "Nome obbligatorio")
    @Size(max = 100)
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "Cognome obbligatorio")
    @Size(max = 100)
    @Column(name = "cognome", nullable = false, length = 100)
    private String cognome;

    @NotBlank(message = "Email obbligatoria")
    @Email(message = "Email non valida")
    @Size(max = 100)
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Email(message = "PEC non valida")
    @Size(max = 250)
    @Column(name = "pec", length = 250)
    private String pec;

    @Size(max = 10)
    @Column(name = "genere", length = 10)
    @Builder.Default
    private String genere = "M"; // M/F

    @Size(max = 100)
    @Column(name = "telefono", length = 100)
    @Builder.Default
    private String telefono = "";

    @Size(max = 100)
    @Column(name = "telefono2", length = 100)
    @Builder.Default
    private String telefono2 = "";

    // ===== DATE =====

    @Column(name = "datacreazione", length = 100)
    private String datacreazione; // Legacy: stored as string "dd-MM-yyyy"

    @Column(name = "dataultimoaccesso", length = 100)
    private String dataultimoaccesso; // Legacy: stored as string "dd-MM-yyyy"

    @Column(name = "datanascita", length = 250)
    @Builder.Default
    private String datanascita = "01-01-1920"; // Legacy format

    // ===== INDIRIZZO =====

    @Size(max = 250)
    @Column(name = "indirizzo", length = 250)
    @Builder.Default
    private String indirizzo = "";

    @Size(max = 250)
    @Column(name = "cap", length = 250)
    @Builder.Default
    private String cap = "0";

    @Size(max = 250)
    @Column(name = "provincia", length = 250)
    @Builder.Default
    private String provincia = "0";

    @Size(max = 250)
    @Column(name = "comune", length = 250)
    @Builder.Default
    private String comune = "0";

    @Size(max = 250)
    @Column(name = "stato", length = 250)
    @Builder.Default
    private String nazione = "0"; // DB field is "stato"

    @Lob
    @Column(name = "indirizzospedizione", columnDefinition = "TEXT")
    private String indirizzospedizione;

    // ===== DATI FISCALI =====

    @Size(max = 250)
    @Column(name = "codicefiscale", length = 250)
    @Builder.Default
    private String codicefiscale = "0";

    @Size(max = 250)
    @Column(name = "partitaiva", length = 250)
    @Builder.Default
    private String partitaiva = "0";

    @Size(max = 250)
    @Column(name = "societa", length = 250)
    @Builder.Default
    private String societa = "0";

    @Size(max = 250)
    @Column(name = "personagiuridica", length = 250)
    @Builder.Default
    private String personagiuridica = "0";

    // ===== STATO E GRUPPO =====

    @Column(name = "status", length = 100)
    @Builder.Default
    private String status = "1"; // 1=attivo, 0=disabilitato

    /**
     * Gruppo principale (legacy: stringa serializzata)
     * Formato: "(id1);(id2);(id3);"
     */
    @Column(name = "id_gruppo", length = 100)
    @Builder.Default
    private String idGruppo = "";

    // ===== LIVELLI ACCESSO (l1-l10) =====

    @Column(name = "l1", length = 100)
    @Builder.Default
    private String l1 = "1";

    @Column(name = "l2", length = 100)
    @Builder.Default
    private String l2 = "1";

    @Column(name = "l3", length = 100)
    @Builder.Default
    private String l3 = "1";

    @Column(name = "l4", length = 100)
    @Builder.Default
    private String l4 = "1";

    @Column(name = "l5", length = 100)
    @Builder.Default
    private String l5 = "1";

    @Column(name = "l6", length = 100)
    @Builder.Default
    private String l6 = "1";

    @Column(name = "l7", length = 100)
    @Builder.Default
    private String l7 = "1";

    @Column(name = "l8", length = 100)
    @Builder.Default
    private String l8 = "1";

    @Column(name = "l9", length = 100)
    @Builder.Default
    private String l9 = "1";

    @Column(name = "l10", length = 100)
    @Builder.Default
    private String l10 = "1";

    // ===== STRINGHE CONFIGURABILI (s1-s10) =====

    @Column(name = "s1", length = 100)
    @Builder.Default
    private String s1 = "0";

    @Column(name = "s2", length = 100)
    @Builder.Default
    private String s2 = "0";

    @Column(name = "s3", length = 100)
    @Builder.Default
    private String s3 = "0";

    @Column(name = "s4", length = 100)
    @Builder.Default
    private String s4 = "0";

    @Column(name = "s5", length = 100)
    @Builder.Default
    private String s5 = "0";

    @Column(name = "s6", length = 100)
    @Builder.Default
    private String s6 = "0";

    @Column(name = "s7", length = 100)
    @Builder.Default
    private String s7 = "0";

    @Column(name = "s8", length = 100)
    @Builder.Default
    private String s8 = "0";

    @Column(name = "s9", length = 100)
    @Builder.Default
    private String s9 = "0";

    @Column(name = "s10", length = 100)
    @Builder.Default
    private String s10 = "0";

    // ===== NAVIGAZIONE =====

    /**
     * Profilo navigazione: ultimi 12 contenuti visitati
     * Formato: "id1,id2,id3,..."
     */
    @Lob
    @Column(name = "navigationProfile", columnDefinition = "TEXT")
    @Builder.Default
    private String navigationProfile = "";

    // ===== SOCIAL LOGIN =====

    @Column(name = "socialId", length = 250)
    private String socialId;

    @Column(name = "socialName", length = 250)
    private String socialName;

    @Column(name = "socialType", length = 250)
    private String socialType;

    @Column(name = "socialImage", length = 250)
    private String socialImage;

    // ===== PROFILO IMMAGINE =====

    @Column(name = "profileImage")
    @Builder.Default
    private Integer profileImage = 0; // 0=default, 1=custom

    @Lob
    @Column(name = "image", columnDefinition = "LONGTEXT")
    private String image; // Base64 or path

    // ===== EXTRA FIELDS =====

    @Lob
    @Column(name = "sottoscrizioni", columnDefinition = "TEXT")
    private String sottoscrizioni;

    @Column(name = "extra1", length = 250)
    private String extra1;

    @Column(name = "extra2", length = 250)
    private String extra2;

    @Column(name = "extra3", length = 250)
    private String extra3;

    @Column(name = "extra4", length = 250)
    private String extra4;

    @Column(name = "extra5", length = 250)
    private String extra5;

    // ===== TEST FLAGS =====

    @Column(name = "testMx")
    @Builder.Default
    private Integer testMx = 0;

    @Column(name = "testEmail")
    @Builder.Default
    private Integer testEmail = 0;

    // ===== TABLENAME (polymorphism support) =====

    @Column(name = "tablename", length = 50)
    @Builder.Default
    private String tablename = "utenteesterno";

    // ===== TRANSIENT FIELDS (non persistiti) =====

    /**
     * Array di ID gruppi parsato da idGruppo
     */
    @Transient
    private String[] idGruppi;

    /**
     * Sezioni private accessibili da questo utente
     */

    @JsonIgnore
    @Transient
    private List<Section> sezioniFrontPrivate;

    /**
     * Path base per file/immagini (runtime)
     */
    @Transient
    private String filePath;

    /**
     * Messaggi utente (runtime)
     */

    @JsonIgnore
    @Transient
    private List<MessaggioUtente> messaggi;

    /**
     * Activity log (runtime)
     */

    @JsonIgnore
    @Transient
    private List<Activity> activity;

    /**
     * Bacheca contenuti (runtime)
     */

    @JsonIgnore
    @Transient
    private List<DatiBase> bacheca;

    // ===== BUSINESS METHODS =====

    /**
     * Ottieni array ID gruppi da stringa serializzata
     * Formato input: "(1);(2);(3);"
     * Output: ["1", "2", "3"]
     */
    public String[] getIdGruppi() {
        if (idGruppi == null && idGruppo != null && !idGruppo.isEmpty()) {
            idGruppi = stringToArray(idGruppo, ";");
        }
        return idGruppi != null ? idGruppi : new String[]{"0"};
    }

    /**
     * Imposta array ID gruppi e aggiorna stringa serializzata
     */
    public void setIdGruppi(String[] idGruppi) {
        this.idGruppi = idGruppi;
        // TODO: serializzare idGruppi → idGruppo se necessario
    }

    /**
     * Costruisce condizione SQL per filtrare contenuti per gruppi utente
     * Usato per contenuti privati accessibili solo a certi gruppi
     *
     * @return SQL WHERE condition per gruppi + ID utente
     */
    public String GruppiSqlCond() {
        StringBuilder sql = new StringBuilder();
        String[] gruppi = getIdGruppi();

        if (gruppi != null && gruppi.length > 0 && gruppi[0] != null) {
            sql.append(" and (idgruppo like '%(").append(gruppi[0]).append(");%' ");

            for (int i = 1; i < gruppi.length; i++) {
                if (gruppi[i] == null) break;
                sql.append(" or idgruppo like '%(").append(gruppi[i]).append(");%' ");
            }

            sql.append(")");
        }

        // Aggiungi anche contenuti assegnati direttamente all'utente
        sql.append(" or utentiAssociati like '%(").append(this.id).append(");%' ");

        return sql.toString();
    }

    /**
     * Ottieni telefono2 in formato SMS (con prefisso internazionale)
     * @return telefono in formato "39xxxxxxxxxx"
     */
    public String getTelefono2Sms() {
        String tel = getTelefono2();

        if (tel != null && !tel.isEmpty() && tel.length() >= 10) {
            if (tel.length() > 10) {
                tel = tel.substring(tel.length() - 10);
            }
            tel = tel.replace("+39", "");
            tel = "39" + tel;
        }

        return tel;
    }

    /**
     * Ottieni URL avatar utente
     * Priorità: 1) profileImage custom, 2) socialImage, 3) default
     */
    public String getAvatar() {
        if (profileImage != null && profileImage == 0) {
            if (socialImage != null && !socialImage.trim().isEmpty()
                    && !socialImage.equals(" ")) {
                return socialImage;
            }
            return filePath + "imageProfile/nofoto.png";
        }

        return filePath + "imageProfile/pfImage" + id + ".jpg";
    }

    /**
     * Conta nuovi messaggi
     */
    public int getNuoviMessaggi() {
        return messaggi != null ? messaggi.size() : 0;
    }

    /**
     * Verifica se utente è attivo
     */
    public boolean isAttivo() {
        return "1".equals(status);
    }

    /**
     * Verifica se utente ha profilo social
     */
    public boolean hasSocialProfile() {
        return socialId != null && !socialId.isEmpty();
    }

    /**
     * Ottieni stato come intero (per compatibilità legacy)
     */
    public int getStatoAsInt() {
        try {
            return Integer.parseInt(status);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Imposta stato da intero (per compatibilità legacy)
     */
    public void setStatoAsInt(int stato) {
        this.status = String.valueOf(stato);
    }

    // ===== GETTERS CON UPPERCASE (legacy compatibility) =====

    public String getNomeUpperCase() {
        return nome != null ? nome.toUpperCase() : "";
    }

    public String getCognomeUpperCase() {
        return cognome != null ? cognome.toUpperCase() : "";
    }

    public String getEmailUpperCase() {
        return email != null ? email.toUpperCase() : "";
    }

    public String getCodicefiscaleUpperCase() {
        return codicefiscale != null ? codicefiscale.toUpperCase() : "";
    }

    public String getPartitaivaUpperCase() {
        return partitaiva != null ? partitaiva.toUpperCase() : "";
    }

    public String getProvinciaUpperCase() {
        return provincia != null ? provincia.toUpperCase() : "";
    }

    public String getComuneUpperCase() {
        return comune != null ? comune.toUpperCase() : "";
    }

    public String getNazioneUpperCase() {
        return nazione != null ? nazione.toUpperCase() : "";
    }

    // ===== HELPER METHODS =====

    /**
     * Parse stringa serializzata in array
     * Formato: "(val1);(val2);(val3);" → ["val1", "val2", "val3"]
     */
    private String[] stringToArray(String input, String separator) {
        if (input == null || input.isEmpty()) {
            return new String[0];
        }

        List<String> result = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(input, separator);

        while (st.hasMoreTokens()) {
            String val = st.nextToken().trim();
            // Rimuovi parentesi: "(123)" → "123"
            if (val.startsWith("(") && val.endsWith(")")) {
                val = val.substring(1, val.length() - 1);
            }
            result.add(val);
        }

        return result.toArray(new String[0]);
    }

    /**
     * Ottieni livello per numero (1-10)
     */
    public String getL(int numero) {
        return switch (numero) {
            case 1 -> l1 != null ? l1 : "0";
            case 2 -> l2 != null ? l2 : "0";
            case 3 -> l3 != null ? l3 : "0";
            case 4 -> l4 != null ? l4 : "0";
            case 5 -> l5 != null ? l5 : "0";
            case 6 -> l6 != null ? l6 : "0";
            case 7 -> l7 != null ? l7 : "0";
            case 8 -> l8 != null ? l8 : "0";
            case 9 -> l9 != null ? l9 : "0";
            case 10 -> l10 != null ? l10 : "0";
            default -> "0";
        };
    }

    /**
     * Ottieni stringa per numero (1-10)
     */
    public String getS(int numero) {
        return switch (numero) {
            case 1 -> s1;
            case 2 -> s2;
            case 3 -> s3;
            case 4 -> s4;
            case 5 -> s5;
            case 6 -> s6;
            case 7 -> s7;
            case 8 -> s8;
            case 9 -> s9;
            case 10 -> s10;
            default -> "0";
        };
    }

    // ===== EQUALS & HASHCODE =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UtenteEsterno that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "UtenteEsterno{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}