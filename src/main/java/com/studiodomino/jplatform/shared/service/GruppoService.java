package com.studiodomino.jplatform.shared.service;

import com.studiodomino.jplatform.shared.entity.Gruppo;
import com.studiodomino.jplatform.shared.repository.GruppoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class GruppoService {

    @Autowired
    private GruppoRepository gruppoRepository;

    /**
     * Ottieni tutti i gruppi
     */
    public List<Gruppo> getAllGruppi() {
        return gruppoRepository.findAll();
    }

    /**
     * Ottieni gruppo per ID
     */
    public Gruppo getGruppoById(Integer id) {
        return gruppoRepository.findById(id).orElse(null);
    }

    /**
     * Ottieni gruppo per nome
     */
    public Gruppo getGruppoByNome(String nome) {
        return gruppoRepository.findByNome(nome).orElse(null);
    }

    /**
     * Crea nuovo gruppo
     */
    @Transactional
    public Gruppo creaGruppo(Gruppo gruppo) {
        gruppo.setId(null); // Forza creazione
        gruppo.setDatamodifica(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return gruppoRepository.save(gruppo);
    }

    /**
     * Salva/aggiorna gruppo
     */
    @Transactional
    public Gruppo salvaGruppo(Gruppo gruppo) {
        gruppo.setDatamodifica(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return gruppoRepository.save(gruppo);
    }

    /**
     * Cancella gruppo
     */
    @Transactional
    public void cancellaGruppo(Integer id) {
        gruppoRepository.deleteById(id);
    }
}