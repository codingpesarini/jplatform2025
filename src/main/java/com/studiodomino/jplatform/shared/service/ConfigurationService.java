package com.studiodomino.jplatform.shared.service;

import com.studiodomino.jplatform.shared.config.ConfigurazioneCore;
import com.studiodomino.jplatform.shared.entity.Site;
import com.studiodomino.jplatform.shared.entity.Utente;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service per gestione ConfigurazioneCore in sessione
 * Centralizza TUTTI gli accessi alla configurazione
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigurationService {

    private static final String CONFIG_KEY = "configCore";
    private static final String REQUESTED_SITE_KEY = "requestedSiteId";

    private final SiteService siteService;

    // ========================================
    // CONFIGURAZIONE CORE
    // ========================================

    /**
     * Ottiene ConfigurazioneCore dalla sessione
     * Se non esiste, ne crea una nuova
     */
    public ConfigurazioneCore getConfig(HttpSession session) {
        ConfigurazioneCore config = (ConfigurazioneCore) session.getAttribute(CONFIG_KEY);

        if (config == null) {
            log.debug("ConfigurazioneCore non presente, creo nuova istanza");
            config = createNewConfig();
            session.setAttribute(CONFIG_KEY, config);
        }

        return config;
    }

    /**
     * Ottiene config da HttpServletRequest
     */
    public ConfigurazioneCore getOrCreateConfiguration(HttpServletRequest request) {
        return getConfig(request.getSession());
    }

    /**
     * Crea una nuova ConfigurazioneCore con valori di default
     */
    private ConfigurazioneCore createNewConfig() {
        ConfigurazioneCore config = new ConfigurazioneCore();

        // Carica sito default
        try {
            Site defaultSite = siteService.findById(1);
            config.setSito(defaultSite);
            log.info("Sito default caricato in nuovo config: {}", defaultSite.getType());
        } catch (Exception e) {
            log.warn("Impossibile caricare sito default: {}", e.getMessage());
        }

        // Imposta locale
        config.setLocale("it_IT");

        return config;
    }

    /**
     * Salva ConfigurazioneCore in sessione
     */
    public void saveConfig(HttpSession session, ConfigurazioneCore config) {
        session.setAttribute(CONFIG_KEY, config);
        log.debug("ConfigurazioneCore salvata in sessione");
    }

    // ========================================
    // GESTIONE SITO
    // ========================================

    /**
     * Imposta il sito corrente (con HttpSession)
     */
    public void setSite(HttpSession session, Site site) {
        ConfigurazioneCore config = getConfig(session);
        config.setSito(site);

        // Imposta anche IDSITE per compatibilità
        session.setAttribute("IDSITE", site.getId().toString());

        saveConfig(session, config);
        log.info("Sito impostato: id={}, type={}, accesso={}",
                site.getId(), site.getType(), site.getAccesso());
    }

    /**
     * Imposta il sito corrente (con HttpServletRequest)
     */
    public void setSite(HttpServletRequest request, Site site) {
        setSite(request.getSession(), site);
    }

    /**
     * Ottiene il sito corrente (con HttpSession)
     */
    public Site getCurrentSite(HttpSession session) {
        ConfigurazioneCore config = getConfig(session);
        return config.getSito();
    }

    /**
     * Ottiene il sito corrente (con HttpServletRequest)
     */
    public Site getCurrentSite(HttpServletRequest request) {
        return getCurrentSite(request.getSession());
    }

    // ========================================
    // GESTIONE UTENTE
    // ========================================

    /**
     * Imposta l'utente loggato (con HttpSession)
     */
    public void setUtente(HttpSession session, Utente utente) {
        ConfigurazioneCore config = getConfig(session);

        // Determina se è admin o utente normale basato sul ruolo
        boolean isAdmin = false;
        if (utente.getRole1() != null) {
            String nomeRuolo = utente.getRole1();
            isAdmin = "ADMIN".equalsIgnoreCase(nomeRuolo) ||
                    "AMMINISTRATORE".equalsIgnoreCase(nomeRuolo);
        }

        if (isAdmin) {
            config.setAmministratore(utente);
            config.setUtente(null);
            log.info("Amministratore impostato: {}", utente.getUsername());
        } else {
            config.setUtente(utente);
            config.setAmministratore(null);
            log.info("Utente impostato: {}", utente.getUsername());
        }

        saveConfig(session, config);
    }

    /**
     * Imposta l'utente loggato (con HttpServletRequest)
     * ALIAS: setUtenteLoggato
     */
    public void setUtenteLoggato(HttpServletRequest request, Utente utente) {
        setUtente(request.getSession(), utente);
    }

    /**
     * Imposta l'utente loggato (con HttpSession)
     * ALIAS: setUtenteLoggato
     */
    public void setUtenteLoggato(HttpSession session, Utente utente) {
        setUtente(session, utente);
    }

    /**
     * Ottiene l'utente loggato (con HttpSession)
     */
    public Utente getUtente(HttpSession session) {
        ConfigurazioneCore config = getConfig(session);
        return config.getUtenteLoggato();
    }

    /**
     * Ottiene l'utente loggato (con HttpServletRequest)
     */
    public Utente getUtente(HttpServletRequest request) {
        return getUtente(request.getSession());
    }

    /**
     * Ottiene l'utente loggato (con HttpSession)
     * ALIAS: getUtenteLoggato
     */
    public Utente getUtenteLoggato(HttpSession session) {
        return getUtente(session);
    }

    /**
     * Ottiene l'utente loggato (con HttpServletRequest)
     * ALIAS: getUtenteLoggato
     */
    public Utente getUtenteLoggato(HttpServletRequest request) {
        return getUtente(request.getSession());
    }

    /**
     * Verifica se l'utente è loggato (con HttpSession)
     */
    public boolean isUserLoggedIn(HttpSession session) {
        return getUtente(session) != null;
    }

    /**
     * Verifica se l'utente è loggato (con HttpServletRequest)
     */
    public boolean isUserLoggedIn(HttpServletRequest request) {
        return isUserLoggedIn(request.getSession());
    }

    /**
     * Rimuove l'utente dalla sessione (logout)
     */
    public void clearUtente(HttpSession session) {
        ConfigurazioneCore config = getConfig(session);
        config.setAmministratore(null);
        config.setUtente(null);
        saveConfig(session, config);
        log.info("Utente rimosso dalla sessione");
    }

    /**
     * Rimuove l'utente dalla sessione (logout) - con HttpServletRequest
     */
    public void clearUtente(HttpServletRequest request) {
        clearUtente(request.getSession());
    }

    // ========================================
    // GESTIONE SITO RICHIESTO (per redirect post-login)
    // ========================================

    /**
     * Memorizza il sito richiesto prima del login
     */
    public void setRequestedSite(HttpSession session, Integer siteId) {
        session.setAttribute(REQUESTED_SITE_KEY, siteId);
        log.debug("RequestedSiteId memorizzato: {}", siteId);
    }

    /**
     * Memorizza il sito richiesto prima del login - con HttpServletRequest
     */
    public void setRequestedSite(HttpServletRequest request, Integer siteId) {
        setRequestedSite(request.getSession(), siteId);
    }

    /**
     * Ottiene il sito richiesto
     */
    public Integer getRequestedSite(HttpSession session) {
        return (Integer) session.getAttribute(REQUESTED_SITE_KEY);
    }

    /**
     * Ottiene il sito richiesto - con HttpServletRequest
     */
    public Integer getRequestedSite(HttpServletRequest request) {
        return getRequestedSite(request.getSession());
    }

    /**
     * Ottiene e rimuove il sito richiesto
     */
    public Integer getAndClearRequestedSite(HttpSession session) {
        Integer siteId = (Integer) session.getAttribute(REQUESTED_SITE_KEY);
        if (siteId != null) {
            session.removeAttribute(REQUESTED_SITE_KEY);
            log.debug("RequestedSiteId recuperato e rimosso: {}", siteId);
        }
        return siteId;
    }

    /**
     * Ottiene e rimuove il sito richiesto - con HttpServletRequest
     */
    public Integer getAndClearRequestedSite(HttpServletRequest request) {
        return getAndClearRequestedSite(request.getSession());
    }

    /**
     * Pulisce il sito richiesto
     */
    public void clearRequestedSite(HttpSession session) {
        session.removeAttribute(REQUESTED_SITE_KEY);
        log.debug("RequestedSiteId rimosso");
    }

    /**
     * Pulisce il sito richiesto - con HttpServletRequest
     */
    public void clearRequestedSite(HttpServletRequest request) {
        clearRequestedSite(request.getSession());
    }

    // ========================================
    // SESSIONE
    // ========================================

    /**
     * Invalida completamente la sessione
     */
    public void invalidateSession(HttpSession session) {
        session.invalidate();
        log.info("Sessione invalidata");
    }

    /**
     * Invalida completamente la sessione - con HttpServletRequest
     */
    public void invalidateSession(HttpServletRequest request) {
        invalidateSession(request.getSession());
    }

    /**
     * Pulisce tutti i dati dalla sessione (soft logout)
     */
    public void clearSession(HttpSession session) {
        clearUtente(session);
        clearRequestedSite(session);
        log.info("Sessione pulita (soft clear)");
    }

    /**
     * Pulisce tutti i dati dalla sessione (soft logout) - con HttpServletRequest
     */
    public void clearSession(HttpServletRequest request) {
        clearSession(request.getSession());
    }
}