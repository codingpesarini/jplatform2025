package com.studiodomino.jplatform.cms.admin.controller;

import com.studiodomino.jplatform.shared.config.Configurazione;
import com.studiodomino.jplatform.shared.entity.Ruolo;
import com.studiodomino.jplatform.shared.repository.RuoloRepository;
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
@RequestMapping("/admin/impostazioni/ruoli")
@RequiredArgsConstructor
@Slf4j
public class RuoliController {

    private final RuoloRepository ruoloRepository;
    private final ConfigurazioneService configurazioneService;

    // ─── ELENCO ────────────────────────────────────────────────────────────────
    @GetMapping
    public String elenco(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        log.info("=== RUOLI ELENCO === user: {}", config.getUsername());

        List<Ruolo> elenco = ruoloRepository.findAll()
                .stream()
                .sorted((a, b) -> {
                    String na = a.getNome() != null ? a.getNome() : "";
                    String nb = b.getNome() != null ? b.getNome() : "";
                    return na.compareToIgnoreCase(nb);
                })
                .toList();

        model.addAttribute("config", config);
        model.addAttribute("elencoRuolo", elenco);

        return ViewUtils.resolveProtectedTemplate("impostazioni/sezioni/elencoRuolo");
    }

    // ─── NEW ────────────────────────────────────────────────────────────────────
    @GetMapping("/new")
    public String newForm(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        log.info("=== RUOLI NEW ===");

        model.addAttribute("config", config);
        model.addAttribute("ruolo", new Ruolo());

        return ViewUtils.resolveProtectedTemplate("impostazioni/contenuti/dettaglioRuolo");
    }

    // ─── OPEN ────────────────────────────────────────────────────────────────────
    @GetMapping("/{id}/open")
    public String open(@PathVariable Integer id,
                       HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        Ruolo ruolo = ruoloRepository.findById(id).orElse(null);
        if (ruolo == null) return "redirect:/admin/ruoli";

        log.info("=== RUOLI OPEN === id: {}", id);

        session.setAttribute("ruolo", ruolo);
        model.addAttribute("config", config);
        model.addAttribute("ruolo", ruolo);

        return ViewUtils.resolveProtectedTemplate("impostazioni/contenuti/dettaglioRuolo");
    }

    // ─── DUPLICA ────────────────────────────────────────────────────────────────
    @GetMapping("/{id}/duplica")
    public String duplica(@PathVariable Integer id,
                          HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        Ruolo originale = ruoloRepository.findById(id).orElse(null);
        if (originale == null) return "redirect:/admin/ruoli";

        log.info("=== RUOLI DUPLICA === da id: {}", id);

        Ruolo copia = new Ruolo();
        // id = null → nuovo record al salvataggio
        copia.setNome(originale.getNome() + " (2)");
        copia.setDescrizione(originale.getDescrizione());
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
        copia.setS1(originale.getS1());
        copia.setS2(originale.getS2());
        copia.setS3(originale.getS3());
        copia.setS4(originale.getS4());
        copia.setS5(originale.getS5());
        copia.setS6(originale.getS6());
        copia.setS7(originale.getS7());
        copia.setS8(originale.getS8());
        copia.setS9(originale.getS9());
        copia.setS10(originale.getS10());
        copia.setDatamodifica("");

        session.setAttribute("ruolo", copia);
        model.addAttribute("config", config);
        model.addAttribute("ruolo", copia);

        return ViewUtils.resolveProtectedTemplate("impostazioni/contenuti/dettaglioRuolo");
    }

    // ─── SAVE ────────────────────────────────────────────────────────────────────
    @PostMapping("/save")
    public String save(
            @ModelAttribute("ruolo") Ruolo ruolo,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        Ruolo salvato = ruoloRepository.save(ruolo);
        log.info("=== RUOLI SAVE === id: {}", salvato.getId());

        session.setAttribute("ruolo", salvato);
        model.addAttribute("config", config);
        model.addAttribute("ruolo", salvato);

        return ViewUtils.resolveProtectedTemplate("impostazioni/contenuti/dettaglioRuolo");
    }

    // ─── DELETE SINGOLO (AJAX) ──────────────────────────────────────────────────
    @PostMapping("/{id}/delete")
    @ResponseBody
    public String delete(@PathVariable Integer id) {
        try {
            ruoloRepository.deleteById(id);
            log.info("=== RUOLI DELETE === id: {}", id);
            return "OK";
        } catch (Exception e) {
            log.error("Errore cancellazione ruolo id={}: {}", id, e.getMessage());
            return "KO";
        }
    }

    // ─── DELETE MULTIPLO (AJAX) ─────────────────────────────────────────────────
    @PostMapping("/delete-multiplo")
    @ResponseBody
    public String deleteMultiplo(@RequestParam(value = "ids") Integer[] ids) {
        try {
            ruoloRepository.deleteAllByIdInBatch(Arrays.asList(ids));
            log.info("=== RUOLI DELETE MULTIPLO === ids: {}", Arrays.toString(ids));
            return "OK";
        } catch (Exception e) {
            log.error("Errore cancellazione multipla ruoli: {}", e.getMessage());
            return "KO";
        }
    }
}