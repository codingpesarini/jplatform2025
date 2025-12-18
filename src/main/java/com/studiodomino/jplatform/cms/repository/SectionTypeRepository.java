package com.studiodomino.jplatform.cms.repository;

import com.studiodomino.jplatform.cms.entity.SectionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository per l'accesso ai dati della tabella content_type
 */
@Repository
public interface SectionTypeRepository extends JpaRepository<SectionType, Integer> {

    /**
     * Trova un tipo di sezione tramite il nome del tipo
     */
    Optional<SectionType> findByType(String type);

    /**
     * Trova tutti i tipi di sezione ordinati per type
     */
    List<SectionType> findAllByOrderByTypeAsc();

    /**
     * Trova tipi di sezione con un determinato valore di l1
     */
    List<SectionType> findByL1(String l1);

    /**
     * Verifica se esiste un tipo con un determinato type
     */
    boolean existsByType(String type);
}