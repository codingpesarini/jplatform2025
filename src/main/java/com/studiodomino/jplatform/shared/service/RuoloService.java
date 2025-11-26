package com.studiodomino.jplatform.shared.service;

import com.studiodomino.jplatform.shared.entity.Ruolo;
import com.studiodomino.jplatform.shared.repository.RuoloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RuoloService {

    @Autowired
    private RuoloRepository ruoloRepository;

    /**
     * Ottieni tutti i ruoli
     */
    public List<Ruolo> getAllRuoli() {
        return ruoloRepository.findAll();
    }

    /**
     * Ottieni ruolo per ID
     */
    public Ruolo getRuoloById(Integer id) {
        return ruoloRepository.findById(id).orElse(null);
    }

    /**
     * Ottieni ruolo per nome
     */
    public Ruolo getRuoloByNome(String nome) {
        return ruoloRepository.findByNome(nome).orElse(null);
    }

    /**
     * Crea nuovo ruolo
     */
    @Transactional
    public Ruolo creaRuolo(Ruolo ruolo) {
        ruolo.setId(null); // Forza creazione
        ruolo.setDatamodifica(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return ruoloRepository.save(ruolo);
    }

    /**
     * Salva/aggiorna ruolo
     */
    @Transactional
    public Ruolo salvaRuolo(Ruolo ruolo) {
        ruolo.setDatamodifica(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return ruoloRepository.save(ruolo);
    }

    /**
     * Cancella ruolo
     */
    @Transactional
    public void cancellaRuolo(Integer id) {
        ruoloRepository.deleteById(id);
    }
}