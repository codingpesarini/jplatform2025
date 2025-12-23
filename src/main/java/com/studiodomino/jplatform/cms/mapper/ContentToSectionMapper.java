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
        section.setRating(content.getRating() != null ? content.getRating() : 0);

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

        // ========== CAMPI L (1-15) ==========
        section.setL1(content.getL1());
        section.setL2(content.getL2());
        section.setL3(content.getL3());
        section.setL4(content.getL4());
        section.setL5(content.getL5());
        section.setL6(content.getL6());
        section.setL7(content.getL7());
        section.setL8(content.getL8());
        section.setL9(content.getL9());
        section.setL10(content.getL10());
        section.setL11(content.getL11());
        section.setL12(content.getL12());
        section.setL13(content.getL13());
        section.setL14(content.getL14());
        section.setL15(content.getL15());

        // ========== INFO (1-5) ==========
        section.setInfo1(content.getInfo1());
        section.setInfo2(content.getInfo2());
        section.setInfo3(content.getInfo3());
        section.setInfo4(content.getInfo4());
        section.setInfo5(content.getInfo5());

        // ========== DATE ==========
        section.setData(content.getData());
        section.setDataVisualizzata(content.getDataVisualizzata());
        section.setDataSql(content.getDatasql());
        section.setCreato(content.getCreato());
        section.setCreatoDa(content.getCreatoda());
        section.setModificato(content.getModificato());
        section.setModificatoDa(content.getModificatoda());
        section.setApertoda(content.getApertoda());

        // ========== NEWSLETTER E SMS ==========
        section.setNewsletter1(content.getNewsletter1());
        section.setNewsletter2(content.getNewsletter2());
        section.setNewsletter3(content.getNewsletter3());
        section.setNewsletter4(content.getNewsletter4());
        section.setNewsletter5(content.getNewsletter5());
        section.setSms1(content.getSms1());
        section.setSms2(content.getSms2());
        section.setSms3(content.getSms3());
        section.setSms4(content.getSms4());
        section.setSms5(content.getSms5());

        // ========== TEXT (1-10) ==========
        section.setText1(content.getText1());
        section.setText2(content.getText2());
        section.setText3(content.getText3());
        section.setText4(content.getText4());
        section.setText5(content.getText5());
        section.setText6(content.getText6());
        section.setText7(content.getText7());
        section.setText8(content.getText8());
        section.setText9(content.getText9());
        section.setText10(content.getText10());

        // ========== DATA (1-10) ==========
        section.setData1(content.getData1());
        section.setData2(content.getData2());
        section.setData3(content.getData3());
        section.setData4(content.getData4());
        section.setData5(content.getData5());
        section.setData6(content.getData6());
        section.setData7(content.getData7());
        section.setData8(content.getData8());
        section.setData9(content.getData9());
        section.setData10(content.getData10());

        // ========== VARCHAR (1-10) ==========
        section.setVarchar1(content.getVarchar1());
        section.setVarchar2(content.getVarchar2());
        section.setVarchar3(content.getVarchar3());
        section.setVarchar4(content.getVarchar4());
        section.setVarchar5(content.getVarchar5());
        section.setVarchar6(content.getVarchar6());
        section.setVarchar7(content.getVarchar7());
        section.setVarchar8(content.getVarchar8());
        section.setVarchar9(content.getVarchar9());
        section.setVarchar10(content.getVarchar10());

        // ========== NUMBER (1-10) ==========
        section.setNumber1(content.getNumber1());
        section.setNumber2(content.getNumber2());
        section.setNumber3(content.getNumber3());
        section.setNumber4(content.getNumber4());
        section.setNumber5(content.getNumber5());
        section.setNumber6(content.getNumber6());
        section.setNumber7(content.getNumber7());
        section.setNumber8(content.getNumber8());
        section.setNumber9(content.getNumber9());
        section.setNumber10(content.getNumber10());

        // ========== ARRAY (1-5) ==========
        section.setArray1(Section.stringToArray(content.getArray1(), ";"));
        section.setArray2(Section.stringToArray(content.getArray2(), ";"));
        section.setArray3(Section.stringToArray(content.getArray3(), ";"));
        section.setArray4(Section.stringToArray(content.getArray4(), ";"));
        section.setArray5(Section.stringToArray(content.getArray5(), ";"));

        // ========== NUMERATORI (1-5) ==========
        section.setNumeratore1(content.getNumeratore1() != null ? (long)content.getNumeratore1() : 0L);
        section.setNumeratore2(content.getNumeratore2() != null ? (long)content.getNumeratore2() : 0L);
        section.setNumeratore3(content.getNumeratore3() != null ? (long)content.getNumeratore3() : 0L);
        section.setNumeratore4(content.getNumeratore4() != null ? (long)content.getNumeratore4() : 0L);
        section.setNumeratore5(content.getNumeratore5() != null ? (long)content.getNumeratore5() : 0L);

        // ========== LOG (1-3) ==========
        section.setLog1(content.getLog1());
        section.setLog2(content.getLog2());
        section.setLog3(content.getLog3());

        // ========== LOGO E TEMP ==========
        section.setLogo(content.getLogo());
        section.setLogo2(content.getLogo2());
        section.setLogo3(content.getLogo3());
        section.setTemp1(content.getTemp1());
        section.setTemp2(content.getTemp2());
        section.setTemp3(content.getTemp3());
        section.setTemp4(content.getTemp4());
        section.setTemp5(content.getTemp5());

        // ========== REPO ==========
        section.setRepo(content.getRepo());
        section.setRepoId(content.getRepoId());
        section.setRepoName(content.getRepoName());

        // ========== EXTRATAG (1-10) ==========
        section.setExtratag1(content.getExtratag1());
        section.setExtratag2(content.getExtratag2());
        section.setExtratag3(content.getExtratag3());
        section.setExtratag4(content.getExtratag4());
        section.setExtratag5(content.getExtratag5());
        section.setExtratag6(content.getExtratag6());
        section.setExtratag7(content.getExtratag7());
        section.setExtratag8(content.getExtratag8());
        section.setExtratag9(content.getExtratag9());
        section.setExtratag10(content.getExtratag10());

        // ========== EXTRATAG REF (1-10) ==========
        section.setExtraTagRef1(content.getExtratagref1());
        section.setExtraTagRef2(content.getExtratagref2());
        section.setExtraTagRef3(content.getExtratagref3());
        section.setExtraTagRef4(content.getExtratagref4());
        section.setExtraTagRef5(content.getExtratagref5());
        section.setExtraTagRef6(content.getExtratagref6());
        section.setExtraTagRef7(content.getExtratagref7());
        section.setExtraTagRef8(content.getExtratagref8());
        section.setExtraTagRef9(content.getExtratagref9());
        section.setExtraTagRef10(content.getExtratagref10());

        // ========== REGOLE EXTRA TAG ==========
        section.setRegolaExtraTag1(content.getRegolaextratag1());
        section.setRegolaExtraTag2(content.getRegolaextratag2());
        section.setOrdineExtraTag(content.getOrdineextratag());
        section.setMaxExtraTag(content.getMaxextratag());

        // ========== ORDINAMENTO ==========
        section.setOrdineContenuti(content.getOrdineContenuti());
        section.setOrdineSottosezioni(content.getOrdineSottosezioni());
        section.setMaxOrdineContenuti(content.getMaxOrdineContenuti());
        section.setMaxOrdineSottosezioni(content.getMaxOrdineSottosezioni());

        // ========== RELAZIONI ==========
        section.setSectionType(content.getSectionType());
        section.setUtentiAssociatiString(content.getUtentiAssociati());

        // Relazioni transient (da popolare successivamente nel service)
        section.setSubsection(new ArrayList<>());
        section.setContenuti(new ArrayList<>());
        section.setParentSection(new ArrayList<>());
        section.setAllegati(new ArrayList<>());
        section.setGalleryList(new ArrayList<>());

        // ========== i18n ==========
        section.setLocale("it_IT"); // Default hardcoded
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
        content.setRating(section.getRating());

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

        // ========== CAMPI L (1-15) ==========
        content.setL1(section.getL1());
        content.setL2(section.getL2());
        content.setL3(section.getL3());
        content.setL4(section.getL4());
        content.setL5(section.getL5());
        content.setL6(section.getL6());
        content.setL7(section.getL7());
        content.setL8(section.getL8());
        content.setL9(section.getL9());
        content.setL10(section.getL10());
        content.setL11(section.getL11());
        content.setL12(section.getL12());
        content.setL13(section.getL13());
        content.setL14(section.getL14());
        content.setL15(section.getL15());

        // ========== INFO ==========
        content.setInfo1(section.getInfo1());
        content.setInfo2(section.getInfo2());
        content.setInfo3(section.getInfo3());
        content.setInfo4(section.getInfo4());
        content.setInfo5(section.getInfo5());

        // ========== DATE ==========
        content.setData(section.getData());
        content.setDataVisualizzata(section.getDataVisualizzata());
        content.setDatasql(section.getDataSql());
        content.setCreato(section.getCreato());
        content.setCreatoda(section.getCreatoDa());
        content.setModificato(section.getModificato());
        content.setModificatoda(section.getModificatoDa());
        content.setApertoda(section.getApertoda());

        // ========== NEWSLETTER E SMS ==========
        content.setNewsletter1(section.getNewsletter1());
        content.setNewsletter2(section.getNewsletter2());
        content.setNewsletter3(section.getNewsletter3());
        content.setNewsletter4(section.getNewsletter4());
        content.setNewsletter5(section.getNewsletter5());
        content.setSms1(section.getSms1());
        content.setSms2(section.getSms2());
        content.setSms3(section.getSms3());
        content.setSms4(section.getSms4());
        content.setSms5(section.getSms5());

        // ========== TEXT ==========
        content.setText1(section.getText1());
        content.setText2(section.getText2());
        content.setText3(section.getText3());
        content.setText4(section.getText4());
        content.setText5(section.getText5());
        content.setText6(section.getText6());
        content.setText7(section.getText7());
        content.setText8(section.getText8());
        content.setText9(section.getText9());
        content.setText10(section.getText10());

        // ========== DATA ==========
        content.setData1(section.getData1());
        content.setData2(section.getData2());
        content.setData3(section.getData3());
        content.setData4(section.getData4());
        content.setData5(section.getData5());
        content.setData6(section.getData6());
        content.setData7(section.getData7());
        content.setData8(section.getData8());
        content.setData9(section.getData9());
        content.setData10(section.getData10());

        // ========== VARCHAR ==========
        content.setVarchar1(section.getVarchar1());
        content.setVarchar2(section.getVarchar2());
        content.setVarchar3(section.getVarchar3());
        content.setVarchar4(section.getVarchar4());
        content.setVarchar5(section.getVarchar5());
        content.setVarchar6(section.getVarchar6());
        content.setVarchar7(section.getVarchar7());
        content.setVarchar8(section.getVarchar8());
        content.setVarchar9(section.getVarchar9());
        content.setVarchar10(section.getVarchar10());

        // ========== NUMBER ==========
        content.setNumber1(section.getNumber1());
        content.setNumber2(section.getNumber2());
        content.setNumber3(section.getNumber3());
        content.setNumber4(section.getNumber4());
        content.setNumber5(section.getNumber5());
        content.setNumber6(section.getNumber6());
        content.setNumber7(section.getNumber7());
        content.setNumber8(section.getNumber8());
        content.setNumber9(section.getNumber9());
        content.setNumber10(section.getNumber10());

        // ========== ARRAY ==========
        content.setArray1(section.getArray1() != null ? String.join(";", section.getArray1()) : "");
        content.setArray2(section.getArray2() != null ? String.join(";", section.getArray2()) : "");
        content.setArray3(section.getArray3() != null ? String.join(";", section.getArray3()) : "");
        content.setArray4(section.getArray4() != null ? String.join(";", section.getArray4()) : "");
        content.setArray5(section.getArray5() != null ? String.join(";", section.getArray5()) : "");

        // ========== NUMERATORI ==========
        content.setNumeratore1(section.getNumeratore1() != null ? Integer.parseInt(section.getNumeratore1()) : 0);
        content.setNumeratore2(section.getNumeratore2() != null ? Integer.parseInt(section.getNumeratore2()) : 0);
        content.setNumeratore3(section.getNumeratore3() != null ? Integer.parseInt(section.getNumeratore3()) : 0);
        content.setNumeratore4(section.getNumeratore4() != null ? Integer.parseInt(section.getNumeratore4()) : 0);
        content.setNumeratore5(section.getNumeratore5() != null ? Integer.parseInt(section.getNumeratore5()) : 0);

        // ========== LOG ==========
        content.setLog1(section.getLog1());
        content.setLog2(section.getLog2());
        content.setLog3(section.getLog3());

        // ========== LOGO E TEMP ==========
        content.setLogo(section.getLogo());
        content.setLogo2(section.getLogo2());
        content.setLogo3(section.getLogo3());
        content.setTemp1(section.getTemp1());
        content.setTemp2(section.getTemp2());
        content.setTemp3(section.getTemp3());
        content.setTemp4(section.getTemp4());
        content.setTemp5(section.getTemp5());

        // ========== REPO ==========
        content.setRepo(section.getRepo());
        content.setRepoId(section.getRepoId());
        content.setRepoName(section.getRepoName());

        // ========== EXTRATAG ==========
        content.setExtratag1(section.getExtratag1());
        content.setExtratag2(section.getExtratag2());
        content.setExtratag3(section.getExtratag3());
        content.setExtratag4(section.getExtratag4());
        content.setExtratag5(section.getExtratag5());
        content.setExtratag6(section.getExtratag6());
        content.setExtratag7(section.getExtratag7());
        content.setExtratag8(section.getExtratag8());
        content.setExtratag9(section.getExtratag9());
        content.setExtratag10(section.getExtratag10());

        content.setExtratagref1(section.getExtraTagRef1());
        content.setExtratagref2(section.getExtraTagRef2());
        content.setExtratagref3(section.getExtraTagRef3());
        content.setExtratagref4(section.getExtraTagRef4());
        content.setExtratagref5(section.getExtraTagRef5());
        content.setExtratagref6(section.getExtraTagRef6());
        content.setExtratagref7(section.getExtraTagRef7());
        content.setExtratagref8(section.getExtraTagRef8());
        content.setExtratagref9(section.getExtraTagRef9());
        content.setExtratagref10(section.getExtraTagRef10());

        content.setRegolaextratag1(section.getRegolaExtraTag1());
        content.setRegolaextratag2(section.getRegolaExtraTag2());
        content.setOrdineextratag(section.getOrdineExtraTag());
        content.setMaxextratag(section.getMaxExtraTag());

        // ========== ORDINAMENTO ==========
        content.setOrdineContenuti(section.getOrdineContenuti());
        content.setOrdineSottosezioni(section.getOrdineSottosezioni());
        content.setMaxOrdineContenuti(section.getMaxOrdineContenuti());
        content.setMaxOrdineSottosezioni(section.getMaxOrdineSottosezioni());

        // ========== UTENTI ASSOCIATI ==========
        content.setUtentiAssociati(section.getUtentiAssociatiString());

        return content;
    }
}