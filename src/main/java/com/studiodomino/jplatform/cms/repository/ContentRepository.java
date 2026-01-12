package com.studiodomino.jplatform.cms.repository;

import com.studiodomino.jplatform.cms.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository per l'accesso ai dati della tabella 'contents'.
 * Gestisce sia le SEZIONI (idRoot=-1) che i CONTENUTI (idRoot!=-1).
 */
@Repository
public interface ContentRepository extends JpaRepository<Content, Integer> {

    // ========================================
    // QUERY PER SEZIONI (idRoot = -1)
    // ========================================

    /**
     * Trova tutte le sezioni di un sito
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite AND c.idRoot = -1")
    List<Content> findSectionsBySite(@Param("idSite") String idSite);

    /**
     * Trova sezioni radice (primo livello) di un sito, ordinate per position
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite AND c.idRoot = -1 " +
            "AND (c.idParent IS NULL OR c.idParent = '' OR c.idParent = '0') " +
            "ORDER BY c.position ASC")
    List<Content> findRootSectionsBySite(@Param("idSite") String idSite);

    /**
     * Trova sotto-sezioni di una sezione parent
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite AND c.idRoot = -1 " +
            "AND c.idParent = :idParent ORDER BY c.position ASC")
    List<Content> findSubsectionsByParent(
            @Param("idSite") String idSite,
            @Param("idParent") String idParent
    );

    /**
     * Trova una sezione per label
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite AND c.idRoot = -1 " +
            "AND c.label = :label")
    Optional<Content> findSectionByLabel(
            @Param("idSite") String idSite,
            @Param("label") String label
    );

    /**
     * Trova sezioni per tipo
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite AND c.idRoot = -1 " +
            "AND c.idType = :idType ORDER BY c.position ASC")
    List<Content> findSectionsByType(
            @Param("idSite") String idSite,
            @Param("idType") Integer idType
    );

    // ========================================
    // QUERY PER CONTENUTI (idRoot != -1)
    // ========================================

    /**
     * Trova tutti i contenuti di una sezione
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite AND c.idRoot = :idRoot " +
            "ORDER BY c.position ASC")
    List<Content> findContentsBySection(
            @Param("idSite") String idSite,
            @Param("idRoot") Integer idRoot
    );

    /**
     * Trova contenuti pubblicati di una sezione
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite AND c.idRoot = :idRoot " +
            "AND c.stato = '1' ORDER BY c.position ASC")
    List<Content> findPublishedContentsBySection(
            @Param("idSite") String idSite,
            @Param("idRoot") Integer idRoot
    );

    /**
     * Trova contenuti per label
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite AND c.idRoot = :idRoot " +
            "AND c.label = :label")
    Optional<Content> findContentByLabel(
            @Param("idSite") String idSite,
            @Param("idRoot") Integer idRoot,
            @Param("label") String label
    );

    // ========================================
    // QUERY PER STATO E VISIBILITÀ
    // ========================================

    /**
     * Trova contenuti per stato
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite AND c.stato = :stato " +
            "ORDER BY c.position ASC")
    List<Content> findByStato(
            @Param("idSite") String idSite,
            @Param("stato") String stato
    );

    /**
     * Trova contenuti pubblici
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite AND c.privato = '0' " +
            "AND c.stato = '1' ORDER BY c.position ASC")
    List<Content> findPublicContents(@Param("idSite") String idSite);

    /**
     * Trova contenuti privati per gruppo
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite AND c.privato != '0' " +
            "AND c.idGruppo LIKE %:idGruppo% ORDER BY c.position ASC")
    List<Content> findPrivateContentsByGroup(
            @Param("idSite") String idSite,
            @Param("idGruppo") String idGruppo
    );

    // ========================================
    // QUERY PER MENU E NAVIGAZIONE
    // ========================================

    /**
     * Trova menu pubblico
     * Sezioni con: stato='1', privato='0', menu1='1'
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite " +
            "AND c.idRoot = -1 " +
            "AND c.stato = '1' " +
            "AND c.privato = '0' " +
            "AND (c.idParent IS NULL OR c.idParent = '' OR c.idParent = '0') " +
            "AND (c.idType != 7) " +
            "ORDER BY c.position ASC")
    List<Content> findPublicMenu(@Param("idSite") String idSite);

     /**
     * Trova menu privato per gruppi utente
     * gruppiSql è una stringa tipo "1,2,3" o "'1','2','3'"
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite " +
            "AND c.idRoot = -1 " +
            "AND c.stato = '1' " +
            "AND c.privato = '1' " +
            "AND (c.idParent IS NULL OR c.idParent = '' OR c.idParent = '0') " +
            "AND (:gruppiSql IS NULL OR :gruppiSql = '' OR " +
            "     c.idGruppo LIKE CONCAT('%', :gruppiSql, '%')) " +
            "ORDER BY c.position ASC")
    List<Content> findPrivateMenu(
            @Param("idSite") String idSite,
            @Param("gruppiSql") String gruppiSql  // ← Cambiato da List<String> a String
    );

    /**
     * Trova home page
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite " +
            "AND c.idRoot = -1 " +
            "AND c.firstPage = '1' " +
            "AND c.stato = '1' " +
            "ORDER BY c.position ASC")
    Optional<Content> findHomePage(@Param("idSite") String idSite);

    /**
     * Trova sezioni menu (generico)
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite " +
            "AND c.idRoot = -1 " +
            "AND c.stato = :stato " +
            "AND c.privato = :privato " +
            "ORDER BY c.position ASC")
    List<Content> findMenuSections(
            @Param("idSite") String idSite,
            @Param("stato") String stato,
            @Param("privato") String privato
    );

    // ========================================
    // QUERY PER SCHEDULING (s1, s2, s3)
    // ========================================

    /**
     * Trova contenuti con scheduling attivo (s3='1')
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite AND c.s3 = '1'")
    List<Content> findScheduledContents(@Param("idSite") String idSite);

    /**
     * Trova contenuti schedulati da pubblicare
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite AND c.s3 = '1' " +
            "AND c.stato = '0' " +
            "AND (c.s1 IS NULL OR c.s1 = '' OR STR_TO_DATE(c.s1, '%d-%m-%Y') <= :today) " +
            "AND (c.s2 IS NULL OR c.s2 = '' OR STR_TO_DATE(c.s2, '%d-%m-%Y') >= :today)")
    List<Content> findContentsToPublish(
            @Param("idSite") String idSite,
            @Param("today") LocalDate today
    );

    /**
     * Trova contenuti schedulati da nascondere
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite AND c.s3 = '1' " +
            "AND c.stato = '1' " +
            "AND STR_TO_DATE(c.s2, '%d-%m-%Y') < :today")
    List<Content> findContentsToHide(
            @Param("idSite") String idSite,
            @Param("today") LocalDate today
    );

    // ========================================
    // QUERY PER DATE E ARCHIVIO
    // ========================================

    /**
     * Trova contenuti per anno
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite AND c.idRoot = :idRoot " +
            "AND c.anno = :anno ORDER BY c.datasql DESC")
    List<Content> findContentsByYear(
            @Param("idSite") String idSite,
            @Param("idRoot") Integer idRoot,
            @Param("anno") Integer anno
    );

    /**
     * Trova contenuti per anno e mese
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite AND c.idRoot = :idRoot " +
            "AND c.anno = :anno AND c.mese = :mese ORDER BY c.datasql DESC")
    List<Content> findContentsByYearMonth(
            @Param("idSite") String idSite,
            @Param("idRoot") Integer idRoot,
            @Param("anno") Integer anno,
            @Param("mese") String mese
    );

    /**
     * Trova anni distinti per una sezione (per archivio)
     */
    @Query("SELECT DISTINCT c.anno FROM Content c WHERE c.idSite = :idSite " +
            "AND c.idRoot = :idRoot AND c.anno IS NOT NULL ORDER BY c.anno DESC")
    List<Integer> findDistinctYearsBySection(
            @Param("idSite") String idSite,
            @Param("idRoot") Integer idRoot
    );

    // ========================================
    // QUERY PER TAG ED EXTRA TAG
    // ========================================

    /**
     * Trova contenuti per tag
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite " +
            "AND c.tag LIKE %:tag% ORDER BY c.position ASC")
    List<Content> findContentsByTag(
            @Param("idSite") String idSite,
            @Param("tag") String tag
    );

    /**
     * Trova tag cloud con conteggi
     */
    @Query("SELECT c.tag, COUNT(c) FROM Content c " +
            "WHERE c.idSite = :idSite " +
            "AND c.tag IS NOT NULL " +
            "AND c.tag != '' " +
            "AND c.stato = '1' " +
            "GROUP BY c.tag " +
            "ORDER BY COUNT(c) DESC")
    List<Object[]> findTagCloud(@Param("idSite") String idSite);

    /**
     * Trova contenuti per extraTag specifico (1-10)
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite " +
            "AND (c.extratag1 LIKE %:extraTag% OR c.extratag2 LIKE %:extraTag% OR " +
            "c.extratag3 LIKE %:extraTag% OR c.extratag4 LIKE %:extraTag% OR " +
            "c.extratag5 LIKE %:extraTag% OR c.extratag6 LIKE %:extraTag% OR " +
            "c.extratag7 LIKE %:extraTag% OR c.extratag8 LIKE %:extraTag% OR " +
            "c.extratag9 LIKE %:extraTag% OR c.extratag10 LIKE %:extraTag%) " +
            "ORDER BY c.position ASC")
    List<Content> findContentsByExtraTag(
            @Param("idSite") String idSite,
            @Param("extraTag") String extraTag
    );

    // ========================================
    // QUERY PER NEWSLETTER E SMS
    // ========================================

    /**
     * Trova contenuti per newsletter specifica (1-5)
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite " +
            "AND (c.newsletter1 = :newsletterId OR c.newsletter2 = :newsletterId OR " +
            "c.newsletter3 = :newsletterId OR c.newsletter4 = :newsletterId OR " +
            "c.newsletter5 = :newsletterId)")
    List<Content> findContentsByNewsletter(
            @Param("idSite") String idSite,
            @Param("newsletterId") String newsletterId
    );

    /**
     * Trova contenuti per SMS specifico (1-5)
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite " +
            "AND (c.sms1 = :smsId OR c.sms2 = :smsId OR " +
            "c.sms3 = :smsId OR c.sms4 = :smsId OR " +
            "c.sms5 = :smsId)")
    List<Content> findContentsBySms(
            @Param("idSite") String idSite,
            @Param("smsId") String smsId
    );

    // ========================================
    // QUERY PER CAMPI CUSTOM
    // ========================================

    /**
     * Cerca contenuti per campo TEXT specifico
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite " +
            "AND (c.text1 LIKE %:searchTerm% OR c.text2 LIKE %:searchTerm% OR " +
            "c.text3 LIKE %:searchTerm% OR c.text4 LIKE %:searchTerm% OR " +
            "c.text5 LIKE %:searchTerm% OR c.text6 LIKE %:searchTerm% OR " +
            "c.text7 LIKE %:searchTerm% OR c.text8 LIKE %:searchTerm% OR " +
            "c.text9 LIKE %:searchTerm% OR c.text10 LIKE %:searchTerm%)")
    List<Content> searchInTextFields(
            @Param("idSite") String idSite,
            @Param("searchTerm") String searchTerm
    );

    /**
     * Cerca contenuti per campo VARCHAR specifico
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite " +
            "AND (c.varchar1 LIKE %:searchTerm% OR c.varchar2 LIKE %:searchTerm% OR " +
            "c.varchar3 LIKE %:searchTerm% OR c.varchar4 LIKE %:searchTerm% OR " +
            "c.varchar5 LIKE %:searchTerm% OR c.varchar6 LIKE %:searchTerm% OR " +
            "c.varchar7 LIKE %:searchTerm% OR c.varchar8 LIKE %:searchTerm% OR " +
            "c.varchar9 LIKE %:searchTerm% OR c.varchar10 LIKE %:searchTerm%)")
    List<Content> searchInVarcharFields(
            @Param("idSite") String idSite,
            @Param("searchTerm") String searchTerm
    );

    /**
     * Trova contenuti con NUMBER in range
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite " +
            "AND (c.number1 BETWEEN :min AND :max OR c.number2 BETWEEN :min AND :max OR " +
            "c.number3 BETWEEN :min AND :max OR c.number4 BETWEEN :min AND :max OR " +
            "c.number5 BETWEEN :min AND :max)")
    List<Content> findContentsByNumberRange(
            @Param("idSite") String idSite,
            @Param("min") Double min,
            @Param("max") Double max
    );

    /**
     * Trova contenuti per range di date (DATA fields)
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite " +
            "AND (c.data2 BETWEEN :startDate AND :endDate OR " +
            "c.data3 BETWEEN :startDate AND :endDate OR " +
            "c.data4 BETWEEN :startDate AND :endDate)")
    List<Content> findContentsByDateRange(
            @Param("idSite") String idSite,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // ========================================
    // QUERY PER FIRST PAGE
    // ========================================

    /**
     * Trova contenuti per first page
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite " +
            "AND c.firstPage = '1' AND c.stato = '1' ORDER BY c.position ASC")
    List<Content> findFirstPageContents(@Param("idSite") String idSite);

    // ========================================
    // QUERY PER RICERCA FULL TEXT
    // ========================================

    /**
     * Ricerca full text su titolo, riassunto, testo
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite AND " +
            "(LOWER(c.titolo) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.riassunto) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.testo) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Content> searchFullText(
            @Param("idSite") String idSite,
            @Param("searchTerm") String searchTerm
    );

    // ========================================
    // QUERY PER STATISTICHE
    // ========================================

    /**
     * Conta contenuti per sezione
     */
    @Query("SELECT COUNT(c) FROM Content c WHERE c.idSite = :idSite " +
            "AND c.idRoot = :idRoot")
    Long countContentsBySection(
            @Param("idSite") String idSite,
            @Param("idRoot") Integer idRoot
    );

    /**
     * Conta contenuti pubblicati per sezione
     */
    @Query("SELECT COUNT(c) FROM Content c WHERE c.idSite = :idSite " +
            "AND c.idRoot = :idRoot AND c.stato = '1'")
    Long countPublishedContentsBySection(
            @Param("idSite") String idSite,
            @Param("idRoot") Integer idRoot
    );

    // ========================================
    // QUERY GENERICHE
    // ========================================

    /**
     * Trova per ID e sito
     */
    @Query("SELECT c FROM Content c WHERE c.id = :id AND c.idSite = :idSite")
    Optional<Content> findByIdAndSite(
            @Param("id") Integer id,
            @Param("idSite") String idSite
    );

    /**
     * Trova tutti i contenuti di un sito
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite ORDER BY c.position ASC")
    List<Content> findAllBySite(@Param("idSite") String idSite);

    /**
     * Trova contenuti per tipo
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite AND c.idType = :idType " +
            "ORDER BY c.position ASC")
    List<Content> findContentsByType(
            @Param("idSite") String idSite,
            @Param("idType") Integer idType
    );


    /**
     * Trova sottosezioni pubblicate di una sezione parent
     * Equivalente legacy: getStrutturaMenu() - query ricorsiva
     *
     * @param idSite ID del sito
     * @param idParent ID della sezione parent
     * @param stato Stato pubblicazione (1=pubblicato)
     * @param privato Flag privato (0=pubblico, 1=privato)
     * @return Lista di contenuti sottosezioni
     */
    @Query("SELECT c FROM Content c WHERE c.idSite = :idSite " +
            "AND c.idRoot = -1 " +
            "AND c.idParent = :idParent " +
            "AND c.stato = :stato " +
            "AND c.privato = :privato " +
            "AND c.idType != 7 " +
            "ORDER BY c.position ASC")
    List<Content> findPublishedSubsections(
            @Param("idSite") String idSite,
            @Param("idParent") String idParent,
            @Param("stato") String stato,
            @Param("privato") String privato
    );
}