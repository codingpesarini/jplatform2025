package com.studiodomino.jplatform.shared.controller;

import com.studiodomino.jplatform.shared.config.AppConfiguration;
import com.studiodomino.jplatform.shared.entity.Utente;
import com.studiodomino.jplatform.shared.service.ConfigurationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HomeController {

    @Autowired
    private ConfigurationService configurationService;

    /**
     * Dashboard generico con parametro L2
     * Usato quando utente non ha un modulo specifico
     */
    @GetMapping("/dashboard/{l2}")
    public String dashboard(
            @PathVariable String l2,
            HttpServletRequest request,
            Model model) {

        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utente");

        // Verifica login
        if (utente == null) {
            return "redirect:/login";
        }

        // Carica configurazione
        AppConfiguration config = configurationService.getOrCreateConfiguration(request);

        model.addAttribute("utente", utente);
        model.addAttribute("config", config);
        model.addAttribute("l2", l2);

        // Template generico basato su L2
        return "dashboard/index";
    }

    /**
     * Home page personalizzata
     * Mostra link a tutti i moduli accessibili dall'utente
     */
    @GetMapping("/home")
    public String home(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utente");

        if (utente == null) {
            return "redirect:/login";
        }

        AppConfiguration config = configurationService.getOrCreateConfiguration(request);

        model.addAttribute("utente", utente);
        model.addAttribute("config", config);

        return "home";
    }
}