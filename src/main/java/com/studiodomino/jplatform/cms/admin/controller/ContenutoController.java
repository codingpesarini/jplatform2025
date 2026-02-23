package com.studiodomino.jplatform.cms.admin.controller;

import com.studiodomino.jplatform.cms.entity.DatiBase;
import com.studiodomino.jplatform.cms.entity.Section;
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
import java.util.List;

/**
 * Controller admin per la gestione dei contenuti CMS.
 * Conversione da GestioneDocumenti.java (Struts DispatchAction) a Spring MVC Controller.
 *
 * Mapping URL:
 *   GET  /admin/contenuti/new              → newForm
 *   GET  /admin/contenuti/{id}             → open (dettaglio/modifica)
 *   POST /admin/contenuti/save             → save
 *   POST /admin/contenuti/{id}/delete      → delete
 *   POST /admin/contenuti/deleteMultiplo   → deleteMultiplo (AJAX)
 *   POST /admin/contenuti/{id}/duplicate   → duplicate
 *   POST /admin/contenuti/riclassifica     → riclassificaDocumenti (AJAX)
 *   POST /admin/contenuti/ordina           → ordinaDocumentiApplica (AJAX)
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
    // Vecchio: newForm() → forward "successopen"
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
            datiBase.setIdRoot(String.valueOf(idRoot));
            datiBase.setIdType(idType);
            datiBase.setData(today);
            datiBase.setS1(today);
            datiBase.setS2(today);
            datiBase.setGalleryString("");

            // Carica la sezione padre
            Section sezione = contentService.findSectionById(idRoot, idSite).orElse(null);
            if (sezione != null) {
                datiBase.setSection(sezione);
                datiBase.setIdParent(sezione.getIdParent());

                // Propaga extratag dalla sezione al nuovo contenuto
                for (int i = 1; i <= 10; i++) {
                    datiBase.setExtraTag(i, sezione.getExtraTag(i));
                    datiBase.setExtraTagRef(i, sezione.getExtraTagRef(i));
                }
            }

            model.addAttribute("documento", datiBase);
            model.addAttribute("sezione", sezione);
            model.addAttribute("config", config);

        } catch (Exception e) {
            log.error("Errore newForm contenuto", e);
        }

        return ViewUtils.resolveProtectedTemplate("cms/contenuti/dettaglioContenuto");
    }

    // =====================================================================
    // OPEN - Apri contenuto esistente per modifica
    // Vecchio: open() → forward "successopen"
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
            Section sezione = contentService.findSectionById(idRoot, idSite).orElse(null);

            if (sezione != null) {
                documento.setSection(sezione);

                // Carica contenuti correlati dalla sezione (campi L15, L11)
                caricaDocumentiCorrelati(documento, sezione, idSite);
            }

            // Carica sezioni disponibili per riclassificazione
            List<Section> elencoSezioni = contentService.findRootSections(idSite);

            model.addAttribute("documento", documento);
            model.addAttribute("sezione", sezione);
            model.addAttribute("elencoSezioni", elencoSezioni);
            model.addAttribute("config", config);

        } catch (Exception e) {
            log.error("Errore open contenuto id={}", id, e);
        }

        return ViewUtils.resolveProtectedTemplate("cms/contenuti/dettaglioContenuto");
    }

    // =====================================================================
    // SAVE - Salva contenuto (nuovo o esistente)
    // Vecchio: save() → forward "successopen"
    // =====================================================================

    @PostMapping("/save")
    public String save(@ModelAttribute DatiBase documento,
                       HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            String idSite = String.valueOf(config.getIdSito());
            String now = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm"));
            String operatore = config.getAmministratore().getNomeCompleto();

            boolean isNew = documento.getId() == null
                    || documento.getId().equals("-1")
                    || documento.getId().equals("0");

            if (isNew) {
                // CREA nuovo contenuto
                documento.setIdSite(idSite);
                documento.setCreato(now);
                documento.setCreatoDa(operatore);
                parseDataVisualizzata(documento);

                DatiBase saved = contentService.saveContent(documento);
                log.info("Contenuto creato: id={} titolo={}", saved.getId(), saved.getTitolo());

                return "redirect:/admin/contenuti/" + saved.getId();

            } else {
                // AGGIORNA contenuto esistente
                documento.setModificato(now);
                documento.setModificatoDa(operatore);
                parseDataVisualizzata(documento);

                contentService.saveContent(documento);
                log.info("Contenuto salvato: id={}", documento.getId());

                return "redirect:/admin/contenuti/" + documento.getId();
            }

        } catch (Exception e) {
            log.error("Errore save contenuto", e);
            model.addAttribute("error", "Errore nel salvataggio: " + e.getMessage());
            model.addAttribute("documento", documento);
            model.addAttribute("config", config);
            return ViewUtils.resolveProtectedTemplate("cms/contenuti/dettaglioContenuto");
        }
    }

    // =====================================================================
    // DELETE MULTIPLO - Elimina più contenuti (AJAX)
    // Vecchio: deleteMultiplo() → forward "successDatiAjax"
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
    // Vecchio: duplicate() → forward "successopen"
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

            // Crea copia
            DatiBase copia = new DatiBase();
            copia.setId("-1"); // forza creazione
            copia.setIdSite(idSite);
            copia.setIdRoot(original.getIdRoot());
            copia.setIdType(original.getIdType());
            copia.setTitolo(original.getTitolo() + " (2)");
            copia.setRiassunto(original.getRiassunto());
            copia.setTesto(original.getTesto());
            copia.setStato("0"); // bozza
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
    // Vecchio: riclassificaDocumenti() → forward "successDatiAjax"
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
    // Vecchio: ordinaDocumentiApplica() → forward "successelencoDocumentiOrdina"
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
                    if (idStr.isEmpty()) continue;
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

    /**
     * Carica i documenti correlati dalla sezione (campi L15 e L11).
     * Vecchio: DAO.getContenutiRelazione(sezione.getL15()) e DAO.getElencoDocumentiCorrelati(sezione.getL11())
     */
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

    /**
     * Estrae anno e mese dalla dataVisualizzata e li imposta sul documento.
     */
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