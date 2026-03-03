package com.studiodomino.jplatform.crm.repository;

import com.studiodomino.jplatform.crm.entity.Numeratore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NumeratoreRepository extends JpaRepository<Numeratore, Long> {
}