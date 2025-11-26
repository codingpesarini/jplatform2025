package com.studiodomino.jplatform.cms.front.controller;

import com.studiodomino.jplatform.shared.config.AppConfiguration;
import com.studiodomino.jplatform.shared.entity.Utente;
import com.studiodomino.jplatform.shared.service.ConfigurationService;
import com.studiodomino.jplatform.shared.util.ViewUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class FrontController {

    @Autowired
    private ConfigurationService configurationService;

    @GetMapping("/cms/front")
    public String home(HttpServletRequest request, HttpSession session, Model model) {

        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null) {
            return "redirect:/login";
        }

        AppConfiguration config = configurationService.getOrCreateConfiguration(request);

        model.addAttribute("utente", utente);
        model.addAttribute("config", config);
        model.addAttribute("pageTitle", "Home");

        // Risolve: site01/front/home o site02/front/home
        return ViewUtils.resolveTemplate(config, "front/home");
    }
}