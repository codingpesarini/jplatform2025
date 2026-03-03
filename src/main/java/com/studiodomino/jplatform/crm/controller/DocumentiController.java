package com.studiodomino.jplatform.crm.controller;

import com.studiodomino.jplatform.cms.entity.DatiBase;
import com.studiodomino.jplatform.crm.service.DocumentiService;
import com.studiodomino.jplatform.shared.config.Configurazione;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/documenti")
@RequiredArgsConstructor
public class DocumentiController {

    private final DocumentiService documentiService;

    private String resolveView(String idType) {
        return "admin/documenti/dettaglioDocumento";
    }

    @GetMapping("/new")
    public String newForm(
            @RequestParam(value = "id_root", defaultValue = "0") String idRoot,
            @RequestParam(value = "id_type", defaultValue = "0") String idType,
            HttpSession session,
            Model model) {

        DatiBase doc = documentiService.buildNewDto(idRoot, idType);
        session.setAttribute("documento", doc);
        model.addAttribute("documento", doc);
        return resolveView(idType);
    }

    @GetMapping("/{id}/edit")
    public String open(
            @PathVariable String id,
            HttpSession session,
            Model model) {

        DatiBase doc = documentiService.loadDtoByIdOrThrow(id);
        session.setAttribute("documento", doc);
        model.addAttribute("documento", doc);
        return resolveView(doc.getIdType());
    }

    @GetMapping("/{id}/duplicate")
    public String duplicate(
            @PathVariable String id,
            HttpSession session,
            Model model) {

        DatiBase doc = documentiService.loadDtoByIdOrThrow(id);

        doc.setId("-1");
        doc.setTitolo(doc.getTitolo() + " (2)");
        doc.setNumeratore1(0L);

        session.setAttribute("documento", doc);
        model.addAttribute("documento", doc);
        return resolveView(doc.getIdType());
    }

    @PostMapping("/save")
    public String save(
            @RequestParam(value = "storeId", required = false) String storeId,
            @ModelAttribute("documento") DatiBase documento,
            HttpSession session,
            Model model) {

        Configurazione config = (Configurazione) session.getAttribute("configCore");

        if ((documento.getId() == null || documento.getId().isEmpty())
                && storeId != null && !storeId.isBlank()) {
            documento.setId(storeId);
        }

        DatiBase salvato = documentiService.saveFromDto(documento, config);

        session.setAttribute("documento", salvato);
        model.addAttribute("documento", salvato);
        return resolveView(salvato.getIdType());
    }

    @GetMapping("/ordina")
    public String elencoOrdina(
            @RequestParam String idSezione,
            @RequestParam(required = false) String substato,
            @RequestParam(required = false) String contstato,
            HttpSession session,
            Model model) {

        documentiService.normalizzaPosition(idSezione, contstato);

        List<DatiBase> elenco = documentiService.elencoOrdinaDto(idSezione, contstato);

        session.setAttribute("idSezione", idSezione);
        session.setAttribute("substato", substato);
        session.setAttribute("contstato", contstato);
        model.addAttribute("elencoDocumentiOrdina", elenco);

        return "admin/documenti/ordinaDocumenti";
    }
}