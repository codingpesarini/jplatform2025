package com.studiodomino.jplatform.shared.repository;

import com.studiodomino.jplatform.shared.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SiteRepository extends JpaRepository<Site, Integer> {

    /**
     * Trova site per campo "type"
     */
    Site findByType(String type);

    /**
     * Trova tutti i siti con un determinato accesso
     */
    List<Site> findByAccesso(Integer accesso);

    /**
     * Trova tutti i siti con accesso >= valore
     */
    List<Site> findByAccessoGreaterThanEqual(Integer accesso);

    /**
     * Trova siti pubblici (accesso = 2)
     */
    default List<Site> findPublicSites() {
        return findByAccesso(2);
    }

    /**
     * Trova siti protetti (accesso = 1)
     */
    default List<Site> findProtectedSites() {
        return findByAccesso(1);
    }
}