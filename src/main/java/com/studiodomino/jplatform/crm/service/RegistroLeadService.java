package com.studiodomino.jplatform.crm.service;

import com.studiodomino.jplatform.crm.entity.RegistroLead;
import com.studiodomino.jplatform.crm.repository.RegistroLeadRepository;
import com.studiodomino.jplatform.shared.config.Configurazione;
import com.studiodomino.jplatform.shared.entity.Utente;
import com.studiodomino.jplatform.shared.entity.UtenteEsterno;
import com.studiodomino.jplatform.shared.repository.UtenteEsternoRepository;
import com.studiodomino.jplatform.shared.repository.UtenteRepository;
import com.studiodomino.jplatform.cms.admin.service.EmailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

/**
 * Service per la gestione del Registro LEAD CRM.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RegistroLeadService {

    private final RegistroLeadRepository registroLeadRepository;
    private final UtenteEsternoRepository utenteEsternoRepository;
    private final UtenteRepository utenteRepository;
    private final AnagraficaService anagraficaService;

    // Adatta questi due service ai nomi reali del tuo progetto
    private final EmailSenderService emailSenderService;
    private final SmsSenderService smsSenderService;

    // =====================================================================
    // FIND
    // =====================================================================

    public List<RegistroLead> findAll(String direzione, String stato) {
        if ("4".equals(stato)) {
            return registroLeadRepository.findByDirezioneOrderByIdDesc(direzione);
        }
        return registroLeadRepository.findByDirezioneAndStatoOrderByIdDesc(direzione, stato);
    }

    public RegistroLead findById(Long id) {
        return registroLeadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RegistroLead non trovato: " + id));
    }

    public UtenteEsterno findUtenteBase(Integer idUtente) {
        if (idUtente == null || idUtente <= 0) return new UtenteEsterno();
        return utenteEsternoRepository.findById(idUtente).orElse(new UtenteEsterno());
    }

    public Utente findAmministratoreById(String id) {
        if (id == null || id.isBlank()) return null;
        return utenteRepository.findById(Integer.parseInt(id)).orElse(null);
    }

    public List<?> findAreeInteresse() {
        return List.of();
    }

    public Object findCommentoById(Long idLeadStore) {
        log.warn("findCommentoById: CommentoRepository non ancora disponibile");
        return null;
    }

    public Object findEmailStoreById(Integer idLeadStore) {
        log.warn("findEmailStoreById: EmailService non ancora disponibile");
        return null;
    }

    // =====================================================================
    // CREA
    // =====================================================================

    @Transactional
    public RegistroLead crea(RegistroLead registroLead, Configurazione config) {
        log.debug("Creazione registro lead store={}", registroLead.getStore());

        // Se arriva -1 o 0 come sentinella, forza nuova insert
        if (registroLead.getId() == null || registroLead.getId() <= 0) {
            registroLead.setId(null);
        }

        // Caso lead diretto con utente inserito a mano
        if ("diretto".equalsIgnoreCase(registroLead.getStore())
                && registroLead.getUtente() != null
                && registroLead.getIdutente() != 0
                && registroLead.getIdutente() <= 0) {

            UtenteEsterno utente = creaOAggiornaUtentePerLead(registroLead, config);
            registroLead.setUtente(utente);
            registroLead.setIdutente(utente.getId());
        }

        RegistroLead saved = registroLeadRepository.save(registroLead);
        log.info("RegistroLead creato: id={}", saved.getId());

        aggiornaStatoSorgente(saved);
        return saved;
    }

    // =====================================================================
    // SALVA
    // =====================================================================

    @Transactional
    public RegistroLead salva(RegistroLead registroLead) {
        log.debug("Salvataggio registro lead: id={}", registroLead.getId());
        return registroLeadRepository.save(registroLead);
    }

    // =====================================================================
    // ELIMINA
    // =====================================================================

    @Transactional
    public void elimina(Long id) {
        registroLeadRepository.deleteById(id);
        log.info("RegistroLead eliminato: id={}", id);
    }

    // =====================================================================
    // INVIA SMS
    // =====================================================================

    public void inviaSms(RegistroLead registroLead, Configurazione config) {
        String telefono = estraiTelefonoDestinatario(registroLead);

        log.info("Invio SMS a: {}", telefono);

        if (telefono == null || telefono.isBlank()) {
            throw new RuntimeException("Numero destinatario assente");
        }

        String testo = registroLead.getNotalead() != null ? registroLead.getNotalead().trim() : "";
        if (testo.isBlank()) {
            throw new RuntimeException("Testo SMS assente");
        }

        String tipoMittente = registroLead.getL2() != null ? registroLead.getL2().trim() : "";

        smsSenderService.inviaSms(telefono, testo, tipoMittente, config);
    }

    // =====================================================================
    // INVIA EMAIL
    // =====================================================================

    public void inviaEmail(RegistroLead registroLead, Configurazione config) {
        String destinatario = estraiEmailDestinatario(registroLead);

        log.info("Invio Email a: {}", destinatario);

        if (destinatario == null || destinatario.isBlank()) {
            throw new RuntimeException("Destinatario email assente");
        }

        String oggetto = registroLead.getL3() != null ? registroLead.getL3().trim() : "";
        if (oggetto.isBlank()) {
            throw new RuntimeException("Oggetto email assente");
        }

        String testo = registroLead.getNotalead() != null ? registroLead.getNotalead() : "";
        if (testo.isBlank()) {
            throw new RuntimeException("Corpo email assente");
        }

        // Firma automatica
        if ("1".equals(registroLead.getL2())
                && config != null
                && config.getAmministratore() != null
                && config.getAmministratore().getExtra1() != null
                && !config.getAmministratore().getExtra1().isBlank()) {

            int pos = testo.indexOf("</body>");
            if (pos > 0) {
                testo = testo.substring(0, pos)
                        + config.getAmministratore().getExtra1()
                        + testo.substring(pos);
            } else {
                testo = testo + config.getAmministratore().getExtra1();
            }
        }

        /*
         * Se il tuo EmailSenderService usa un metodo diverso,
         * cambia solo questa riga.
         *
         * Esempi possibili:
         * emailSenderService.inviaEmail(destinatario, oggetto, testo);
         * emailSenderService.sendHtml(destinatario, oggetto, testo);
         */
        emailSenderService.inviaEmail(destinatario, null, oggetto, testo);
    }

    // =====================================================================
    // UTILITY PRIVATI
    // =====================================================================

    private String estraiEmailDestinatario(RegistroLead registroLead) {
        if (registroLead == null) return null;

        if (registroLead.getUtente() != null
                && registroLead.getUtente().getEmail() != null
                && !registroLead.getUtente().getEmail().isBlank()) {
            return registroLead.getUtente().getEmail().trim();
        }

        if (registroLead.getIdutente() != 0 && registroLead.getIdutente() > 0) {
            UtenteEsterno utente = findUtenteBase(registroLead.getIdutente());
            if (utente.getEmail() != null && !utente.getEmail().isBlank()) {
                return utente.getEmail().trim();
            }
        }

        return null;
    }

    private String estraiTelefonoDestinatario(RegistroLead registroLead) {
        if (registroLead == null) return null;

        if (registroLead.getUtente() != null) {
            if (registroLead.getUtente().getTelefono2() != null
                    && !registroLead.getUtente().getTelefono2().isBlank()) {
                return registroLead.getUtente().getTelefono2().trim();
            }

            if (registroLead.getUtente().getTelefono() != null
                    && !registroLead.getUtente().getTelefono().isBlank()) {
                return registroLead.getUtente().getTelefono().trim();
            }
        }

        if (registroLead.getIdutente() != 0 && registroLead.getIdutente() > 0) {
            UtenteEsterno utente = findUtenteBase(registroLead.getIdutente());

            if (utente.getTelefono2() != null && !utente.getTelefono2().isBlank()) {
                return utente.getTelefono2().trim();
            }

            if (utente.getTelefono() != null && !utente.getTelefono().isBlank()) {
                return utente.getTelefono().trim();
            }
        }

        return null;
    }

    /**
     * Crea o aggiorna utente esterno per lead diretto.
     */
    private UtenteEsterno creaOAggiornaUtentePerLead(RegistroLead lead, Configurazione config) {
        UtenteEsterno datiUtente = lead.getUtente();
        if (datiUtente == null) {
            throw new RuntimeException("Utente esterno assente");
        }

        String email = datiUtente.getEmail() != null ? datiUtente.getEmail().trim() : "";

        Integer idEsistente = null;
        if (!email.isBlank()) {
            idEsistente = anagraficaService.getIdByEmail(email);
        }

        UtenteEsterno utente = idEsistente != null
                ? anagraficaService.findByIdSafe(idEsistente)
                : new UtenteEsterno();

        Random rnd = new Random(System.currentTimeMillis());
        int rnn = Math.abs(rnd.nextInt() / 1000);

        utente.setNome(datiUtente.getNome());
        utente.setCognome(datiUtente.getCognome());
        utente.setEmail(email);
        utente.setUsername(!email.isBlank() ? email : "utente" + rnn);
        utente.setPassword(Integer.toString(rnn));
        utente.setTelefono(datiUtente.getTelefono());
        utente.setTelefono2(datiUtente.getTelefono2());
        utente.setL1("1");
        utente.setL2("1");

        if (utente.getStatus() == null || utente.getStatus().isBlank()) {
            utente.setStatus("1");
        }

        if (config.getSito() != null && config.getSito().getLibero3() != null) {
            try {
                utente.setNazione(config.getSito().getLibero3());
            } catch (Exception e) {
                utente.setNazione("1");
            }
        } else if (utente.getNazione() == null || utente.getNazione().isBlank()) {
            utente.setNazione("1");
        }

        return idEsistente == null
                ? anagraficaService.crea(utente)
                : anagraficaService.salva(utente);
    }

    @Transactional
    private void aggiornaStatoSorgente(RegistroLead lead) {
        if ("commenti".equals(lead.getStore())) {
            log.debug("TODO: aggiorna commento id={} stato=2", lead.getIdleadstore());
        } else if ("emailstore".equals(lead.getStore())) {
            log.debug("TODO: aggiorna emailstore id={} stato=2", lead.getIdleadstore());
        }
    }
}