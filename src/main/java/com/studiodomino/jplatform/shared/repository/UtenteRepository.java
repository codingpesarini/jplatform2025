package com.studiodomino.jplatform.shared.repository;

import com.studiodomino.jplatform.shared.entity.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Integer> {

    Optional<Utente> findByUsername(String username);

    Optional<Utente> findByUsernameAndPassword(String username, String password);

    Optional<Utente> findByEmail(String email);
}