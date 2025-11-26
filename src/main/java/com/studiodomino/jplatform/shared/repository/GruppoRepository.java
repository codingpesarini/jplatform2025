package com.studiodomino.jplatform.shared.repository;

import com.studiodomino.jplatform.shared.entity.Gruppo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GruppoRepository extends JpaRepository<Gruppo, Integer> {

    Optional<Gruppo> findByNome(String nome);

    // JPA crea automaticamente: findAll(), findById(), save(), delete()
}