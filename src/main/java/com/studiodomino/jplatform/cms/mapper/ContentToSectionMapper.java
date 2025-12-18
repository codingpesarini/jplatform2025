package com.studiodomino.jplatform.cms.mapper;

import com.studiodomino.jplatform.cms.entity.Content;
import com.studiodomino.jplatform.cms.entity.Section;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper per convertire Content entity in Section DTO.
 * Utilizzato per le sezioni (idRoot = -1).
 */
@Component
public class ContentToSectionMapper {

    /**
     * Converte una Content entity in Section DTO
     */
    public Section toSection(Content content) {
        if (content == null) {
            return null;
        }

        // Verifica che sia effettivamente una sezione
        if (!content.isSection()) {
            throw new IllegalArgumentException(
                    "Cannot convert Content with idRoot=" + content.getIdRoot() + " to Section. Expected idRoot=-1"
            );
        }

        Section section = new Section();

        // ========== IDENTIFICAZIONE ==========
        section.setId(content.getId());
        section.setIdSite(content.getIdSite());
        section.setIdRoot(content.getIdRoot());
        section.setIdType(content.getIdType());
        section.setIdParent(content.getIdParent());
        section.setLabel(content.getLabel());

        // ========== CONTENUTO BASE ==========
        section.setTitolo(content.getTitolo());
        section.setRiassunto(content.getRiassunto());
        section.setTesto(content.getTesto());
        section.setTitoloEN(content.getTitoloEN());
        section.setRiassuntoEN(content.getRiassuntoEN());
        section.setTestoEN(content.getTestoEN());

        // ========== STATO E VISIBILITÀ ==========
        section.setStato(content.getStato());
        section.setPrivato(content.getPrivato());
        section.setIdGruppo(content.getIdGruppo());
        section.setVisualizza(content.getVisualizza());

        // ========== POSIZIONAMENTO ==========
        section.setPosition(content.getPosition());
        section.setFirstPage(content.getFirstPage());
        section.setClick(content.getClick());

        // ========== MEDIA ==========
        section.setGallery(content.getGallery());
        section.setIdAllegato(content.getIdAllegato());

        // ========== TAG ==========
        section.setTag(content.getTag());

        // ========== MENU ==========
        section.setMenu1(content.getMenu1());
        section.setMenu2(content.getMenu2());
        section.setMenu3(content.getMenu3());
        section.setMenu4(content.getMenu4());
        section.setMenu5(content.getMenu5());

        // ========== CAMPI S ==========
        section.setS1(content.getS1());
        section.setS2(content.getS2());
        section.setS3(content.getS3());
        section.setS4(content.getS4());
        section.setS5(content.getS5());
        section.setS6(content.getS6());
        section.setS7(content.getS7());
        section.setS8(content.getS8());
        section.setS9(content.getS9());
        section.setS10(content.getS10());

        // ========== DATE ==========
        section.setData(content.getData());
        section.setDataVisualizzata(content.getDataVisualizzata());
        section.setDataSql(content.getDataSql());
        section.setCreato(content.getCreato());
        section.setCreatoDa(content.getCreatoDa());
        section.setModificato(content.getModificato());
        section.setModificatoDa(content.getModificatoDa());

        // ========== RELAZIONI ==========
        section.setSectionType(content.getSectionType());

        // Relazioni transient (da popolare successivamente nel service)
        section.setSubsection(new ArrayList<>());
        section.setContenuti(new ArrayList<>());
        section.setParentSection(new ArrayList<>());
        section.setAllegati(new ArrayList<>());
        section.setGalleryList(new ArrayList<>());

        // ========== i18n ==========
        section.setLocale(content.getLocale() != null ? content.getLocale() : "it_IT");

        return section;
    }

    /**
     * Converte una lista di Content in lista di Section
     */
    public List<Section> toSectionList(List<Content> contents) {
        if (contents == null) {
            return new ArrayList<>();
        }

        return contents.stream()
                .filter(Content::isSection)  // Filtra solo le sezioni
                .map(this::toSection)
                .collect(Collectors.toList());
    }

    /**
     * Aggiorna una Section esistente con i dati da Content
     */
    public void updateSection(Content content, Section section) {
        if (content == null || section == null) {
            return;
        }

        if (!content.isSection()) {
            throw new IllegalArgumentException(
                    "Cannot update Section from Content with idRoot=" + content.getIdRoot()
            );
        }

        // Aggiorna tutti i campi (come nel metodo toSection)
        section.setId(content.getId());
        section.setIdSite(content.getIdSite());
        section.setIdRoot(content.getIdRoot());
        section.setIdType(content.getIdType());
        section.setIdParent(content.getIdParent());
        section.setLabel(content.getLabel());
        section.setTitolo(content.getTitolo());
        section.setRiassunto(content.getRiassunto());
        section.setTesto(content.getTesto());
        section.setTitoloEN(content.getTitoloEN());
        section.setRiassuntoEN(content.getRiassuntoEN());
        section.setTestoEN(content.getTestoEN());
        section.setStato(content.getStato());
        section.setPrivato(content.getPrivato());
        section.setIdGruppo(content.getIdGruppo());
        section.setVisualizza(content.getVisualizza());
        section.setPosition(content.getPosition());
        section.setFirstPage(content.getFirstPage());
        section.setClick(content.getClick());
        section.setGallery(content.getGallery());
        section.setIdAllegato(content.getIdAllegato());
        section.setTag(content.getTag());
        section.setMenu1(content.getMenu1());
        section.setMenu2(content.getMenu2());
        section.setMenu3(content.getMenu3());
        section.setMenu4(content.getMenu4());
        section.setMenu5(content.getMenu5());
        section.setS1(content.getS1());
        section.setS2(content.getS2());
        section.setS3(content.getS3());
        section.setS4(content.getS4());
        section.setS5(content.getS5());
        section.setS6(content.getS6());
        section.setS7(content.getS7());
        section.setS8(content.getS8());
        section.setS9(content.getS9());
        section.setS10(content.getS10());
        section.setData(content.getData());
        section.setDataVisualizzata(content.getDataVisualizzata());
        section.setDataSql(content.getDataSql());
        section.setCreato(content.getCreato());
        section.setCreatoDa(content.getCreatoDa());
        section.setModificato(content.getModificato());
        section.setModificatoDa(content.getModificatoDa());
        section.setSectionType(content.getSectionType());
    }

    /**
     * Converte Section DTO in Content entity (per salvataggio)
     */
    public Content toContent(Section section) {
        if (section == null) {
            return null;
        }

        Content content = new Content();

        // ========== IDENTIFICAZIONE ==========
        content.setId(section.getId());
        content.setIdSite(section.getIdSite());
        content.setIdRoot(-1);  // Forza -1 per le sezioni
        content.setIdType(section.getIdType());
        content.setIdParent(section.getIdParent());
        content.setLabel(section.getLabel());

        // ========== CONTENUTO BASE ==========
        content.setTitolo(section.getTitolo());
        content.setRiassunto(section.getRiassunto());
        content.setTesto(section.getTesto());
        content.setTitoloEN(section.getTitoloEN());
        content.setRiassuntoEN(section.getRiassuntoEN());
        content.setTestoEN(section.getTestoEN());

        // ========== STATO E VISIBILITÀ ==========
        content.setStato(section.getStato());
        content.setPrivato(section.getPrivato());
        content.setIdGruppo(section.getIdGruppo());
        content.setVisualizza(section.getVisualizza());

        // ========== POSIZIONAMENTO ==========
        content.setPosition(section.getPosition());
        content.setFirstPage(section.getFirstPage());
        content.setClick(section.getClick());

        // ========== MEDIA ==========
        content.setGallery(section.getGallery());
        content.setIdAllegato(section.getIdAllegato());

        // ========== TAG ==========
        content.setTag(section.getTag());

        // ========== MENU ==========
        content.setMenu1(section.getMenu1());
        content.setMenu2(section.getMenu2());
        content.setMenu3(section.getMenu3());
        content.setMenu4(section.getMenu4());
        content.setMenu5(section.getMenu5());

        // ========== CAMPI S ==========
        content.setS1(section.getS1());
        content.setS2(section.getS2());
        content.setS3(section.getS3());
        content.setS4(section.getS4());
        content.setS5(section.getS5());
        content.setS6(section.getS6());
        content.setS7(section.getS7());
        content.setS8(section.getS8());
        content.setS9(section.getS9());
        content.setS10(section.getS10());

        // ========== DATE ==========
        content.setData(section.getData());
        content.setDataVisualizzata(section.getDataVisualizzata());
        content.setDataSql(section.getDataSql());
        content.setCreato(section.getCreato());
        content.setCreatoDa(section.getCreatoDa());
        content.setModificato(section.getModificato());
        content.setModificatoDa(section.getModificatoDa());

        // ========== i18n ==========
        content.setLocale(section.getLocale() != null ? section.getLocale() : "it_IT");

        return content;
    }
}