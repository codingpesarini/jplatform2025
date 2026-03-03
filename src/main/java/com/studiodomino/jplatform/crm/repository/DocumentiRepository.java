package com.studiodomino.jplatform.crm.repository;

import com.studiodomino.jplatform.cms.entity.Content;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentiRepository extends JpaRepository<Content, Integer> {

    List<Content> findByIdRootOrderByPositionAsc(Integer idRoot);

    List<Content> findByIdRootAndStatoOrderByPositionAsc(Integer idRoot, String stato);

    @Modifying
    @Query("UPDATE Content c SET c.position = :pos WHERE c.id = :id")
    void updatePosition(@Param("id") Integer id, @Param("pos") Integer pos);

    @Modifying
    @Query("UPDATE Content c SET c.idRoot = :idRoot, c.idType = :idType WHERE c.id = :id")
    void updateRiclassifica(@Param("id") Integer id,
                            @Param("idRoot") Integer idRoot,
                            @Param("idType") Integer idType);

    @Modifying
    @Query("UPDATE Content c SET c.newsletter3 = :val WHERE c.id = :id")
    void updateNewsletter3(@Param("id") Integer id, @Param("val") String val);
}