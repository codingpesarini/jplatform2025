package com.studiodomino.jplatform.shared.repository;

import com.studiodomino.jplatform.shared.entity.MessaggioUtente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessaggioUtenteRepository extends JpaRepository<MessaggioUtente, Integer> {

    /**
     * Trova messaggi ricevuti da utente (non cancellati)
     */
    @Query("SELECT m FROM MessaggioUtente m " +
            "WHERE m.idDestinatario = :idUtente " +
            "AND m.tablenameDestinatario = :tablename " +
            "AND m.statoDestinatario < 3 " +
            "ORDER BY m.id DESC")
    List<MessaggioUtente> findMessaggiRicevuti(
            @Param("idUtente") String idUtente,
            @Param("tablename") String tablename
    );

    /**
     * Trova messaggi inviati da utente (non cancellati)
     */
    @Query("SELECT m FROM MessaggioUtente m " +
            "WHERE m.idMittente = :idUtente " +
            "AND m.tablenameMittente = :tablename " +
            "AND m.statoMittente < 2 " +
            "ORDER BY m.id DESC")
    List<MessaggioUtente> findMessaggiInviati(
            @Param("idUtente") String idUtente,
            @Param("tablename") String tablename
    );

    /**
     * Conta messaggi non letti per utente
     */
    @Query("SELECT COUNT(m) FROM MessaggioUtente m " +
            "WHERE m.idDestinatario = :idUtente " +
            "AND m.tablenameDestinatario = :tablename " +
            "AND m.statoDestinatario = 0")
    long countMessaggiNonLetti(
            @Param("idUtente") String idUtente,
            @Param("tablename") String tablename
    );

    /**
     * Trova risposte a messaggio (thread)
     */
    @Query("SELECT m FROM MessaggioUtente m " +
            "WHERE m.idparent = :idParent " +
            "ORDER BY m.id ASC")
    List<MessaggioUtente> findRisposteMessaggio(@Param("idParent") String idParent);

    /**
     * Trova messaggi principali ricevuti (non reply)
     */
    @Query("SELECT m FROM MessaggioUtente m " +
            "WHERE m.idDestinatario = :idUtente " +
            "AND m.tablenameDestinatario = :tablename " +
            "AND m.idparent = '0' " +
            "AND m.statoDestinatario < 3 " +
            "ORDER BY m.id DESC")
    List<MessaggioUtente> findMessaggiPrincipaliRicevuti(
            @Param("idUtente") String idUtente,
            @Param("tablename") String tablename
    );

    /**
     * Trova conversazione tra due utenti
     */
    @Query("SELECT m FROM MessaggioUtente m " +
            "WHERE ((m.idMittente = :idUtente1 AND m.idDestinatario = :idUtente2) " +
            "OR (m.idMittente = :idUtente2 AND m.idDestinatario = :idUtente1)) " +
            "ORDER BY m.id DESC")
    List<MessaggioUtente> findConversazione(
            @Param("idUtente1") String idUtente1,
            @Param("idUtente2") String idUtente2
    );

    /**
     * Trova messaggi archiviati per utente (destinatario)
     */
    @Query("SELECT m FROM MessaggioUtente m " +
            "WHERE m.idDestinatario = :idUtente " +
            "AND m.tablenameDestinatario = :tablename " +
            "AND m.statoDestinatario = 2 " +
            "ORDER BY m.id DESC")
    List<MessaggioUtente> findMessaggiArchiviatiRicevuti(
            @Param("idUtente") String idUtente,
            @Param("tablename") String tablename
    );

    /**
     * Trova messaggi archiviati per utente (mittente)
     */
    @Query("SELECT m FROM MessaggioUtente m " +
            "WHERE m.idMittente = :idUtente " +
            "AND m.tablenameMittente = :tablename " +
            "AND m.statoMittente = 1 " +
            "ORDER BY m.id DESC")
    List<MessaggioUtente> findMessaggiArchiviatiInviati(
            @Param("idUtente") String idUtente,
            @Param("tablename") String tablename
    );
}