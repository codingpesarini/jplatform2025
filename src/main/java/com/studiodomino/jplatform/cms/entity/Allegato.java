package com.studiodomino.jplatform.cms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity che rappresenta un allegato/file nel sistema.
 * Mappato sulla tabella 'allegati' del database.
 */
@Entity
@Table(name = "allegati")
@Data
@NoArgsConstructor
public class Allegato implements Serializable {

    private static final long serialVersionUID = -5699776620223226569L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_allegato")
    private Integer id;

    @Column(name = "idfolder")
    private Integer idFolder = -1;

    @Column(name = "version")
    private String version = "0";

    @Column(name = "idversion")
    private String idVersion = "0";

    @Column(name = "nome_file")
    private String nome = "Nessun nome";

    @Column(name = "size_file")
    private String size = "0";

    @Column(name = "type_file")
    private String type = "notype";

    @Column(name = "relative_path_file")
    private String path = "";

    @Column(name = "improntaSHA1")
    private String improntaSHA1 = "";

    @Column(name = "tipoallegato")
    private Long tipoAllegato = 0L;

    @Column(name = "anno")
    private String anno = "";

    @Column(name = "annotazioni", columnDefinition = "TEXT")
    private String annotazioni = "";

    @Column(name = "datainserimento")
    private String dataInserimento = "";

    @Column(name = "idutente")
    private String idUtente = "";

    @Column(name = "apertoda")
    private String apertoDa = "";

    @Column(name = "apertodove")
    private String apertoDove = "";

    @Column(name = "dove")
    private String dove = "";

    @Column(name = "idorigine")
    private String idOrigine = "0";

    @Column(name = "idfirma")
    private String idFirma = "0";

    @Column(name = "datafirma")
    private String dataFirma = "";

    // Campi l1-l10
    @Column(name = "l1")
    private String l1 = "";

    @Column(name = "l2")
    private String l2 = "false";

    @Column(name = "l3")
    private String l3 = "";

    @Column(name = "l4")
    private String l4 = "";

    @Column(name = "l5")
    private String l5 = "";

    @Column(name = "l6")
    private String l6 = "";

    @Column(name = "l7")
    private String l7 = "";

    @Column(name = "l8")
    private String l8 = "";

    @Column(name = "l9")
    private String l9 = "";

    @Column(name = "l10")
    private String l10 = "";

    // ========================================
    // TRANSIENT FIELDS (runtime)
    // ========================================

    @Transient
    private InputStream inputStream;

    @Transient
    private String repoPath = "";

    @Transient
    private byte[] bytes;

    @Transient
    private List<Allegato> versioni = new ArrayList<>();

    @Transient
    private String idDocAllegati = "0";

    @Transient
    private String ordine = "0";

    @Transient
    private String log = "";

    // ========================================
    // METODI HELPER
    // ========================================

    public String getSizeKB() {
        if ("0".equals(size)) return "0";
        try {
            return String.valueOf(Integer.parseInt(size) / 1000);
        } catch (NumberFormatException e) {
            return "0";
        }
    }

    public String getTypeIcon() {
        if (this.l2 == null) return "null";
        if ("true".equals(this.l2)) return "zip";
        return type;
    }

    public boolean isCompressed() {
        return "true".equals(this.l2);
    }

    public boolean hasFirma() {
        return idFirma != null && !"0".equals(idFirma);
    }

    public boolean hasVersions() {
        return versioni != null && !versioni.isEmpty();
    }

    public String getUrlRW() {
        return "files/" + this.id + "/" + this.l2 + "/" + this.l1 + "." + this.type;
    }

    public String getFullPath() {
        if (path != null && !path.isEmpty()) {
            return path;
        }
        return nome;
    }
}