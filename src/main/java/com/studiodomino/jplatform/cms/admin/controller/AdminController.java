package com.studiodomino.jplatform.cms.admin.controller;
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
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ConfigurationService configurationService;

    @GetMapping("")
    public String dashboard(HttpServletRequest request, HttpSession session, Model model) {

        Utente utente = (Utente) session.getAttribute("utente");

        if (utente == null || !utente.isAmministratore()) {
            return "redirect:/login";
        }

        AppConfiguration config = configurationService.getOrCreateConfiguration(request);

        model.addAttribute("config", config);
        model.addAttribute("pageTitle", "Dashboard Amministrazione");

        // Sempre: manager/front/dashboard (non dipende da site)
        return ViewUtils.resolveManagerTemplate("front/dashboard");
    }
}