package com.studiodomino.jplatform.cms.admin.controller;

import com.studiodomino.jplatform.shared.config.Configurazione;
import com.studiodomino.jplatform.shared.entity.Account;
import com.studiodomino.jplatform.shared.repository.AccountRepository;
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

import java.util.List;

@Controller
@RequestMapping("/admin/account")
@RequiredArgsConstructor
@Slf4j
public class MailAccountController {

    private final ConfigurazioneService configurazioneService;
    private final AccountRepository accountRepository;

    // ─────────────────────────────────────────────────────────────
    // ELENCO
    // ─────────────────────────────────────────────────────────────

    @GetMapping
    public String elencoAccount(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            List<Account> elencoAccount = accountRepository.findAllByOrderByDescrizioneAsc();
            model.addAttribute("elencoAccount", elencoAccount);
        } catch (Exception e) {
            log.error("Errore elencoAccount", e);
            model.addAttribute("elencoAccount", List.of());
        }

        model.addAttribute("config", config);
        model.addAttribute("account", new Account());
        return ViewUtils.resolveProtectedTemplate("impostazioni/sezioni/elencoMailAccount");
    }

    @GetMapping("/tipo")
    public String elencoAccountTipo(
            @RequestParam("tipo") String tipo,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            List<Account> elencoAccount = accountRepository.findByTipoAccountOrderByDescrizioneAsc(tipo);
            model.addAttribute("elencoAccount", elencoAccount);
        } catch (Exception e) {
            log.error("Errore elencoAccountTipo tipo={}", tipo, e);
            model.addAttribute("elencoAccount", List.of());
        }

        model.addAttribute("config", config);
        model.addAttribute("account", new Account());
        return ViewUtils.resolveProtectedTemplate("impostazioni/sezioni/elencoMailAccount");
    }

    // ─────────────────────────────────────────────────────────────
    // DETTAGLIO / NUOVO
    // ─────────────────────────────────────────────────────────────

    @GetMapping("/new")
    public String newForm(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        Account account = new Account();
        model.addAttribute("account", account);
        model.addAttribute("config", config);
        return ViewUtils.resolveProtectedTemplate("impostazioni/contenuti/dettaglioMailAccount");
    }

    @GetMapping("/{id}")
    public String open(@PathVariable Integer id,
                       HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            Account account = accountRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Account non trovato: " + id));
            model.addAttribute("account", account);
        } catch (Exception e) {
            log.error("Errore open account id={}", id, e);
            return "redirect:/admin/account?error=notfound";
        }

        model.addAttribute("config", config);
        return ViewUtils.resolveProtectedTemplate("impostazioni/contenuti/dettaglioMailAccount");
    }

    // ─────────────────────────────────────────────────────────────
    // DUPLICA
    // ─────────────────────────────────────────────────────────────

    @GetMapping("/duplica")
    public String duplica(@RequestParam("id") Integer id,
                          HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            Account account = accountRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Account non trovato: " + id));
            account.setId(null); // nuovo record
            model.addAttribute("account", account);
        } catch (Exception e) {
            log.error("Errore duplica account id={}", id, e);
            return "redirect:/admin/account?error=duplica_failed";
        }

        model.addAttribute("config", config);
        return ViewUtils.resolveProtectedTemplate("impostazioni/contenuti/dettaglioMailAccount");
    }

    // ─────────────────────────────────────────────────────────────
    // SALVA
    // ─────────────────────────────────────────────────────────────

    @PostMapping("/save")
    public String save(@ModelAttribute Account form,
                       HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            normalizzaAccount(form);

            Account db;
            boolean isNew = form.getId() == null;

            if (isNew) {
                db = new Account();
            } else {
                db = accountRepository.findById(form.getId())
                        .orElseThrow(() -> new RuntimeException("Account non trovato"));
            }

            db.setDescrizione(form.getDescrizione());
            db.setTipoAccount(form.getTipoAccount());
            db.setLasciaCopiaServer(form.getLasciaCopiaServer());

            // EMAIL
            db.setEmailAccount(form.getEmailAccount());
            db.setEmailIntestazione(form.getEmailIntestazione());
            db.setEmailUser(form.getEmailUser());
            db.setEmailPassword(form.getEmailPassword());
            db.setEmailInServer(form.getEmailInServer());
            db.setEmailInServerPort(form.getEmailInServerPort());
            db.setEmailOutServer(form.getEmailOutServer());
            db.setEmailOutServerPort(form.getEmailOutServerPort());
            db.setEmailInServerType(form.getEmailInServerType());
            db.setEmailOutServerAuth(form.getEmailOutServerAuth());

            // PEC
            db.setPecAccount(form.getPecAccount());
            db.setPecIntestazione(form.getPecIntestazione());
            db.setPecUser(form.getPecUser());
            db.setPecPassword(form.getPecPassword());
            db.setPecInServer(form.getPecInServer());
            db.setPecInServerPort(form.getPecInServerPort());
            db.setPecOutServer(form.getPecOutServer());
            db.setPecOutServerPort(form.getPecOutServerPort());
            db.setPecInServerType(form.getPecInServerType());
            db.setPecOutServerAuth(form.getPecOutServerAuth());

            Account saved = accountRepository.save(db);
            return "redirect:/admin/account/" + saved.getId() + "?success=saved";

        } catch (Exception e) {
            log.error("Errore save account", e);
            model.addAttribute("account", form);
            model.addAttribute("config", config);
            return ViewUtils.resolveProtectedTemplate("impostazioni/contenuti/dettaglioMailAccount");
        }
    }

    private void normalizzaAccount(Account a) {
        a.setDescrizione(nvl(a.getDescrizione()));
        a.setTipoAccount(nvl(a.getTipoAccount()));
        a.setLasciaCopiaServer(nvl(a.getLasciaCopiaServer()));

        // EMAIL
        a.setEmailAccount(nvl(a.getEmailAccount()));
        a.setEmailIntestazione(nvl(a.getEmailIntestazione()));
        a.setEmailUser(nvl(a.getEmailUser()));
        a.setEmailPassword(nvl(a.getEmailPassword()));
        a.setEmailInServer(nvl(a.getEmailInServer()));
        a.setEmailInServerPort(nvlNumero(a.getEmailInServerPort()));
        a.setEmailOutServer(nvl(a.getEmailOutServer()));
        a.setEmailOutServerPort(nvlNumero(a.getEmailOutServerPort()));
        a.setEmailInServerType(nvl(a.getEmailInServerType()));
        a.setEmailOutServerAuth(nvl(a.getEmailOutServerAuth()));

        // PEC
        a.setPecAccount(nvl(a.getPecAccount()));
        a.setPecIntestazione(nvl(a.getPecIntestazione()));
        a.setPecUser(nvl(a.getPecUser()));
        a.setPecPassword(nvl(a.getPecPassword()));
        a.setPecInServer(nvl(a.getPecInServer()));
        a.setPecInServerPort(nvlNumero(a.getPecInServerPort()));
        a.setPecOutServer(nvl(a.getPecOutServer()));
        a.setPecOutServerPort(nvlNumero(a.getPecOutServerPort()));
        a.setPecInServerType(nvl(a.getPecInServerType()));
        a.setPecOutServerAuth(nvl(a.getPecOutServerAuth()));
    }

    private String nvl(String value) {
        return value == null ? "" : value.trim();
    }

    private Integer nvlNumero(Integer value) {
        return value == null ? 0 : value;
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String v = value.trim();
        return v.isEmpty() ? null : v;
    }

    // ─────────────────────────────────────────────────────────────
    // ELIMINA
    // ─────────────────────────────────────────────────────────────

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            accountRepository.deleteById(id);
            return "redirect:/admin/account?success=deleted";
        } catch (Exception e) {
            log.error("Errore eliminazione account id={}", id, e);
            return "redirect:/admin/account?error=delete_failed";
        }
    }

    @PostMapping("/eliminaMultiplo")
    @ResponseBody
    public ResponseEntity<String> eliminaMultiplo(
            @RequestParam("ids") List<Integer> ids,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged() || ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body("Parametri non validi");
        }

        try {
            accountRepository.deleteAllById(ids);
            return ResponseEntity.ok("Cancellazione completata");
        } catch (Exception e) {
            log.error("Errore eliminazione multipla account", e);
            return ResponseEntity.internalServerError().body("Errore durante la cancellazione");
        }
    }

    @GetMapping("/popup")
    @ResponseBody
    public ResponseEntity<List<Account>> popupAccount(
            @RequestParam(required = false) String tipo,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return ResponseEntity.status(401).build();
        if (tipo == null || tipo.isEmpty()) return ResponseEntity.badRequest().build();

        List<Account> lista = accountRepository.findByTipoAccountOrderByDescrizioneAsc(tipo);
        return ResponseEntity.ok(lista);
    }
}