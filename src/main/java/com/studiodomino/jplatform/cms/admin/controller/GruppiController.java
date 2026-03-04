package com.studiodomino.jplatform.cms.admin.controller;

import com.studiodomino.jplatform.shared.config.Configurazione;
import com.studiodomino.jplatform.shared.entity.Gruppo;
import com.studiodomino.jplatform.shared.repository.GruppoRepository;
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
@RequestMapping("/admin/impostazioni/gruppi")
@RequiredArgsConstructor
@Slf4j
public class GruppiController {

    private final GruppoRepository gruppoRepository;
    private final ConfigurazioneService configurazioneService;

    // ─── ELENCO ────────────────────────────────────────────────────────────────
    @GetMapping
    public String elenco(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        log.info("=== GRUPPI ELENCO === user: {}", config.getUsername());

        List<Gruppo> elenco = gruppoRepository.findAll()
                .stream()
                .sorted((a, b) -> {
                    String na = a.getNome() != null ? a.getNome() : "";
                    String nb = b.getNome() != null ? b.getNome() : "";
                    return na.compareToIgnoreCase(nb);
                })
                .toList();

        model.addAttribute("config", config);
        model.addAttribute("elencoGruppo", elenco);

        return ViewUtils.resolveProtectedTemplate("impostazioni/sezioni/elencoGruppo");
    }

    // ─── NEW ────────────────────────────────────────────────────────────────────
    @GetMapping("/new")
    public String newForm(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        log.info("=== GRUPPI NEW ===");

        model.addAttribute("config", config);
        model.addAttribute("gruppo", new Gruppo());

        return ViewUtils.resolveProtectedTemplate("impostazioni/contenuti/dettaglioGruppo");
    }

    // ─── OPEN ────────────────────────────────────────────────────────────────────
    @GetMapping("/{id}/open")
    public String open(@PathVariable Integer id,
                       HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        Gruppo gruppo = gruppoRepository.findById(id).orElse(null);
        if (gruppo == null) return "redirect:/admin/gruppi";

        log.info("=== GRUPPI OPEN === id: {}", id);

        session.setAttribute("gruppo", gruppo);
        model.addAttribute("config", config);
        model.addAttribute("gruppo", gruppo);

        return ViewUtils.resolveProtectedTemplate("impostazioni/contenuti/dettaglioGruppo");
    }

    // ─── DUPLICA ────────────────────────────────────────────────────────────────
    @GetMapping("/{id}/duplica")
    public String duplica(@PathVariable Integer id,
                          HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        Gruppo originale = gruppoRepository.findById(id).orElse(null);
        if (originale == null) return "redirect:/admin/gruppi";

        log.info("=== GRUPPI DUPLICA === da id: {}", id);

        Gruppo copia = new Gruppo();
        // id = null → nuovo record al salvataggio
        copia.setNome(originale.getNome() + " (2)");
        copia.setL1(originale.getL1());
        copia.setL2(originale.getL2());
        copia.setL3(originale.getL3());
        copia.setL4(originale.getL4());
        copia.setL5(originale.getL5());
        copia.setL6(originale.getL6());
        copia.setL7(originale.getL7());
        copia.setL8(originale.getL8());
        copia.setL9(originale.getL9());
        copia.setL10(originale.getL10());
        copia.setDatamodifica("");

        session.setAttribute("gruppo", copia);
        model.addAttribute("config", config);
        model.addAttribute("gruppo", copia);

        return ViewUtils.resolveProtectedTemplate("impostazioni/contenuti/dettaglioGruppo");
    }

    // ─── SAVE ────────────────────────────────────────────────────────────────────
    @PostMapping("/save")
    public String save(
            @ModelAttribute("gruppo") Gruppo gruppo,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        Gruppo salvato = gruppoRepository.save(gruppo);
        log.info("=== GRUPPI SAVE === id: {}", salvato.getId());

        session.setAttribute("gruppo", salvato);
        model.addAttribute("config", config);
        model.addAttribute("gruppo", salvato);

        return ViewUtils.resolveProtectedTemplate("impostazioni/contenuti/dettaglioGruppo");
    }

    // ─── DELETE SINGOLO (AJAX) ──────────────────────────────────────────────────
    @PostMapping("/{id}/delete")
    @ResponseBody
    public String delete(@PathVariable Integer id) {
        try {
            gruppoRepository.deleteById(id);
            log.info("=== GRUPPI DELETE === id: {}", id);
            return "OK";
        } catch (Exception e) {
            log.error("Errore cancellazione gruppo id={}: {}", id, e.getMessage());
            return "KO";
        }
    }

    // ─── DELETE MULTIPLO (AJAX) ─────────────────────────────────────────────────
    @PostMapping("/delete-multiplo")
    @ResponseBody
    public String deleteMultiplo(@RequestParam(value = "ids") Integer[] ids) {
        try {
            gruppoRepository.deleteAllByIdInBatch(Arrays.asList(ids));
            log.info("=== GRUPPI DELETE MULTIPLO === ids: {}", Arrays.toString(ids));
            return "OK";
        } catch (Exception e) {
            log.error("Errore cancellazione multipla gruppi: {}", e.getMessage());
            return "KO";
        }
    }
}