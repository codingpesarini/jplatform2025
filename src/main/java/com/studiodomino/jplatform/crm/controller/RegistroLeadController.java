package com.studiodomino.jplatform.crm.controller;

import com.studiodomino.jplatform.crm.entity.RegistroLead;
import com.studiodomino.jplatform.crm.service.RegistroLeadService;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller admin per la gestione del Registro LEAD CRM.
 * Conversione da GestioneRegistroLead.java (Struts) a Spring MVC.
 *
 * Mapping URL:
 *   GET  /admin/crm/lead                 → elencoRegistroLead
 *   GET  /admin/crm/lead/{id}            → openRegistroLead (entrata)
 *   GET  /admin/crm/lead/{id}/uscita     → openRegistroLeadUscita
 *   POST /admin/crm/lead/save            → saveRegistroLead
 *   POST /admin/crm/lead/{id}/delete     → deleteRegistroLead (AJAX)
 *   POST /admin/crm/lead/{id}/inviaSms   → inviaSmsRegistroLead
 *   POST /admin/crm/lead/{id}/inviaEmail → inviaEmailRegistroLead
 */
@Controller
@RequestMapping("/admin/crm/lead")
@RequiredArgsConstructor
@Slf4j
public class RegistroLeadController {

    private final ConfigurazioneService configurazioneService;
    private final RegistroLeadService registroLeadService;

    private static final DateTimeFormatter DF  = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");
    private static final DateTimeFormatter DTA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter ORA = DateTimeFormatter.ofPattern("HH:mm");

    private static final String[] STATO_LEAD = {
            "Non gestito", "Da richiamare", "Inviare email",
            "In lavorazione", "Completato", "Annullato"
    };

    // =====================================================================
    // ELENCO
    // Vecchio: elencoRegistroLeadUtente() → forward "successelencoRegistroLead"
    // =====================================================================

    @GetMapping
    public String elencoRegistroLead(
            @RequestParam(value = "stato", defaultValue = "4") String stato,
            @RequestParam(value = "direzione", defaultValue = "e") String direzione,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            model.addAttribute("elencoRegistroLead",
                    registroLeadService.findAll(direzione, stato));
            model.addAttribute("statoLead", STATO_LEAD);
            model.addAttribute("statoSelezionato", stato);
            model.addAttribute("direzioneSelezionata", direzione);
            model.addAttribute("config", config);
        } catch (Exception e) {
            log.error("Errore elencoRegistroLead", e);
        }

        return ViewUtils.resolveProtectedTemplate("crm/sezioni/elencoRegistroLead");
    }


    // =====================================================================
    // OPEN (entrata)
    // Vecchio: openRegistroLead() → forward "successopenRegistroLead"
    // =====================================================================

    @GetMapping("/{id}")
    public String openRegistroLead(
            @PathVariable Long id,
            @RequestParam(value = "idleadstore", defaultValue = "0") Integer idLeadStore,
            @RequestParam(value = "idutente", defaultValue = "0") Integer idUtente,
            @RequestParam(value = "store", defaultValue = "") String store,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            RegistroLead registroLead;

            if (id == -1) {
                registroLead = new RegistroLead();
                registroLead.setId(-1L);
                registroLead.setDirezione("e");
                registroLead.setData(LocalDate.now().format(DTA));
                registroLead.setOra(LocalTime.now().format(ORA));
                registroLead.setIdamministratore(config.getAmministratore().getId());
                registroLead.setAmministratore(config.getAmministratore());
                registroLead.setIdleadstore(idLeadStore);
                registroLead.setIdutente(idUtente);
                registroLead.setStore(store);

                if (idUtente > 0) {
                    registroLead.setUtente(registroLeadService.findUtenteBase(idUtente));
                }
            } else {
                registroLead = registroLeadService.findById(id);
            }

            // Arricchisce con commento o email in base allo store
            arricchisciConStore(registroLead);

            model.addAttribute("areeInteresse", registroLeadService.findAreeInteresse());
            model.addAttribute("statoLead", STATO_LEAD);
            model.addAttribute("registroLead", registroLead);
            model.addAttribute("config", config);

        } catch (Exception e) {
            log.error("Errore openRegistroLead id={}", id, e);
        }

        return ViewUtils.resolveProtectedTemplate("crm/contenuti/dettaglioRegistroLead");
    }

    // =====================================================================
    // OPEN USCITA
    // Vecchio: openRegistroLeadUscita() → forward "successopenRegistroLeadUscita{tipologia}"
    // =====================================================================

    @GetMapping("/{id}/uscita")
    public String openRegistroLeadUscita(
            @PathVariable Long id,
            @RequestParam(value = "tipologia", defaultValue = "") String tipologia,
            @RequestParam(value = "idleadstore", defaultValue = "0") Integer idLeadStore,
            @RequestParam(value = "idutente", defaultValue = "0") Integer idUtente,
            @RequestParam(value = "store", defaultValue = "") String store,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            RegistroLead registroLead;

            if (id == -1) {
                registroLead = new RegistroLead();
                registroLead.setId(-1L);
                registroLead.setDirezione("u");
                registroLead.setData(LocalDate.now().format(DTA));
                registroLead.setOra(LocalTime.now().format(ORA));
                registroLead.setIdamministratore((config.getAmministratore().getId()));
                registroLead.setIdleadstore(idLeadStore);
                registroLead.setIdutente(idUtente);
                registroLead.setStore(store);
                registroLead.setUtente(registroLeadService.findUtenteBase(idUtente));
            } else {
                registroLead = registroLeadService.findById(id);
            }

            model.addAttribute("tipologia", tipologia);
            model.addAttribute("statoLead", STATO_LEAD);
            model.addAttribute("registroLead", registroLead);
            model.addAttribute("config", config);

        } catch (Exception e) {
            log.error("Errore openRegistroLeadUscita id={}", id, e);
        }

        // Template diverso per tipologia (Email, Sms, ecc.)
        String templateSuffix = tipologia.isEmpty() ? "" : tipologia;
        return ViewUtils.resolveProtectedTemplate(
                "crm/contenuti/dettaglioRegistroLeadUscita" + templateSuffix);
    }

    // =====================================================================
    // SAVE
    // Vecchio: saveRegistroLead() → forward "successopenRegistroLead"
    // =====================================================================

    @PostMapping("/save")
    public String saveRegistroLead(
            @ModelAttribute RegistroLead registroLead,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            String now = java.time.LocalDateTime.now().format(DF);
            String operatore = config.getAmministratore().getNome()
                    + " " + config.getAmministratore().getCognome();

            // Carica amministratore assegnatario
            registroLead.setAmministratore(
                    registroLeadService.findAmministratoreById(
                            String.valueOf(registroLead.getIdamministratore())));

            boolean isNew = registroLead.getId() == null || registroLead.getId() == -1;

            if (isNew) {
                String statoText = STATO_LEAD[Integer.parseInt(registroLead.getStato())];
                registroLead.setLog(buildLog(now, operatore, registroLead,
                        statoText, "Inserimento nuovo lead da"));
                registroLead = registroLeadService.crea(registroLead, config);
            } else {
                registroLead.setUtente(
                        registroLeadService.findUtenteBase(registroLead.getIdutente()));
                String statoText = STATO_LEAD[Integer.parseInt(registroLead.getStato())];
                String logEntry = buildLog(now, operatore, registroLead,
                        statoText, "Modificato da");
                registroLead.setLog(registroLead.getLog() + logEntry);
                registroLead = registroLeadService.salva(registroLead);
            }

            arricchisciConStore(registroLead);

            model.addAttribute("areeInteresse", registroLeadService.findAreeInteresse());
            model.addAttribute("statoLead", STATO_LEAD);
            model.addAttribute("registroLead", registroLead);
            model.addAttribute("config", config);

        } catch (Exception e) {
            log.error("Errore saveRegistroLead", e);
            model.addAttribute("error", "Errore nel salvataggio: " + e.getMessage());
            model.addAttribute("registroLead", registroLead);
            model.addAttribute("config", config);
        }

        return ViewUtils.resolveProtectedTemplate("crm/contenuti/dettaglioRegistroLead");
    }

    // =====================================================================
    // DELETE (AJAX)
    // Vecchio: deleteRegistroLead() → null
    // =====================================================================

    @PostMapping("/{id}/delete")
    @ResponseBody
    public ResponseEntity<String> deleteRegistroLead(
            @PathVariable Long id,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return ResponseEntity.status(401).body("KO");

        try {
            registroLeadService.elimina(id);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Errore deleteRegistroLead id={}", id, e);
            return ResponseEntity.ok("KO");
        }
    }

    // =====================================================================
    // INVIA SMS
    // Vecchio: InviaSmsRegistroLead() → forward "successopenRegistroLeadUscitaSms"
    // =====================================================================

    @PostMapping("/{id}/inviaSms")
    public String inviaSmsRegistroLead(
            @PathVariable Long id,
            @ModelAttribute RegistroLead registroLead,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            registroLead.setUtente(
                    registroLeadService.findUtenteBase(registroLead.getIdutente()));
            registroLeadService.inviaSms(registroLead, config);

            String now = java.time.LocalDateTime.now().format(DF);
            String operatore = config.getAmministratore().getNome()
                    + " " + config.getAmministratore().getCognome();
            registroLead.setStore("Sms");
            registroLead.setLog(buildLogSemplice(now, operatore, "Creazione nuovo lead da"));
            registroLead = registroLeadService.crea(registroLead, config);

            model.addAttribute("registroLead", registroLead);
            model.addAttribute("config", config);

        } catch (Exception e) {
            log.error("Errore inviaSmsRegistroLead id={}", id, e);
        }

        return ViewUtils.resolveProtectedTemplate("crm/contenuti/dettaglioRegistroLeadUscitaSms");
    }

    // =====================================================================
    // INVIA EMAIL
    // Vecchio: InviaEmailRegistroLead() → forward "successopenRegistroLeadUscitaEmail"
    // =====================================================================

    @PostMapping("/{id}/inviaEmail")
    public String inviaEmailRegistroLead(
            @PathVariable Long id,
            @ModelAttribute RegistroLead registroLead,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            registroLead.setUtente(
                    registroLeadService.findUtenteBase(registroLead.getIdutente()));
            registroLeadService.inviaEmail(registroLead, config);

            String now = java.time.LocalDateTime.now().format(DF);
            String operatore = config.getAmministratore().getNome()
                    + " " + config.getAmministratore().getCognome();
            registroLead.setStore("Email");
            registroLead.setLog(buildLogSemplice(now, operatore, "Creazione nuovo lead da"));
            registroLead = registroLeadService.crea(registroLead, config);

            model.addAttribute("registroLead", registroLead);
            model.addAttribute("config", config);

        } catch (Exception e) {
            log.error("Errore inviaEmailRegistroLead id={}", id, e);
        }

        return ViewUtils.resolveProtectedTemplate("crm/contenuti/dettaglioRegistroLeadUscitaEmail");
    }

    // =====================================================================
    // UTILITY PRIVATI
    // =====================================================================

    private void arricchisciConStore(RegistroLead registroLead) {
        try {
            if ("commenti".equals(registroLead.getStore())) {
                var commento = registroLeadService.findCommentoById(
                        (long) registroLead.getIdleadstore());
                registroLead.setCommento(commento);
                if (commento != null) {
                    // commento.getMessaggio() — adatta al tipo reale quando CommentoRepository è pronto
                }
                registroLead.setNotalead("Commento caricato");
            } else if ("emailstore".equals(registroLead.getStore())
                    && registroLead.getIdutente() > 0) {
                var email = registroLeadService.findEmailStoreById(
                        registroLead.getIdleadstore());
                registroLead.setMessaggioEmail(email);
                registroLead.setNotalead("Nuovo lead da email ricevuta");
            }
        } catch (Exception e) {
            log.warn("Errore arricchimento store lead id={}: {}",
                    registroLead.getId(), e.getMessage());
        }
    }

    private String buildLog(String now, String operatore, RegistroLead rl,
                            String statoText, String azione) {
        return "<code>" + now + "</code>" +
                "<blockquote class='success'>" +
                "<p>" + azione + ": " + operatore + "</p>" +
                "<p>Data: " + rl.getData() + " " + rl.getOra() + "</p>" +
                "<p>Assegnatario: " + (rl.getAmministratore() != null
                ? rl.getAmministratore().getNome() + " " + rl.getAmministratore().getCognome()
                : "") + "</p>" +
                "<p>Stato: " + statoText + "</p>" +
                "<p>Testo: " + rl.getNotalead() + "</p>" +
                "</blockquote>";
    }

    private String buildLogSemplice(String now, String operatore, String azione) {
        return "<code>" + now + "</code>" +
                "<blockquote class='success'>" +
                "<p>" + azione + ": " + operatore + "</p>" +
                "</blockquote>";
    }
}