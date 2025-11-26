package com.studiodomino.jplatform.shared.repository;

import com.studiodomino.jplatform.shared.entity.Ruolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RuoloRepository extends JpaRepository<Ruolo, Integer> {

    Optional<Ruolo> findByNome(String nome);

    // JPA crea automaticamente: findAll(), findById(), save(), delete()
}