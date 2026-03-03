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
import java.time.LocalDateTime;
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

        return ViewUtils.resolveProtectedTemplate("cms/front/elencoSezioni");
    }

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

            // FIX: Inizializziamo un SectionType vuoto per evitare NullPointerException nel template
            section.setSectionType(new SectionType());

            model.addAttribute("section", section);
            populateDetailModel(model, config, idSite);

        } catch (Exception e) {
            log.error("Errore newForm sezione", e);
        }

        return ViewUtils.resolveProtectedTemplate("cms/dettaglioSezioneTemplate");
    }

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

            // =====================================================================
            // FIX FONDAMENTALE:
            // Se la sezione nel database non ha un tipo (id_type è null),
            // creiamo un oggetto SectionType vuoto.
            // Questo attiverà i tuoi metodi "getPage1OrDefault()" evitando il crash.
            // =====================================================================
            if (section.getSectionType() == null) {
                log.warn("La sezione {} ha un SectionType NULL. Applico default.", id);
                section.setSectionType(new SectionType());
            }

            model.addAttribute("section", section);
            populateDetailModel(model, config, idSite);

        } catch (Exception e) {
            log.error("Errore open sezione id={}", id, e);
            return "redirect:/admin/sezioni?error=notfound";
        }

        return ViewUtils.resolveProtectedTemplate("cms/dettaglioSezioneTemplate");
    }

    @GetMapping("/{id}/preview")
    public String preview(@PathVariable Integer id,
                          HttpServletRequest request,
                          Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            String idSite = String.valueOf(config.getIdSito());

            // carico la sezione COMPLETA (subsection + contenuti ecc)
            Section previewSezione = contentService.findSectionComplete(id, idSite)
                    .orElseThrow(() -> new RuntimeException("Sezione non trovata: " + id));

            // se per qualche motivo manca il tipo, evito NPE nei template
            if (previewSezione.getSectionType() == null) {
                previewSezione.setSectionType(new SectionType());
            }

            // sidebar: elenco sezioni (con eventuale filtro gruppi come fai in elencoSezioni)
            String userGroups = getSelettoreGruppo(config);
            List<Section> elencoSezioni = loadRootSectionsComplete(idSite);
            if (!userGroups.isEmpty()) {
                elencoSezioni = filterTreeByGroups(elencoSezioni, userGroups);
            }

            model.addAttribute("elencoSezioni", elencoSezioni);
            model.addAttribute("previewSezione", previewSezione);
            model.addAttribute("sectionTypes", contentService.findAllSectionTypes());
            model.addAttribute("config", config);

        } catch (Exception e) {
            log.error("Errore preview sezione id={}", id, e);
            return "redirect:/admin/sezioni?error=preview";
        }

        return ViewUtils.resolveProtectedTemplate("cms/front/previewSezioni");
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Section form,
                       HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            String idSite = String.valueOf(config.getIdSito());
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm"));
            String operatore = config.getAmministratore().getNomeCompleto();

            boolean isNew = form.getId() == null || form.getId() == -1 || form.getId() == 0;

            if (isNew) {
                // NEW: qui puoi salvare direttamente (ma assicurati ai NOT NULL)
                form.setIdSite(idSite);
                form.setIdRoot(-1);
                form.setCreato(now);
                form.setCreatoDa(operatore);

                // ---- DEFAULT MINIMI per colonne NOT NULL ----
                if (form.getLabel() == null || form.getLabel().isBlank()) {
                    form.setLabel(form.getTitolo()); // oppure "sezione"
                }
                if (form.getDataSql() == null) {
                    form.setDataSql(LocalDate.now());
                }

                parseDataVisualizzata(form);
                Section saved = contentService.saveSection(form);
                return "redirect:/admin/sezioni/" + saved.getId();
            }

            // UPDATE: carica dal DB e fai MERGE
            Section db = contentService.findSectionById(form.getId(), idSite)
                    .orElseThrow(() -> new RuntimeException("Sezione non trovata: " + form.getId()));

            // campi modificabili dalla pagina
            db.setTitolo(form.getTitolo());
            db.setStato(form.getStato());
            db.setIdType(form.getIdType());
            db.setDataVisualizzata(form.getDataVisualizzata());
            db.setData(form.getData());
            db.setTag(form.getTag());
            db.setS3(form.getS3());
            db.setMenu1(form.getMenu1());
            db.setMenu5(form.getMenu5());
            db.setS1(form.getS1());
            db.setS2(form.getS2());
            db.setMenu2(form.getMenu2());
            db.setMenu3(form.getMenu3());
            db.setMenu4(form.getMenu4());
            db.setRiassunto(form.getRiassunto());
            db.setTesto(form.getTesto());
            // ecc… (tutti quelli che hai nel form)

            // audit
            db.setModificato(now);
            db.setModificatoDa(operatore);

            // parse che aggiorna anno/mese ecc
            parseDataVisualizzata(db);

            // se label è NOT NULL e non lo gestisci in form, garantiscilo qui:
            if (db.getLabel() == null || db.getLabel().isBlank()) {
                db.setLabel(db.getTitolo());
            }
            if (db.getDataSql() == null) {
                db.setDataSql(LocalDate.now());
            }

            contentService.saveSection(db);
            return "redirect:/admin/sezioni/" + db.getId();

        } catch (Exception e) {
            log.error("Errore save sezione", e);
            populateDetailModel(model, config, String.valueOf(config.getIdSito()));
            model.addAttribute("error", "Errore nel salvataggio: " + e.getMessage());
            model.addAttribute("section", form);
            return ViewUtils.resolveProtectedTemplate("cms/dettaglioSezioneTemplate");
        }
    }

    // =====================================================================
    // UTILITY PRIVATE (Mantenute e usate per pulizia)
    // =====================================================================

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

    // ... (restanti metodi helper privati uguali a prima)

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
            section.setAnno(LocalDate.now().getYear());
            section.setMese("gennaio");
        }
    }
}