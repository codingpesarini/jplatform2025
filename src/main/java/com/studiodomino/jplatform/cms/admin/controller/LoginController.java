package com.studiodomino.jplatform.cms.admin.controller;

import com.studiodomino.jplatform.shared.config.ConfigurazioneCore;
import com.studiodomino.jplatform.shared.entity.Site;
import com.studiodomino.jplatform.shared.entity.Utente;
import com.studiodomino.jplatform.shared.enums.ModuloApplicativo;
import com.studiodomino.jplatform.shared.service.ConfigurationService;
import com.studiodomino.jplatform.shared.service.SiteService;
import com.studiodomino.jplatform.shared.service.UtenteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final UtenteService utenteService;
    private final ConfigurationService configurationService;
    private final SiteService siteService;

    /**
     * GET /login - Mostra form di login
     */
    @GetMapping("/login")
    public String showLogin(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        ConfigurazioneCore configCore = configurationService.getOrCreateConfiguration(request);

        // Se già loggato, redirect
        Utente utente = configurationService.getUtente(session);
        if (utente != null) {
            String endpoint = ModuloApplicativo.getEndpoint(utente.getL2());
            log.info("Utente già loggato, redirect a: {}", endpoint);
            return "redirect:/" + endpoint;
        }

        model.addAttribute("config", configCore);
        return "auth/login";  // ✅ Mostra templates/auth/login.html
    }

    /**
     * POST /login - Processa login
     */
    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) boolean remember,
            HttpServletRequest request,
            Model model) {

        log.info("=== LOGIN === username: {}", username);

        HttpSession session = request.getSession();
        ConfigurazioneCore configCore = configurationService.getOrCreateConfiguration(request);

        // Autentica
        Utente utente = utenteService.authenticate(username, password);

        if (utente != null) {
            log.info("✓ Login riuscito per: {}", username);

            // Aggiorna statistiche accesso
            utenteService.aggiornaStatisticheAccesso(utente, request);

            // Imposta utente in sessione
            configurationService.setUtente(session, utente);

            // Cookie "remember me"
            if (remember) {
                log.info("Remember me richiesto (TODO: implementare)");
            }

            // ════════════════════════════════════════════════════
            // DETERMINA DESTINAZIONE POST-LOGIN
            // ════════════════════════════════════════════════════

            // 1. Controlla se c'era un sito richiesto prima del login
            Integer requestedSiteId = configurationService.getAndClearRequestedSite(session);

            if (requestedSiteId != null) {
                Site requestedSite = siteService.findById(requestedSiteId);

                if (requestedSite != null) {
                    log.info("→ Redirect a sito richiesto: {}", requestedSite.getType());
                    configurationService.setSite(session, requestedSite);
                    String endpoint = ModuloApplicativo.getEndpoint(utente.getL2());
                    return "redirect:/" + endpoint;
                }
            }

            // 2. Altrimenti usa campo l2 dell'utente
            String l2 = utente.getL2();
            String endpoint = ModuloApplicativo.getEndpoint(l2);
            String descrizione = ModuloApplicativo.getDescrizione(l2);

            log.info("→ L2: {}", l2 != null && !l2.isEmpty() ? l2 : "null/vuoto");
            log.info("→ Modulo: {}", descrizione);
            log.info("→ Endpoint: {}", endpoint);

            return "redirect:/" + endpoint;

        } else {
            // ✅ ERRORE: ritorna allo STESSO template del GET
            log.warn("✗ Login fallito per: {}", username);
            model.addAttribute("error", "Credenziali non valide");
            model.addAttribute("config", configCore);
            return "auth/login";  // ✅ CAMBIATO da "manager/front/login"
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        log.info("=== LOGOUT ===");
        HttpSession session = request.getSession();
        configurationService.invalidateSession(session);
        return "redirect:/?uscita=true";
    }
}