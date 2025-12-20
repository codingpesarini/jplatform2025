package com.studiodomino.jplatform.cms.front.service;

import com.studiodomino.jplatform.cms.entity.*;
import com.studiodomino.jplatform.cms.front.dto.ExtraTag;
import com.studiodomino.jplatform.cms.service.ContentService;
import com.studiodomino.jplatform.shared.config.ConfigurazioneCore;
import com.studiodomino.jplatform.shared.entity.Site;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.studiodomino.jplatform.cms.front.dto.Breadcrumb;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortalConfigurationService {

    private final ContentService contentService;
    private final CookieNavigationService cookieNavigationService; // ✅ CORRETTO
    private final ExtraTagService extraTagService; // ✅ AGGIUNTO

    /**
     * Punto di accesso al portale - equivalente a PuntoAccessoPortale()
     */
    public Configurazione initializePortal(
            HttpServletRequest request,
            HttpServletResponse response,
            ConfigurazioneCore configCore) {

        Configurazione configPortal = new Configurazione();

        try {
            configPortal.restoreExtraTag();
            configPortal.restorePageReturn();

            // Gestione cookies navigazione
            cookieNavigationService.updateNavigationCookies(request, response, configCore);

            // Carica utente esterno da cookie
            cookieNavigationService.loadExternalUser(request, response, configCore);

            // Carica tutti i contenuti comuni del portale
            refreshPortalContents(request, response, configPortal, configCore);

            // Breadcrumb home
            initializeHomeBreadcrumb(configCore);

        } catch (Exception e) {
            log.error("Errore in initializePortal", e);
        }

        return configPortal;
    }

    /**
     * Refresh dei contenuti del portale - equivalente a Refresh()
     */
    public void refreshPortalContents(
            HttpServletRequest request,
            HttpServletResponse response,
            Configurazione configPortal,
            ConfigurazioneCore configCore) {

        try {
            Integer idSito = Integer.valueOf(configCore.getIdSito());
            Site sito = configCore.getSito();

            // Ordina contenuti
            contentService.ordinaContenuti(idSito);

            // Carica sezione home
            configPortal.setHome(contentService.getSezioneHome(idSito));

            // Carica struttura menu
            configPortal.setSezioniFront(
                    contentService.getStrutturaMenu(idSito, "-1", "0", "1", "0",
                            "position", true)
            );

            // Carica i 10 slot di contenuti configurabili
            loadHomeContentSlots(configPortal, configCore, sito);

            // Carica tag cloud
            configPortal.setTagCloud(contentService.getTagCloud(""));

            // Contenuti basati su profilo navigazione utente
            if (configCore.getProfileIDCookieValue() != null) {
                configPortal.setContenutiProfileID(
                        contentService.getContenutiFront(idSito, "id",
                                configCore.getProfileIDCookieValue(),
                                "", "titolo", "12")
                );
            }

        } catch (Exception e) {
            log.error("Errore in refreshPortalContents", e);
        }
    }

    /**
     * Carica i 10 slot di contenuti della home page
     */
    private void loadHomeContentSlots(
            Configurazione configPortal,
            ConfigurazioneCore configCore,
            Site sito) {

        Integer idSito = Integer.valueOf(configCore.getIdSito());

        // Slot 01
        loadContentSlot(configPortal, configCore, idSito,
                sito.getContenutiFront01(),
                " or l2='1' ",
                sito.getContenutiOrdineFront01(),
                sito.getMaxContenutiFront01(),
                1);

        // Slot 02
        loadContentSlot(configPortal, configCore, idSito,
                sito.getContenutiFront02(),
                "",
                sito.getContenutiOrdineFront02(),
                sito.getMaxContenutiFront02(),
                2);

        // Slot 03
        loadContentSlot(configPortal, configCore, idSito,
                sito.getContenutiFront03(),
                "",
                sito.getContenutiOrdineFront03(),
                sito.getMaxContenutiFront03(),
                3);

        // Slot 04
        loadContentSlot(configPortal, configCore, idSito,
                sito.getContenutiFront04(),
                "",
                sito.getContenutiOrdineFront04(),
                sito.getMaxContenutiFront04(),
                4);

        // Slot 05
        loadContentSlot(configPortal, configCore, idSito,
                sito.getContenutiFront05(),
                "",
                sito.getContenutiOrdineFront05(),
                sito.getMaxContenutiFront05(),
                5);

        // Slot 06
        loadContentSlot(configPortal, configCore, idSito,
                sito.getContenutiFront06(),
                "",
                sito.getContenutiOrdineFront06(),
                sito.getMaxContenutiFront06(),
                6);

        // Slot 07
        loadContentSlot(configPortal, configCore, idSito,
                sito.getContenutiFront07(),
                "",
                sito.getContenutiOrdineFront07(),
                sito.getMaxContenutiFront07(),
                7);

        // Slot 08
        loadContentSlot(configPortal, configCore, idSito,
                sito.getContenutiFront08(),
                "",
                sito.getContenutiOrdineFront08(),
                sito.getMaxContenutiFront08(),
                8);

        // Slot 09
        loadContentSlot(configPortal, configCore, idSito,
                sito.getContenutiFront09(),
                "",
                sito.getContenutiOrdineFront09(),
                sito.getMaxContenutiFront09(),
                9);

        // Slot 10
        loadContentSlot(configPortal, configCore, idSito,
                sito.getContenutiFront10(),
                "",
                sito.getContenutiOrdineFront10(),
                sito.getMaxContenutiFront10(),
                10);
    }

    /**
     * Carica un singolo slot di contenuti con ExtraTag
     */
    private void loadContentSlot(
            Configurazione configPortal,
            ConfigurazioneCore configCore,
            Integer idSito,
            String slotId,
            String condition,
            String orderBy,
            String max,
            int slotNumber) {

        List<DatiBase> contents = contentService.getContenutiFront(
                idSito, "id_root", slotId, condition, orderBy, max
        );

        // Elabora ExtraTag per ogni contenuto
        if (contents != null) {
            for (DatiBase content : contents) {
                ExtraTag extraTag = extraTagService.elaboraExtraTagDatiBase(
                        content,
                        content.getOrdineExtraTag(),
                        content.getMaxExtraTag(),
                        configCore
                );
                content.setExtratag(extraTag);
            }
        }

        // Assegna allo slot corretto
        switch(slotNumber) {
            case 1: configPortal.setContenutiFront01(contents); break;
            case 2: configPortal.setContenutiFront02(contents); break;
            case 3: configPortal.setContenutiFront03(contents); break;
            case 4: configPortal.setContenutiFront04(contents); break;
            case 5: configPortal.setContenutiFront05(contents); break;
            case 6: configPortal.setContenutiFront06(contents); break;
            case 7: configPortal.setContenutiFront07(contents); break;
            case 8: configPortal.setContenutiFront08(contents); break;
            case 9: configPortal.setContenutiFront09(contents); break;
            case 10: configPortal.setContenutiFront10(contents); break;
        }
    }

    /**
     * Inizializza breadcrumb home
     */
    private void initializeHomeBreadcrumb(ConfigurazioneCore configCore) {
        Breadcrumb breadC = new Breadcrumb();
        breadC.setItemAttuale("Home Page");
        breadC.setItemIdAttuale("0");
        breadC.setItems(new ArrayList<>());
        configCore.setBreadcrumb(breadC);
    }
}