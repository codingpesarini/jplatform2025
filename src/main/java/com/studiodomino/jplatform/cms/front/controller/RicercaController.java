package com.studiodomino.jplatform.cms.front.controller;

import com.studiodomino.jplatform.cms.entity.DatiBase;
import com.studiodomino.jplatform.cms.service.ContentService;
import com.studiodomino.jplatform.shared.config.Configurazione;
import com.studiodomino.jplatform.shared.service.ConfigurazioneService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RicercaController {

    private final ConfigurazioneService configurazioneService;
    private final ContentService contentService;

    @GetMapping("/ricerca")
    public String ricerca(
            @RequestParam(required = false) String campo1,
            @RequestParam(required = false) String site,
            Model model,
            HttpServletRequest request
    ) {

        // 1) recupero configurazione (se sessione scaduta la ricreo)
        Configurazione config = configurazioneService.getOrCreateConfiguration(request);
        model.addAttribute("config", config);

        // 2) siteId: se arriva da query lo uso, altrimenti uso quello del config
        String siteId = (site != null && !site.isBlank())
                ? site.trim()
                : String.valueOf(config.getSito().getId());

        // 3) q: testo da cercare
        String q = (campo1 != null) ? campo1.trim() : "";

        List<DatiBase> results = new ArrayList<>();
        if (!q.isEmpty()) {
            // USA UN METODO CHE ESISTE NEL TUO ContentService:
            results = contentService.searchFullText(siteId, q);

            // limite a 50 (come nel vecchio)
            if (results != null && results.size() > 50) {
                results = results.subList(0, 50);
            }
        }

        model.addAttribute("campo1", q);
        model.addAttribute("results", results != null ? results : new ArrayList<>());
        model.addAttribute("resultsCount", results != null ? results.size() : 0);

        // 4) pagina di output
        return config.getPublicTemplateFolder() + "/front/ricerca";
    }
}