package com.studiodomino.jplatform.cms.admin.controller;

import com.studiodomino.jplatform.cms.admin.service.EmailSenderService;
import com.studiodomino.jplatform.shared.entity.Utente;
import com.studiodomino.jplatform.shared.repository.UtenteRepository;
import com.studiodomino.jplatform.shared.util.CryptBean;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/recover-password")
@RequiredArgsConstructor
@Slf4j
public class RecuperoPasswordController {

    private final UtenteRepository utenteRepository;
    private final EmailSenderService emailSenderService;

    // token → username
    private final ConcurrentHashMap<String, String> tokenStore = new ConcurrentHashMap<>();
    // token → OTP
    private final ConcurrentHashMap<String, String> otpStore = new ConcurrentHashMap<>();
    // token → nuova password temporanea
    private final ConcurrentHashMap<String, String> passwordStore = new ConcurrentHashMap<>();

    // ─── GET /recover-password → redirect a /login ────────────────────────────
    @GetMapping
    public String showForm() {
        return "redirect:/login";
    }

    // ─── STEP 1: verifica username+email, invia link via email ───────────────
    @PostMapping("/richiedi")
    @ResponseBody
    public ResponseEntity<Map<String, String>> richiedi(
            @RequestParam String username,
            @RequestParam String emailRecupero,
            HttpServletRequest request) {

        Map<String, String> result = new HashMap<>();
        log.info("=== RECOVER PASSWORD === richiesta per username: {}", username);

        Optional<Utente> utenteOpt = utenteRepository.findAll().stream()
                .filter(u -> username.equalsIgnoreCase(u.getUsername()))
                .findFirst();

        boolean usernameOk = utenteOpt.isPresent();
        boolean emailOk = usernameOk &&
                emailRecupero.equalsIgnoreCase(utenteOpt.get().getEmail());

        if (usernameOk && emailOk) {
            Utente utente = utenteOpt.get();

            String token = generaToken();
            tokenStore.put(token, username.toLowerCase());

            String baseUrl = request.getScheme() + "://" + request.getServerName()
                    + ":" + request.getServerPort();
            String link = baseUrl + "/recover-password/reset/" + token;

            // ── Log del link per test in locale ──────────────────────────────
            log.info("╔══════════════════════════════════════════════════════════╗");
            log.info("║  LINK RECUPERO PASSWORD (solo per test in locale)        ║");
            log.info("║  {}  ║", link);
            log.info("╚══════════════════════════════════════════════════════════╝");
            // ─────────────────────────────────────────────────────────────────

            try {
                String corpo = "<p>Gentile <strong>" + utente.getNome() + " " + utente.getCognome() + "</strong>,</p>"
                        + "<p>Hai richiesto il recupero della password per J-Platform.</p>"
                        + "<p>Clicca sul link seguente per impostare una nuova password:</p>"
                        + "<p><a href='" + link + "' style='background:#0060D3;color:white;padding:10px 20px;"
                        + "text-decoration:none;border-radius:5px;display:inline-block;margin:10px 0;'>"
                        + "Reimposta password</a></p>"
                        + "<p>Oppure copia questo link nel browser:</p>"
                        + "<p><small>" + link + "</small></p>"
                        + "<p>Il link è valido per una singola sessione.</p>"
                        + "<br><p><em>Staff J-Platform</em></p>";

                emailSenderService.inviaEmail(utente.getEmail(), null,
                        "Recupero Password J-Platform", corpo);

                log.info("Email recupero inviata a: {}", utente.getEmail());

            } catch (Exception e) {
                log.error("=== ERRORE INVIO EMAIL RECUPERO PASSWORD ===", e);
                log.warn("Invio email fallito (normale in locale): {}", e.getMessage());
                // In locale l'email fallisce ma il link è nel log — non blocchiamo il flusso
            }

            result.put("esito", "OK");
            result.put("messaggio", "Ti abbiamo inviato un'email con il link per reimpostare la password.");

        } else if (usernameOk) {
            result.put("esito", "EMAIL_NO");
            result.put("messaggio", "L'indirizzo email non corrisponde a quello registrato.");
        } else {
            boolean emailTrovata = utenteRepository.findAll().stream()
                    .anyMatch(u -> emailRecupero.equalsIgnoreCase(u.getEmail()));
            if (emailTrovata) {
                result.put("esito", "USER_NO");
                result.put("messaggio", "Utente disabilitato o non trovato.");
            } else {
                result.put("esito", "KO");
                result.put("messaggio", "Utente e email non trovati. Verificare le credenziali.");
            }
        }

        return ResponseEntity.ok(result);
    }

    // ─── STEP 2: pagina cambio password (aperta dal link email) ──────────────
    @GetMapping("/reset/{token}")
    public String showReset(@PathVariable String token, Model model) {
        if (!tokenStore.containsKey(token)) {
            model.addAttribute("errore", "Link non valido o scaduto.");
            return "auth/recover-error";
        }
        model.addAttribute("token", token);
        return "auth/recover-reset";
    }

    // ─── STEP 3a: verifica password e genera+invia OTP ───────────────────────
    @PostMapping("/reset/{token}/verifica-password")
    @ResponseBody
    public ResponseEntity<Map<String, String>> verificaPassword(
            @PathVariable String token,
            @RequestParam String nuovaPassword,
            @RequestParam String confermaPassword) {

        Map<String, String> result = new HashMap<>();

        if (!tokenStore.containsKey(token)) {
            result.put("esito", "KO");
            result.put("messaggio", "Link non valido o scaduto.");
            return ResponseEntity.ok(result);
        }

        if (!nuovaPassword.equals(confermaPassword)) {
            result.put("esito", "KO");
            result.put("messaggio", "Le password non coincidono.");
            return ResponseEntity.ok(result);
        }

        if (nuovaPassword.length() < 6) {
            result.put("esito", "KO");
            result.put("messaggio", "La password deve avere almeno 6 caratteri.");
            return ResponseEntity.ok(result);
        }

        String otp = generaOtp();
        otpStore.put(token, otp);
        passwordStore.put(token, nuovaPassword);

        String username = tokenStore.get(token);
        Optional<Utente> utenteOpt = utenteRepository.findAll().stream()
                .filter(u -> username.equalsIgnoreCase(u.getUsername()))
                .findFirst();

        // ── Log OTP per test in locale ────────────────────────────────────────
        log.info("╔══════════════════════════════════════╗");
        log.info("║  OTP RECUPERO PASSWORD: {}           ║", otp);
        log.info("╚══════════════════════════════════════╝");
        // ─────────────────────────────────────────────────────────────────────

        if (utenteOpt.isPresent()) {
            try {
                String corpo = "<p>Il tuo codice OTP per completare il recupero password è:</p>"
                        + "<h2 style='letter-spacing:6px;font-family:monospace;color:#0060D3;'>" + otp + "</h2>"
                        + "<p>Inseriscilo nella pagina di recupero password.</p>"
                        + "<p>Il codice è valido per una singola sessione.</p>";

                emailSenderService.inviaEmail(utenteOpt.get().getEmail(), null,
                        "Codice OTP - Recupero Password J-Platform", corpo);

                log.info("OTP inviato via email a: {}", utenteOpt.get().getEmail());

            } catch (Exception e) {
                log.warn("Invio OTP via email fallito (normale in locale): {}", e.getMessage());
                // In locale fallisce ma l'OTP è nel log
            }
        }

        result.put("esito", "OK");
        result.put("messaggio", "Codice OTP inviato alla tua email.");
        return ResponseEntity.ok(result);
    }

    // ─── STEP 3b: verifica OTP e salva password ───────────────────────────────
    @PostMapping("/reset/{token}/conferma")
    @ResponseBody
    public ResponseEntity<Map<String, String>> conferma(
            @PathVariable String token,
            @RequestParam String otp) {

        Map<String, String> result = new HashMap<>();

        String otpAtteso = otpStore.get(token);
        if (otpAtteso == null || !otpAtteso.equals(otp.trim())) {
            result.put("esito", "KO");
            result.put("messaggio", "Codice OTP non valido o scaduto.");
            return ResponseEntity.ok(result);
        }

        String username = tokenStore.get(token);
        String nuovaPassword = passwordStore.get(token);

        Optional<Utente> utenteOpt = utenteRepository.findAll().stream()
                .filter(u -> username.equalsIgnoreCase(u.getUsername()))
                .findFirst();

        if (utenteOpt.isEmpty()) {
            result.put("esito", "KO");
            result.put("messaggio", "Utente non trovato.");
            return ResponseEntity.ok(result);
        }

        try {
            Utente utente = utenteOpt.get();
            utente.setPassword(CryptBean.cryptString(nuovaPassword.toUpperCase()));
            utenteRepository.save(utente);

            tokenStore.remove(token);
            otpStore.remove(token);
            passwordStore.remove(token);

            log.info("Password aggiornata con successo per: {}", username);
            result.put("esito", "OK");
            result.put("messaggio", "Password aggiornata con successo!");

        } catch (Exception e) {
            log.error("Errore aggiornamento password: {}", e.getMessage());
            result.put("esito", "ERRORE");
            result.put("messaggio", "Errore durante l'aggiornamento. Contatta l'amministratore.");
        }

        return ResponseEntity.ok(result);
    }

    // ─── UTILITY ─────────────────────────────────────────────────────────────
    private String generaToken() {
        return UUID.randomUUID().toString().replace("-", "")
                + Long.toHexString(System.currentTimeMillis());
    }

    private String generaOtp() {
        return String.valueOf(100000 + new SecureRandom().nextInt(900000));
    }
}