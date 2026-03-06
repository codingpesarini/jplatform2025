package com.studiodomino.jplatform.cms.admin.controller;

import com.studiodomino.jplatform.cms.entity.SectionType;
import com.studiodomino.jplatform.cms.repository.SectionTypeRepository;
import com.studiodomino.jplatform.shared.config.Configurazione;
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
@RequestMapping("/admin/tipologia-documenti")
@RequiredArgsConstructor
@Slf4j
public class TipologiaDocumentiController {

    private final SectionTypeRepository sectionTypeRepository;
    private final ConfigurazioneService configurazioneService;

    // ─── ELENCO ────────────────────────────────────────────────────────────────
    @GetMapping
    public String elenco(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        log.info("=== TIPOLOGIA DOCUMENTI ELENCO === user: {}", config.getUsername());

        List<SectionType> elenco = sectionTypeRepository.findAll()
                .stream()
                .sorted((a, b) -> {
                    String ta = a.getType() != null ? a.getType() : "";
                    String tb = b.getType() != null ? b.getType() : "";
                    return ta.compareToIgnoreCase(tb);
                })
                .toList();

        model.addAttribute("config", config);
        model.addAttribute("elencoTypes", elenco);

        return ViewUtils.resolveProtectedTemplate("impostazioni/sezioni/elencoTipologiaDocumenti");
    }

    // ─── NEW ────────────────────────────────────────────────────────────────────
    @GetMapping("/new")
    public String newForm(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        log.info("=== TIPOLOGIA DOCUMENTI NEW ===");

        model.addAttribute("config", config);
        model.addAttribute("sectionType", new SectionType());

        return ViewUtils.resolveProtectedTemplate("impostazioni/contenuti/dettaglioTipologiaDocumenti");
    }

    // ─── OPEN ────────────────────────────────────────────────────────────────────
    @GetMapping("/{id}/open")
    public String open(@PathVariable Integer id,
                       HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        SectionType sectionType = sectionTypeRepository.findById(id).orElse(null);
        if (sectionType == null) return "redirect:/admin/tipologia-documenti";

        log.info("=== TIPOLOGIA DOCUMENTI OPEN === id: {}", id);

        session.setAttribute("sectionType", sectionType);
        model.addAttribute("config", config);
        model.addAttribute("sectionType", sectionType);

        return ViewUtils.resolveProtectedTemplate("impostazioni/contenuti/dettaglioTipologiaDocumenti");
    }

    // ─── DUPLICA ────────────────────────────────────────────────────────────────
    @GetMapping("/{id}/duplica")
    public String duplica(@PathVariable Integer id,
                          HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        SectionType originale = sectionTypeRepository.findById(id).orElse(null);
        if (originale == null) return "redirect:/admin/tipologia-documenti";

        log.info("=== TIPOLOGIA DOCUMENTI DUPLICA === da id: {}", id);

        SectionType copia = new SectionType();
        // id = null → nuovo record al salvataggio
        copia.setType(originale.getType() + " (2)");
        copia.setDescription(originale.getDescription());
        copia.setL1(originale.getL1());
        copia.setL2(originale.getL2());
        copia.setL3(originale.getL3());
        copia.setL4(originale.getL4());
        copia.setL5(originale.getL5());
        copia.setPage1(originale.getPage1());
        copia.setPage2(originale.getPage2());
        copia.setPage3(originale.getPage3());
        copia.setPage4(originale.getPage4());
        copia.setPage5(originale.getPage5());
        copia.setNumeratore1(originale.getNumeratore1());
        copia.setNumeratore2(originale.getNumeratore2());
        copia.setNumeratore3(originale.getNumeratore3());
        copia.setNumeratore4(originale.getNumeratore4());
        copia.setNumeratore5(originale.getNumeratore5());
        copia.setSpecial1(originale.getSpecial1());
        copia.setSpecial2(originale.getSpecial2());
        copia.setSpecial3(originale.getSpecial3());
        copia.setSpecial4(originale.getSpecial4());
        copia.setSpecial5(originale.getSpecial5());
        copia.setSpecial6(originale.getSpecial6());
        copia.setSpecial7(originale.getSpecial7());
        copia.setSpecial8(originale.getSpecial8());
        copia.setSpecial9(originale.getSpecial9());
        copia.setSpecial10(originale.getSpecial10());

        session.setAttribute("sectionType", copia);
        model.addAttribute("config", config);
        model.addAttribute("sectionType", copia);

        return ViewUtils.resolveProtectedTemplate("impostazioni/contenuti/dettaglioTipologiaDocumenti");
    }

    // ─── SAVE ────────────────────────────────────────────────────────────────────
    @PostMapping("/save")
    public String save(
            @ModelAttribute("sectionType") SectionType sectionType,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        SectionType salvato = sectionTypeRepository.save(sectionType);
        log.info("=== TIPOLOGIA DOCUMENTI SAVE === id: {}", salvato.getId());

        session.setAttribute("sectionType", salvato);
        model.addAttribute("config", config);
        model.addAttribute("sectionType", salvato);

        return ViewUtils.resolveProtectedTemplate("impostazioni/contenuti/dettaglioTipologiaDocumenti");
    }

    // ─── DELETE SINGOLO (AJAX) ──────────────────────────────────────────────────
    @PostMapping("/{id}/delete")
    @ResponseBody
    public String delete(@PathVariable Integer id) {
        try {
            sectionTypeRepository.deleteById(id);
            log.info("=== TIPOLOGIA DOCUMENTI DELETE === id: {}", id);
            return "OK";
        } catch (Exception e) {
            log.error("Errore cancellazione tipologia id={}: {}", id, e.getMessage());
            return "KO";
        }
    }

    // ─── DELETE MULTIPLO (AJAX) ─────────────────────────────────────────────────
    @PostMapping("/delete-multiplo")
    @ResponseBody
    public String deleteMultiplo(@RequestParam(value = "ids") Integer[] ids) {
        try {
            sectionTypeRepository.deleteAllByIdInBatch(Arrays.asList(ids));
            log.info("=== TIPOLOGIA DOCUMENTI DELETE MULTIPLO === ids: {}", Arrays.toString(ids));
            return "OK";
        } catch (Exception e) {
            log.error("Errore cancellazione multipla tipologie: {}", e.getMessage());
            return "KO";
        }
    }
}