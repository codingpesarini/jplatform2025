package com.studiodomino.jplatform.cms.admin.controller;

import com.studiodomino.jplatform.shared.config.Configurazione;
import com.studiodomino.jplatform.shared.entity.Gruppo;
import com.studiodomino.jplatform.shared.entity.Utente;
import com.studiodomino.jplatform.shared.repository.GruppoRepository;
import com.studiodomino.jplatform.shared.repository.UtenteRepository;
import com.studiodomino.jplatform.shared.service.ConfigurazioneService;
import com.studiodomino.jplatform.shared.util.ViewUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin/amministratori")
@RequiredArgsConstructor
@Slf4j
public class AmministratoriController {

    private final UtenteRepository utenteRepository;
    private final ConfigurazioneService configurazioneService;
    private final GruppoRepository gruppoRepository;

    // ─── ELENCO ──────────────────────────────────────────────────────────────
    @GetMapping
    public String elenco(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        log.info("=== AMMINISTRATORI ELENCO === user: {}", config.getUsername());

        List<Utente> elenco = utenteRepository.findAll();

        model.addAttribute("config", config);
        model.addAttribute("elencoAnagrafico", elenco);

        return ViewUtils.resolveProtectedTemplate("admin/sezioni/elencoAmministratori");
    }

    // ─── RICERCA ─────────────────────────────────────────────────────────────
    @GetMapping("/ricerca")
    public String ricerca(
            @RequestParam(defaultValue = "") String cognome,
            @RequestParam(defaultValue = "") String nome,
            @RequestParam(defaultValue = "") String username,
            @RequestParam(defaultValue = "") String telefono,
            @RequestParam(defaultValue = "") String email,
            @RequestParam(defaultValue = "") String pec,
            @RequestParam(defaultValue = "") String indirizzo,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        log.info("=== AMMINISTRATORI RICERCA ===");

        // Filtro in memoria su findAll() — evita query JPQL custom
        List<Utente> elenco = utenteRepository.findAll().stream()
                .filter(u -> cognome.isEmpty()  || contains(u.getCognome(), cognome))
                .filter(u -> nome.isEmpty()     || contains(u.getNome(), nome))
                .filter(u -> username.isEmpty() || contains(u.getUsername(), username))
                .filter(u -> telefono.isEmpty() || contains(u.getTelefono(), telefono))
                .filter(u -> email.isEmpty()    || contains(u.getEmail(), email))
                .filter(u -> pec.isEmpty()      || contains(u.getPec(), pec))
                .filter(u -> indirizzo.isEmpty()|| contains(u.getIndirizzo(), indirizzo))
                .sorted((a, b) -> {
                    String ca = a.getCognome() != null ? a.getCognome() : "";
                    String cb = b.getCognome() != null ? b.getCognome() : "";
                    return ca.compareToIgnoreCase(cb);
                })
                .toList();

        model.addAttribute("config", config);
        model.addAttribute("elencoAnagrafico", elenco);

        return ViewUtils.resolveProtectedTemplate("admin/sezioni/elencoAmministratori");
    }

    // ─── NEW ─────────────────────────────────────────────────────────────────
    @GetMapping("/new")
    public String newForm(HttpServletRequest request, Model model) {

        List<Gruppo> tuttiGruppi = gruppoRepository.findAll();

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        model.addAttribute("config", config);
        model.addAttribute("configCore", config);
        model.addAttribute("anagraficaUtente", new Utente());
        model.addAttribute("amministratoriUtente", new Utente());
        model.addAttribute("listaGruppi", tuttiGruppi);

        return ViewUtils.resolveProtectedTemplate("admin/contenuti/dettaglioAmministratore");
    }

    // ─── OPEN ────────────────────────────────────────────────────────────────
    @GetMapping("/{id}/open")
    public String open(@PathVariable Integer id,
                       HttpServletRequest request, Model model) {

        List<Gruppo> tuttiGruppi = gruppoRepository.findAll();

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        Utente utente = utenteRepository.findById(id).orElse(null);
        if (utente == null) return "redirect:/admin/amministratori";

        log.info("=== AMMINISTRATORI OPEN === id: {}", id);

        session.setAttribute("anagraficaUtente", utente);
        model.addAttribute("config", config);
        model.addAttribute("configCore", config);
        model.addAttribute("anagraficaUtente", utente);
        model.addAttribute("amministratoriUtente", utente);
        model.addAttribute("listaGruppi", tuttiGruppi);

        return ViewUtils.resolveProtectedTemplate("admin/contenuti/dettaglioAmministratore");
    }

    // ─── DUPLICA ─────────────────────────────────────────────────────────────
    @GetMapping("/{id}/duplica")
    public String duplica(@PathVariable Integer id,
                          HttpServletRequest request, Model model) {

        List<Gruppo> tuttiGruppi = gruppoRepository.findAll();

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        Utente originale = utenteRepository.findById(id).orElse(null);
        if (originale == null) return "redirect:/admin/amministratori";

        log.info("=== AMMINISTRATORI DUPLICA === da id: {}", id);

        // Clona manualmente (Utente usa @Data, non @Builder)
        Utente copia = new Utente();
        copia.setNome(originale.getNome());
        copia.setCognome(originale.getCognome() + " (2)");
        copia.setEmail(originale.getEmail());
        copia.setPec(originale.getPec());
        copia.setTelefono(originale.getTelefono());
        copia.setIndirizzo(originale.getIndirizzo());
        copia.setIncarico(originale.getIncarico());
        copia.setIdsite(originale.getIdsite());
        copia.setRole1(originale.getRole1());
        copia.setRole2(originale.getRole2());
        copia.setStatoaccesso(1);
        copia.setPassword(""); // reset password
        // id = null → nuovo record

        session.setAttribute("anagraficaUtente", copia);
        model.addAttribute("config", config);
        model.addAttribute("configCore", config);
        model.addAttribute("anagraficaUtente", copia);
        model.addAttribute("amministratoriUtente", copia);
        model.addAttribute("listaGruppi", tuttiGruppi);

        return ViewUtils.resolveProtectedTemplate("admin/contenuti/dettaglioAmministratore");
    }

    // ─── SAVE ────────────────────────────────────────────────────────────────
    @PostMapping("/save")
    public String save(
            @ModelAttribute("anagraficaUtente") Utente utenteDatiForm,
            @RequestParam(value = "newpassword", required = false) String newPassword,
            @RequestParam(value = "newpasswordretype", required = false) String newPasswordRetype,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        Utente utenteDaSalvare;

        if (utenteDatiForm.getId() != null) {
            // --- CASO UPDATE ---
            // 1. Carichiamo l'utente REALE dal database
            utenteDaSalvare = utenteRepository.findById(utenteDatiForm.getId())
                    .orElseThrow(() -> new RuntimeException("Utente non trovato"));

            // 2. Copiamo i dati dal form all'oggetto reale (usando i setter per i campi presenti)
            // Questo protegge TUTTI i campi che non sono nel form (telefono, date, ecc.)
            utenteDaSalvare.setNome(utenteDatiForm.getNome());
            utenteDaSalvare.setCognome(utenteDatiForm.getCognome());
            utenteDaSalvare.setEmail(utenteDatiForm.getEmail());
            utenteDaSalvare.setUsername(utenteDatiForm.getUsername());
            // Aggiungi qui gli altri setter per i campi che hai effettivamente nel form HTML...

            // Gestione specifica per i gruppi
            if (utenteDatiForm.getIdGruppiArray() != null) {
                utenteDaSalvare.setIdgruppi(String.join(",", utenteDatiForm.getIdGruppiArray()));
            }

        } else {
            // --- CASO NUOVO UTENTE ---
            utenteDaSalvare = utenteDatiForm;
            String now = new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(new Date());
            utenteDaSalvare.setDatacreazione(now);
            utenteDaSalvare.setDataultimoaccesso(now);
            utenteDaSalvare.setIpultimoaccesso(request.getRemoteAddr());
            utenteDaSalvare.setNumeroaccessi("0");

            // Inizializziamo a stringa vuota i campi che il DB vuole NOT NULL
            if (utenteDaSalvare.getTelefono() == null) utenteDaSalvare.setTelefono("");
            if (utenteDaSalvare.getTelefono2() == null) utenteDaSalvare.setTelefono2("");
            if (utenteDaSalvare.getIncarico() == null) utenteDaSalvare.setIncarico("");
            // ... e così via per gli altri campi segnalati dai log
        }

        // Salvataggio finale
        try {
            Utente salvato = utenteRepository.save(utenteDaSalvare);
            log.info("=== SAVE SUCCESS === id: {}", salvato.getId());

            model.addAttribute("anagraficaUtente", salvato);
            model.addAttribute("amministratoriUtente", salvato);
        } catch (Exception e) {
            log.error("ERRORE SQL: {}", e.getMessage());
            return "error";
        }

        model.addAttribute("config", config);
        model.addAttribute("listaGruppi", gruppoRepository.findAll());

        return ViewUtils.resolveProtectedTemplate("admin/contenuti/dettaglioAmministratore");
    }

    // ─── DELETE SINGOLO (AJAX) ───────────────────────────────────────────────
    // Aggiungi questo import

    @PostMapping("/{id}/delete")
    @ResponseBody
    @Transactional // Garantisce che la cancellazione avvenga correttamente nel DB
    public String delete(@PathVariable Integer id) {
        try {
            if (utenteRepository.existsById(id)) {
                utenteRepository.deleteById(id);
                log.info("=== AMMINISTRATORI DELETE === id: {}", id);
                return "OK";
            }
            return "KO: Utente non trovato";
        } catch (Exception e) {
            log.error("Errore cancellazione amministratore id={}: {}", id, e.getMessage());
            return "KO";
        }
    }

    // ─── DELETE MULTIPLO (AJAX) ──────────────────────────────────────────────
    @PostMapping("/delete-multiplo")
    @ResponseBody
    @Transactional
    public String deleteMultiplo(@RequestParam(value = "ids[]", required = false) Integer[] ids) {
        // NOTA: Ho aggiunto "ids[]" perché molti framework JS (come jQuery o URLSearchParams)
        // quando inviano un array usano le parentesi quadre nel nome del parametro.

        if (ids == null || ids.length == 0) {
            return "KO: Nessun ID ricevuto";
        }

        try {
            // Usiamo Arrays.asList per convertire l'array in lista
            utenteRepository.deleteAllByIdInBatch(Arrays.asList(ids));
            log.info("=== AMMINISTRATORI DELETE MULTIPLO === ids: {}", Arrays.toString(ids));
            return "OK";
        } catch (Exception e) {
            log.error("Errore cancellazione multipla amministratori: {}", e.getMessage());
            return "KO";
        }
    }

    // ─── QR CODE 2FA (senza ZXing) ───────────────────────────────────────────
    @GetMapping(value = "/qrcode")
    public void qrCode(
            @RequestParam String email,
            @RequestParam String securkey,
            jakarta.servlet.http.HttpServletResponse response) {
        try {
            String otpUrl = "otpauth://totp/J-Platform%3A"
                    + java.net.URLEncoder.encode(email, "UTF-8").replace("+", "%20")
                    + "?secret=" + securkey
                    + "&issuer=J-Platform";

            String qrApiUrl = "https://api.qrserver.com/v1/create-qr-code/?size=800x800&data="
                    + java.net.URLEncoder.encode(otpUrl, "UTF-8");

            java.net.URL url = new java.net.URL(qrApiUrl);
            java.io.InputStream in = url.openStream();
            response.setContentType("image/png");
            byte[] buffer = new byte[4096];
            int n;
            while ((n = in.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, n);
            }
            in.close();
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("Errore generazione QR code: {}", e.getMessage());
        }
    }

    // ─── GENERA SECRET KEY 2FA (senza Commons Codec) ─────────────────────────
    @GetMapping("/genera-secret-key")
    @ResponseBody
    public String generaSecretKey() {
        try {
            java.security.SecureRandom random = new java.security.SecureRandom();
            byte[] bytes = new byte[20];
            random.nextBytes(bytes);
            String base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
            StringBuilder result = new StringBuilder();
            int buffer = 0, bitsLeft = 0;
            for (byte b : bytes) {
                buffer = (buffer << 8) | (b & 0xFF);
                bitsLeft += 8;
                while (bitsLeft >= 5) {
                    result.append(base32Chars.charAt((buffer >> (bitsLeft - 5)) & 31));
                    bitsLeft -= 5;
                }
            }
            if (bitsLeft > 0) {
                result.append(base32Chars.charAt((buffer << (5 - bitsLeft)) & 31));
            }
            return result.toString();
        } catch (Exception e) {
            log.error("Errore generazione secret key: {}", e.getMessage());
            return "NO";
        }
    }

    // ─── UTILITY ─────────────────────────────────────────────────────────────
    private boolean contains(String field, String search) {
        return field != null && field.toLowerCase().contains(search.toLowerCase());
    }
}