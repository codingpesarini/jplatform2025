package com.studiodomino.jplatform.shared.service;

import com.studiodomino.jplatform.shared.config.AppConfiguration;
import com.studiodomino.jplatform.shared.config.JplatformProperties;
import com.studiodomino.jplatform.shared.entity.Site;
import com.studiodomino.jplatform.shared.entity.Utente;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationService {

    @Autowired
    private SiteService siteService;

    @Autowired
    private GruppoService gruppoService;

    @Autowired
    private RuoloService ruoloService;

    @Autowired
    private JplatformProperties jplatformProperties;

    private static final String CONFIG_SESSION_KEY = "appConfig";

    /**
     * Ottieni o crea la configurazione dalla sessione
     */
    public AppConfiguration getOrCreateConfiguration(HttpServletRequest request) {
        HttpSession session = request.getSession();
        AppConfiguration config = (AppConfiguration) session.getAttribute(CONFIG_SESSION_KEY);

        String requestedSiteId = getSiteIdFromRequest(request);

        if (config == null || config.getSito() == null ||
                !config.getIdSito().equals(requestedSiteId)) {

            config = loadBaseConfiguration(request, requestedSiteId);
            session.setAttribute(CONFIG_SESSION_KEY, config);
        }

        return config;
    }

    /**
     * Carica la configurazione base dal database
     */
    public AppConfiguration loadBaseConfiguration(HttpServletRequest request, String siteId) {
        AppConfiguration config = new AppConfiguration();

        // Usa site_id passato o default da properties
        if (siteId == null || siteId.isEmpty()) {
            siteId = jplatformProperties.getSite().getDefaultId();
        }

        try {
            Site site = siteService.getSiteById(Integer.parseInt(siteId));

            if (site == null) {
                // Se site non esiste, carica default da properties
                site = siteService.getSiteById(
                        Integer.parseInt(jplatformProperties.getSite().getDefaultId())
                );
            }

            if (site != null) {
                config.setSito(site);
            }

        } catch (NumberFormatException e) {
            Site site = siteService.getSiteById(
                    Integer.parseInt(jplatformProperties.getSite().getDefaultId())
            );
            if (site != null) {
                config.setSito(site);
            }
        }

        config.setGruppi(gruppoService.getAllGruppi());
        config.setRuoli(ruoloService.getAllRuoli());
        config.setAmministratore(null);
        config.setUtente(null);

        return config;
    }

    /**
     * Ottieni site_id dalla request
     */
    private String getSiteIdFromRequest(HttpServletRequest request) {

        // 1. Parametro GET/POST "site"
        String siteId = request.getParameter("site");
        if (siteId != null && !siteId.isEmpty()) {
            return siteId;
        }

        // 2. Attributo request "site"
        Object siteAttr = request.getAttribute("site");
        if (siteAttr != null) {
            return siteAttr.toString();
        }

        // 3. Parametro context (per retrocompatibilità)
        String contextSiteId = request.getSession().getServletContext()
                .getInitParameter("IDSITE");
        if (contextSiteId != null && !contextSiteId.isEmpty()) {
            return contextSiteId;
        }

        // 4. Default da properties
        return jplatformProperties.getSite().getDefaultId();
    }

    /**
     * Refresh configurazione
     */
    public AppConfiguration refreshConfiguration(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute(CONFIG_SESSION_KEY);

        String siteId = getSiteIdFromRequest(request);
        AppConfiguration config = loadBaseConfiguration(request, siteId);
        session.setAttribute(CONFIG_SESSION_KEY, config);

        return config;
    }

    /**
     * Cambia site attivo
     */
    public AppConfiguration changeSite(HttpServletRequest request, String newSiteId) {
        HttpSession session = request.getSession();
        session.removeAttribute(CONFIG_SESSION_KEY);

        AppConfiguration config = loadBaseConfiguration(request, newSiteId);
        session.setAttribute(CONFIG_SESSION_KEY, config);

        return config;
    }

    /**
     * Salva utente loggato in configurazione
     */
    public void setUtenteLoggato(HttpServletRequest request, Utente utente) {
        HttpSession session = request.getSession();
        AppConfiguration config = getOrCreateConfiguration(request);

        if (utente.getRole1() != null &&
                (utente.getRole1().equals("a") || utente.getRole1().equals("s"))) {
            config.setAmministratore(utente);
            config.setUtente(null);
        } else {
            config.setUtente(utente);
            config.setAmministratore(null);
        }

        session.setAttribute(CONFIG_SESSION_KEY, config);
    }

    /**
     * Ottieni URL di redirect
     */
    public String getStartupRedirect(AppConfiguration config) {
        if (config == null || config.getSito() == null) {
            return "/login";
        }

        Integer accesso = config.getSito().getAccesso();
        if (accesso == null) {
            accesso = 0;
        }

        return switch (accesso) {
            case 0 -> "/login";
            case 1 -> "/admin";
            case 2 -> "/cms/front";
            case 3 -> "/protocollo";
            case 4 -> "/ardsu/borse";
            case 5 -> "/ardsu/mensa";
            case 6 -> "/ardsu/alloggi";
            case 7 -> "/workflow";
            case 8 -> "/crm";
            default -> "/login";
        };
    }
}