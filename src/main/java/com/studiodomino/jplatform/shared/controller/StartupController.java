package com.studiodomino.jplatform.shared.controller;

import com.studiodomino.jplatform.shared.config.Configurazione;
import com.studiodomino.jplatform.shared.entity.Site;
import com.studiodomino.jplatform.shared.entity.Utente;
import com.studiodomino.jplatform.shared.enums.ModuloApplicativo;
import com.studiodomino.jplatform.shared.service.ConfigurazioneService;
import com.studiodomino.jplatform.shared.service.SiteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StartupController {

    private final SiteService siteService;
    private final ConfigurazioneService configurazioneService;

    @GetMapping("/")
    public String startupDefault(
            HttpServletRequest request,
            HttpServletResponse response) {
        return startup("1", null, request, response);
    }

    // SOLO numeri (id sito) oppure type "testuale" MA escludendo rotte applicative
    @GetMapping("/{idsite:(?!admin|login|front|error|logout|api|manager|cms|crm|favicon\\.ico|images|css|js|fonts|assets|static|webjars).*}")
    public String startupConIdsite(
            @PathVariable String idsite,
            HttpServletRequest request,
            HttpServletResponse response) {
        return startup(idsite, null, request, response);
    }

    @GetMapping("/{idsite:(?!admin|login|front|error|logout|api|manager|cms|crm|favicon\\.ico|images|css|js|fonts|assets|static|webjars).*}/{uscita:true|false}")
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
            configurazioneService.invalidateSession(session);
            return "redirect:/";
        }

        Configurazione configCore = configurazioneService.getOrCreateConfiguration(request);

        // Carica sito specifico se richiesto
        if (idsite != null && !idsite.isEmpty()) {
            try {
                Integer accessoId = Integer.parseInt(idsite);
                Site site = siteService.findById(accessoId);
                if (site != null) {
                    configurazioneService.setSite(session, site);
                    configCore = configurazioneService.getConfig(session);
                }
            } catch (NumberFormatException e) {
                Site site = siteService.findByType(idsite);
                if (site != null) {
                    configurazioneService.setSite(session, site);
                    configCore = configurazioneService.getConfig(session);
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
            Utente utente = configurazioneService.getAmministratore(session);

            if (utente != null) {
                log.info("→ Utente loggato: {}", utente.getUsername());
                String endpoint = ModuloApplicativo.getEndpoint(utente.getL2());
                return "redirect:/" + endpoint;
            } else {
                log.info("→ Login richiesto");

                // ✅ MEMORIZZA IL SITO RICHIESTO per redirect post-login
                configurazioneService.setRequestedSite(session, site.getId());

                return "redirect:/login";
            }
        } else {
            log.error("Valore idsite non valido: {}", site.getAccesso());
            return "redirect:/error";
        }
    }
}