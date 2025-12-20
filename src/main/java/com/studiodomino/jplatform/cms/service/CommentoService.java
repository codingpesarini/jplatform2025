package com.studiodomino.jplatform.cms.service;

import com.studiodomino.jplatform.cms.entity.Commento;
import com.studiodomino.jplatform.cms.repository.CommentoRepository;
import com.studiodomino.jplatform.shared.entity.UtenteEsterno;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentoService {

    private final CommentoRepository commentoRepository;
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    // ===== CREA COMMENTO =====

    /**
     * Crea nuovo commento
     */
    public Commento creaCommento(
            String idoggetto,
            String nome,
            String cognome,
            String email,
            String messaggio,
            String iduser,
            String tipologia) {

        Commento commento = Commento.builder()
                .idoggetto(idoggetto)
                .nome(nome)
                .cognome(cognome)
                .email(email)
                .messaggio(messaggio)
                .iduser(iduser != null ? iduser : "")
                .tipologia(tipologia != null ? tipologia : "c")
                .idparent("0") // Commento principale
                .stato("0") // In attesa moderazione
                .data(DATE_FORMAT.format(new Date()))
                .build();

        return commentoRepository.save(commento);
    }

    /**
     * Crea risposta a commento esistente
     */
    public Commento creaRisposta(
            String idParent,
            String idoggetto,
            String nome,
            String cognome,
            String email,
            String messaggio,
            String iduser) {

        Commento risposta = Commento.builder()
                .idoggetto(idoggetto)
                .idparent(idParent)
                .nome(nome)
                .cognome(cognome)
                .email(email)
                .messaggio(messaggio)
                .iduser(iduser != null ? iduser : "")
                .tipologia("c")
                .stato("0") // In attesa moderazione
                .data(DATE_FORMAT.format(new Date()))
                .build();

        return commentoRepository.save(risposta);
    }

    // ===== CARICA COMMENTI =====

    /**
     * Carica commenti con thread completo per oggetto
     */
    public List<Commento> getCommentiConThread(String idoggetto, boolean soloApprovati) {
        List<Commento> principali;

        if (soloApprovati) {
            principali = commentoRepository.findCommentiPrincipaliApprovatiByOggetto(idoggetto);
        } else {
            principali = commentoRepository.findCommentiPrincipaliByOggetto(idoggetto);
        }

        // Carica risposte per ogni commento
        for (Commento commento : principali) {
            List<Commento> risposte;
            if (soloApprovati) {
                risposte = commentoRepository.findRisposteApprovateCommento(
                        commento.getId().toString()
                );
            } else {
                risposte = commentoRepository.findRisposteCommento(
                        commento.getId().toString()
                );
            }
            commento.setSubCommenti(risposte);
        }

        return principali;
    }

    /**
     * Carica tutti i commenti per oggetto (flat, no thread)
     */
    public List<Commento> getCommentiByOggetto(String idoggetto) {
        return commentoRepository.findByIdoggettoOrderByIdDesc(idoggetto);
    }

    /**
     * Carica commenti approvati per oggetto
     */
    public List<Commento> getCommentiApprovati(String idoggetto) {
        return commentoRepository.findCommentiApprovatiByOggetto(idoggetto);
    }

    // ===== MODERAZIONE =====

    /**
     * Approva commento
     */
    public void approvaCommento(Integer id) {
        commentoRepository.findById(id).ifPresent(commento -> {
            commento.approva();
            commentoRepository.save(commento);
            log.info("Commento approvato: id={}", id);
        });
    }

    /**
     * Rifiuta commento
     */
    public void rifiutaCommento(Integer id) {
        commentoRepository.findById(id).ifPresent(commento -> {
            commento.rifiuta();
            commentoRepository.save(commento);
            log.info("Commento rifiutato: id={}", id);
        });
    }

    /**
     * Elimina commento
     */
    public void eliminaCommento(Integer id) {
        commentoRepository.deleteById(id);
        log.info("Commento eliminato: id={}", id);
    }

    /**
     * Carica commenti in attesa moderazione
     */
    public List<Commento> getCommentiInAttesa() {
        return commentoRepository.findCommentiInAttesa();
    }

    // ===== STATISTICHE =====

    /**
     * Conta commenti per oggetto
     */
    public long contaCommenti(String idoggetto) {
        return commentoRepository.countByIdoggetto(idoggetto);
    }

    /**
     * Conta commenti approvati per oggetto
     */
    public long contaCommentiApprovati(String idoggetto) {
        return commentoRepository.countCommentiApprovatiByOggetto(idoggetto);
    }

    /**
     * Conta commenti in attesa per oggetto
     */
    public long contaCommentiInAttesa(String idoggetto) {
        return commentoRepository.countCommentiInAttesaByOggetto(idoggetto);
    }

    /**
     * Verifica se oggetto ha commenti
     */
    public boolean hasCommenti(String idoggetto) {
        return commentoRepository.countByIdoggetto(idoggetto) > 0;
    }

    // ===== UTILITY =====

    /**
     * Trova commento per ID
     */
    public Optional<Commento> findById(Integer id) {
        return commentoRepository.findById(id);
    }

    /**
     * Salva commento
     */
    public Commento save(Commento commento) {
        return commentoRepository.save(commento);
    }
}