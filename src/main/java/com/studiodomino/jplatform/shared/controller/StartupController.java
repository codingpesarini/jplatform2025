package com.studiodomino.jplatform.shared.controller;

import com.studiodomino.jplatform.shared.config.AppConfiguration;
import com.studiodomino.jplatform.shared.entity.Utente;
import com.studiodomino.jplatform.shared.service.ConfigurationService;
import com.studiodomino.jplatform.shared.service.UtenteService;
import com.studiodomino.jplatform.shared.util.CryptBean;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StartupController {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private UtenteService utenteService;

    private static final String COOKIE_NAME = "JPlatformEdit";

    /**
     * PUNTO DI INGRESSO PRINCIPALE
     * Equivalente di /Startup.do in Struts
     *
     * Esempi:
     * http://localhost:8080/                  → carica site id=1 (default)
     * http://localhost:8080/?site=2           → carica site id=2
     * http://localhost:8080/?site=3&reload=true → carica site id=3 e ricarica config
     */
    @GetMapping("/")
    public String startup(
            @RequestParam(required = false) String site,
            @RequestParam(required = false) Boolean reload,
            HttpServletRequest request,
            HttpServletResponse response) {

        HttpSession session = request.getSession();

        // 1. Carica o aggiorna configurazione (gestisce automaticamente site)
        AppConfiguration config = configurationService.getOrCreateConfiguration(request);

        // Se richiesto reload esplicito, forza ricarica
        if (reload != null && reload) {
            config = configurationService.refreshConfiguration(request);
        }

        // 2. Controlla se c'è un utente loggato in sessione
        Utente utente = (Utente) session.getAttribute("utente");

        // 3. Se non c'è utente in sessione, prova con cookie
        if (utente == null) {
            utente = loginFromCookie(request, response);
            if (utente != null && utente.getStatoaccesso() == 0) {
                session.setAttribute("utente", utente);
                configurationService.setUtenteLoggato(request, utente);
            }
        }

        // 4. Se ancora non c'è utente → redirect al login
        if (utente == null || utente.getStatoaccesso() != 0) {
            return "redirect:/login";
        }

        // 5. Utente loggato → redirect in base a configurazione
        String redirectUrl = getRedirectUrl(utente, config);

        return "redirect:" + redirectUrl;
    }

    /**
     * Cambia site attivo
     * Esempio: http://localhost:8080/changeSite?site=2
     */
    @GetMapping("/changeSite")
    public String changeSite(
            @RequestParam String site,
            HttpServletRequest request) {

        // Cambia site in sessione
        configurationService.changeSite(request, site);

        // Redirect alla home per ricaricare
        return "redirect:/";
    }

    /**
     * Tenta login automatico da cookie
     */
    private Utente loginFromCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                try {
                    String encryptedId = cookie.getValue();
                    String userId = CryptBean.hexToString(encryptedId);

                    Utente utente = utenteService.loginByCookie(userId, "1", request);

                    if (utente != null && utente.getStatoaccesso() == 0) {
                        return utente;
                    }
                } catch (Exception e) {
                    // Cookie non valido, cancellalo
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }

        return null;
    }

    /**
     * Determina URL di redirect in base a utente e configurazione
     */
    private String getRedirectUrl(Utente utente, AppConfiguration config) {

        // PRIORITÀ 1: Se utente ha L2 specifico, usa quello
        String l2 = utente.getL2();
        if (l2 != null && !l2.isEmpty() && !"0".equals(l2)) {
            return "/dashboard/" + l2;
        }

        // PRIORITÀ 2: Usa "accesso" dal Site
        String redirectUrl = configurationService.getStartupRedirect(config);

        return redirectUrl;
    }
}