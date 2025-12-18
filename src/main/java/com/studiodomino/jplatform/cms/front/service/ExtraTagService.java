package com.studiodomino.jplatform.cms.front.service;

import com.studiodomino.jplatform.cms.entity.*;
import com.studiodomino.jplatform.cms.front.dao.DAOPubblico;
import com.studiodomino.jplatform.shared.config.ConfigurazioneCore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExtraTagService {

    private final DAOPubblico daoPubblico;

    /**
     * Elabora tutti gli ExtraTag di una sezione/documento
     * Equivalente a elaboraExtraTag()
     */
    public void elaboraExtraTag(
            Section section,
            DatiBase base,
            Configurazione configPortal,
            ConfigurazioneCore configCore) {

        try {
            String ordine = base.getOrdineExtraTag();
            String max = base.getMaxExtraTag();
            Integer idSito = configCore.getIdSito();

            // ExtraTag 01
            if (base.getExtraTag1() != null && !base.getExtraTag1().isEmpty()) {
                configPortal.setContenutiExtraTag01(
                        loadExtraTag(idSito, "extratag1", base.getId(),
                                base.getExtraTag1(), base.getExtraTagRef1(),
                                ordine, max)
                );
            }

            // ExtraTag 02
            if (base.getExtraTag2() != null && !base.getExtraTag2().isEmpty()) {
                configPortal.setContenutiExtraTag02(
                        loadExtraTag(idSito, "extratag2", base.getId(),
                                base.getExtraTag2(), base.getExtraTagRef2(),
                                ordine, max)
                );
            }

            // ExtraTag 03
            if (base.getExtraTag3() != null && !base.getExtraTag3().isEmpty()) {
                configPortal.setContenutiExtraTag03(
                        loadExtraTag(idSito, "extratag3", base.getId(),
                                base.getExtraTag3(), base.getExtraTagRef3(),
                                ordine, max)
                );
            }

            // ExtraTag 04-10 (ripeti pattern)
            // ...

        } catch (Exception e) {
            log.error("Errore in elaboraExtraTag", e);
        }
    }

    /**
     * Carica singolo ExtraTag
     */
    private List<DatiBase> loadExtraTag(
            Integer idSito, String fieldName, String baseId,
            String tagValue, String refId, String ordine, String max) {

        String sql = buildExtraTagSql(refId);

        return daoPubblico.getExtraTag(
                idSito, fieldName, baseId, tagValue, sql, ordine, max
        );
    }

    /**
     * Costruisce SQL per cercare contenuti con ExtraTag
     * in sezione ref e sottosezioni
     */
    private String buildExtraTagSql(String ref) {
        return String.format(
                " and (id_root='%s' or idparent='%s' " +
                        "or idparent in (select id from contents aa where aa.idparent='%s') " +
                        "or id_root in (select id from contents aa where aa.idparent='%s')) ",
                ref, ref, ref, ref
        );
    }
}