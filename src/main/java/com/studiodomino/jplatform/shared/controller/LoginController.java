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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private UtenteService utenteService;

    @Autowired
    private ConfigurationService configurationService;

    private static final String COOKIE_NAME = "JPlatformEdit";

    /**
     * Mostra pagina login
     */
    @GetMapping("/login")
    public String showLoginPage(
            @RequestParam(required = false) String error,
            HttpServletRequest request,
            Model model) {

        // Se già loggato, redirect a home
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utente");

        if (utente != null && utente.getStatoaccesso() == 0) {
            return "redirect:/";
        }

        // Messaggi di errore
        if ("access_denied".equals(error)) {
            model.addAttribute("error", "Accesso negato. Non hai i permessi necessari.");
        }

        return "auth/login";
    }

    /**
     * Processa login
     */
    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String remember,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) {

        // Carica configurazione
        AppConfiguration config = configurationService.getOrCreateConfiguration(request);

        // Tenta login
        String idSite = config.getIdSito();
        Utente utente = utenteService.login(username, password, idSite, request);

        // Verifica risultato
        if (utente.getStatoaccesso() == 0) {
            // ✅ LOGIN OK

            HttpSession session = request.getSession();
            session.setAttribute("utente", utente);

            // Salva utente in configurazione
            configurationService.setUtenteLoggato(request, utente);

            // Cookie "remember me"
            if ("1".equals(remember)) {
                String encryptedId = CryptBean.stringToHex(utente.getId().toString());
                Cookie cookie = new Cookie(COOKIE_NAME, encryptedId);
                cookie.setMaxAge(7 * 24 * 60 * 60); // 7 giorni
                cookie.setPath("/");
                cookie.setHttpOnly(true); // Sicurezza
                response.addCookie(cookie);
            }

            // Redirect alla home (StartupController smista)
            return "redirect:/";

        } else {
            // ❌ LOGIN FALLITO

            String error = switch (utente.getStatoaccesso()) {
                case 1 -> "Password errata";
                case 2 -> "Accesso non autorizzato";
                case 3 -> "Username non trovato";
                default -> "Errore durante il login";
            };

            model.addAttribute("error", error);
            model.addAttribute("username", username);
            return "auth/login";
        }
    }

    /**
     * Logout
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        // Invalida sessione
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Cancella cookie
        Cookie cookie = new Cookie(COOKIE_NAME, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:/login?logout=true";
    }
}