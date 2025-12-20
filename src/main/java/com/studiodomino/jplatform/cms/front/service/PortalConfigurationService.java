package com.studiodomino.jplatform.cms.front.service;

import com.studiodomino.jplatform.cms.entity.DatiBase;
import com.studiodomino.jplatform.cms.entity.Section;
import com.studiodomino.jplatform.cms.front.dto.Breadcrumb;
import com.studiodomino.jplatform.cms.front.dto.Tag;
import com.studiodomino.jplatform.cms.service.ContentService;
import com.studiodomino.jplatform.shared.config.Configurazione;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service per inizializzazione configurazione portale.
 * Popola le parti REQUEST della Configurazione (menu, slot, etc.)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PortalConfigurationService {

    private final ContentService contentService;
    private final ExtraTagService extraTagService;

    /**
     * Inizializza le parti REQUEST della configurazione:
     * - Menu pubblico
     * - Menu privato
     * - Home page
     * - Slot contenuti (01-10)
     * - Tag cloud
     * - Breadcrumb iniziale
     *
     * NOTA: Non crea una nuova Configurazione, popola quella esistente
     */
    public void initializePortal(
            HttpServletRequest request,
            HttpServletResponse response,
            Configurazione config) {

        log.debug("Inizializzazione portale per sito: {}", config.getSito().getId());

        Integer idSito = config.getSito().getId();
        String idSitoStr = idSito.toString();

        try {
            // ===== 1. CARICA MENU PUBBLICO =====
            List<Section> menuPubblico = contentService.findPublicMenu(idSitoStr);
            config.setSezioniFront(menuPubblico);
            log.debug("Menu pubblico caricato: {} sezioni", menuPubblico.size());

            // ===== 2. CARICA MENU PRIVATO (se utente loggato) =====
            if (config.hasUtente()) {
                List<Section> menuPrivato = contentService.findPrivateMenu(
                        idSitoStr, config.getUtente()
                );
                config.setSezioniFrontPrivate(menuPrivato);
                log.debug("Menu privato caricato: {} sezioni", menuPrivato.size());
            } else {
                config.setSezioniFrontPrivate(new ArrayList<>());
            }

            // ===== 3. CARICA HOME PAGE =====
            Section home = contentService.findHomePage(idSitoStr);
            config.setHome(home);

            // ===== 4. CARICA SLOT CONTENUTI HOME PAGE (01-10) =====
            loadContentSlots(config, idSito);

            // ===== 5. CARICA TAG CLOUD =====
            List<Tag> tagCloud = generateTagCloud(idSitoStr);
            config.setTagCloud(tagCloud);

            // ===== 6. INIZIALIZZA BREADCRUMB =====
            initializeHomeBreadcrumb(config);

            log.info("Portale inizializzato correttamente per sito: {}", idSito);

        } catch (Exception e) {
            log.error("Errore inizializzazione portale per sito: {}", idSito, e);
            // Imposta valori di fallback
            config.setSezioniFront(new ArrayList<>());
            config.setSezioniFrontPrivate(new ArrayList<>());
        }
    }

    /**
     * Carica tutti gli slot di contenuti (01-10) configurati nel sito
     */
    /**
     * Carica tutti gli slot di contenuti (01-10) configurati nel sito
     */
    private void loadContentSlots(Configurazione config, Integer idSito) {

        // ===== SLOT 01 =====
        if (config.getSito().getContenutiFront01() != null &&
                !config.getSito().getContenutiFront01().isEmpty()) {

            List<DatiBase> slot01 = contentService.getContenutiFront(
                    idSito,
                    "id_root",
                    config.getSito().getContenutiFront01(),
                    "", // condition
                    config.getSito().getContenutiOrdineFront01(),
                    config.getSito().getMaxContenutiFront01()
            );
            config.setContenutiFront01(slot01);
        }

        // ===== SLOT 02 =====
        if (config.getSito().getContenutiFront02() != null &&
                !config.getSito().getContenutiFront02().isEmpty()) {

            List<DatiBase> slot02 = contentService.getContenutiFront(
                    idSito,
                    "id_root",
                    config.getSito().getContenutiFront02(),
                    "",
                    config.getSito().getContenutiOrdineFront02(),
                    config.getSito().getMaxContenutiFront02()
            );
            config.setContenutiFront02(slot02);
        }

        // ===== SLOT 03 =====
        if (config.getSito().getContenutiFront03() != null &&
                !config.getSito().getContenutiFront03().isEmpty()) {

            List<DatiBase> slot03 = contentService.getContenutiFront(
                    idSito,
                    "id_root",
                    config.getSito().getContenutiFront03(),
                    "",
                    config.getSito().getContenutiOrdineFront03(),
                    config.getSito().getMaxContenutiFront03()
            );
            config.setContenutiFront03(slot03);
        }

        // ===== SLOT 04 =====
        if (config.getSito().getContenutiFront04() != null &&
                !config.getSito().getContenutiFront04().isEmpty()) {

            List<DatiBase> slot04 = contentService.getContenutiFront(
                    idSito,
                    "id_root",
                    config.getSito().getContenutiFront04(),
                    "",
                    config.getSito().getContenutiOrdineFront04(),
                    config.getSito().getMaxContenutiFront04()
            );
            config.setContenutiFront04(slot04);
        }

        // ===== SLOT 05 =====
        if (config.getSito().getContenutiFront05() != null &&
                !config.getSito().getContenutiFront05().isEmpty()) {

            List<DatiBase> slot05 = contentService.getContenutiFront(
                    idSito,
                    "id_root",
                    config.getSito().getContenutiFront05(),
                    "",
                    config.getSito().getContenutiOrdineFront05(),
                    config.getSito().getMaxContenutiFront05()
            );
            config.setContenutiFront05(slot05);
        }

        // ===== SLOT 06 =====
        if (config.getSito().getContenutiFront06() != null &&
                !config.getSito().getContenutiFront06().isEmpty()) {

            List<DatiBase> slot06 = contentService.getContenutiFront(
                    idSito,
                    "id_root",
                    config.getSito().getContenutiFront06(),
                    "",
                    config.getSito().getContenutiOrdineFront06(),
                    config.getSito().getMaxContenutiFront06()
            );
            config.setContenutiFront06(slot06);
        }

        // ===== SLOT 07 =====
        if (config.getSito().getContenutiFront07() != null &&
                !config.getSito().getContenutiFront07().isEmpty()) {

            List<DatiBase> slot07 = contentService.getContenutiFront(
                    idSito,
                    "id_root",
                    config.getSito().getContenutiFront07(),
                    "",
                    config.getSito().getContenutiOrdineFront07(),
                    config.getSito().getMaxContenutiFront07()
            );
            config.setContenutiFront07(slot07);
        }

        // ===== SLOT 08 =====
        if (config.getSito().getContenutiFront08() != null &&
                !config.getSito().getContenutiFront08().isEmpty()) {

            List<DatiBase> slot08 = contentService.getContenutiFront(
                    idSito,
                    "id_root",
                    config.getSito().getContenutiFront08(),
                    "",
                    config.getSito().getContenutiOrdineFront08(),
                    config.getSito().getMaxContenutiFront08()
            );
            config.setContenutiFront08(slot08);
        }

        // ===== SLOT 09 =====
        if (config.getSito().getContenutiFront09() != null &&
                !config.getSito().getContenutiFront09().isEmpty()) {

            List<DatiBase> slot09 = contentService.getContenutiFront(
                    idSito,
                    "id_root",
                    config.getSito().getContenutiFront09(),
                    "",
                    config.getSito().getContenutiOrdineFront09(),
                    config.getSito().getMaxContenutiFront09()
            );
            config.setContenutiFront09(slot09);
        }

        // ===== SLOT 10 =====
        if (config.getSito().getContenutiFront10() != null &&
                !config.getSito().getContenutiFront10().isEmpty()) {

            List<DatiBase> slot10 = contentService.getContenutiFront(
                    idSito,
                    "id_root",
                    config.getSito().getContenutiFront10(),
                    "",
                    config.getSito().getContenutiOrdineFront10(),
                    config.getSito().getMaxContenutiFront10()
            );
            config.setContenutiFront10(slot10);
        }

        log.debug("Content slots loaded for site: {}", idSito);
    }

     /**
     * Genera tag cloud dai contenuti del sito
     */
    private List<Tag> generateTagCloud(String idSito) {
        try {
            // Usa il metodo già implementato in ContentService
            List<Tag> tags = contentService.getTagCloud(idSito);
            log.debug("Tag cloud generated: {} tags", tags.size());
            return tags;
        } catch (Exception e) {
            log.warn("Error generating tag cloud: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Inizializza breadcrumb home
     */
    private void initializeHomeBreadcrumb(Configurazione config) {
        Breadcrumb breadC = new Breadcrumb();
        breadC.setItemAttuale("Home Page");
        breadC.setItemIdAttuale("0");
        breadC.setItems(new ArrayList<>());
        config.setBreadcrumb(breadC);
    }
}