package com.studiodomino.jplatform.crm.controller;

import com.studiodomino.jplatform.shared.config.Configurazione;
import com.studiodomino.jplatform.shared.service.ConfigurazioneService;
import com.studiodomino.jplatform.shared.util.ViewUtils;
import com.studiodomino.jplatform.crm.entity.Numeratore;
import com.studiodomino.jplatform.crm.repository.NumeratoreRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Controller
@RequestMapping("/admin/numeratori")
@RequiredArgsConstructor
@Slf4j
public class NumeratoriController {

    private final NumeratoreRepository numeratoreRepository;
    private final ConfigurazioneService configurazioneService;

    // ─── ELENCO ──────────────────────────────────────────────────────────────
    @GetMapping
    public String elenco(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        log.info("=== NUMERATORI ELENCO === user: {}", config.getUsername());

        model.addAttribute("config", config);
        model.addAttribute("elencoNumeratori", numeratoreRepository.findAll());

        return ViewUtils.resolveProtectedTemplate("crm/sezioni/elencoNumeratori");
    }

    // ─── NEW ─────────────────────────────────────────────────────────────────
    @GetMapping("/new")
    public String newForm(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        model.addAttribute("config", config);
        model.addAttribute("numeratore", new Numeratore());

        return ViewUtils.resolveProtectedTemplate("crm/contenuti/dettaglioNumeratori");
    }

    // ─── OPEN ────────────────────────────────────────────────────────────────
    @GetMapping("/{id}/open")
    public String open(@PathVariable Long id, HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        Numeratore numeratore = numeratoreRepository.findById(id).orElse(null);
        if (numeratore == null) return "redirect:/admin/numeratori";

        log.info("=== NUMERATORI OPEN === id: {}", id);

        model.addAttribute("config", config);
        model.addAttribute("numeratore", numeratore);

        return ViewUtils.resolveProtectedTemplate("crm/contenuti/dettaglioNumeratori");
    }

    // ─── SAVE ────────────────────────────────────────────────────────────────
    @PostMapping("/save")
    @Transactional
    public String save(@ModelAttribute("numeratore") Numeratore numeratore,
                       HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        Numeratore salvato = numeratoreRepository.save(numeratore);
        log.info("=== NUMERATORI SAVE === id: {}", salvato.getId());

        model.addAttribute("config", config);
        model.addAttribute("numeratore", salvato);

        return ViewUtils.resolveProtectedTemplate("crm/contenuti/dettaglioNumeratori");
    }

    // ─── DELETE SINGOLO (AJAX) ───────────────────────────────────────────────
    @PostMapping("/{id}/delete")
    @ResponseBody
    @Transactional
    public String delete(@PathVariable Long id) {
        try {
            numeratoreRepository.deleteById(id);
            log.info("=== NUMERATORI DELETE === id: {}", id);
            return "OK";
        } catch (Exception e) {
            log.error("Errore cancellazione numeratore id={}: {}", id, e.getMessage());
            return "KO";
        }
    }

    // ─── DELETE MULTIPLO (AJAX) ──────────────────────────────────────────────
    @PostMapping("/delete-multiplo")
    @ResponseBody
    @Transactional
    public String deleteMultiplo(@RequestParam(value = "ids") Long[] ids) {
        try {
            numeratoreRepository.deleteAllByIdInBatch(Arrays.asList(ids));
            log.info("=== NUMERATORI DELETE MULTIPLO === ids: {}", Arrays.toString(ids));
            return "OK";
        } catch (Exception e) {
            log.error("Errore cancellazione multipla: {}", e.getMessage());
            return "KO";
        }
    }
}