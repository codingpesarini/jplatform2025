package com.studiodomino.jplatform.shared.controller;

import com.studiodomino.jplatform.shared.config.ConfigurazioneCore;
import com.studiodomino.jplatform.shared.entity.Site;
import com.studiodomino.jplatform.shared.entity.Utente;
import com.studiodomino.jplatform.shared.enums.ModuloApplicativo;
import com.studiodomino.jplatform.shared.service.ConfigurationService;
import com.studiodomino.jplatform.shared.service.SiteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StartupController {

    private final SiteService siteService;
    private final ConfigurationService configurationService;

    @GetMapping("/")
    public String startupDefault(
            HttpServletRequest request,
            HttpServletResponse response) {
        return startup("1", null, request, response);
    }

    @GetMapping("/{idsite}")
    public String startupConIdsite(
            @PathVariable String idsite,
            HttpServletRequest request,
            HttpServletResponse response) {
        return startup(idsite, null, request, response);
    }

    @GetMapping("/{idsite}/{uscita}")
    public String startupCompleto(
            @PathVariable String idsite,
            @PathVariable Boolean uscita,
            HttpServletRequest request,
            HttpServletResponse response) {
        return startup(idsite, uscita, request, response);
    }

    private String startup(
            String idsite,
            Boolean uscita,
            HttpServletRequest request,
            HttpServletResponse response) {

        log.info("=== STARTUP === idsite: {}, uscita: {}", idsite, uscita);

        HttpSession session = request.getSession();

        if (Boolean.TRUE.equals(uscita)) {
            configurationService.invalidateSession(session);
            return "redirect:/";
        }

        ConfigurazioneCore configCore = configurationService.getOrCreateConfiguration(request);

        // Carica sito specifico se richiesto
        if (idsite != null && !idsite.isEmpty()) {
            try {
                Integer accessoId = Integer.parseInt(idsite);
                Site site = siteService.findById(accessoId);
                if (site != null) {
                    configurationService.setSite(session, site);
                    configCore = configurationService.getConfig(session);
                }
            } catch (NumberFormatException e) {
                Site site = siteService.findByType(idsite);
                if (site != null) {
                    configurationService.setSite(session, site);
                    configCore = configurationService.getConfig(session);
                }
            }
        }

        Site site = configCore.getSito();

        log.info("Sito: id={}, type={}, idsite={}",
                site.getId(), site.getType(), site.getAccesso());

        // Routing
        if (site.getAccesso() != null && site.getAccesso() == 2) {
            log.info("→ SITO PUBBLICO");
            return "redirect:/front";

        } else if (site.getAccesso() != null && site.getAccesso() == 1) {
            Utente utente = configurationService.getUtente(session);

            if (utente != null) {
                log.info("→ Utente loggato: {}", utente.getUsername());
                String endpoint = ModuloApplicativo.getEndpoint(utente.getL2());
                return "redirect:/" + endpoint;
            } else {
                log.info("→ Login richiesto");

                // ✅ MEMORIZZA IL SITO RICHIESTO per redirect post-login
                configurationService.setRequestedSite(session, site.getId());

                return "redirect:/login";
            }
        } else {
            log.error("Valore idsite non valido: {}", site.getAccesso());
            return "redirect:/error";
        }
    }
}