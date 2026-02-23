package com.studiodomino.jplatform.cms.admin.controller;

import com.studiodomino.jplatform.cms.entity.Section;
import com.studiodomino.jplatform.cms.entity.SectionType;
import com.studiodomino.jplatform.cms.service.ContentService;
import com.studiodomino.jplatform.shared.config.Configurazione;
import com.studiodomino.jplatform.shared.service.ConfigurazioneService;
import com.studiodomino.jplatform.shared.util.ViewUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/sezioni")
@RequiredArgsConstructor
@Slf4j
public class SezioniController {

    private final ConfigurazioneService configurazioneService;
    private final ContentService contentService;

    // =====================================================================
    // ELENCO SEZIONI
    // =====================================================================
    @GetMapping
    public String elencoSezioni(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            String idSite = String.valueOf(config.getIdSito());
            String userGroups = getSelettoreGruppo(config);

            List<Section> elencoSezioni = loadRootSectionsComplete(idSite);
            if (!userGroups.isEmpty()) {
                elencoSezioni = filterTreeByGroups(elencoSezioni, userGroups);
            }

            model.addAttribute("elencoSezioni", elencoSezioni);
            model.addAttribute("previewSezione", null);
            model.addAttribute("sectionTypes", contentService.findAllSectionTypes());
            model.addAttribute("config", config);

        } catch (Exception e) {
            log.error("Errore elencoSezioni", e);
        }

        return ViewUtils.resolveProtectedTemplate("cms/sezioni/elencoSezioni");
    }

    // =====================================================================
    // NEW FORM
    // =====================================================================
    @GetMapping("/new")
    public String newForm(
            @RequestParam(value = "id_parent", defaultValue = "0") String idParent,
            @RequestParam(value = "groupid", defaultValue = "0") String groupId,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            String idSite = String.valueOf(config.getIdSito());
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            Section section = new Section();
            section.setId(-1);
            section.setIdParent(idParent);
            section.setIdGruppo(groupId);
            section.setData(today);
            section.setS1(today);
            section.setS2(today);
            section.setGalleryString("");

            List<Section> elencoSezioni = contentService.findRootSections(idSite);
            List<SectionType> sectionTypes = contentService.findAllSectionTypes();

            model.addAttribute("section", section);
            model.addAttribute("elencoSezioni", elencoSezioni);
            model.addAttribute("sectionTypes", sectionTypes);
            model.addAttribute("elencoSezioniCompleto", elencoSezioni);
            model.addAttribute("config", config);

        } catch (Exception e) {
            log.error("Errore newForm sezione", e);
        }

        return ViewUtils.resolveProtectedTemplate("cms/sezioni/dettaglioSezione");
    }

    // =====================================================================
    // OPEN - Apri sezione esistente per modifica
    // =====================================================================
    @GetMapping("/{id}")
    public String open(@PathVariable Integer id,
                       HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            String idSite = String.valueOf(config.getIdSito());

            Section section = contentService.findSectionById(id, idSite)
                    .orElseThrow(() -> new RuntimeException("Sezione non trovata: " + id));

            List<Section> elencoSezioni = contentService.findRootSections(idSite);
            List<SectionType> sectionTypes = contentService.findAllSectionTypes();

            model.addAttribute("section", section);
            model.addAttribute("elencoSezioni", elencoSezioni);
            model.addAttribute("sectionTypes", sectionTypes);
            model.addAttribute("elencoSezioniCompleto", elencoSezioni);
            model.addAttribute("config", config);

        } catch (Exception e) {
            log.error("Errore open sezione id={}", id, e);
        }

        return ViewUtils.resolveProtectedTemplate("cms/sezioni/dettaglioSezione");
    }

    // =====================================================================
    // PREVIEW
    // =====================================================================
    @GetMapping("/{id}/preview")
    public String preview(@PathVariable Integer id,
                          HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            String idSite = String.valueOf(config.getIdSito());
            String userGroups = getSelettoreGruppo(config);

            Section sezione = contentService.findSectionComplete(id, idSite)
                    .orElseThrow(() -> new RuntimeException("Sezione non trovata: " + id));

            if (!userGroups.isEmpty()) {
                sezione = filterSingleSectionTree(sezione, userGroups);
            }

            var breadcrumb = contentService.buildBreadcrumbForSection(sezione, idSite);

            List<Section> elencoSezioni = loadRootSectionsComplete(idSite);
            if (!userGroups.isEmpty()) {
                elencoSezioni = filterTreeByGroups(elencoSezioni, userGroups);
            }

            List<SectionType> sectionTypes = contentService.findAllSectionTypes();

            model.addAttribute("previewSezione", sezione);
            model.addAttribute("elencoSezioni", elencoSezioni);
            model.addAttribute("sectionTypes", sectionTypes);
            model.addAttribute("breadcrumb", breadcrumb);
            model.addAttribute("config", config);

        } catch (Exception e) {
            log.error("Errore preview sezione id={}", id, e);
        }

        return ViewUtils.resolveProtectedTemplate("cms/sezioni/previewSezioni");
    }

    // =====================================================================
    // SAVE
    // =====================================================================
    @PostMapping("/save")
    public String save(@ModelAttribute Section section,
                       HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            String idSite = String.valueOf(config.getIdSito());
            String now = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm"));
            String operatore = config.getAmministratore().getNomeCompleto();

            boolean isNew = section.getId() == null
                    || section.getId() == -1
                    || section.getId() == 0;

            if (isNew) {
                section.setIdSite(idSite);
                section.setIdRoot(-1);
                section.setCreato(now);
                section.setCreatoDa(operatore);
                parseDataVisualizzata(section);

                Section saved = contentService.saveSection(section);
                return "redirect:/admin/sezioni/" + saved.getId() + "/preview";

            } else {
                section.setModificato(now);
                section.setModificatoDa(operatore);
                parseDataVisualizzata(section);

                contentService.saveSection(section);
                return "redirect:/admin/sezioni/" + section.getId() + "/preview";
            }

        } catch (Exception e) {
            log.error("Errore save sezione", e);

            // In caso di errore ricarica tutto il necessario per tornare alla vista
            try {
                String idSite = String.valueOf(config.getIdSito());
                List<Section> elencoSezioni = contentService.findRootSections(idSite);
                List<SectionType> sectionTypes = contentService.findAllSectionTypes();

                model.addAttribute("elencoSezioni", elencoSezioni);
                model.addAttribute("sectionTypes", sectionTypes);
                model.addAttribute("elencoSezioniCompleto", elencoSezioni);
            } catch (Exception ex) {
                log.error("Errore reload dati dopo save fallito", ex);
            }

            model.addAttribute("error", "Errore nel salvataggio: " + e.getMessage());
            model.addAttribute("section", section);
            model.addAttribute("config", config);
            return ViewUtils.resolveProtectedTemplate("cms/sezioni/dettaglioSezione");
        }
    }

    // =====================================================================
    // DELETE
    // =====================================================================
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id,
                         HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        String idSite = String.valueOf(config.getIdSito());
        String idParent = "0";

        try {
            Section sezione = contentService.findSectionById(id, idSite)
                    .orElseThrow(() -> new RuntimeException("Sezione non trovata"));

            idParent = sezione.getIdParent() != null ? sezione.getIdParent() : "0";
            contentService.deleteSection(id);

        } catch (Exception e) {
            log.error("Errore delete sezione id={}", id, e);
        }

        if (!"0".equals(idParent)) {
            return "redirect:/admin/sezioni/" + idParent + "/preview";
        }
        return "redirect:/admin/sezioni";
    }

    // =====================================================================
    // DELETE MULTIPLO
    // =====================================================================
    @PostMapping("/deleteMultiplo")
    @ResponseBody
    public ResponseEntity<String> deleteMultiplo(
            @RequestParam("delSectID") List<Integer> ids,
            @RequestParam(value = "SectID", defaultValue = "0") String parentId,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return ResponseEntity.status(401).body("KO");

        try {
            for (Integer id : ids) {
                contentService.deleteSection(id);
            }
            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            log.error("Errore deleteMultiplo sezioni", e);
            return ResponseEntity.ok("KO");
        }
    }

    // =====================================================================
    // RICLASSIFICA
    // =====================================================================
    @PostMapping("/riclassifica")
    @ResponseBody
    public ResponseEntity<String> riclassificaSezioni(
            @RequestParam("ricSectID") List<Integer> ids,
            @RequestParam("tempIDPARENT") String newParentId,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return ResponseEntity.status(401).body("KO");

        String idSite = String.valueOf(config.getIdSito());

        try {
            for (Integer id : ids) {
                contentService.findSectionById(id, idSite).ifPresent(s -> {
                    s.setIdParent(newParentId);
                    contentService.saveSection(s);
                });
            }
            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            log.error("Errore riclassificaSezioni", e);
            return ResponseEntity.ok("KO");
        }
    }

    // =====================================================================
    // ORDINA
    // =====================================================================
    @PostMapping("/ordina")
    @ResponseBody
    public ResponseEntity<String> ordinaSezioni(
            @RequestParam("ordineSect") String ordine,
            @RequestParam(value = "ordineBaseSect", defaultValue = "") String ordineBase,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return ResponseEntity.status(401).body("KO");

        String idSite = String.valueOf(config.getIdSito());

        try {
            if (!ordine.equals(ordineBase) && !ordine.isEmpty()) {
                String[] ids = ordine.split(";");
                int position = 1;
                for (String idStr : ids) {
                    if (idStr.isEmpty()) continue;
                    int sectId = Integer.parseInt(idStr.trim());
                    int pos = position;
                    contentService.findSectionById(sectId, idSite).ifPresent(s -> {
                        s.setPosition(pos);
                        contentService.saveSection(s);
                    });
                    position++;
                }
            }
            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            log.error("Errore ordinaSezioni", e);
            return ResponseEntity.ok("KO");
        }
    }

    // =====================================================================
    // DUPLICATE
    // =====================================================================
    @PostMapping("/{id}/duplicate")
    public String duplicate(@PathVariable Integer id,
                            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        String idSite = String.valueOf(config.getIdSito());

        try {
            Section original = contentService.findSectionById(id, idSite)
                    .orElseThrow(() -> new RuntimeException("Sezione non trovata"));

            Section copia = new Section();
            copia.setId(-1);
            copia.setIdSite(idSite);
            copia.setIdRoot(-1);
            copia.setIdParent(original.getIdParent());
            copia.setTitolo(original.getTitolo() + " - Copia");
            copia.setRiassunto(original.getRiassunto());
            copia.setTesto(original.getTesto());
            copia.setStato("0");
            copia.setNumeratore1(0L);

            Section saved = contentService.saveSection(copia);
            return "redirect:/admin/sezioni/" + saved.getId();

        } catch (Exception e) {
            log.error("Errore duplicate sezione id={}", id, e);
            return "redirect:/admin/sezioni";
        }
    }

    // =====================================================================
    // APPLICA EXTRA TAG
    // =====================================================================
    @PostMapping("/{id}/applicaExtraTag")
    @ResponseBody
    public ResponseEntity<String> applicaExtraTag(
            @PathVariable Integer id,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return ResponseEntity.status(401).body("KO");

        String idSite = String.valueOf(config.getIdSito());

        try {
            Section section = contentService.findSectionById(id, idSite)
                    .orElseThrow(() -> new RuntimeException("Sezione non trovata"));

            var contenuti = contentService.findContentsBySection(idSite, id);
            for (var contenuto : contenuti) {
                for (int i = 1; i <= 10; i++) {
                    contenuto.setExtraTag(i, section.getExtraTag(i));
                    contenuto.setExtraTagRef(i, section.getExtraTagRef(i));
                }
                contentService.saveContent(contenuto);
            }
            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            log.error("Errore applicaExtraTag sezione id={}", id, e);
            return ResponseEntity.ok("KO");
        }
    }

    // =====================================================================
    // UTILITY PRIVATE
    // =====================================================================

    /**
     * Metodo helper: popola il model con tutti gli attributi comuni
     * necessari alla vista dettaglioSezione.
     */
    private void populateDetailModel(Model model, Configurazione config, String idSite) {
        try {
            List<Section> elencoSezioni = contentService.findRootSections(idSite);
            List<SectionType> sectionTypes = contentService.findAllSectionTypes();
            model.addAttribute("elencoSezioni", elencoSezioni);
            model.addAttribute("elencoSezioniCompleto", elencoSezioni);
            model.addAttribute("sectionTypes", sectionTypes);
            model.addAttribute("config", config);
        } catch (Exception e) {
            log.error("Errore populateDetailModel", e);
        }
    }

    private String getSelettoreGruppo(Configurazione config) {
        String role1 = config.getAmministratore().getRole1();
        if ("s".equals(role1) || "a".equals(role1)) return "";
        String g = config.getAmministratore().getIdgruppi();
        return g != null ? g.trim() : "";
    }

    private List<Section> loadRootSectionsComplete(String idSite) {
        List<Section> root = contentService.findRootSections(idSite);
        List<Section> complete = new ArrayList<>();
        for (Section r : root) {
            try {
                Section full = contentService.findSectionComplete(r.getId(), idSite).orElse(r);
                complete.add(full);
            } catch (Exception ex) {
                complete.add(r);
            }
        }
        return complete;
    }

    private List<Section> filterTreeByGroups(List<Section> sections, String userGroups) {
        List<Section> out = new ArrayList<>();
        for (Section s : sections) {
            Section filtered = filterSingleSectionTree(s, userGroups);
            if (filtered != null) out.add(filtered);
        }
        return out;
    }

    private Section filterSingleSectionTree(Section s, String userGroups) {
        if (!canUserSeeSection(s, userGroups)) return null;

        if (s.getSubsection() != null && !s.getSubsection().isEmpty()) {
            List<Section> children = new ArrayList<>();
            for (Section c : s.getSubsection()) {
                Section childFiltered = filterSingleSectionTree(c, userGroups);
                if (childFiltered != null) children.add(childFiltered);
            }
            s.setSubsection(children);
        }
        return s;
    }

    private boolean canUserSeeSection(Section s, String userGroups) {
        String sectGroup = s.getIdGruppo();
        if (sectGroup == null || sectGroup.isBlank()) return true;
        if (userGroups == null || userGroups.isBlank()) return true;

        String normalized = userGroups.replace(",", ";");
        String[] ug = normalized.split(";");
        for (String g : ug) {
            String gg = g.trim();
            if (!gg.isEmpty() && sectGroup.contains(gg)) return true;
        }
        return false;
    }

    private void parseDataVisualizzata(Section section) {
        try {
            String dataVis = section.getDataVisualizzata();
            if (dataVis == null || dataVis.isEmpty()) {
                section.setAnno(LocalDate.now().getYear());
                section.setMese("gennaio");
            } else {
                int comma = dataVis.indexOf(',');
                if (comma > 0) {
                    String annoStr = dataVis.substring(comma + 2).trim();
                    section.setAnno(Integer.parseInt(annoStr));
                    String meseStr = dataVis.substring(3, comma).trim();
                    section.setMese(meseStr.toLowerCase());
                }
            }
        } catch (Exception e) {
            log.warn("Errore parsing dataVisualizzata: {}", section.getDataVisualizzata());
            section.setAnno(LocalDate.now().getYear());
            section.setMese("gennaio");
        }
    }
}