package com.studiodomino.jplatform.cms.front.service;
import com.studiodomino.jplatform.cms.entity.DatiBase;
import com.studiodomino.jplatform.cms.entity.Section;
import com.studiodomino.jplatform.cms.front.dto.ExtraTag;
import com.studiodomino.jplatform.cms.service.ContentService;

import com.studiodomino.jplatform.shared.config.Configurazione;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ExtraTagService - Gestisce caricamento contenuti correlati
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExtraTagService {

    private final ContentService contentService;

    /**
     * Elabora ExtraTag per un DatiBase
     * Carica contenuti correlati per ogni tag configurato (1-10)
     *
     * @param base DatiBase con configurazione ExtraTag
     * @param ordine Ordinamento contenuti
     * @param max Numero massimo contenuti per tag
     * @param appConfig Configurazione applicazione
     * @return ExtraTag popolato
     */
    public ExtraTag elaboraExtraTagDatiBase(
            DatiBase base,
            String ordine,
            String max,
            Configurazione appConfig) {

        ExtraTag extraTag = new ExtraTag();

        if (base == null || appConfig == null) {
            return extraTag;
        }

        try {
            Integer idSito = appConfig.getSito().getId();

            // Elabora i 10 possibili ExtraTag
            for (int i = 1; i <= 10; i++) {
                String tagName = base.getExtraTag(i);

                if (tagName != null && !tagName.isEmpty() && !"0".equals(tagName)) {
                    // Carica contenuti con questo tag
                    List<DatiBase> contenuti = contentService.findContentsByExtraTag(
                            idSito.toString(),
                            tagName
                    );

                    // Applica max se specificato
                    if (max != null && !max.isEmpty()) {
                        try {
                            int maxItems = Integer.parseInt(max);
                            if (contenuti.size() > maxItems) {
                                contenuti = contenuti.subList(0, maxItems);
                            }
                        } catch (NumberFormatException e) {
                            log.warn("Invalid max value: {}", max);
                        }
                    }

                    // Imposta nello slot corrispondente
                    extraTag.setExtraTagByNumber(i, contenuti);

                    log.debug("ExtraTag slot {}: caricati {} contenuti per tag '{}'",
                            i, contenuti.size(), tagName);
                }
            }

        } catch (Exception e) {
            log.error("Errore elaborazione ExtraTag per DatiBase id: {}", base.getId(), e);
        }

        return extraTag;
    }

    /**
     * Elabora ExtraTag per una Section
     *
     * @param section Section con configurazione ExtraTag
     * @param ordine Ordinamento contenuti
     * @param max Numero massimo contenuti per tag
     * @param appConfig Configurazione applicazione
     * @return ExtraTag popolato
     */
    public ExtraTag elaboraExtraTagSection(
            Section section,
            String ordine,
            String max,
            Configurazione appConfig) {

        ExtraTag extraTag = new ExtraTag();

        if (section == null || appConfig == null) {
            return extraTag;
        }

        try {
            Integer idSito = appConfig.getSito().getId();

            // Elabora i 10 possibili ExtraTag
            for (int i = 1; i <= 10; i++) {
                String tagName = section.getExtraTag(i);

                if (tagName != null && !tagName.isEmpty() && !"0".equals(tagName)) {
                    List<DatiBase> contenuti = contentService.findContentsByExtraTag(
                            idSito.toString(),
                            tagName
                    );

                    if (max != null && !max.isEmpty()) {
                        try {
                            int maxItems = Integer.parseInt(max);
                            if (contenuti.size() > maxItems) {
                                contenuti = contenuti.subList(0, maxItems);
                            }
                        } catch (NumberFormatException e) {
                            log.warn("Invalid max value: {}", max);
                        }
                    }

                    extraTag.setExtraTagByNumber(i, contenuti);

                    log.debug("ExtraTag slot {}: caricati {} contenuti per tag '{}'",
                            i, contenuti.size(), tagName);
                }
            }

        } catch (Exception e) {
            log.error("Errore elaborazione ExtraTag per Section id: {}", section.getId(), e);
        }

        return extraTag;
    }

    /**
     * Verifica se DatiBase ha ExtraTag configurati
     */
    public boolean hasExtraTagConfiguration(DatiBase base) {
        if (base == null) return false;

        for (int i = 1; i <= 10; i++) {
            String tag = base.getExtraTag(i);
            if (tag != null && !tag.isEmpty() && !"0".equals(tag)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica se Section ha ExtraTag configurati
     */
    public boolean hasExtraTagConfiguration(Section section) {
        if (section == null) return false;

        for (int i = 1; i <= 10; i++) {
            String tag = section.getExtraTag(i);
            if (tag != null && !tag.isEmpty() && !"0".equals(tag)) {
                return true;
            }
        }
        return false;
    }
}