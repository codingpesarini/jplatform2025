package com.studiodomino.jplatform.shared.repository;

import com.studiodomino.jplatform.shared.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {

    /**
     * Trova attività per utente
     */
    List<Activity> findByIdutenteOrderByIdDesc(Integer idutente);

    /**
     * Trova attività per messaggio
     */
    List<Activity> findByIdmsgutenteOrderByIdDesc(Integer idmsgutente);

    /**
     * Trova attività aperte per utente
     */
    @Query("SELECT a FROM Activity a " +
            "WHERE a.idutente = :idutente " +
            "AND a.statocompletamento IN ('aperta', 'in_corso') " +
            "ORDER BY a.id DESC")
    List<Activity> findAttivitaAperteByUtente(@Param("idutente") Integer idutente);

    /**
     * Trova attività urgenti per utente
     */
    @Query("SELECT a FROM Activity a " +
            "WHERE a.idutente = :idutente " +
            "AND a.statourgenza IN ('alta', 'critica') " +
            "AND a.statocompletamento NOT IN ('completata', 'annullata') " +
            "ORDER BY " +
            "CASE a.statourgenza " +
            "  WHEN 'critica' THEN 1 " +
            "  WHEN 'alta' THEN 2 " +
            "  ELSE 3 " +
            "END, a.id DESC")
    List<Activity> findAttivitaUrgentiByUtente(@Param("idutente") Integer idutente);

    /**
     * Conta attività aperte per utente
     */
    @Query("SELECT COUNT(a) FROM Activity a " +
            "WHERE a.idutente = :idutente " +
            "AND a.statocompletamento IN ('aperta', 'in_corso')")
    long countAttivitaAperteByUtente(@Param("idutente") Integer idutente);

    /**
     * Trova attività per tipo
     */
    List<Activity> findByTipoattivitaOrderByIdDesc(String tipoattivita);

    /**
     * Trova attività completate per utente
     */
    @Query("SELECT a FROM Activity a " +
            "WHERE a.idutente = :idutente " +
            "AND a.statocompletamento = 'completata' " +
            "ORDER BY a.datacompletamento DESC")
    List<Activity> findAttivitaCompletateByUtente(@Param("idutente") Integer idutente);

    /**
     * Trova attività sospese per utente
     */
    @Query("SELECT a FROM Activity a " +
            "WHERE a.idutente = :idutente " +
            "AND a.statocompletamento = 'sospesa' " +
            "ORDER BY a.id DESC")
    List<Activity> findAttivitaSospeseByUtente(@Param("idutente") Integer idutente);

    /**
     * Trova attività per stato completamento
     */
    List<Activity> findByStatocompletamentoOrderByIdDesc(String statocompletamento);

    /**
     * Trova attività per stato urgenza
     */
    List<Activity> findByStatourgenzaOrderByIdDesc(String statourgenza);

    /**
     * Trova tutte le attività di un utente per tipo e stato
     */
    @Query("SELECT a FROM Activity a " +
            "WHERE a.idutente = :idutente " +
            "AND a.tipoattivita = :tipo " +
            "AND a.statocompletamento = :stato " +
            "ORDER BY a.id DESC")
    List<Activity> findByUtenteAndTipoAndStato(
            @Param("idutente") Integer idutente,
            @Param("tipo") String tipo,
            @Param("stato") String stato
    );
}