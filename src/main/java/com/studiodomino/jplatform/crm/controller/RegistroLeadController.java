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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller admin per la gestione del Registro LEAD CRM.
 * Conversione da GestioneRegistroLead.java (Struts) a Spring MVC.
 *
 * Mapping URL:
 *   GET  /admin/crm/lead                        → elencoRegistroLead
 *   GET  /admin/crm/lead/new/{store}            → nuovoLead (entrata diretta)
 *   GET  /admin/crm/lead/new/uscita/{tipologia} → nuovoLeadUscita (uscita diretta)
 *   GET  /admin/crm/lead/{id}                   → openRegistroLead (entrata esistente)
 *   GET  /admin/crm/lead/{id}/uscita            → openRegistroLeadUscita (uscita esistente)
 *   POST /admin/crm/lead/save                   → saveRegistroLead
 *   POST /admin/crm/lead/{id}/delete            → deleteRegistroLead (AJAX)
 *   POST /admin/crm/lead/{id}/inviaSms          → inviaSmsRegistroLead
 *   POST /admin/crm/lead/{id}/inviaEmail        → inviaEmailRegistroLead
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
    // GET /admin/crm/lead
    // =====================================================================

    @GetMapping
    public String elencoRegistroLead(
            @RequestParam(value = "stato",     defaultValue = "4") String stato,
            @RequestParam(value = "direzione", defaultValue = "e") String direzione,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            model.addAttribute("elencoRegistroLead",
                    registroLeadService.findAll(direzione, stato));
            model.addAttribute("statoLead",           STATO_LEAD);
            model.addAttribute("statoSelezionato",    stato);
            model.addAttribute("direzioneSelezionata", direzione);
            model.addAttribute("config",              config);
        } catch (Exception e) {
            log.error("Errore elencoRegistroLead", e);
        }

        return ViewUtils.resolveProtectedTemplate("crm/sezioni/elencoRegistroLead");
    }

    // =====================================================================
    // NUOVO LEAD ENTRATA — URL pulito
    // GET /admin/crm/lead/new/{store}
    // Es: /admin/crm/lead/new/diretto
    // =====================================================================

    @GetMapping("/new/{store}")
    public String nuovoLead(
            @PathVariable String store,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            RegistroLead registroLead = new RegistroLead();
            registroLead.setId(-1L);
            registroLead.setDirezione("e");
            registroLead.setData(LocalDate.now().format(DTA));
            registroLead.setOra(LocalTime.now().format(ORA));
            registroLead.setIdamministratore(config.getAmministratore().getId());
            registroLead.setAmministratore(config.getAmministratore());
            registroLead.setIdleadstore(0);
            registroLead.setIdutente(0);
            registroLead.setStore(store);

            model.addAttribute("areeInteresse", registroLeadService.findAreeInteresse());
            model.addAttribute("statoLead",     STATO_LEAD);
            model.addAttribute("registroLead",  registroLead);
            model.addAttribute("config",        config);

        } catch (Exception e) {
            log.error("Errore nuovoLead store={}", store, e);
        }

        return ViewUtils.resolveProtectedTemplate("crm/contenuti/dettaglioRegistroLead");
    }

    // =====================================================================
    // NUOVO LEAD USCITA — URL pulito
    // GET /admin/crm/lead/new/uscita/{tipologia}
    // Es: /admin/crm/lead/new/uscita/Email
    //     /admin/crm/lead/new/uscita/Sms
    // =====================================================================

    @GetMapping("/new/uscita/{tipologia}")
    public String nuovoLeadUscita(
            @PathVariable String tipologia,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            RegistroLead registroLead = new RegistroLead();
            registroLead.setId(-1L);
            registroLead.setDirezione("u");
            registroLead.setData(LocalDate.now().format(DTA));
            registroLead.setOra(LocalTime.now().format(ORA));
            registroLead.setIdamministratore(config.getAmministratore().getId());
            registroLead.setAmministratore(config.getAmministratore());
            registroLead.setIdleadstore(-1);
            registroLead.setIdutente(0);
            registroLead.setStore("diretto");

            model.addAttribute("tipologia",    tipologia);
            model.addAttribute("statoLead",    STATO_LEAD);
            model.addAttribute("registroLead", registroLead);
            model.addAttribute("config",       config);

        } catch (Exception e) {
            log.error("Errore nuovoLeadUscita tipologia={}", tipologia, e);
        }

        return ViewUtils.resolveProtectedTemplate(
                "crm/contenuti/dettaglioRegistroLeadUscita" + tipologia);
    }

    // =====================================================================
    // OPEN LEAD ESISTENTE (entrata)
    // GET /admin/crm/lead/{id}
    // =====================================================================

    @GetMapping("/{id}")
    public String openRegistroLead(
            @PathVariable Long id,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            RegistroLead registroLead = registroLeadService.findById(id);
            arricchisciConStore(registroLead);

            model.addAttribute("areeInteresse", registroLeadService.findAreeInteresse());
            model.addAttribute("statoLead",     STATO_LEAD);
            model.addAttribute("registroLead",  registroLead);
            model.addAttribute("config",        config);

        } catch (Exception e) {
            log.error("Errore openRegistroLead id={}", id, e);
        }

        return ViewUtils.resolveProtectedTemplate("crm/contenuti/dettaglioRegistroLead");
    }

    // =====================================================================
    // OPEN LEAD ESISTENTE USCITA
    // GET /admin/crm/lead/{id}/uscita/{tipologia}
    // =====================================================================

    @GetMapping("/{id}/uscita/{tipologia}")
    public String openRegistroLeadUscita(
            @PathVariable Long id,
            @PathVariable String tipologia,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            RegistroLead registroLead = registroLeadService.findById(id);

            model.addAttribute("tipologia",    tipologia);
            model.addAttribute("statoLead",    STATO_LEAD);
            model.addAttribute("registroLead", registroLead);
            model.addAttribute("config",       config);

        } catch (Exception e) {
            log.error("Errore openRegistroLeadUscita id={} tipologia={}", id, tipologia, e);
        }

        return ViewUtils.resolveProtectedTemplate(
                "crm/contenuti/dettaglioRegistroLeadUscita" + tipologia);
    }

    // =====================================================================
    // SAVE
    // POST /admin/crm/lead/save
    // =====================================================================

    @PostMapping("/save")
    public String saveRegistroLead(
            @ModelAttribute RegistroLead registroLead,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            String now      = LocalDateTime.now().format(DF);
            String operatore = config.getAmministratore().getNome()
                    + " " + config.getAmministratore().getCognome();

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
                String logEntry  = buildLog(now, operatore, registroLead,
                        statoText, "Modificato da");
                registroLead.setLog(registroLead.getLog() + logEntry);
                registroLead = registroLeadService.salva(registroLead);
            }

            arricchisciConStore(registroLead);

            model.addAttribute("areeInteresse", registroLeadService.findAreeInteresse());
            model.addAttribute("statoLead",     STATO_LEAD);
            model.addAttribute("registroLead",  registroLead);
            model.addAttribute("config",        config);

        } catch (Exception e) {
            log.error("Errore saveRegistroLead", e);
            model.addAttribute("error",        "Errore nel salvataggio: " + e.getMessage());
            model.addAttribute("registroLead", registroLead);
            model.addAttribute("config",       config);
        }

        return ViewUtils.resolveProtectedTemplate("crm/contenuti/dettaglioRegistroLead");
    }

    // =====================================================================
    // DELETE (AJAX)
    // POST /admin/crm/lead/{id}/delete
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
    // POST /admin/crm/lead/{id}/inviaSms
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
            if (registroLead.getIdutente() != 0 && registroLead.getIdutente() > 0) {
                registroLead.setUtente(
                        registroLeadService.findUtenteBase(registroLead.getIdutente()));
            } else if (registroLead.getUtente() != null) {
                registroLead.setIdutente(
                        registroLead.getUtente().getId() != null ? registroLead.getUtente().getId() : 0);
            }

            log.info("idutente ricevuto: {}", registroLead.getIdutente());
            log.info("utente ricevuto: {}", registroLead.getUtente() != null ? registroLead.getUtente().toString() : "null");
            log.info("telefono destinatario: {}", registroLead.getUtente() != null ? registroLead.getUtente().getTelefono() : "null");
            log.info("telefono2 destinatario: {}", registroLead.getUtente() != null ? registroLead.getUtente().getTelefono2() : "null");

            registroLeadService.inviaSms(registroLead, config);

            String now = LocalDateTime.now().format(DF);
            String operatore = config.getAmministratore().getNome()
                    + " " + config.getAmministratore().getCognome();

            registroLead.setId(null); // FONDAMENTALE
            registroLead.setDirezione("u");
            registroLead.setStore("Sms");
            registroLead.setLog(buildLogSemplice(now, operatore, "Creazione nuovo lead da"));

            registroLead = registroLeadService.crea(registroLead, config);

            model.addAttribute("registroLead", registroLead);
            model.addAttribute("config", config);

        } catch (Exception e) {
            log.error("Errore inviaSmsRegistroLead id={}", id, e);
            model.addAttribute("registroLead", registroLead);
            model.addAttribute("config", config);
        }

        return ViewUtils.resolveProtectedTemplate("crm/contenuti/dettaglioRegistroLeadUscitaSms");
    }

    // =====================================================================
    // INVIA EMAIL
    // POST /admin/crm/lead/{id}/inviaEmail
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
            if ((registroLead.getIdutente() != 0) && (registroLead.getIdutente() > 0)) {
                registroLead.setUtente(
                        registroLeadService.findUtenteBase(registroLead.getIdutente()));
            } else if (registroLead.getUtente() != null) {
                registroLead.setIdutente(
                        registroLead.getUtente().getId() != null ? registroLead.getUtente().getId() : 0);
            }

            log.info("idutente ricevuto: {}", registroLead.getIdutente());
            log.info("utente ricevuto: {}", registroLead.getUtente() != null ? registroLead.getUtente().toString() : "null");
            log.info("email destinatario: {}", registroLead.getUtente() != null ? registroLead.getUtente().getEmail() : "null");

            registroLeadService.inviaEmail(registroLead, config);

            String now = LocalDateTime.now().format(DF);
            String operatore = config.getAmministratore().getNome()
                    + " " + config.getAmministratore().getCognome();

            registroLead.setId(null); // FONDAMENTALE
            registroLead.setDirezione("u");
            registroLead.setStore("Email");
            registroLead.setLog(buildLogSemplice(now, operatore, "Creazione nuovo lead da"));

            registroLead = registroLeadService.crea(registroLead, config);

            model.addAttribute("registroLead", registroLead);
            model.addAttribute("config", config);

        } catch (Exception e) {
            log.error("Errore inviaEmailRegistroLead id={}", id, e);
            model.addAttribute("registroLead", registroLead);
            model.addAttribute("config", config);
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