package com.studiodomino.jplatform.cms.admin.controller;

import com.studiodomino.jplatform.cms.admin.service.EmailSenderService;
import com.studiodomino.jplatform.shared.config.Configurazione;
import com.studiodomino.jplatform.shared.entity.Account;
import com.studiodomino.jplatform.shared.entity.MessaggioUtente;
import com.studiodomino.jplatform.shared.repository.AccountRepository;
import com.studiodomino.jplatform.shared.repository.MessaggioUtenteRepository;
import com.studiodomino.jplatform.shared.service.ConfigurazioneService;
import com.studiodomino.jplatform.shared.service.ImapService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/admin/email")
@RequiredArgsConstructor
@Slf4j
public class EmailController {

    private final ConfigurazioneService configurazioneService;
    private final EmailSenderService emailSenderService;
    private final ImapService imapService;
    private final AccountRepository accountRepository;
    private final MessaggioUtenteRepository messaggioUtenteRepository;

    // ============================================================
// 1. FIX inbox() — gestisce idAccount multiplo "13,3"
//    Sostituisci il metodo esistente con questo
// ============================================================

    @GetMapping({"", "/inbox", "/inbox/{idAccount}", "/{idAccount}/cartella/{folder}"})
    public String inbox(
            @PathVariable(value = "idAccount", required = false) String idAccount,
            @PathVariable(value = "folder", required = false) String folderPath,
            @RequestParam(value = "folder", defaultValue = "INBOX") String folderParam,
            HttpServletRequest request,
            Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        String folder = (folderPath != null) ? folderPath : folderParam;

        // --- NUOVA LOGICA PER LISTA ACCOUNT ASSOCIATI ---
        List<Account> associati = new ArrayList<>();
        String idEmailRaw = config.getAmministratore().getIdaccountemail();
        String idPecRaw = config.getAmministratore().getIdaccountpec();
        String tuttiIds = (idEmailRaw != null ? idEmailRaw : "") + "," + (idPecRaw != null ? idPecRaw : "");

        Set<Integer> setIds = new HashSet<>();
        for (String s : tuttiIds.split(",")) {
            if (!s.trim().isEmpty()) setIds.add(Integer.parseInt(s.trim()));
        }
        if (!setIds.isEmpty()) {
            associati = accountRepository.findAllById(setIds);
        }
        model.addAttribute("listaAccountAssociati", associati);
        // ------------------------------------------------

        // Se idAccount contiene virgola (es. "13,3"), prendi solo il primo per caricare le mail
        String accountDaCaricare = idAccount;
        if (accountDaCaricare != null && accountDaCaricare.contains(",")) {
            accountDaCaricare = accountDaCaricare.split(",")[0].trim();
        }

        try {
            List<Map<String, Object>> emails = Collections.emptyList();
            if (accountDaCaricare != null && !accountDaCaricare.isBlank() && !"0".equals(accountDaCaricare)) {
                config.setActualMailboxId(accountDaCaricare);
                config.setActualMailboxFolderName(folder);
                configurazioneService.saveConfig(session, config);
                emails = imapService.leggiInbox(Integer.parseInt(accountDaCaricare), folder);
            }
            model.addAttribute("listaEmailArrivo", emails);
            model.addAttribute("folderAttivo", folder);
            model.addAttribute("currentAccountId", accountDaCaricare);
        } catch (Exception e) {
            log.error("Errore caricamento inbox account={} folder={}", accountDaCaricare, folder, e);
            model.addAttribute("listaEmailArrivo", Collections.emptyList());
            model.addAttribute("folderAttivo", folder);
        }

        model.addAttribute("config", config);
        return ViewUtils.resolveProtectedTemplate("email/elencoEmail");
    }


// ============================================================
// 2. NUOVO endpoint — conta non letti per tutti gli account
//    dell'utente loggato. Chiamato via fetch dall'header.
//    Restituisce: [ { id, descrizione, nonLetti }, ... ]
// ============================================================

    @GetMapping("/api/nonletti")
    @ResponseBody
    public List<Map<String, Object>> nonLetti(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return Collections.emptyList();

        List<Map<String, Object>> risultato = new ArrayList<>();

        // Prendiamo gli ID separati da virgola
        String idEmailRaw = config.getAmministratore().getIdaccountemail();
        String idPecRaw = config.getAmministratore().getIdaccountpec();
        String tuttiIds = (idEmailRaw != null ? idEmailRaw : "") + "," + (idPecRaw != null ? idPecRaw : "");

        // Usiamo un Set per evitare duplicati ed evitare l'ID "0"
        Set<String> setIds = new HashSet<>();
        for (String s : tuttiIds.split(",")) {
            String trim = s.trim();
            if (!trim.isEmpty() && !"0".equals(trim)) setIds.add(trim);
        }

        for (String idStr : setIds) {
            try {
                int idAcc = Integer.parseInt(idStr);
                Account acc = accountRepository.findById(idAcc).orElse(null);

                if (acc != null) {
                    // Chiamata IMAP per contare i messaggi non letti sul server
                    int conteggio = imapService.contaMessaggiNonLetti(idAcc);

                    Map<String, Object> m = new HashMap<>();
                    m.put("id", idAcc);
                    m.put("descrizione", acc.getDescrizione());

                    // Usiamo emailAccount se presente, altrimenti pecAccount
                    String emailVisualizzata = (acc.getEmailAccount() != null && !acc.getEmailAccount().isEmpty())
                            ? acc.getEmailAccount() : acc.getPecAccount();
                    m.put("email", emailVisualizzata);

                    m.put("nonLetti", conteggio);
                    m.put("tipo", (acc.getPecAccount() != null && !acc.getPecAccount().isEmpty()) ? "PEC" : "EMAIL");

                    risultato.add(m);
                }
            } catch (Exception e) {
                log.warn("Errore recupero account o conteggio per ID {}: {}", idStr, e.getMessage());
            }
        }
        return risultato;
    }


    @GetMapping({"/open/{id}", "/open/{id}/{idAccount}"})
    public String openEmail(
            @PathVariable("id") int id,
            @PathVariable(value = "idAccount", required = false) String idAccount,
            @RequestParam(value = "fragment", defaultValue = "false") String fragment,
            HttpServletRequest request,
            Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        MessaggioUtente messaggio = new MessaggioUtente();
        try {
            String accId = (idAccount != null && !idAccount.isBlank() && !"0".equals(idAccount))
                    ? idAccount : config.getActualMailboxId();

            if (accId != null && !"0".equals(accId)) {
                Map<String, Object> emailData = imapService.leggiMessaggio(
                        Integer.parseInt(accId), id, config.getActualMailboxFolderName());
                if (emailData != null) {
                    messaggio.setMittente((String) emailData.getOrDefault("mittente", ""));
                    messaggio.setIndirizzoMittente((String) emailData.getOrDefault("mittente", ""));
                    messaggio.setOggetto((String) emailData.getOrDefault("oggetto", ""));
                    messaggio.setMessaggio((String) emailData.getOrDefault("testo", ""));
                    messaggio.setDate(emailData.get("data") != null ? emailData.get("data").toString() : "");
                    messaggio.setId(id);
                }
            }
        } catch (Exception e) {
            log.error("Errore open email id={} account={}", id, idAccount, e);
        }

        model.addAttribute("messaggioEmail", messaggio);
        model.addAttribute("config", config);

        if ("true".equals(fragment)) {
            return ViewUtils.resolveProtectedTemplate("email/dettaglioEmailFragment");
        }
        return ViewUtils.resolveProtectedTemplate("email/dettaglioEmail");
    }

    @GetMapping({"/rispondi/{id}", "/rispondi/{id}/{idAccount}"})
    public String rispondiEmail(
            @PathVariable("id") int id,
            @PathVariable(value = "idAccount", required = false) String idAccount,
            HttpServletRequest request,
            Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        MessaggioUtente messaggio = new MessaggioUtente();
        try {
            String accId = (idAccount != null && !idAccount.isBlank() && !"0".equals(idAccount))
                    ? idAccount : config.getActualMailboxId();

            if (accId != null && !"0".equals(accId)) {
                Map<String, Object> emailData = imapService.leggiMessaggio(
                        Integer.parseInt(accId), id, config.getActualMailboxFolderName());
                if (emailData != null) {
                    messaggio.setOggetto("Re: " + emailData.getOrDefault("oggetto", ""));
                    messaggio.setEmailDestinatario((String) emailData.getOrDefault("mittente", ""));
                    messaggio.setMessaggio("");
                }
            }
        } catch (Exception e) {
            log.error("Errore rispondi email id={}", id, e);
        }

        model.addAttribute("messaggioEmail", messaggio);
        model.addAttribute("config", config);
        return ViewUtils.resolveProtectedTemplate("email/componiEmail");
    }

    @GetMapping({"/inoltra/{id}", "/inoltra/{id}/{idAccount}"})
    public String inoltraEmail(
            @PathVariable("id") int id,
            @PathVariable(value = "idAccount", required = false) String idAccount,
            HttpServletRequest request,
            Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        MessaggioUtente messaggio = new MessaggioUtente();
        try {
            String accId = (idAccount != null && !idAccount.isBlank() && !"0".equals(idAccount))
                    ? idAccount : config.getActualMailboxId();

            if (accId != null && !"0".equals(accId)) {
                Map<String, Object> emailData = imapService.leggiMessaggio(
                        Integer.parseInt(accId), id, config.getActualMailboxFolderName());
                if (emailData != null) {
                    messaggio.setOggetto("Fwd: " + emailData.getOrDefault("oggetto", ""));
                    messaggio.setMessaggio((String) emailData.getOrDefault("testo", ""));
                }
            }
        } catch (Exception e) {
            log.error("Errore inoltra email id={}", id, e);
        }

        model.addAttribute("messaggioEmail", messaggio);
        model.addAttribute("config", config);
        return ViewUtils.resolveProtectedTemplate("email/componiEmail");
    }

    @GetMapping({"/componi", "/componi/{idAccount}"})
    public String componiEmail(
            @PathVariable(value = "idAccount", required = false) String idAccount,
            HttpServletRequest request,
            Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        model.addAttribute("messaggioEmail", new MessaggioUtente());
        model.addAttribute("config", config);
        return ViewUtils.resolveProtectedTemplate("email/componiEmail");
    }

    @PostMapping("/invia")
    public String inviaEmail(
            @RequestParam("to") String to,
            @RequestParam(value = "cc", required = false) String cc,
            @RequestParam("oggetto") String oggetto,
            @RequestParam("testo") String testo,
            @RequestParam(value = "file_upload", required = false) List<MultipartFile> allegati,
            HttpServletRequest request,
            Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        MessaggioUtente messaggio = new MessaggioUtente();
        messaggio.setEmailMittente(to);
        messaggio.setMessaggio(testo);
        messaggio.setOggetto(oggetto);

        try {
            emailSenderService.inviaEmail(to, cc, oggetto, testo, allegati);
            model.addAttribute("messaggioEmail", new MessaggioUtente());
            model.addAttribute("esito", "ok");
        } catch (Exception e) {
            log.error("Errore invio email a={}", to, e);
            model.addAttribute("esito", "errore");
            model.addAttribute("errorMessage", "Errore: " + e.getMessage());
            model.addAttribute("messaggioEmail", messaggio);
        }

        model.addAttribute("config", config);
        return ViewUtils.resolveProtectedTemplate("email/componiEmail");
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
            log.info("Inizio eliminazione totale per account {}: ids {}", idAccount, ids);

            Account account = accountRepository.findById(Integer.parseInt(idAccount))
                    .orElseThrow(() -> new RuntimeException("Account non trovato"));

            // 1. ELIMINAZIONE FISICA DAL SERVER MAIL (Outlook/Cellulari)
            imapService.cancellaMessaggiDalServer(account, ids);

            // 2. ELIMINAZIONE DAL DATABASE LOCALE
            for (String idStr : ids) {
                // Qui devi decidere: se cancelli fisicamente dal server,
                // devi cancellare anche la riga dal tuo DB
                try {
                    Integer dbId = Integer.parseInt(idStr);
                    messaggioUtenteRepository.deleteById(dbId);
                } catch (Exception ex) {
                    log.warn("Errore durante la rimozione dal DB dell'id {}", idStr);
                }
            }

            return ResponseEntity.ok("Cancellazione completata su server e database");

        } catch (Exception e) {
            log.error("Errore critico durante l'eliminazione", e);
            return ResponseEntity.internalServerError().body("Errore: " + e.getMessage());
        }
    }

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