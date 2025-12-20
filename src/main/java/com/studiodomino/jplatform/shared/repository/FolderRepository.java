package com.studiodomino.jplatform.shared.repository;

import com.studiodomino.jplatform.shared.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository per gestione Folder
 */
@Repository
public interface FolderRepository extends JpaRepository<Folder, Integer> {

    /**
     * Trova folder per nome
     */
    Optional<Folder> findByNome(String nome);

    /**
     * Trova tutti i subfolder di un folder parent
     */
    List<Folder> findByIdfolderOrderByNomeAsc(String idfolder);

    /**
     * Trova folder root (idfolder = "0" o "-1")
     */
    @Query("SELECT f FROM Folder f WHERE f.idfolder IN ('0', '-1')")
    List<Folder> findRootFolders();

    /**
     * Trova folder per gruppo
     */
    List<Folder> findByIdgruppoOrderByNomeAsc(String idgruppo);

    /**
     * Trova folder per nome (LIKE)
     */
    List<Folder> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

    /**
     * Trova folder creati dopo una data
     */
    List<Folder> findByDatacreazioneAfterOrderByDatacreazioneDesc(LocalDate data);

    /**
     * Trova folder per stato specifico
     */
    List<Folder> findByStato1(Integer stato);

    /**
     * Conta subfolder di un parent
     */
    @Query("SELECT COUNT(f) FROM Folder f WHERE f.idfolder = :idfolder")
    Long countSubfolders(@Param("idfolder") String idfolder);

    /**
     * Verifica se folder ha subfolder
     */
    boolean existsByIdfolder(String idfolder);

    /**
     * Trova folder con almeno uno stato attivo
     */
    @Query("SELECT f FROM Folder f WHERE f.stato1 = 1 OR f.stato2 = 1 OR f.stato3 = 1 OR f.stato4 = 1 OR f.stato5 = 1")
    List<Folder> findFoldersWithActiveStatus();

    /**
     * Trova struttura gerarchica completa (ricorsiva)
     */
    @Query(value = """
        WITH RECURSIVE folder_tree AS (
            SELECT id, idfolder, nome, datacreazione, 0 as level
            FROM folder
            WHERE idfolder IN ('0', '-1')
            UNION ALL
            SELECT f.id, f.idfolder, f.nome, f.datacreazione, ft.level + 1
            FROM folder f
            INNER JOIN folder_tree ft ON f.idfolder = ft.id
        )
        SELECT * FROM folder_tree ORDER BY level, nome
        """, nativeQuery = true)
    List<Folder> findFolderTreeHierarchy();

    /**
     * Elimina tutti i subfolder di un parent
     */
    void deleteByIdfolder(String idfolder);
}