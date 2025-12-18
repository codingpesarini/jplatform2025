package com.studiodomino.jplatform.cms.repository;

import com.studiodomino.jplatform.cms.entity.DocAllegati;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository per la tabella di join docallegati.
 */
@Repository
public interface DocAllegatiRepository extends JpaRepository<DocAllegati, Integer> {

    /**
     * Trova tutti gli allegati di un documento
     */
    @Query("SELECT d FROM DocAllegati d WHERE d.idDocumento = :idDocumento ORDER BY d.ordine ASC")
    List<DocAllegati> findByDocumento(@Param("idDocumento") Integer idDocumento);

    /**
     * Trova tutti i documenti che usano un allegato
     */
    @Query("SELECT d FROM DocAllegati d WHERE d.idAllegato = :idAllegato")
    List<DocAllegati> findByAllegato(@Param("idAllegato") Integer idAllegato);

    /**
     * Trova collegamento specifico documento-allegato
     */
    @Query("SELECT d FROM DocAllegati d WHERE d.idDocumento = :idDocumento AND d.idAllegato = :idAllegato")
    List<DocAllegati> findByDocumentoAndAllegato(@Param("idDocumento") Integer idDocumento,
                                                 @Param("idAllegato") Integer idAllegato);

    /**
     * Elimina tutti i collegamenti di un documento
     */
    @Modifying
    @Query("DELETE FROM DocAllegati d WHERE d.idDocumento = :idDocumento")
    void deleteByDocumento(@Param("idDocumento") Integer idDocumento);

    /**
     * Elimina collegamenti per un allegato
     */
    @Modifying
    @Query("DELETE FROM DocAllegati d WHERE d.idAllegato = :idAllegato")
    void deleteByAllegato(@Param("idAllegato") Integer idAllegato);

    /**
     * Conta allegati per documento
     */
    @Query("SELECT COUNT(d) FROM DocAllegati d WHERE d.idDocumento = :idDocumento")
    Long countByDocumento(@Param("idDocumento") Integer idDocumento);
}