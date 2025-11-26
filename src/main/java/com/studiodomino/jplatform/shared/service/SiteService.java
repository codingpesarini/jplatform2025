package com.studiodomino.jplatform.shared.service;

import com.studiodomino.jplatform.shared.entity.Site;
import com.studiodomino.jplatform.shared.repository.SiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class SiteService {

    @Autowired
    private SiteRepository siteRepository;

    /**
     * Ottieni site per ID
     */
    public Site getSiteById(Integer id) {
        return siteRepository.findById(id).orElse(null);
    }

    /**
     * Ottieni site default (ID = 1)
     */
    public Site getDefaultSite() {
        return getSiteById(1);
    }

    /**
     * Ottieni tutti i site
     */
    public List<Site> getAllSites() {
        return siteRepository.findAll();
    }

    /**
     * Salva site
     */
    @Transactional
    public Site save(Site site) {
        return siteRepository.save(site);
    }

    /**
     * Crea nuovo site
     */
    @Transactional
    public Site create(Site site) {
        site.setId(null); // Forza creazione nuovo
        return siteRepository.save(site);
    }

    /**
     * Cancella site
     */
    @Transactional
    public void delete(Integer id) {
        siteRepository.deleteById(id);
    }
}