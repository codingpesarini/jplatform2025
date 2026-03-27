package com.studiodomino.jplatform.crm.repository;

import com.studiodomino.jplatform.crm.entity.AreaInteresse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AreaInteresseRepository extends JpaRepository<AreaInteresse, Integer> {

    /**
     * Recupera tutte le aree ordinate alfabeticamente per descrizione.
     */
    List<AreaInteresse> findAllByOrderByDescrizioneAsc();
}
