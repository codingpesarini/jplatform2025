package com.studiodomino.jplatform.cms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * Entity che rappresenta il collegamento tra documenti (Contents) e allegati.
 * Mappato sulla tabella 'docallegati' del database.
 *
 * Questa è una tabella di join che permette la relazione many-to-many
 * tra contenuti e allegati, con metadati aggiuntivi (ordine, user, data).
 */
@Entity
@Table(name = "docallegati")
@Data
@NoArgsConstructor
public class DocAllegati implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /**
     * ID del documento (dalla tabella contents)
     */
    @Column(name = "iddocumento")
    private Integer idDocumento;

    /**
     * ID dell'allegato (dalla tabella allegati)
     */
    @Column(name = "idallegato")
    private Integer idAllegato;

    /**
     * Username che ha collegato l'allegato
     */
    @Column(name = "user")
    private String user;

    /**
     * Data di inserimento del collegamento
     */
    @Column(name = "datainsert")
    private String dataInsert;

    /**
     * ID utente che ha collegato l'allegato
     */
    @Column(name = "iduser")
    private String idUser;

    /**
     * Ordinamento degli allegati nel documento
     */
    @Column(name = "ordine")
    private Integer ordine = 0;

    // ========================================
    // RELAZIONI (opzionali, se serve lazy loading)
    // ========================================

    /**
     * Relazione con l'allegato (opzionale)
     * Nota: Allegato deve essere convertito in JPA Entity
     */
    @Transient
    private Allegato allegato;

    /**
     * Relazione con il contenuto (opzionale)
     */
    @Transient
    private Content content;

    // ========================================
    // COSTRUTTORI
    // ========================================

    public DocAllegati(Integer idDocumento, Integer idAllegato, String user,
                       String dataInsert, String idUser, Integer ordine) {
        this.idDocumento = idDocumento;
        this.idAllegato = idAllegato;
        this.user = user;
        this.dataInsert = dataInsert;
        this.idUser = idUser;
        this.ordine = ordine;
    }
}