package com.studiodomino.jplatform.shared.service;

import com.studiodomino.jplatform.shared.config.Configurazione;
import com.studiodomino.jplatform.shared.entity.Site;
import com.studiodomino.jplatform.shared.entity.Utente;
import com.studiodomino.jplatform.shared.entity.UtenteEsterno;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service per gestione Configurazione in sessione.
 * Centralizza TUTTI gli accessi alla configurazione.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigurazioneService {

    private static final String CONFIG_KEY = "config";
    private static final String REQUESTED_SITE_KEY = "requestedSiteId";

    private final SiteService siteService;

    // ========================================
    // CONFIGURAZIONE CORE
    // ========================================

    /**
     * Ottiene Configurazione dalla sessione
     * Se non esiste, ne crea una nuova
     */
    public Configurazione getConfig(HttpSession session) {
        Configurazione config = (Configurazione) session.getAttribute(CONFIG_KEY);

        if (config == null) {
            log.debug("Configurazione non presente, creo nuova istanza");
            config = createNewConfig();
            session.setAttribute(CONFIG_KEY, config);
        }

        return config;
    }

    /**
     * Ottiene config da HttpServletRequest
     */
    public Configurazione getOrCreateConfiguration(HttpServletRequest request) {
        return getConfig(request.getSession());
    }

    /**
     * Crea una nuova Configurazione con valori di default
     */
    private Configurazione createNewConfig() {
        Configurazione config = new Configurazione();

        // Carica sito default
        try {
            Site defaultSite = siteService.findById(1);
            config.setSito(defaultSite);
            log.info("Sito default caricato: {}", defaultSite.getType());
        } catch (Exception e) {
            log.warn("Impossibile caricare sito default: {}", e.getMessage());
        }

        // Imposta locale
        config.setLocale("it_IT");

        return config;
    }

    /**
     * Salva Configurazione in sessione
     */
    public void saveConfig(HttpSession session, Configurazione config) {
        session.setAttribute(CONFIG_KEY, config);
        log.debug("Configurazione salvata in sessione");
    }

    // ========================================
    // GESTIONE SITO
    // ========================================

    /**
     * Imposta il sito corrente
     */
    public void setSite(HttpSession session, Site site) {
        Configurazione config = getConfig(session);
        config.setSito(site);
        saveConfig(session, config);
        log.info("Sito impostato: id={}, type={}, accesso={}",
                site.getId(), site.getType(), site.getAccesso());
    }

    /**
     * Imposta il sito corrente (da HttpServletRequest)
     */
    public void setSite(HttpServletRequest request, Site site) {
        setSite(request.getSession(), site);
    }

    /**
     * Ottiene il sito corrente
     */
    public Site getCurrentSite(HttpSession session) {
        Configurazione config = getConfig(session);
        return config.getSito();
    }

    /**
     * Ottiene il sito corrente (da HttpServletRequest)
     */
    public Site getCurrentSite(HttpServletRequest request) {
        return getCurrentSite(request.getSession());
    }

    // ========================================
    // GESTIONE UTENTE
    // ========================================

    /**
     * Imposta l'utente amministratore loggato
     */
    public void setAmministratore(HttpSession session, Utente utente) {
        Configurazione config = getConfig(session);
        config.setAmministratore(utente);
        saveConfig(session, config);
        log.info("Amministratore loggato: {}", utente.getUsername());
    }

    /**
     * Imposta l'utente esterno loggato
     */
    public void setUtente(HttpSession session, UtenteEsterno utente) {
        Configurazione config = getConfig(session);
        config.setUtente(utente);
        saveConfig(session, config);
        log.info("Utente loggato: {}", utente.getUsername());
    }

    /**
     * Ottiene l'utente amministratore loggato
     */
    public Utente getAmministratore(HttpSession session) {
        Configurazione config = getConfig(session);
        return config.getAmministratore();
    }

    /**
     * Ottiene l'utente esterno loggato
     */
    public UtenteEsterno getUtente(HttpSession session) {
        Configurazione config = getConfig(session);
        return config.getUtente();
    }

    // ========================================
    // LOGOUT E INVALIDAZIONE
    // ========================================

    /**
     * Logout completo - invalida sessione
     */
    public void invalidateSession(HttpSession session) {
        log.info("Invalidating session");
        session.invalidate();
    }

    /**
     * Logout utente mantenendo sessione
     */
    public void logoutUtente(HttpSession session) {
        Configurazione config = getConfig(session);
        config.setAmministratore(null);
        config.setUtente(null);
        config.setGruppi(null);
        config.setRuoli(null);
        saveConfig(session, config);
        log.info("Utente disconnesso");
    }

    // ========================================
    // REQUESTED SITE (POST-LOGIN REDIRECT)
    // ========================================

    /**
     * Memorizza sito richiesto per redirect post-login
     */
    public void setRequestedSite(HttpSession session, Integer siteId) {
        Configurazione config = getConfig(session);
        config.setRequestedSiteId(siteId);
        saveConfig(session, config);
        log.debug("Requested site ID memorizzato: {}", siteId);
    }

    /**
     * Ottiene sito richiesto e lo rimuove
     */
    public Integer getAndClearRequestedSite(HttpSession session) {
        Configurazione config = getConfig(session);
        Integer siteId = config.getRequestedSiteId();
        config.setRequestedSiteId(null);
        saveConfig(session, config);
        return siteId;
    }
}