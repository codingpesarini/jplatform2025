package com.studiodomino.jplatform.shared.service;

import com.studiodomino.jplatform.shared.entity.Site;
import com.studiodomino.jplatform.shared.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SiteService {

    private final SiteRepository siteRepository;

    /**
     * Trova site per ID
     */
    public Site findById(Integer id) {
        return siteRepository.findById(id).orElse(null);
    }

    /**
     * Trova site per campo "type"
     */
    public Site findByType(String type) {
        Site site = siteRepository.findByType(type);
        if (site != null) {
            log.debug("Site trovato per type '{}': id={}", type, site.getId());
        } else {
            log.warn("Nessun site trovato per type: {}", type);
        }
        return site;
    }

    /**
     * Ottiene il sito di default
     * PRIORITÀ:
     * 1. Primo sito PUBBLICO (accesso = 2)
     * 2. Primo sito in assoluto
     */
    public Site getDefaultSite() {
        // Cerca primo sito pubblico
        List<Site> publicSites = siteRepository.findByAccesso(2);
        if (!publicSites.isEmpty()) {
            Site site = publicSites.get(0);
            log.info("Sito default pubblico: id={}, type={}, path2={}",
                    site.getId(), site.getType(), site.getPath2());
            return site;
        }

        // Altrimenti primo sito disponibile
        List<Site> allSites = siteRepository.findAll();
        if (!allSites.isEmpty()) {
            Site site = allSites.get(0);
            log.info("Sito default (primo disponibile): id={}, type={}",
                    site.getId(), site.getType());
            return site;
        }

        // Nessun sito trovato - ERRORE CRITICO
        log.error("ERRORE CRITICO: Nessun sito configurato nel database!");
        throw new RuntimeException("Nessun sito configurato nel database");
    }

    /**
     * Ottiene tutti i siti pubblici (accesso = 2)
     */
    public List<Site> getPublicSites() {
        return siteRepository.findByAccesso(2);
    }

    /**
     * Ottiene tutti i siti protetti (accesso = 1)
     */
    public List<Site> getProtectedSites() {
        return siteRepository.findByAccesso(1);
    }

    /**
     * Ottiene tutti i siti
     */
    public List<Site> getAllSites() {
        return siteRepository.findAll();
    }

    /**
     * Salva site
     */
    public Site save(Site site) {
        Site saved = siteRepository.save(site);
        log.info("Site salvato: id={}, type={}", saved.getId(), saved.getType());
        return saved;
    }

    /**
     * Cancella site
     */
    public void delete(Integer id) {
        siteRepository.deleteById(id);
        log.info("Site cancellato: id={}", id);
    }

    /**
     * Verifica se esiste un site con un determinato type
     */
    public boolean existsByType(String type) {
        return siteRepository.findByType(type) != null;
    }

    /**
     * Conta i siti pubblici
     */
    public long countPublicSites() {
        return siteRepository.findByAccesso(2).size();
    }

    /**
     * Conta i siti protetti
     */
    public long countProtectedSites() {
        return siteRepository.findByAccesso(1).size();
    }
}