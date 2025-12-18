package com.studiodomino.jplatform.shared.repository;

import com.studiodomino.jplatform.shared.entity.UtenteEsterno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtenteEsternoRepository extends JpaRepository<UtenteEsterno, Integer> {

    // ===== RICERCHE BASE =====

    /**
     * Trova utente per username (case-insensitive)
     */
    @Query("SELECT u FROM UtenteEsterno u WHERE LOWER(u.username) = LOWER(:username)")
    Optional<UtenteEsterno> findByUsernameIgnoreCase(@Param("username") String username);

    /**
     * Trova utente per email (case-insensitive)
     */
    @Query("SELECT u FROM UtenteEsterno u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<UtenteEsterno> findByEmailIgnoreCase(@Param("email") String email);

    /**
     * Trova utente per social ID
     */
    Optional<UtenteEsterno> findBySocialId(String socialId);

    /**
     * Trova utente per codice fiscale
     */
    Optional<UtenteEsterno> findByCodicefiscale(String codicefiscale);

    /**
     * Trova utente per partita IVA
     */
    Optional<UtenteEsterno> findByPartitaiva(String partitaiva);

    /**
     * Verifica esistenza email
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Verifica esistenza username
     */
    boolean existsByUsernameIgnoreCase(String username);

    // ===== RICERCHE AVANZATE =====

    /**
     * Trova utenti per gruppo
     */
    @Query("SELECT u FROM UtenteEsterno u WHERE u.idGruppo LIKE %:gruppo%")
    List<UtenteEsterno> findByGruppo(@Param("gruppo") String gruppo);

    /**
     * Trova utenti attivi
     */
    @Query("SELECT u FROM UtenteEsterno u WHERE u.status = '1' ORDER BY u.cognome, u.nome")
    List<UtenteEsterno> findUtentiAttivi();

    /**
     * Trova utenti per stato
     */
    List<UtenteEsterno> findByStatusOrderByCognomeAscNomeAsc(String status);

    /**
     * Ricerca utenti per nome o cognome
     */
    @Query("SELECT u FROM UtenteEsterno u " +
            "WHERE LOWER(u.nome) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(u.cognome) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "ORDER BY u.cognome, u.nome")
    List<UtenteEsterno> searchByNomeCognome(@Param("search") String search);

    /**
     * Ricerca utenti con query SQL custom (per compatibilità legacy)
     */
    @Query(value = "SELECT * FROM utenteesterno WHERE 1=1 :sqlCondition",
            nativeQuery = true)
    List<UtenteEsterno> findWithCustomSql(@Param("sqlCondition") String sqlCondition);

    // ===== QUERY STATISTICHE =====

    /**
     * Conta utenti per gruppo
     */
    @Query("SELECT COUNT(u) FROM UtenteEsterno u WHERE u.idGruppo LIKE %:gruppo%")
    long countByGruppo(@Param("gruppo") String gruppo);

    /**
     * Conta utenti attivi
     */
    @Query("SELECT COUNT(u) FROM UtenteEsterno u WHERE u.status = '1'")
    long countUtentiAttivi();

    /**
     * Trova utenti per comune
     */
    List<UtenteEsterno> findByComuneOrderByCognomeAscNomeAsc(String comune);

    /**
     * Trova utenti per provincia
     */
    List<UtenteEsterno> findByProvinciaOrderByCognomeAscNomeAsc(String provincia);

    // ===== UPDATE OPERATIONS =====

    /**
     * Aggiorna navigation profile
     */
    @Modifying
    @Query("UPDATE UtenteEsterno u SET u.navigationProfile = :profile WHERE u.id = :id")
    void updateNavigationProfile(@Param("id") Integer id, @Param("profile") String profile);

    /**
     * Aggiorna password
     */
    @Modifying
    @Query("UPDATE UtenteEsterno u SET u.password = :password WHERE u.id = :id")
    void updatePassword(@Param("id") Integer id, @Param("password") String password);

    /**
     * Aggiorna stato
     */
    @Modifying
    @Query("UPDATE UtenteEsterno u SET u.status = :status WHERE u.id = :id")
    void updateStatus(@Param("id") Integer id, @Param("status") String status);

    /**
     * Aggiorna ultimo accesso
     */
    @Modifying
    @Query("UPDATE UtenteEsterno u SET u.dataultimoaccesso = :data WHERE u.id = :id")
    void updateUltimoAccesso(@Param("id") Integer id, @Param("data") String data);

    /**
     * Aggiorna profileImage
     */
    @Modifying
    @Query("UPDATE UtenteEsterno u SET u.profileImage = :profileImage WHERE u.id = :id")
    void updateProfileImage(@Param("id") Integer id, @Param("profileImage") Integer profileImage);

    /**
     * Aggiorna campo generico (per compatibilità legacy)
     */
    @Modifying
    @Query(value = "UPDATE utenteesterno SET :fieldName = :fieldValue WHERE :condition",
            nativeQuery = true)
    void updateCampoGenerico(
            @Param("fieldName") String fieldName,
            @Param("fieldValue") String fieldValue,
            @Param("condition") String condition
    );
}