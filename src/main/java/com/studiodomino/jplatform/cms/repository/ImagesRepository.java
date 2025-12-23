package com.studiodomino.jplatform.cms.repository;

import com.studiodomino.jplatform.cms.entity.Images;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagesRepository extends JpaRepository<Images, Integer> {

    /**
     * Trova immagini per folder
     */
    List<Images> findByIdfolder(String idfolder);

    /**
     * Trova immagini pubbliche
     */
    List<Images> findByPrivato(String privato);

    /**
     * Trova immagini per folder e visibilità
     */
    List<Images> findByIdfolderAndPrivato(String idfolder, String privato);

    /**
     * Cerca immagini per nome
     */
    @Query("SELECT i FROM Images i WHERE i.name LIKE %:name%")
    List<Images> searchByName(@Param("name") String name);

    /**
     * Trova immagini per tipo
     */
    List<Images> findByType(String type);

    /**
     * Trova immagini per tmpid (upload in corso)
     */
    List<Images> findByTmpid(String tmpid);

    /**
     * Conta immagini per folder
     */
    Long countByIdfolder(String idfolder);

    /**
     * Calcola dimensione totale per folder
     */
    @Query("SELECT SUM(CAST(i.size AS long)) FROM Images i WHERE i.idfolder = :idfolder")
    Long sumSizeByFolder(@Param("idfolder") String idfolder);
}