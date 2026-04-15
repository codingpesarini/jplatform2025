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
    // GET /admin/account
    // ─────────────────────────────────────────────────────────────
    @GetMapping
    public String elencoAccount(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            model.addAttribute("elencoAccount", accountRepository.findAllByOrderByDescrizioneAsc());
        } catch (Exception e) {
            log.error("Errore elencoAccount", e);
            model.addAttribute("elencoAccount", List.of());
        }
        model.addAttribute("account", new Account());
        model.addAttribute("config", config);
        return ViewUtils.resolveProtectedTemplate("impostazioni/sezioni/elencoMailAccount");
    }

    // ─────────────────────────────────────────────────────────────
    // GET /admin/account/new
    // ─────────────────────────────────────────────────────────────
    @GetMapping("/new")
    public String newForm(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        model.addAttribute("account", new Account());
        model.addAttribute("config", config);
        return ViewUtils.resolveProtectedTemplate("impostazioni/contenuti/dettaglioMailAccount");
    }

    // ─────────────────────────────────────────────────────────────
    // GET /admin/account/{id}
    // ─────────────────────────────────────────────────────────────
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
            return "redirect:/admin/account";
        }
        model.addAttribute("config", config);
        return ViewUtils.resolveProtectedTemplate("impostazioni/contenuti/dettaglioMailAccount");
    }

    // ─────────────────────────────────────────────────────────────
    // GET /admin/account/{id}/duplica
    // ─────────────────────────────────────────────────────────────
    @GetMapping("/{id}/duplica")
    public String duplica(@PathVariable Integer id,
                          HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            Account account = accountRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Account non trovato: " + id));
            account.setId(null);
            model.addAttribute("account", account);
        } catch (Exception e) {
            log.error("Errore duplica account id={}", id, e);
            return "redirect:/admin/account";
        }
        model.addAttribute("config", config);
        return ViewUtils.resolveProtectedTemplate("impostazioni/contenuti/dettaglioMailAccount");
    }

    // ─────────────────────────────────────────────────────────────
    // POST /admin/account/save
    // ─────────────────────────────────────────────────────────────
    @PostMapping("/save")
    public String save(@ModelAttribute Account form,
                       HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            normalizzaAccount(form);

            Account db = (form.getId() == null)
                    ? new Account()
                    : accountRepository.findById(form.getId())
                    .orElseThrow(() -> new RuntimeException("Account non trovato"));

            db.setDescrizione(form.getDescrizione());
            db.setTipoAccount(form.getTipoAccount());
            db.setLasciaCopiaServer(form.getLasciaCopiaServer());
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
            log.info("Account salvato id={}", saved.getId());
            return "redirect:/admin/account/" + saved.getId();

        } catch (Exception e) {
            log.error("Errore save account", e);
            model.addAttribute("account", form);
            model.addAttribute("config", config);
            return ViewUtils.resolveProtectedTemplate("impostazioni/contenuti/dettaglioMailAccount");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // POST /admin/account/{id}/delete  (AJAX)
    // ─────────────────────────────────────────────────────────────
    @PostMapping("/{id}/delete")
    @ResponseBody
    public ResponseEntity<String> delete(@PathVariable Integer id,
                                         HttpServletRequest request) {
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return ResponseEntity.status(401).body("KO");

        try {
            accountRepository.deleteById(id);
            log.info("Account eliminato id={}", id);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Errore eliminazione account id={}", id, e);
            return ResponseEntity.ok("KO");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // POST /admin/account/delete-multiplo  (AJAX)
    // ─────────────────────────────────────────────────────────────
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
            for (Integer id : ids) accountRepository.deleteById(id);
            log.info("Delete multiplo account: {}", java.util.Arrays.toString(ids));
            return "OK";
        } catch (Exception e) {
            log.error("Errore delete multiplo account", e);
            return "KO";
        }
    }

    @GetMapping("/popup/{tipo}")
    @ResponseBody
    public ResponseEntity<List<Account>> popup(
            @PathVariable String tipo,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return ResponseEntity.status(401).build();
        if (tipo == null || tipo.isEmpty()) return ResponseEntity.badRequest().build();

        List<Account> lista = accountRepository.findByTipoAccountOrderByDescrizioneAsc(tipo);
        return ResponseEntity.ok(lista);
    }

    // ─────────────────────────────────────────────────────────────
    // UTILITY
    // ─────────────────────────────────────────────────────────────
    private void normalizzaAccount(Account a) {
        a.setDescrizione(nvl(a.getDescrizione()));
        a.setTipoAccount(nvl(a.getTipoAccount()));
        a.setLasciaCopiaServer(nvl(a.getLasciaCopiaServer()));
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
}