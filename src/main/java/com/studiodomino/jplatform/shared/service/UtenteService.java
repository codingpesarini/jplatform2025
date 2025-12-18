package com.studiodomino.jplatform.shared.service;

import com.studiodomino.jplatform.shared.entity.Utente;
import com.studiodomino.jplatform.shared.repository.UtenteRepository;
import com.studiodomino.jplatform.shared.util.CryptBean;
import com.studiodomino.jplatform.shared.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class UtenteService {

    @Autowired
    private UtenteRepository utenteRepository;

    // ========================================
    // METODI NUOVI PER SPRING BOOT
    // ========================================

    /**
     * Autentica utente con username e password
     * Versione semplificata per Spring Boot (senza update accesso)
     *
     * @return Utente se credenziali corrette, null altrimenti
     */
    public Utente authenticate(String username, String password) {
        Optional<Utente> utenteOpt = utenteRepository.findByUsername(username);

        if (!utenteOpt.isPresent()) {
            return null; // Username non trovato
        }

        Utente utente = utenteOpt.get();

        // Cripta password in input e confronta
        String passwordCriptata = CryptBean.cryptString(password.toUpperCase());

        if (!utente.getPassword().equals(passwordCriptata)) {
            return null; // Password errata
        }

        // Credenziali corrette
        return utente;
    }

    /**
     * Salva/aggiorna utente
     * Alias per compatibilità Spring Boot
     */
    @Transactional
    public Utente save(Utente utente) {
        return utenteRepository.save(utente);
    }

    // ========================================
    // METODI LEGACY (mantenuti per compatibilità)
    // ========================================

    /**
     * Login con username e password (metodo legacy completo)
     * Aggiorna automaticamente ultimo accesso, IP, numero accessi
     */
    public Utente login(String username, String password, String idSite, HttpServletRequest request) {
        Optional<Utente> utenteOpt = utenteRepository.findByUsername(username);

        if (!utenteOpt.isPresent()) {
            Utente u = new Utente();
            u.setStatoaccesso(3); // Username inesistente
            return u;
        }

        Utente utente = utenteOpt.get();
        String passwordCriptata = CryptBean.cryptString(password.toUpperCase());

        if (!utente.getPassword().equals(passwordCriptata)) {
            utente.setStatoaccesso(1); // Password errata
            return utente;
        }

        // Login OK
        utente.setStatoaccesso(0);

        // Aggiorna ultimo accesso
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        utente.setDataultimoaccesso(now);
        utente.setIpultimoaccesso(getClientIP(request));

        int accessi = Integer.parseInt(utente.getNumeroaccessi() == null ? "0" : utente.getNumeroaccessi());
        utente.setNumeroaccessi(String.valueOf(accessi + 1));

        utenteRepository.save(utente);

        return utente;
    }

    /**
     * Login automatico da cookie
     */
    public Utente loginByCookie(String userId, String idSite, HttpServletRequest request) {
        try {
            Integer id = Integer.parseInt(userId);
            Optional<Utente> utenteOpt = utenteRepository.findById(id);

            if (!utenteOpt.isPresent()) {
                Utente u = new Utente();
                u.setStatoaccesso(3);
                return u;
            }

            Utente utente = utenteOpt.get();
            utente.setStatoaccesso(0);

            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            utente.setDataultimoaccesso(now);
            utente.setIpultimoaccesso(getClientIP(request));

            int accessi = Integer.parseInt(utente.getNumeroaccessi() == null ? "0" : utente.getNumeroaccessi());
            utente.setNumeroaccessi(String.valueOf(accessi + 1));

            utenteRepository.save(utente);

            return utente;

        } catch (Exception e) {
            Utente u = new Utente();
            u.setStatoaccesso(3);
            return u;
        }
    }

    /**
     * Ottieni utente per ID
     */
    public Utente getUtenteById(Integer id) {
        return utenteRepository.findById(id).orElse(null);
    }

    /**
     * Ottieni utente per username
     */
    public Utente getUtenteByUsername(String username) {
        return utenteRepository.findByUsername(username).orElse(null);
    }

    /**
     * Ottieni tutti gli utenti
     */
    public List<Utente> getAllUtenti() {
        return utenteRepository.findAll();
    }

    /**
     * Crea nuovo utente
     */
    @Transactional
    public Utente creaUtente(Utente utente) {
        String passwordCriptata = CryptBean.cryptString(utente.getPassword().toUpperCase());
        utente.setPassword(passwordCriptata);

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        utente.setDatacreazione(now);
        utente.setNumeroaccessi("0");

        return utenteRepository.save(utente);
    }

    /**
     * Salva/aggiorna utente (metodo legacy)
     */
    @Transactional
    public Utente salvaUtente(Utente utente) {
        return utenteRepository.save(utente);
    }

    /**
     * Cancella utente
     */
    @Transactional
    public void cancellaUtente(Integer id) {
        utenteRepository.deleteById(id);
    }

    /**
     * Cambia password
     */
    @Transactional
    public void cambiaPassword(Integer id, String nuovaPassword) {
        Optional<Utente> utenteOpt = utenteRepository.findById(id);
        if (utenteOpt.isPresent()) {
            Utente utente = utenteOpt.get();
            String passwordCriptata = CryptBean.cryptString(nuovaPassword.toUpperCase());
            utente.setPassword(passwordCriptata);
            utenteRepository.save(utente);
        }
    }

    /**
     * Ottiene IP del client
     */
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * Parse gruppi da stringa a array
     */
    public void parseGruppi(Utente utente) {
        if (StringUtils.isNotEmpty(utente.getIdgruppi())) {
            String[] gruppiArray = StringUtils.stringToArray(utente.getIdgruppi(), ";");
            utente.setIdGruppiArray(gruppiArray);
        }
    }

    /**
     * Aggiorna statistiche accesso (helper)
     * Usato dopo authenticate() per aggiornare ultimo accesso
     */
    @Transactional
    public void aggiornaStatisticheAccesso(Utente utente, HttpServletRequest request) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        utente.setDataultimoaccesso(now);
        utente.setIpultimoaccesso(getClientIP(request));

        int accessi = Integer.parseInt(utente.getNumeroaccessi() == null ? "0" : utente.getNumeroaccessi());
        utente.setNumeroaccessi(String.valueOf(accessi + 1));

        utenteRepository.save(utente);
    }
}