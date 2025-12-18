package com.studiodomino.jplatform.cms.repository;

import com.studiodomino.jplatform.cms.entity.Allegato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository per la gestione degli allegati.
 * Supporta query su tabelle multiple tramite suffisso.
 */
@Repository
public interface AllegatoRepository extends JpaRepository<Allegato, Integer> {

    /**
     * Trova allegato per ID
     */
    Optional<Allegato> findById(Integer id);

    /**
     * Trova allegati per folder
     */
    @Query("SELECT a FROM Allegato a WHERE a.idFolder = :idFolder ORDER BY a.id ASC")
    List<Allegato> findByFolder(@Param("idFolder") Integer idFolder);

    /**
     * Trova allegati per versione (idversion)
     */
    @Query("SELECT a FROM Allegato a WHERE a.idVersion = :idVersion ORDER BY a.version ASC")
    List<Allegato> findVersions(@Param("idVersion") String idVersion);

    /**
     * Trova ultima versione di un allegato
     */
    @Query("SELECT a FROM Allegato a WHERE a.idVersion = :idVersion ORDER BY CAST(a.version AS int) DESC LIMIT 1")
    Optional<Allegato> findLatestVersion(@Param("idVersion") String idVersion);

    /**
     * Trova allegati per tipo
     */
    @Query("SELECT a FROM Allegato a WHERE a.tipoAllegato = :tipo ORDER BY a.id ASC")
    List<Allegato> findByTipo(@Param("tipo") Long tipo);

    /**
     * Trova allegati per anno
     */
    @Query("SELECT a FROM Allegato a WHERE a.anno = :anno ORDER BY a.id DESC")
    List<Allegato> findByAnno(@Param("anno") String anno);

    /**
     * Query nativa per tabelle con suffisso (allegaticms, allegatidoc, etc.)
     * Nota: Usare questo per retrocompatibilità con sistema legacy
     */
    @Query(value = "SELECT * FROM allegati:suffix WHERE idfolder = :idFolder ORDER BY id_allegato",
            nativeQuery = true)
    List<Allegato> findByFolderNative(@Param("idFolder") Integer idFolder,
                                      @Param("suffix") String suffix);
}