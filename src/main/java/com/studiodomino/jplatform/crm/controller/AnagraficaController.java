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

@Controller
@RequestMapping("/admin/crm/utenti")
@RequiredArgsConstructor
@Slf4j
public class AnagraficaController {

    private final ConfigurazioneService configurazioneService;
    private final AnagraficaService anagraficaService;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");

    @GetMapping
    public String elencoAnagrafico(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            model.addAttribute("elencoAnagrafico", anagraficaService.findAll());
        } catch (Exception e) {
            log.error("Errore elencoAnagrafico", e);
            model.addAttribute("elencoAnagrafico", List.of());
        }
        model.addAttribute("ricerca", new UtenteEsterno());
        model.addAttribute("config", config);

        return ViewUtils.resolveProtectedTemplate("crm/sezioni/elencoAnagrafica");
    }

    @GetMapping("/new")
    public String newAnagrafica(
            @RequestParam(value = "email", defaultValue = "") String email,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        config.setGruppi(configurazioneService.getAllGruppi(String.valueOf(config.getIdSito())));

        UtenteEsterno utente = new UtenteEsterno();
        utente.setId(null);
        utente.setStatus("1");
        utente.setNazione("");
        utente.setDatanascita("");
        utente.setCodicefiscale("");
        utente.setPartitaiva("");
        utente.setSocieta("");
        utente.setComune("");
        utente.setCap("");
        utente.setProvincia("");
        utente.setIndirizzo("");
        utente.setIndirizzospedizione("");
        utente.setTelefono("");
        utente.setTelefono2("");
        utente.setPec("");
        if (!email.isEmpty()) utente.setEmail(email);

        model.addAttribute("utente", utente);
        model.addAttribute("elencoAnagrafico", List.of(utente));
        model.addAttribute("config", config);

        return ViewUtils.resolveProtectedTemplate("crm/contenuti/dettaglioAnagrafica");
    }

    @GetMapping("/{id}")
    public String openAnagrafica(@PathVariable Integer id,
                                 HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        config.setGruppi(configurazioneService.getAllGruppi(String.valueOf(config.getIdSito())));

        try {
            UtenteEsterno utente = anagraficaService.findById(id);
            model.addAttribute("utente", utente);
            model.addAttribute("elencoAnagrafico", List.of(utente));
            model.addAttribute("config", config);
        } catch (Exception e) {
            log.error("Errore openAnagrafica id={}", id, e);
            return "redirect:/admin/crm/utenti";
        }

        return ViewUtils.resolveProtectedTemplate("crm/contenuti/dettaglioAnagrafica");
    }

    @PostMapping("/save")
    public String saveAnagrafica(
            @RequestParam(value = "id", required = false) Integer id,
            @ModelAttribute UtenteEsterno utente,
            @RequestParam(value = "newpassword", defaultValue = "") String newPassword,
            @RequestParam(value = "newpasswordretype", defaultValue = "") String newPasswordRetype,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        config.setGruppi(configurazioneService.getAllGruppi(String.valueOf(config.getIdSito())));

        try {
            String now = LocalDateTime.now().format(DF);
            boolean isNew = (id == null || id <= 0);

            if (isNew) {
                utente.setId(null);
                Random rnd = new Random(System.currentTimeMillis());
                int rnn = Math.abs(rnd.nextInt() / 1000);
                String username = (utente.getNome() != null && !utente.getNome().isEmpty()
                        ? utente.getNome() : "user") + "@" + rnn;
                utente.setUsername(username);
                utente.setPassword(username);
                utente.setDatacreazione(now);
                utente.setDataultimoaccesso(now);

                utente = anagraficaService.crea(utente);
                log.info("Utente creato: id={}", utente.getId());

            } else {
                utente.setId(id);
                if (!newPassword.isEmpty() && newPassword.equals(newPasswordRetype)) {
                    anagraficaService.cambiaPassword(id, newPassword);
                    log.info("Password cambiata per utente id={}", id);
                }
                utente = anagraficaService.salva(utente);
                log.info("Utente salvato: id={}", utente.getId());
            }

            model.addAttribute("utente", utente);
            model.addAttribute("elencoAnagrafico", List.of(utente));
            model.addAttribute("config", config);

        } catch (Exception e) {
            log.error("Errore saveAnagrafica", e);
            model.addAttribute("error", "Errore nel salvataggio: " + e.getMessage());
            model.addAttribute("utente", utente);
            model.addAttribute("elencoAnagrafico", List.of(utente));
            model.addAttribute("config", config);
        }

        return ViewUtils.resolveProtectedTemplate("crm/contenuti/dettaglioAnagrafica");
    }

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

        return ViewUtils.resolveProtectedTemplate("crm/sezioni/elencoAnagrafica");
    }

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

    @PostMapping("/delete-multiplo")
    @ResponseBody
    public String deleteMultiplo(
            @RequestParam(value = "ids[]", required = false) Integer[] ids,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "KO";
        if (ids == null || ids.length == 0) return "KO";

        try {
            for (Integer id : ids) anagraficaService.elimina(id);
            log.info("Delete multiplo utenti: {}", java.util.Arrays.toString(ids));
            return "OK";
        } catch (Exception e) {
            log.error("Errore delete multiplo", e);
            return "KO";
        }
    }

    @GetMapping("/{id}/duplicate")
    public String duplicaAnagrafica(
            @PathVariable Integer id,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        config.setGruppi(configurazioneService.getAllGruppi(String.valueOf(config.getIdSito())));

        try {
            UtenteEsterno original = anagraficaService.findById(id);
            original.setId(null);
            original.setCognome(original.getCognome() + " (copia)");
            original.setEmail("");
            original.setUsername("");

            model.addAttribute("utente", original);
            model.addAttribute("elencoAnagrafico", List.of(original));
            model.addAttribute("config", config);

        } catch (Exception e) {
            log.error("Errore duplicaAnagrafica id={}", id, e);
            return "redirect:/admin/crm/utenti";
        }

        return ViewUtils.resolveProtectedTemplate("crm/contenuti/dettaglioAnagrafica");
    }

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
            log.info("Password modificata per utente id={}", id);
            return ResponseEntity.ok(nuovaPassword);
        } catch (Exception e) {
            log.error("Errore modificaPassword id={}", id, e);
            return ResponseEntity.ok("KO");
        }
    }
}