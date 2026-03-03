package com.studiodomino.jplatform.cms.admin.controller;

import com.studiodomino.jplatform.cms.entity.DatiBase;
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
import java.util.List;

/**
 * Controller admin per la gestione dei contenuti CMS.
 */
@Controller
@RequestMapping("/admin/contenuti")
@RequiredArgsConstructor
@Slf4j
public class ContenutoController {

    private final ConfigurazioneService configurazioneService;
    private final ContentService contentService;

    // =====================================================================
    // NEW FORM - Nuovo contenuto
    // =====================================================================
    @GetMapping("/new")
    public String newForm(
            @RequestParam(value = "id_root", defaultValue = "0") Integer idRoot,
            @RequestParam(value = "id_type", defaultValue = "0") String idType,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            String idSite = String.valueOf(config.getIdSito());
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            // Nuovo contenuto vuoto
            DatiBase datiBase = new DatiBase();
            datiBase.setId("-1");
            datiBase.setIdSite(idSite);
            datiBase.setIdRoot(String.valueOf(idRoot));
            datiBase.setIdType(idType);
            datiBase.setData(today);
            datiBase.setS1(today);
            datiBase.setS2(today);
            datiBase.setGalleryString("");

            // Carica la sezione padre
            Section sezione = contentService.findSectionById(idRoot, idSite).orElse(null);

            if (sezione == null) {
                // placeholder per evitare NPE nel template (content.section....)
                sezione = new Section();
                sezione.setId(idRoot);
                sezione.setSectionType(new SectionType());
            } else {
                // sezione esiste ma magari sectionType è NULL nel DB
                if (sezione.getSectionType() == null) {
                    sezione.setSectionType(new SectionType());
                }
            }

            datiBase.setSection(sezione);
            datiBase.setIdParent(sezione.getIdParent());

            // Propaga extratag dalla sezione al nuovo contenuto
            for (int i = 1; i <= 10; i++) {
                datiBase.setExtraTag(i, sezione.getExtraTag(i));
                datiBase.setExtraTagRef(i, sezione.getExtraTagRef(i));
            }

            // >>> IMPORTANTISSIMO: il template usa "content", non "post"
            model.addAttribute("content", datiBase);
            model.addAttribute("post", datiBase);        // compatibilità se altrove usi "post"
            model.addAttribute("sezione", sezione);
            model.addAttribute("elencoSezioni", contentService.findRootSections(idSite));
            model.addAttribute("config", config);

        } catch (Exception e) {
            log.error("Errore newForm contenuto", e);
        }

        return ViewUtils.resolveProtectedTemplate("cms/dettaglioContenutoTemplate");
    }

    // =====================================================================
    // OPEN - Apri contenuto esistente per modifica
    // =====================================================================
    @GetMapping("/{id}")
    public String open(@PathVariable Integer id,
                       HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            String idSite = String.valueOf(config.getIdSito());

            DatiBase documento = contentService.findContentById(id, idSite)
                    .orElseThrow(() -> new RuntimeException("Contenuto non trovato: " + id));

            // Carica la sezione padre
            Integer idRoot = parseIntSafe(documento.getIdRoot());
            Section sezione = (idRoot != null)
                    ? contentService.findSectionById(idRoot, idSite).orElse(null)
                    : null;

            if (sezione == null) {
                // placeholder per evitare NPE nel template (content.section....)
                sezione = new Section();
                sezione.setId(idRoot != null ? idRoot : 0);
                sezione.setSectionType(new SectionType());
            } else {
                if (sezione.getSectionType() == null) {
                    sezione.setSectionType(new SectionType());
                }
            }

            documento.setSection(sezione);

            // Carica contenuti correlati dalla sezione (campi L15, L11)
            caricaDocumentiCorrelati(documento, sezione, idSite);

            // Carica sezioni disponibili per riclassificazione
            List<Section> elencoSezioni = contentService.findRootSections(idSite);

            // >>> IMPORTANTISSIMO: il template usa "content", non "post"
            model.addAttribute("content", documento);
            model.addAttribute("post", documento);       // compatibilità
            model.addAttribute("sezione", sezione);
            model.addAttribute("elencoSezioni", elencoSezioni);
            model.addAttribute("config", config);

        } catch (Exception e) {
            log.error("Errore open contenuto id={}", id, e);
            return "redirect:/admin/contenuti?error=open";
        }

        return ViewUtils.resolveProtectedTemplate("cms/dettaglioContenutoTemplate");
    }

    // =====================================================================
    // SAVE - Salva contenuto (nuovo o esistente)
    // =====================================================================
    @PostMapping("/save")
    public String save(@ModelAttribute DatiBase documento,
                       HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            String idSite = String.valueOf(config.getIdSito());

            // 1. FIX: Usiamo LocalDateTime per supportare il pattern con ore e minuti (HH:mm)
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm"));
            String operatore = config.getAmministratore().getNomeCompleto();

            // 2. FIX: Protezione per la colonna 'datasql' che non può essere null nel DB
            if (documento.getDataSql() == null) {
                documento.setDataSql(LocalDate.now());
            }

            boolean isNew = documento.getId() == null
                    || documento.getId().equals("-1")
                    || documento.getId().equals("0");

            if (isNew) {
                documento.setIdSite(idSite);
                documento.setCreato(now);
                documento.setCreatoDa(operatore);
                parseDataVisualizzata(documento);

                DatiBase saved = contentService.saveContent(documento);
                log.info("Contenuto creato: id={} titolo={}", saved.getId(), saved.getTitolo());
                return "redirect:/admin/contenuti/" + saved.getId();

            } else {
                documento.setModificato(now);
                documento.setModificatoDa(operatore);
                parseDataVisualizzata(documento);

                contentService.saveContent(documento);
                log.info("Contenuto salvato: id={}", documento.getId());
                return "redirect:/admin/contenuti/" + documento.getId();
            }

        } catch (Exception e) {
            log.error("Errore save contenuto", e);

            String idSite = String.valueOf(config.getIdSito());

            // Gestione sezione per il ritorno alla vista in caso di errore
            Section sezione = null;
            Integer idRoot = parseIntSafe(documento.getIdRoot());
            if (idRoot != null) {
                sezione = contentService.findSectionById(idRoot, idSite).orElse(null);
            }

            // Protezione sectionType per evitare crash nel template
            if (sezione == null) {
                sezione = new Section();
                sezione.setId(idRoot != null ? idRoot : 0);
                sezione.setSectionType(new SectionType());
            } else if (sezione.getSectionType() == null) {
                sezione.setSectionType(new SectionType());
            }

            documento.setSection(sezione);

            model.addAttribute("error", "Errore nel salvataggio: " + e.getMessage());
            model.addAttribute("content", documento);
            model.addAttribute("post", documento);
            model.addAttribute("sezione", sezione);
            model.addAttribute("elencoSezioni", contentService.findRootSections(idSite));
            model.addAttribute("config", config);

            return ViewUtils.resolveProtectedTemplate("cms/dettaglioContenutoTemplate");
        }
    }

    // =====================================================================
    // DELETE MULTIPLO - Elimina più contenuti (AJAX)
    // =====================================================================
    @PostMapping("/deleteMultiplo")
    @ResponseBody
    public ResponseEntity<String> deleteMultiplo(
            @RequestParam("delContenutiID") List<Integer> ids,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return ResponseEntity.status(401).body("KO");

        try {
            for (Integer id : ids) {
                contentService.deleteContent(id);
            }
            log.info("Eliminati {} contenuti", ids.size());
            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            log.error("Errore deleteMultiplo contenuti", e);
            return ResponseEntity.ok("KO");
        }
    }

    // =====================================================================
    // DUPLICATE - Duplica contenuto esistente
    // =====================================================================
    @PostMapping("/{id}/duplicate")
    public String duplicate(@PathVariable Integer id,
                            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        String idSite = String.valueOf(config.getIdSito());

        try {
            DatiBase original = contentService.findContentById(id, idSite)
                    .orElseThrow(() -> new RuntimeException("Contenuto non trovato"));

            DatiBase copia = new DatiBase();
            copia.setId("-1");
            copia.setIdSite(idSite);
            copia.setIdRoot(original.getIdRoot());
            copia.setIdType(original.getIdType());
            copia.setTitolo(original.getTitolo() + " (2)");
            copia.setRiassunto(original.getRiassunto());
            copia.setTesto(original.getTesto());
            copia.setStato("0");
            copia.setNumeratore1String("00000000");

            DatiBase saved = contentService.saveContent(copia);
            log.info("Contenuto duplicato: original={} → new={}", id, saved.getId());

            return "redirect:/admin/contenuti/" + saved.getId();

        } catch (Exception e) {
            log.error("Errore duplicate contenuto id={}", id, e);
            return "redirect:/admin/contenuti";
        }
    }

    // =====================================================================
    // RICLASSIFICA - Sposta contenuti in altra sezione (AJAX)
    // =====================================================================
    @PostMapping("/riclassifica")
    @ResponseBody
    public ResponseEntity<String> riclassificaDocumenti(
            @RequestParam("ricContID") List<Integer> ids,
            @RequestParam("id") Integer nuovaSezioneId,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return ResponseEntity.status(401).body("KO");

        String idSite = String.valueOf(config.getIdSito());

        try {
            Section nuovaSezione = contentService.findSectionById(nuovaSezioneId, idSite)
                    .orElseThrow(() -> new RuntimeException("Sezione non trovata"));

            for (Integer id : ids) {
                contentService.findDatiBaseById(id).ifPresent(doc -> {
                    doc.setIdRoot(nuovaSezione.getId().toString());
                    doc.setIdType(nuovaSezione.getIdType() != null
                            ? nuovaSezione.getIdType().toString() : "0");
                    contentService.saveContent(doc);
                });
            }

            log.info("Riclassificati {} contenuti → sezione {}", ids.size(), nuovaSezioneId);
            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            log.error("Errore riclassificaDocumenti", e);
            return ResponseEntity.ok("KO");
        }
    }

    // =====================================================================
    // ORDINA - Applica nuovo ordinamento contenuti (AJAX)
    // =====================================================================
    @PostMapping("/ordina")
    @ResponseBody
    public ResponseEntity<String> ordinaDocumenti(
            @RequestParam("ordine") String ordine,
            @RequestParam(value = "ordineBase", defaultValue = "") String ordineBase,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return ResponseEntity.status(401).body("KO");

        try {
            if (!ordine.equals(ordineBase) && !ordine.isEmpty()) {
                String[] ids = ordine.split(";");
                int position = 1;

                for (String idStr : ids) {
                    if (idStr == null || idStr.isBlank()) continue;
                    int id = Integer.parseInt(idStr.trim());
                    int pos = position;

                    contentService.findDatiBaseById(id).ifPresent(doc -> {
                        doc.setPosition(String.valueOf(pos));
                        contentService.saveContent(doc);
                    });

                    position++;
                }
            }
            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            log.error("Errore ordinaDocumenti", e);
            return ResponseEntity.ok("KO");
        }
    }

    // =====================================================================
    // UTILITY PRIVATI
    // =====================================================================

    private void caricaDocumentiCorrelati(DatiBase documento, Section sezione, String idSite) {
        try {
            String l15 = sezione.getL(15);
            if (l15 != null && !l15.isEmpty()) {
                Integer idRootCorrelati = parseIntSafe(l15);
                if (idRootCorrelati != null) {
                    documento.setDocCorrelati1(
                            contentService.findContentsBySection(idSite, idRootCorrelati)
                    );
                }
            }

            String l11 = sezione.getL(11);
            if (l11 != null && !l11.isEmpty()) {
                Integer idRootCorrelati2 = parseIntSafe(l11);
                if (idRootCorrelati2 != null) {
                    documento.setDocCorrelati2(
                            contentService.findContentsBySection(idSite, idRootCorrelati2)
                    );
                }
            }
        } catch (Exception e) {
            log.warn("Errore caricamento documenti correlati: {}", e.getMessage());
        }
    }

    private void parseDataVisualizzata(DatiBase documento) {
        try {
            String dataVis = documento.getDataVisualizzata();
            if (dataVis == null || dataVis.isEmpty()) {
                documento.setAnno(LocalDate.now().getYear());
                documento.setMese("gennaio");
            } else {
                int comma = dataVis.indexOf(',');
                if (comma > 0) {
                    String annoStr = dataVis.substring(comma + 2).trim();
                    documento.setAnno(Integer.parseInt(annoStr));
                    String meseStr = dataVis.substring(3, comma).trim();
                    documento.setMese(meseStr.toLowerCase());
                }
            }
        } catch (Exception e) {
            log.warn("Errore parsing dataVisualizzata: {}", documento.getDataVisualizzata());
            documento.setAnno(LocalDate.now().getYear());
            documento.setMese("gennaio");
        }
    }

    private Integer parseIntSafe(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}