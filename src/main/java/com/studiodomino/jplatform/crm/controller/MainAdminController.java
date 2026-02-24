package com.studiodomino.jplatform.crm.controller;

import com.studiodomino.jplatform.cms.service.ContentService;
import com.studiodomino.jplatform.crm.repository.RegistroLeadRepository;
import com.studiodomino.jplatform.shared.config.Configurazione;
import com.studiodomino.jplatform.shared.repository.UtenteEsternoRepository;
import com.studiodomino.jplatform.shared.service.ConfigurazioneService;
import com.studiodomino.jplatform.shared.util.ViewUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller per la dashboard principale del CRM admin.
 * Conversione da MainAdmin.java (Struts) a Spring MVC.
 *
 * Mapping URL:
 *   GET /admin/crm            → dashboard
 *   GET /admin/crm/logout     → logoutAmministratore
 */
@Controller
@RequestMapping("/admin/crm/dashboard")
@RequiredArgsConstructor
@Slf4j
public class MainAdminController {

    private final ConfigurazioneService configurazioneService;
    private final UtenteEsternoRepository utenteEsternoRepository;
    private final RegistroLeadRepository registroLeadRepository;

    // =====================================================================
    // DASHBOARD
    // Vecchio: dashboard() → forward "successDashboard"
    // =====================================================================

    @GetMapping
    public String dashboard(
            @RequestParam(value = "all", required = false) String all,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            String idAmministratore = String.valueOf(config.getAmministratore().getId());
            boolean isAdminOrSuper = "s".equals(config.getAmministratore().getRole1())
                    || "a".equals(config.getAmministratore().getRole1());

            // Ultimi 10 utenti esterni
            // Vecchio: DAO.getElencoUtenteEsterno("order by id desc limit 0,10")
            // Usa findByStatusOrderBy e limita a 10 — chiedi a Raffaele se ha findTop10
            model.addAttribute("dashUltimiUtentiEsterni",
                    utenteEsternoRepository
                            .findByStatusOrderByCognomeAscNomeAsc("1")
                            .stream().limit(10).toList());

            // Todo lead aperti (stato != 4 e != 5)
            // Vecchio: getRegistroLeadUtenteSQL("stato !=4 AND stato!=5 [and idamministratore=X]")
            if (all != null || isAdminOrSuper) {
                model.addAttribute("todoLead",
                        registroLeadRepository.findByStatoNotInOrderByIdDesc(
                                List.of("4", "5")));
            } else {
                model.addAttribute("todoLead",
                        registroLeadRepository.findByIdamministratoreAndStatoNotInOrderByIdDesc(
                                Integer.parseInt(idAmministratore), List.of("4", "5")));
            }

            // TODO: sostituire con CommentoRepository quando disponibile
            model.addAttribute("dashCommenti", 0);
            model.addAttribute("dashUltimiCommenti", List.of());

            // TODO: sostituire con AreaInteresseRepository quando disponibile
            model.addAttribute("areeInteresse", List.of());

            model.addAttribute("config", config);

        } catch (Exception e) {
            log.error("Errore dashboard CRM", e);
        }

        return ViewUtils.resolveProtectedTemplate("crm/sezioni/dashboard");
    }

    // =====================================================================
    // LOGOUT
    // Vecchio: logoutAmministratore() → forward "successExit"
    // =====================================================================

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);

        try {
            if (config.getSito() != null) {
                String cookieName = "JPlatformEdit" + config.getSito().getCheck();
                Cookie cookie = new Cookie(cookieName, "");
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
            config.setAmministratore(null);
            session.setAttribute("configCore", config);
        } catch (Exception e) {
            log.error("Errore logout CRM", e);
        }

        return "redirect:/login";
    }
}