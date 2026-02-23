package com.studiodomino.jplatform.crm.service;

import com.studiodomino.jplatform.crm.entity.RegistroLead;
import com.studiodomino.jplatform.crm.repository.RegistroLeadRepository;
import com.studiodomino.jplatform.shared.config.Configurazione;
import com.studiodomino.jplatform.shared.entity.Utente;
import com.studiodomino.jplatform.shared.entity.UtenteEsterno;
import com.studiodomino.jplatform.shared.repository.UtenteEsternoRepository;
import com.studiodomino.jplatform.shared.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

/**
 * Service per la gestione del Registro LEAD CRM.
 * Usa solo metodi già esistenti nei repository di Raffaele.
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

    // =====================================================================
    // FIND
    // =====================================================================

    /**
     * Vecchio: getRegistroLead(direzione, stato)
     * stato "4" nel vecchio = mostra tutti
     */
    public List<RegistroLead> findAll(String direzione, String stato) {
        if ("4".equals(stato)) {
            return registroLeadRepository.findByDirezioneOrderByIdDesc(direzione);
        }
        return registroLeadRepository.findByDirezioneAndStatoOrderByIdDesc(direzione, stato);
    }

    /** Vecchio: getRegistroLeadById(id) */
    public RegistroLead findById(Long id) {
        return registroLeadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RegistroLead non trovato: " + id));
    }

    /** Vecchio: getUtenteEsternoBase(idUtente) */
    public UtenteEsterno findUtenteBase(Integer idUtente) {
        if (idUtente == null || idUtente <= 0) return new UtenteEsterno();
        return utenteEsternoRepository.findById(idUtente).orElse(new UtenteEsterno());
    }

    /** Vecchio: getAmministratorebyId(id) */
    public Utente findAmministratoreById(String id) {
        if (id == null || id.isBlank()) return null;
        return utenteRepository.findById(Integer.parseInt(id)).orElse(null);
    }

    /** Vecchio: DAOCrm.getElencoAreaInteressebySql("") */
    public List<?> findAreeInteresse() {
        // TODO: implementare con CrmAreaInteresseRepository quando disponibile
        return List.of();
    }

    /** Vecchio: DAOCommentiUtente.getCommentoUtenteById(id) */
    public Object findCommentoById(Long idLeadStore) {
        // TODO: implementare con CommentoRepository quando disponibile
        log.warn("findCommentoById: CommentoRepository non ancora disponibile");
        return null;
    }

    /** Vecchio: GestioneEmail.getMessageEmailStoreById(...) */
    public Object findEmailStoreById(Integer idLeadStore) {
        // TODO: implementare con EmailService quando disponibile
        log.warn("findEmailStoreById: EmailService non ancora disponibile");
        return null;
    }

    // =====================================================================
    // CREA
    // Vecchio: creaRegistroLead(registroLead)
    // =====================================================================

    @Transactional
    public RegistroLead crea(RegistroLead registroLead, Configurazione config) {
        log.debug("Creazione registro lead store={}", registroLead.getStore());

        // Caso speciale: lead "diretto" con utente da creare/aggiornare
        if ("diretto".equals(registroLead.getStore())
                && registroLead.getIdutente() == -1
                && registroLead.getUtente() != null) {

            UtenteEsterno utente = creaOAggiornaUtentePerLead(registroLead, config);
            registroLead.setIdutente(utente.getId());
        }

        RegistroLead saved = registroLeadRepository.save(registroLead);
        log.info("RegistroLead creato: id={}", saved.getId());

        // Aggiorna stato sorgente (commenti → 2, emailstore → 2)
        aggiornaStatoSorgente(registroLead);

        return saved;
    }

    // =====================================================================
    // SALVA
    // Vecchio: saveRegistroLead(registroLead)
    // =====================================================================

    @Transactional
    public RegistroLead salva(RegistroLead registroLead) {
        log.debug("Salvataggio registro lead: id={}", registroLead.getId());
        return registroLeadRepository.save(registroLead);
    }

    // =====================================================================
    // ELIMINA
    // Vecchio: cancellaRegistroLead(id)
    // =====================================================================

    @Transactional
    public void elimina(Long id) {
        registroLeadRepository.deleteById(id);
        log.info("RegistroLead eliminato: id={}", id);
    }

    // =====================================================================
    // INVIA SMS
    // Vecchio: GestioneSms(testo, null, null, cellulare, null, l2, null, config).start()
    // =====================================================================

    public void inviaSms(RegistroLead registroLead, Configurazione config) {
        log.info("Invio SMS a: {}",
                registroLead.getUtente() != null ? registroLead.getUtente().getTelefono2() : "N/A");
        // TODO: implementare con SmsService quando disponibile
    }

    // =====================================================================
    // INVIA EMAIL
    // Vecchio: GestioneEmail(account, messaggio, true, false, config).start()
    // =====================================================================

    public void inviaEmail(RegistroLead registroLead, Configurazione config) {
        log.info("Invio Email a: {}",
                registroLead.getUtente() != null ? registroLead.getUtente().getEmail() : "N/A");

        // Gestione firma automatica (l2='1')
        // Vecchio: if(L2.equals("1")) → inserisce extra1 dell'amministratore prima di </body>
        if ("1".equals(registroLead.getL2()) && registroLead.getNotalead() != null
                && config.getAmministratore().getExtra1() != null) {
            String testo = registroLead.getNotalead();
            int pos = testo.indexOf("</body>");
            if (pos > 0) {
                testo = testo.substring(0, pos)
                        + config.getAmministratore().getExtra1()
                        + testo.substring(pos);
                registroLead.setNotalead(testo);
            }
        }

        // TODO: implementare con EmailService quando disponibile
        // account = config.getAmministratore().getAccountEMAILById(registroLead.getL1())
        // oggetto = registroLead.getL3()
        // testo   = registroLead.getNotalead()
    }

    // =====================================================================
    // UTILITY PRIVATI
    // =====================================================================

    /**
     * Crea o aggiorna l'utente esterno per un lead "diretto" con idutente=-1.
     * Vecchio: logica dentro saveRegistroLead, store='diretto' e idutente=-1
     */
    private UtenteEsterno creaOAggiornaUtentePerLead(RegistroLead lead, Configurazione config) {
        UtenteEsterno datiUtente = lead.getUtente();
        String email = datiUtente.getEmail();

        // Cerca utente esistente per email
        Integer idEsistente = anagraficaService.getIdByEmail(email);

        UtenteEsterno utente = idEsistente != null
                ? anagraficaService.findByIdSafe(idEsistente)
                : new UtenteEsterno();

        Random rnd = new Random(System.currentTimeMillis());
        int rnn = Math.abs(rnd.nextInt() / 1000);

        utente.setNome(datiUtente.getNome());
        utente.setCognome(datiUtente.getCognome());
        utente.setEmail(email);
        utente.setUsername(email);
        utente.setPassword(Integer.toString(rnn));
        utente.setTelefono(datiUtente.getTelefono());
        utente.setTelefono2(datiUtente.getTelefono2());
        utente.setL1("1");
        utente.setL2("1");

        if (config.getSito() != null && config.getSito().getLibero3() != null) {
            try { utente.setNazione(config.getSito().getLibero3()); }
            catch (NumberFormatException e) { utente.setNazione("1"); }
        }

        return idEsistente == null
                ? anagraficaService.crea(utente)
                : anagraficaService.salva(utente);
    }

    /**
     * Aggiorna lo stato della sorgente del lead dopo la creazione.
     * Vecchio: DAO.aggiornaCampoDatabase("commenti"/"emailstore", "stato", "2", ...)
     */
    @Transactional
    private void aggiornaStatoSorgente(RegistroLead lead) {
        // TODO: implementare con CommentoRepository e EmailStoreRepository
        if ("commenti".equals(lead.getStore())) {
            log.debug("TODO: aggiorna commento id={} stato=2", lead.getIdleadstore());
        } else if ("emailstore".equals(lead.getStore())) {
            log.debug("TODO: aggiorna emailstore id={} stato=2", lead.getIdleadstore());
        }
    }
}