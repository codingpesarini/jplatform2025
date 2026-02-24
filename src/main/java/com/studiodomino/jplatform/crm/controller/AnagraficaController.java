package com.studiodomino.jplatform.crm.controller;

import com.studiodomino.jplatform.crm.service.AnagraficaService;
import com.studiodomino.jplatform.shared.config.Configurazione;
import com.studiodomino.jplatform.shared.entity.UtenteEsterno;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

/**
 * Controller admin per la gestione dell'anagrafica utenti CRM.
 * Conversione da GestioneAnagraficaUtenti.java (Struts) a Spring MVC.
 *
 * Mapping URL:
 *   GET  /admin/crm/utenti                → elencoAnagrafico
 *   GET  /admin/crm/utenti/new            → newAnagrafica
 *   GET  /admin/crm/utenti/{id}           → openAnagrafica
 *   POST /admin/crm/utenti/save           → saveAnagrafica
 *   POST /admin/crm/utenti/cerca          → ricercaAnagrafica
 *   POST /admin/crm/utenti/{id}/delete    → deleteAnagrafica (AJAX)
 *   POST /admin/crm/utenti/{id}/duplicate → duplicaAnagrafica
 *   POST /admin/crm/utenti/{id}/password  → modificaPasswordAjax (AJAX)
 */
@Controller
@RequestMapping("/admin/crm/utenti")
@RequiredArgsConstructor
@Slf4j
public class AnagraficaController {

    private final ConfigurazioneService configurazioneService;
    private final AnagraficaService anagraficaService;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");

    // =====================================================================
    // ELENCO ANAGRAFICO
    // Vecchio: elencoAnagrafico() → forward "successelencoAnagrafico"
    // =====================================================================

    @GetMapping
    public String elencoAnagrafico(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        // L'elenco parte vuoto, si popola con la ricerca
        // Vecchio: elencoAnagrafico era vuoto finché non si faceva RicercaAnagrafica
        model.addAttribute("elencoAnagrafico", List.of());
        model.addAttribute("ricerca", new UtenteEsterno());
        model.addAttribute("config", config);

        return ViewUtils.resolveProtectedTemplate("crm/sezioni/elencoAnagrafica");
    }

    // =====================================================================
    // NEW ANAGRAFICA
    // Vecchio: newAnagrafica() → forward "successopenAnagrafica"
    // =====================================================================

    @GetMapping("/new")
    public String newAnagrafica(
            @RequestParam(value = "email", defaultValue = "") String email,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        UtenteEsterno utente = new UtenteEsterno();
        utente.setId(-1);
        utente.setStatus("1");
        utente.setNazione("1");
        if (!email.isEmpty()) {
            utente.setEmail(email); // newAnagraficaByEmail
        }

        model.addAttribute("utente", utente);
        model.addAttribute("config", config);

        return ViewUtils.resolveProtectedTemplate("crm/contenuti/dettaglioAnagrafica");
    }

    // =====================================================================
    // OPEN ANAGRAFICA
    // Vecchio: openAnagrafica() → forward "successopenAnagrafica"
    // =====================================================================

    @GetMapping("/{id}")
    public String openAnagrafica(@PathVariable Integer id,
                                 HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";
        if (id == null || id == -1) {
            return "redirect:/admin/crm/utenti/new";
        }

        try {
            UtenteEsterno utente = anagraficaService.findById(id);
            model.addAttribute("utente", utente);
            model.addAttribute("config", config);
        } catch (Exception e) {
            log.error("Errore openAnagrafica id={}", id, e);
        }

        return ViewUtils.resolveProtectedTemplate("crm/contenuti/dettaglioAnagrafica");
    }

    // =====================================================================
    // SAVE ANAGRAFICA
    // Vecchio: saveAnagrafica() → forward "successopenAnagrafica"
    // =====================================================================

    @PostMapping("/save")
    public String saveAnagrafica(
            @ModelAttribute UtenteEsterno utente,
            @RequestParam(value = "newpassword", defaultValue = "") String newPassword,
            @RequestParam(value = "newpasswordretype", defaultValue = "") String newPasswordRetype,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            String now = LocalDateTime.now().format(DF);

            boolean isNew = utente.getId() == null || utente.getId().equals("-1");

            if (isNew) {
                // CREA nuovo utente con username auto-generato
                Random rnd = new Random(System.currentTimeMillis());
                int rnn = Math.abs(rnd.nextInt() / 1000);
                String username = utente.getNome() + "@" + rnn;
                utente.setUsername(username);
                utente.setPassword(username);
                utente.setDatacreazione(now);
                utente.setDataultimoaccesso(now);

                utente = anagraficaService.crea(utente);
                log.info("Utente creato: id={}", utente.getId());

            } else {
                // AGGIORNA utente esistente
                // Cambia password solo se entrambi i campi sono valorizzati e coincidono
                if (!newPassword.isEmpty() && newPassword.equals(newPasswordRetype)) {
                    anagraficaService.cambiaPassword(utente.getId(), newPassword);
                    log.info("Password cambiata per utente id={}", utente.getId());
                }

                utente = anagraficaService.salva(utente);
                log.info("Utente salvato: id={}", utente.getId());
            }

            model.addAttribute("utente", utente);
            model.addAttribute("config", config);

        } catch (Exception e) {
            log.error("Errore saveAnagrafica", e);
            model.addAttribute("error", "Errore nel salvataggio: " + e.getMessage());
            model.addAttribute("utente", utente);
            model.addAttribute("config", config);
        }

        return ViewUtils.resolveProtectedTemplate("crm/contenuti/dettaglioAnagrafica");
    }

    // =====================================================================
    // RICERCA ANAGRAFICA
    // Vecchio: RicercaAnagrafica() → forward "successelencoAnagrafico"
    // =====================================================================

    @PostMapping("/cerca")
    public String ricercaAnagrafica(
            @ModelAttribute UtenteEsterno ricerca,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            List<UtenteEsterno> risultati = anagraficaService.cerca(ricerca);
            model.addAttribute("elencoAnagrafico", risultati);
            model.addAttribute("ricerca", ricerca);
            model.addAttribute("config", config);

        } catch (Exception e) {
            log.error("Errore ricercaAnagrafica", e);
            model.addAttribute("elencoAnagrafico", List.of());
            model.addAttribute("ricerca", ricerca);
            model.addAttribute("config", config);
        }

        return ViewUtils.resolveProtectedTemplate("crm/contenuti/elencoAnagrafico");
    }

    // =====================================================================
    // DELETE ANAGRAFICA (AJAX)
    // Vecchio: deleteAnagrafica() → null (risposta diretta)
    // =====================================================================

    @PostMapping("/{id}/delete")
    @ResponseBody
    public ResponseEntity<String> deleteAnagrafica(
            @PathVariable Integer id,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return ResponseEntity.status(401).body("KO");

        try {
            anagraficaService.elimina(id);
            log.info("Utente eliminato: id={}", id);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Errore deleteAnagrafica id={}", id, e);
            return ResponseEntity.ok("KO");
        }
    }

    // =====================================================================
    // DUPLICA ANAGRAFICA
    // Vecchio: duplicaAnagrafica() → forward "successopenAnagrafica"
    // =====================================================================

    @PostMapping("/{id}/duplicate")
    public String duplicaAnagrafica(@PathVariable Integer id,
                                    HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            UtenteEsterno original = anagraficaService.findById(id);
            original.setId(-1);
            original.setCognome(original.getCognome() + " (2)");

            model.addAttribute("utente", original);
            model.addAttribute("config", config);

        } catch (Exception e) {
            log.error("Errore duplicaAnagrafica id={}", id, e);
        }

        return ViewUtils.resolveProtectedTemplate("crm/contenuti/dettaglioAnagrafica");
    }

    // =====================================================================
    // MODIFICA PASSWORD (AJAX)
    // Vecchio: modificaPasswordAjax() → forward "successmodificaPasswordAnagrafica"
    // =====================================================================

    @PostMapping("/{id}/password")
    @ResponseBody
    public ResponseEntity<String> modificaPassword(
            @PathVariable Integer id,
            @RequestParam String password,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return ResponseEntity.status(401).body("KO");

        try {
            String nuovaPassword = anagraficaService.cambiaPassword(id, password);
            log.info("Password modificata via AJAX per utente id={}", id);
            return ResponseEntity.ok(nuovaPassword);
        } catch (Exception e) {
            log.error("Errore modificaPassword id={}", id, e);
            return ResponseEntity.ok("KO");
        }
    }
}