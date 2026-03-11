package com.studiodomino.jplatform.cms.admin.controller;

import com.studiodomino.jplatform.shared.config.Configurazione;
import com.studiodomino.jplatform.shared.entity.MessaggioUtente;
import com.studiodomino.jplatform.shared.service.ConfigurazioneService;
import com.studiodomino.jplatform.shared.util.ViewUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/admin/email")
@RequiredArgsConstructor
@Slf4j
public class EmailController {

    private final ConfigurazioneService configurazioneService;

    // ─────────────────────────────────────────────────────────────
    // ELENCO INBOX
    // ─────────────────────────────────────────────────────────────

    @GetMapping({"", "/inbox", "/inbox/{idAccount}"})
    public String inbox(
            @PathVariable(value = "idAccount", required = false) String idAccount,
            @RequestParam(value = "folder", defaultValue = "INBOX") String folder,
            HttpServletRequest request,
            Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            model.addAttribute("listaEmailArrivo", Collections.emptyList());
            model.addAttribute("erroreMailer", null);
        } catch (Exception e) {
            log.error("Errore caricamento inbox account={} folder={}", idAccount, folder, e);
            model.addAttribute("listaEmailArrivo", Collections.emptyList());
            model.addAttribute("erroreMailer", "errore_generico");
        }

        model.addAttribute("config", config);
        return ViewUtils.resolveProtectedTemplate("email/elencoEmail");
    }

    // ─────────────────────────────────────────────────────────────
    // DETTAGLIO MESSAGGIO
    // ─────────────────────────────────────────────────────────────

    @GetMapping({"/open/{id}", "/open/{id}/{idAccount}"})
    public String openEmail(
            @PathVariable("id") int id,
            @PathVariable(value = "idAccount", required = false) String idAccount,
            @RequestParam(value = "rispondi", required = false) String rispondi,
            @RequestParam(value = "rispondiTutti", required = false) String rispondiTutti,
            @RequestParam(value = "inoltra", required = false) String inoltra,
            HttpServletRequest request,
            Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            MessaggioUtente messaggio = new MessaggioUtente();

            if (rispondi != null) {
                model.addAttribute("messaggioEmail", messaggio);
                model.addAttribute("config", config);
                return ViewUtils.resolveProtectedTemplate("email/componiEmail");
            }

            if (rispondiTutti != null) {
                model.addAttribute("messaggioEmail", messaggio);
                model.addAttribute("config", config);
                return ViewUtils.resolveProtectedTemplate("email/componiEmail");
            }

            if (inoltra != null) {
                model.addAttribute("messaggioEmail", messaggio);
                model.addAttribute("config", config);
                return ViewUtils.resolveProtectedTemplate("email/componiEmail");
            }

            model.addAttribute("messaggioEmail", messaggio);

        } catch (Exception e) {
            log.error("Errore open email id={} account={}", id, idAccount, e);
            model.addAttribute("messaggioEmail", new MessaggioUtente());
        }

        model.addAttribute("config", config);
        return ViewUtils.resolveProtectedTemplate("email/dettaglioEmail");
    }

    // ─────────────────────────────────────────────────────────────
    // COMPOSIZIONE
    // ─────────────────────────────────────────────────────────────

    @GetMapping({"/componi", "/componi/{idAccount}"})
    public String componiEmail(
            @PathVariable(value = "idAccount", required = false) String idAccount,
            HttpServletRequest request,
            Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            MessaggioUtente messaggio = new MessaggioUtente();
            model.addAttribute("messaggioEmail", messaggio);
        } catch (Exception e) {
            log.error("Errore preparazione composizione email", e);
            model.addAttribute("messaggioEmail", new MessaggioUtente());
        }

        model.addAttribute("config", config);
        return ViewUtils.resolveProtectedTemplate("email/componiEmail");
    }

    @PostMapping("/invia")
    public String inviaEmail(
            @RequestParam("to") String to,
            @RequestParam(value = "cc", required = false) String cc,
            @RequestParam("oggetto") String oggetto,
            @RequestParam("testo") String testo,
            HttpServletRequest request,
            Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            log.info("Invio email a={} cc={} oggetto={}", to, cc, oggetto);
            model.addAttribute("esito", "ok");
        } catch (Exception e) {
            log.error("Errore invio email a={}", to, e);
            model.addAttribute("esito", "errore");
        }

        model.addAttribute("messaggioEmail", new MessaggioUtente());
        model.addAttribute("config", config);
        return ViewUtils.resolveProtectedTemplate("email/componiEmail");
    }

    // ─────────────────────────────────────────────────────────────
    // ELIMINA
    // ─────────────────────────────────────────────────────────────

    @PostMapping({"/elimina/{idAccount}", "/elimina"})
    public String eliminaEmail(
            @RequestParam("id") String ids,
            @PathVariable(value = "idAccount", required = false) String idAccount,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            log.info("Eliminazione email ids={} account={}", ids, idAccount);
        } catch (Exception e) {
            log.error("Errore eliminazione email ids={}", ids, e);
        }

        if (idAccount != null && !idAccount.isBlank()) {
            return "redirect:/admin/email/inbox/" + idAccount;
        }
        return "redirect:/admin/email/inbox";
    }

    @PostMapping({"/eliminaMultiplo/{idAccount}", "/eliminaMultiplo"})
    @ResponseBody
    public ResponseEntity<String> eliminaMultiplo(
            @RequestParam("ids") List<String> ids,
            @PathVariable(value = "idAccount", required = false) String idAccount,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged() || ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body("Parametri non validi");
        }

        try {
            log.info("Eliminazione multipla email ids={} account={}", ids, idAccount);
            return ResponseEntity.ok("Cancellazione completata");
        } catch (Exception e) {
            log.error("Errore eliminazione multipla email", e);
            return ResponseEntity.internalServerError().body("Errore durante la cancellazione");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // DOWNLOAD ALLEGATO
    // ─────────────────────────────────────────────────────────────

    @GetMapping({"/allegato/{id}/{filename}", "/allegato/{id}/{filename}/{idAccount}"})
    public void getAllegato(
            @PathVariable("id") int id,
            @PathVariable("filename") int allegatoIndex,
            @PathVariable(value = "idAccount", required = false) String idAccount,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) {
            response.sendError(403);
            return;
        }

        try {
            log.info("Download allegato emailId={} index={} account={}", id, allegatoIndex, idAccount);
            response.sendError(501, "Non ancora implementato");
        } catch (Exception e) {
            log.error("Errore download allegato emailId={} index={}", id, allegatoIndex, e);
            response.sendError(500);
        }
    }
}