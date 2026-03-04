package com.studiodomino.jplatform.cms.admin.controller;

import com.studiodomino.jplatform.shared.config.Configurazione;
import com.studiodomino.jplatform.shared.entity.Site;
import com.studiodomino.jplatform.shared.repository.SiteRepository;
import com.studiodomino.jplatform.shared.service.ConfigurazioneService;
import com.studiodomino.jplatform.shared.util.ViewUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("admin/impostazioni/configurazioni")
@RequiredArgsConstructor
@Slf4j
public class ConfigurazioniController {

    private final SiteRepository siteRepository;
    private final ConfigurazioneService configurazioneService;

    // ─── ELENCO ────────────────────────────────────────────────────────────────
    @GetMapping
    public String elenco(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        log.info("=== CONFIGURAZIONI ELENCO === user: {}", config.getUsername());

        List<Site> elenco = siteRepository.findAll();
        model.addAttribute("config", config);
        model.addAttribute("elencoConfigurazione", elenco);

        return ViewUtils.resolveProtectedTemplate("impostazioni/sezioni/elencoConfigurazione");
    }

    // ─── RICERCA ────────────────────────────────────────────────────────────────
    @GetMapping("/ricerca")
    public String ricerca(
            @RequestParam(defaultValue = "") String type,
            @RequestParam(defaultValue = "") String descrizione,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        List<Site> elenco = siteRepository.findAll().stream()
                .filter(s -> type.isEmpty()        || contains(s.getType(), type))
                .filter(s -> descrizione.isEmpty() || contains(s.getDescrizione(), descrizione))
                .toList();

        model.addAttribute("config", config);
        model.addAttribute("elencoConfigurazione", elenco);

        return ViewUtils.resolveProtectedTemplate("impostazioni/sezioni/elencoConfigurazione");
    }

    // ─── NEW ────────────────────────────────────────────────────────────────────
    @GetMapping("/new")
    public String newForm(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        model.addAttribute("config", config);
        model.addAttribute("sito", new Site());

        return ViewUtils.resolveProtectedTemplate("impostazioni/contenuti/dettaglioConfigurazione");
    }

    // ─── OPEN ────────────────────────────────────────────────────────────────────
    @GetMapping("/{id}/open")
    public String open(@PathVariable Integer id,
                       HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        Site sito = siteRepository.findById(id).orElse(null);
        if (sito == null) return "redirect:/admin/configurazioni";

        log.info("=== CONFIGURAZIONI OPEN === id: {}", id);

        session.setAttribute("sito", sito);
        model.addAttribute("config", config);
        model.addAttribute("sito", sito);

        return ViewUtils.resolveProtectedTemplate("impostazioni/contenuti/dettaglioConfigurazione");
    }

    // ─── DUPLICA ────────────────────────────────────────────────────────────────
    @GetMapping("/{id}/duplica")
    public String duplica(@PathVariable Integer id,
                          HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        Site originale = siteRepository.findById(id).orElse(null);
        if (originale == null) return "redirect:/admin/configurazioni";

        log.info("=== CONFIGURAZIONI DUPLICA === da id: {}", id);

        Site copia = new Site();
        // id = null → nuovo record al salvataggio
        copia.setType(originale.getType() + " (2)");
        copia.setDescrizione(originale.getDescrizione());
        copia.setStatus(originale.getStatus());
        copia.setAccesso(originale.getAccesso());
        copia.setLang(originale.getLang());
        copia.setPath2(originale.getPath2());
        copia.setPathRepository(originale.getPathRepository());
        copia.setPathWeb(originale.getPathWeb());
        copia.setKeywords(originale.getKeywords());
        copia.setEmailpop(originale.getEmailpop());
        copia.setEmailsmtp(originale.getEmailsmtp());
        copia.setEmailsmtpauth(originale.getEmailsmtpauth());
        copia.setEmailintestazione(originale.getEmailintestazione());
        copia.setNewsletteremail(originale.getNewsletteremail());
        copia.setNewsletteruser(originale.getNewsletteruser());
        copia.setNewsletterpassword(originale.getNewsletterpassword());
        copia.setServizi1(originale.getServizi1());
        copia.setServizi2(originale.getServizi2());
        copia.setServizi3(originale.getServizi3());
        copia.setServizi4(originale.getServizi4());
        copia.setServizi5(originale.getServizi5());
        copia.setLibero1(originale.getLibero1());
        copia.setLibero2(originale.getLibero2());
        copia.setLibero3(originale.getLibero3());
        copia.setLibero4(originale.getLibero4());
        copia.setLibero5(originale.getLibero5());
        copia.setLibero6(originale.getLibero6());
        copia.setLibero7(originale.getLibero7());
        copia.setLibero8(originale.getLibero8());
        copia.setLibero9(originale.getLibero9());
        copia.setLibero10(originale.getLibero10());

        session.setAttribute("sito", copia);
        model.addAttribute("config", config);
        model.addAttribute("sito", copia);

        return ViewUtils.resolveProtectedTemplate("impostazioni/contenuti/dettaglioConfigurazione");
    }

    // ─── SAVE ────────────────────────────────────────────────────────────────────
    @PostMapping("/save")
    public String save(
            @ModelAttribute("sito") Site sito,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        Site salvato = siteRepository.save(sito);
        log.info("=== CONFIGURAZIONI SAVE === id: {}", salvato.getId());

        // Se è il sito attivo in sessione, aggiorna la configurazione
        Site sitoAttuale = config.getSito();
        if (sitoAttuale != null && sitoAttuale.getId() != null
                && sitoAttuale.getId().equals(salvato.getId())) {
            configurazioneService.setSite(session, salvato);
            log.info("Sito attivo aggiornato in sessione");
        }

        session.setAttribute("sito", salvato);
        model.addAttribute("config", config);
        model.addAttribute("sito", salvato);

        return ViewUtils.resolveProtectedTemplate("impostazioni/contenuti/dettaglioConfigurazione");
    }

    // ─── DELETE SINGOLO (AJAX) ──────────────────────────────────────────────────
    @PostMapping("/{id}/delete")
    @ResponseBody
    public String delete(@PathVariable Integer id) {
        try {
            siteRepository.deleteById(id);
            log.info("=== CONFIGURAZIONI DELETE === id: {}", id);
            return "OK";
        } catch (Exception e) {
            log.error("Errore cancellazione configurazione id={}: {}", id, e.getMessage());
            return "KO";
        }
    }

    // ─── DELETE MULTIPLO (AJAX) ─────────────────────────────────────────────────
    @PostMapping("/delete-multiplo")
    @ResponseBody
    public String deleteMultiplo(@RequestParam(value = "ids") Integer[] ids) {
        try {
            siteRepository.deleteAllByIdInBatch(Arrays.asList(ids));
            log.info("=== CONFIGURAZIONI DELETE MULTIPLO === ids: {}", Arrays.toString(ids));
            return "OK";
        } catch (Exception e) {
            log.error("Errore cancellazione multipla configurazioni: {}", e.getMessage());
            return "KO";
        }
    }

    // ─── UTILITY ────────────────────────────────────────────────────────────────
    private boolean contains(String field, String search) {
        return field != null && field.toLowerCase().contains(search.toLowerCase());
    }
}