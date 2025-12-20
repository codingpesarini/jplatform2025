package com.studiodomino.jplatform.shared.repository;

import com.studiodomino.jplatform.shared.entity.Images;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository per gestione Images
 */
@Repository
public interface ImagesRepository extends JpaRepository<Images, Integer> {

    /**
     * Trova immagine per nome
     */
    Optional<Images> findByName(String name);

    /**
     * Trova tutte le immagini in un folder
     */
    List<Images> findByIdfolderOrderByNameAsc(String idfolder);

    /**
     * Trova immagini per tmpid (upload temporaneo)
     */
    List<Images> findByTmpid(String tmpid);

    /**
     * Trova immagini pubbliche
     */
    @Query("SELECT i FROM Images i WHERE i.privato = '0' OR i.privato = '' OR i.privato IS NULL")
    List<Images> findPublicImages();

    /**
     * Trova immagini private
     */
    @Query("SELECT i FROM Images i WHERE i.privato = '1'")
    List<Images> findPrivateImages();

    /**
     * Trova immagini pubbliche in un folder
     */
    @Query("SELECT i FROM Images i WHERE i.idfolder = :idfolder AND (i.privato = '0' OR i.privato = '' OR i.privato IS NULL)")
    List<Images> findPublicImagesByFolder(@Param("idfolder") String idfolder);

    /**
     * Trova immagini per tipo (MIME type)
     */
    List<Images> findByTypeContainingIgnoreCase(String type);

    /**
     * Cerca immagini per nome (LIKE)
     */
    List<Images> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

    /**
     * Cerca immagini per didascalia
     */
    List<Images> findByDidascaliaContainingIgnoreCaseOrderByNameAsc(String didascalia);

    /**
     * Trova immagini più grandi di una certa dimensione
     */
    @Query("SELECT i FROM Images i WHERE CAST(i.size AS long) > :minSize")
    List<Images> findByMinSize(@Param("minSize") Long minSize);

    /**
     * Conta immagini in un folder
     */
    Long countByIdfolder(String idfolder);

    /**
     * Somma dimensione totale immagini in un folder
     */
    @Query("SELECT SUM(CAST(i.size AS long)) FROM Images i WHERE i.idfolder = :idfolder")
    Long sumSizeByFolder(@Param("idfolder") String idfolder);

    /**
     * Trova immagini senza thumbnail
     */
    @Query("SELECT i FROM Images i WHERE i.paththumb IS NULL OR i.paththumb = ''")
    List<Images> findImagesWithoutThumbnail();

    /**
     * Trova immagini per estensione
     */
    @Query("SELECT i FROM Images i WHERE LOWER(i.name) LIKE CONCAT('%', LOWER(:extension))")
    List<Images> findByExtension(@Param("extension") String extension);

    /**
     * Trova ultime N immagini
     */
    List<Images> findTop10ByOrderByIdDesc();

    /**
     * Trova immagini per campo l1
     */
    List<Images> findByL1OrderByNameAsc(String l1);



    /**
     * Verifica se esiste immagine con nome
     */
    boolean existsByName(String name);

    /**
     * Verifica se esiste immagine con fullpath
     */
    boolean existsByFullpath(String fullpath);

    /**
     * Elimina immagini per folder
     */
    @Modifying  // ✅ IMPORTANTE per operazioni DELETE/UPDATE
    @Query("DELETE FROM Images i WHERE i.idfolder = :idfolder")
    void deleteByIdfolder(@Param("idfolder") String idfolder);

    /**
     * Elimina immagini per tmpid
     */
    @Modifying
    @Query("DELETE FROM Images i WHERE i.tmpid = :tmpid")
    void deleteByTmpid(@Param("tmpid") String tmpid);
}