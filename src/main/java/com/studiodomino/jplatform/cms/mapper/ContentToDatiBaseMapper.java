package com.studiodomino.jplatform.cms.mapper;

import com.studiodomino.jplatform.cms.entity.Content;
import com.studiodomino.jplatform.cms.entity.DatiBase;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper per convertire Content entity in DatiBase DTO.
 * Utilizzato per i contenuti (idRoot != -1).
 */
@Component
public class ContentToDatiBaseMapper {

    /**
     * Converte una Content entity in DatiBase DTO
     */
    public DatiBase toDatiBase(Content content) {
        if (content == null) {
            return null;
        }

        // Verifica che sia effettivamente un contenuto (non una sezione)
        if (content.isSection()) {
            throw new IllegalArgumentException(
                    "Cannot convert Section (idRoot=-1) to DatiBase. Use ContentToSectionMapper instead."
            );
        }

        DatiBase datiBase = new DatiBase();

        // ========== IDENTIFICATORI ==========
        datiBase.setId(content.getId() != null ? content.getId().toString() : "");
        datiBase.setIdRoot(content.getIdRoot() != null ? content.getIdRoot().toString() : "");
        datiBase.setIdType(content.getIdType() != null ? content.getIdType().toString() : "");
        datiBase.setIdParent(content.getIdParent() != null ? content.getIdParent() : "");
        datiBase.setIdSite(content.getIdSite() != null ? content.getIdSite() : "");
        datiBase.setLabel(content.getLabel() != null ? content.getLabel() : "");

        // ========== CONTENUTO PRINCIPALE ==========
        datiBase.setTitolo(content.getTitolo() != null ? content.getTitolo() : "");
        datiBase.setRiassunto(content.getRiassunto() != null ? content.getRiassunto() : "");
        datiBase.setTesto(content.getTesto() != null ? content.getTesto() : "");
        datiBase.setTitoloEN(content.getTitoloEN() != null ? content.getTitoloEN() : "");
        datiBase.setRiassuntoEN(content.getRiassuntoEN() != null ? content.getRiassuntoEN() : "");
        datiBase.setTestoEN(content.getTestoEN() != null ? content.getTestoEN() : "");

        // ========== DATE ==========
        datiBase.setData(content.getData() != null ? content.getData() : "");
        datiBase.setDataVisualizzata(content.getDataVisualizzata() != null ? content.getDataVisualizzata() : "");
        datiBase.setDataSql(content.getDatasql()); // ← Mapping corretto
        datiBase.setAnno(content.getAnno() != null ? content.getAnno() : 2012);
        datiBase.setMese(content.getMese() != null ? content.getMese() : "");
        datiBase.setCreato(content.getCreato() != null ? content.getCreato() : "");
        datiBase.setCreatoDa(content.getCreatoda() != null ? content.getCreatoda() : "");
        datiBase.setModificato(content.getModificato() != null ? content.getModificato() : "");
        datiBase.setModificatoDa(content.getModificatoda() != null ? content.getModificatoda() : "");
        datiBase.setApertoDa(content.getApertoda() != null ? content.getApertoda() : "");

        // ========== STATO E VISIBILITÀ ==========
        datiBase.setStato(content.getStato() != null ? content.getStato() : "");
        datiBase.setPrivato(content.getPrivato() != null ? content.getPrivato() : "");
        datiBase.setIdGruppo(content.getIdGruppo() != null ? content.getIdGruppo() : "0");
        datiBase.setPosition(content.getPosition() != null ? content.getPosition().toString() : "");
        datiBase.setVis(content.getVisualizza() != null ? content.getVisualizza() : "");

        // ========== TAG E RATING ==========
        datiBase.setTag(content.getTag() != null ? content.getTag() : "");
        datiBase.setRating(content.getRating() != null ? content.getRating() : 0);

        // ========== MEDIA ==========
        datiBase.setGalleryString(content.getGallery() != null ? content.getGallery() : "");

        // ========== CLICK ==========
        datiBase.setClick(content.getClick() != null ? content.getClick() : 0);

        // ========== LIVELLI (l1-l15) ==========
        datiBase.setL1(content.getL1() != null ? content.getL1() : "");
        datiBase.setL2(content.getL2() != null ? content.getL2() : "");
        datiBase.setL3(content.getL3() != null ? content.getL3() : "");
        datiBase.setL4(content.getL4() != null ? content.getL4() : "");
        datiBase.setL5(content.getL5() != null ? content.getL5() : "");
        datiBase.setL6(content.getL6() != null ? content.getL6() : "");
        datiBase.setL7(content.getL7() != null ? content.getL7() : "");
        datiBase.setL8(content.getL8() != null ? content.getL8() : "");
        datiBase.setL9(content.getL9() != null ? content.getL9() : "");
        datiBase.setL10(content.getL10() != null ? content.getL10() : "");
        datiBase.setL11(content.getL11() != null ? content.getL11() : "");
        datiBase.setL12(content.getL12() != null ? content.getL12() : "");
        datiBase.setL13(content.getL13() != null ? content.getL13() : "");
        datiBase.setL14(content.getL14() != null ? content.getL14() : "");
        datiBase.setL15(content.getL15() != null ? content.getL15() : "");

        // ========== INFO (info1-info5) ==========
        datiBase.setInfo1(content.getInfo1() != null ? content.getInfo1() : "");
        datiBase.setInfo2(content.getInfo2() != null ? content.getInfo2() : "");
        datiBase.setInfo3(content.getInfo3() != null ? content.getInfo3() : "");
        datiBase.setInfo4(content.getInfo4() != null ? content.getInfo4() : "");
        datiBase.setInfo5(content.getInfo5() != null ? content.getInfo5() : "");

        // ========== CAMPI S (s1-s10) ==========
        datiBase.setS1(content.getS1() != null ? content.getS1() : "");
        datiBase.setS2(content.getS2() != null ? content.getS2() : "");
        datiBase.setS3(content.getS3() != null ? content.getS3() : "");
        datiBase.setS4(content.getS4() != null ? content.getS4() : "");
        datiBase.setS5(content.getS5() != null ? content.getS5() : "");
        datiBase.setS6(content.getS6() != null ? content.getS6() : "");
        datiBase.setS7(content.getS7() != null ? content.getS7() : "");
        datiBase.setS8(content.getS8() != null ? content.getS8() : "");
        datiBase.setS9(content.getS9() != null ? content.getS9() : "");
        datiBase.setS10(content.getS10() != null ? content.getS10() : "");

        // ========== NEWSLETTER (newsletter1-newsletter5) ==========
        datiBase.setNewsletter1(content.getNewsletter1() != null ? content.getNewsletter1() : "0");
        datiBase.setNewsletter2(content.getNewsletter2() != null ? content.getNewsletter2() : "0");
        datiBase.setNewsletter3(content.getNewsletter3() != null ? content.getNewsletter3() : "0");
        datiBase.setNewsletter4(content.getNewsletter4() != null ? content.getNewsletter4() : "0");
        datiBase.setNewsletter5(content.getNewsletter5() != null ? content.getNewsletter5() : "0");

        // ========== SMS (sms1-sms5) ==========
        datiBase.setSms1(content.getSms1() != null ? content.getSms1() : "");
        datiBase.setSms2(content.getSms2() != null ? content.getSms2() : "");
        datiBase.setSms3(content.getSms3() != null ? content.getSms3() : "");
        datiBase.setSms4(content.getSms4() != null ? content.getSms4() : "");
        datiBase.setSms5(content.getSms5() != null ? content.getSms5() : "");

        // ========== ARRAY (array1-array5) ==========
        datiBase.setArray1(DatiBase.stringToArray(content.getArray1(), ";"));
        datiBase.setArray2(DatiBase.stringToArray(content.getArray2(), ";"));
        datiBase.setArray3(DatiBase.stringToArray(content.getArray3(), ";"));
        datiBase.setArray4(DatiBase.stringToArray(content.getArray4(), ";"));
        datiBase.setArray5(DatiBase.stringToArray(content.getArray5(), ";"));

        // ========== TEXT (text1-text10) ==========
        datiBase.setText1(content.getText1() != null ? content.getText1() : "");
        datiBase.setText2(content.getText2() != null ? content.getText2() : "");
        datiBase.setText3(content.getText3() != null ? content.getText3() : "");
        datiBase.setText4(content.getText4() != null ? content.getText4() : "");
        datiBase.setText5(content.getText5() != null ? content.getText5() : "");
        datiBase.setText6(content.getText6() != null ? content.getText6() : "");
        datiBase.setText7(content.getText7() != null ? content.getText7() : "");
        datiBase.setText8(content.getText8() != null ? content.getText8() : "");
        datiBase.setText9(content.getText9() != null ? content.getText9() : "");
        datiBase.setText10(content.getText10() != null ? content.getText10() : "");

        // ========== DATE CUSTOM (data1-data10) ==========
        datiBase.setData1(content.getData1());
        datiBase.setData2(content.getData2());
        datiBase.setData3(content.getData3());
        datiBase.setData4(content.getData4());
        datiBase.setData5(content.getData5());
        datiBase.setData6(content.getData6());
        datiBase.setData7(content.getData7());
        datiBase.setData8(content.getData8());
        datiBase.setData9(content.getData9());
        datiBase.setData10(content.getData10());

        // ========== VARCHAR (varchar1-varchar10) ==========
        datiBase.setVarchar1(content.getVarchar1() != null ? content.getVarchar1() : "");
        datiBase.setVarchar2(content.getVarchar2() != null ? content.getVarchar2() : "");
        datiBase.setVarchar3(content.getVarchar3() != null ? content.getVarchar3() : "");
        datiBase.setVarchar4(content.getVarchar4() != null ? content.getVarchar4() : "");
        datiBase.setVarchar5(content.getVarchar5() != null ? content.getVarchar5() : "");
        datiBase.setVarchar6(content.getVarchar6() != null ? content.getVarchar6() : "");
        datiBase.setVarchar7(content.getVarchar7() != null ? content.getVarchar7() : "");
        datiBase.setVarchar8(content.getVarchar8() != null ? content.getVarchar8() : "");
        datiBase.setVarchar9(content.getVarchar9() != null ? content.getVarchar9() : "");
        datiBase.setVarchar10(content.getVarchar10() != null ? content.getVarchar10() : "");

        // ========== NUMBER (number1-number10) ==========
        datiBase.setNumber1(content.getNumber1() != null ? content.getNumber1() : 0.0);
        datiBase.setNumber2(content.getNumber2() != null ? content.getNumber2() : 0.0);
        datiBase.setNumber3(content.getNumber3() != null ? content.getNumber3() : 0.0);
        datiBase.setNumber4(content.getNumber4() != null ? content.getNumber4() : 0.0);
        datiBase.setNumber5(content.getNumber5() != null ? content.getNumber5() : 0.0);
        datiBase.setNumber6(content.getNumber6() != null ? content.getNumber6() : 0.0);
        datiBase.setNumber7(content.getNumber7() != null ? content.getNumber7() : 0.0);
        datiBase.setNumber8(content.getNumber8() != null ? content.getNumber8() : 0.0);
        datiBase.setNumber9(content.getNumber9() != null ? content.getNumber9() : 0.0);
        datiBase.setNumber10(content.getNumber10() != null ? content.getNumber10() : 0.0);

        // ========== LOG (log1-log3) ==========
        datiBase.setLog1(content.getLog1() != null ? content.getLog1() : "");
        datiBase.setLog2(content.getLog2() != null ? content.getLog2() : "");
        datiBase.setLog3(content.getLog3() != null ? content.getLog3() : "");

        // ========== NUMERATORI (numeratore1-numeratore5) ==========
        datiBase.setNumeratore1(content.getNumeratore1() != null ? (long)content.getNumeratore1() : 0L);
        datiBase.setNumeratore2(content.getNumeratore2() != null ? (long)content.getNumeratore2() : 0L);
        datiBase.setNumeratore3(content.getNumeratore3() != null ? (long)content.getNumeratore3() : 0L);
        datiBase.setNumeratore4(content.getNumeratore4() != null ? (long)content.getNumeratore4() : 0L);
        datiBase.setNumeratore5(content.getNumeratore5() != null ? (long)content.getNumeratore5() : 0L);

        // ========== EXTRA TAG (extratag1-extratag10) ==========
        datiBase.setExtraTag1(content.getExtratag1() != null ? content.getExtratag1() : "");
        datiBase.setExtraTag2(content.getExtratag2() != null ? content.getExtratag2() : "");
        datiBase.setExtraTag3(content.getExtratag3() != null ? content.getExtratag3() : "");
        datiBase.setExtraTag4(content.getExtratag4() != null ? content.getExtratag4() : "");
        datiBase.setExtraTag5(content.getExtratag5() != null ? content.getExtratag5() : "");
        datiBase.setExtraTag6(content.getExtratag6() != null ? content.getExtratag6() : "");
        datiBase.setExtraTag7(content.getExtratag7() != null ? content.getExtratag7() : "");
        datiBase.setExtraTag8(content.getExtratag8() != null ? content.getExtratag8() : "");
        datiBase.setExtraTag9(content.getExtratag9() != null ? content.getExtratag9() : "");
        datiBase.setExtraTag10(content.getExtratag10() != null ? content.getExtratag10() : "");

        // ========== EXTRA TAG REF (extratagref1-extratagref10) ==========
        datiBase.setExtraTagRef1(content.getExtratagref1() != null ? content.getExtratagref1() : "");
        datiBase.setExtraTagRef2(content.getExtratagref2() != null ? content.getExtratagref2() : "");
        datiBase.setExtraTagRef3(content.getExtratagref3() != null ? content.getExtratagref3() : "");
        datiBase.setExtraTagRef4(content.getExtratagref4() != null ? content.getExtratagref4() : "");
        datiBase.setExtraTagRef5(content.getExtratagref5() != null ? content.getExtratagref5() : "");
        datiBase.setExtraTagRef6(content.getExtratagref6() != null ? content.getExtratagref6() : "");
        datiBase.setExtraTagRef7(content.getExtratagref7() != null ? content.getExtratagref7() : "");
        datiBase.setExtraTagRef8(content.getExtratagref8() != null ? content.getExtratagref8() : "");
        datiBase.setExtraTagRef9(content.getExtratagref9() != null ? content.getExtratagref9() : "");
        datiBase.setExtraTagRef10(content.getExtratagref10() != null ? content.getExtratagref10() : "");

        datiBase.setOrdineExtraTag(content.getOrdineextratag() != null ? content.getOrdineextratag() : "rand()");
        datiBase.setMaxExtraTag(content.getMaxextratag() != null ? content.getMaxextratag() : "5");
        datiBase.setRegolaExtraTag1(content.getRegolaextratag1() != null ? content.getRegolaextratag1() : "0");
        datiBase.setRegolaExtraTag2(content.getRegolaextratag2() != null ? content.getRegolaextratag2() : "0");

        // ========== RELAZIONI (da popolare nel service) ==========
        datiBase.setAllegati(new ArrayList<>());
        datiBase.setGallery(new ArrayList<>());
        datiBase.setCommenti(new ArrayList<>());
        datiBase.setRatings(new ArrayList<>());
        datiBase.setDocCorrelati1(new ArrayList<>());
        datiBase.setDocCorrelati2(new ArrayList<>());
        datiBase.setDocCorrelati3(new ArrayList<>());
        datiBase.setDocCorrelati4(new ArrayList<>());
        datiBase.setDocCorrelati5(new ArrayList<>());
        datiBase.setRelazioneSezioni(new ArrayList<>());

        // ========== i18n ==========
        datiBase.setLocale("it_IT"); // Default hardcoded

        return datiBase;
    }

    /**
     * Converte una lista di Content in lista di DatiBase
     */
    public List<DatiBase> toDatiBaseList(List<Content> contents) {
        if (contents == null) {
            return new ArrayList<>();
        }

        return contents.stream()
                .filter(Content::isContent)  // Filtra solo i contenuti (non sezioni)
                .map(this::toDatiBase)
                .collect(Collectors.toList());
    }

    /**
     * Converte DatiBase DTO in Content entity (per salvataggio)
     */
    public Content toContent(DatiBase datiBase) {
        if (datiBase == null) {
            return null;
        }

        Content content = new Content();

        // ========== IDENTIFICATORI ==========
        if (datiBase.getId() != null && !datiBase.getId().isEmpty()) {
            content.setId(Integer.parseInt(datiBase.getId()));
        }
        if (datiBase.getIdRoot() != null && !datiBase.getIdRoot().isEmpty()) {
            content.setIdRoot(Integer.parseInt(datiBase.getIdRoot()));
        }
        if (datiBase.getIdType() != null && !datiBase.getIdType().isEmpty()) {
            content.setIdType(Integer.parseInt(datiBase.getIdType()));
        }
        content.setIdParent(datiBase.getIdParent());
        content.setIdSite(datiBase.getIdSite());
        content.setLabel(datiBase.getLabel());

        // ========== CONTENUTO PRINCIPALE ==========
        content.setTitolo(datiBase.getTitolo());
        content.setRiassunto(datiBase.getRiassunto());
        content.setTesto(datiBase.getTesto());
        content.setTitoloEN(datiBase.getTitoloEN());
        content.setRiassuntoEN(datiBase.getRiassuntoEN());
        content.setTestoEN(datiBase.getTestoEN());

        // ========== DATE ==========
        content.setData(datiBase.getData());
        content.setDataVisualizzata(datiBase.getDataVisualizzata());
        content.setDatasql(datiBase.getDataSql());
        content.setAnno(datiBase.getAnno());
        content.setMese(datiBase.getMese());
        content.setCreato(datiBase.getCreato());
        content.setCreatoda(datiBase.getCreatoDa());
        content.setModificato(datiBase.getModificato());
        content.setModificatoda(datiBase.getModificatoDa());
        content.setApertoda(datiBase.getApertoDa());

        // ========== STATO E VISIBILITÀ ==========
        content.setStato(datiBase.getStato());
        content.setPrivato(datiBase.getPrivato());
        content.setIdGruppo(datiBase.getIdGruppo());
        if (datiBase.getPosition() != null && !datiBase.getPosition().isEmpty()) {
            content.setPosition(Integer.parseInt(datiBase.getPosition()));
        }
        content.setVisualizza(datiBase.getVis());

        // ========== TAG E RATING ==========
        content.setTag(datiBase.getTag());
        content.setRating(datiBase.getRating());

        // ========== MEDIA ==========
        content.setGallery(datiBase.getGalleryString());

        // ========== CLICK ==========
        content.setClick(datiBase.getClick());

        // ========== LIVELLI ==========
        content.setL1(datiBase.getL1());
        content.setL2(datiBase.getL2());
        content.setL3(datiBase.getL3());
        content.setL4(datiBase.getL4());
        content.setL5(datiBase.getL5());
        content.setL6(datiBase.getL6());
        content.setL7(datiBase.getL7());
        content.setL8(datiBase.getL8());
        content.setL9(datiBase.getL9());
        content.setL10(datiBase.getL10());
        content.setL11(datiBase.getL11());
        content.setL12(datiBase.getL12());
        content.setL13(datiBase.getL13());
        content.setL14(datiBase.getL14());
        content.setL15(datiBase.getL15());

        // ========== INFO ==========
        content.setInfo1(datiBase.getInfo1());
        content.setInfo2(datiBase.getInfo2());
        content.setInfo3(datiBase.getInfo3());
        content.setInfo4(datiBase.getInfo4());
        content.setInfo5(datiBase.getInfo5());

        // ========== S ==========
        content.setS1(datiBase.getS1());
        content.setS2(datiBase.getS2());
        content.setS3(datiBase.getS3());
        content.setS4(datiBase.getS4());
        content.setS5(datiBase.getS5());
        content.setS6(datiBase.getS6());
        content.setS7(datiBase.getS7());
        content.setS8(datiBase.getS8());
        content.setS9(datiBase.getS9());
        content.setS10(datiBase.getS10());

        // ========== NEWSLETTER ==========
        content.setNewsletter1(datiBase.getNewsletter1());
        content.setNewsletter2(datiBase.getNewsletter2());
        content.setNewsletter3(datiBase.getNewsletter3());
        content.setNewsletter4(datiBase.getNewsletter4());
        content.setNewsletter5(datiBase.getNewsletter5());

        // ========== SMS ==========
        content.setSms1(datiBase.getSms1());
        content.setSms2(datiBase.getSms2());
        content.setSms3(datiBase.getSms3());
        content.setSms4(datiBase.getSms4());
        content.setSms5(datiBase.getSms5());

        // ========== ARRAY (converti da array a stringa) ==========
        content.setArray1(datiBase.getArray1() != null ? String.join(";", datiBase.getArray1()) : "");
        content.setArray2(datiBase.getArray2() != null ? String.join(";", datiBase.getArray2()) : "");
        content.setArray3(datiBase.getArray3() != null ? String.join(";", datiBase.getArray3()) : "");
        content.setArray4(datiBase.getArray4() != null ? String.join(";", datiBase.getArray4()) : "");
        content.setArray5(datiBase.getArray5() != null ? String.join(";", datiBase.getArray5()) : "");

        // ========== TEXT ==========
        content.setText1(datiBase.getText1());
        content.setText2(datiBase.getText2());
        content.setText3(datiBase.getText3());
        content.setText4(datiBase.getText4());
        content.setText5(datiBase.getText5());
        content.setText6(datiBase.getText6());
        content.setText7(datiBase.getText7());
        content.setText8(datiBase.getText8());
        content.setText9(datiBase.getText9());
        content.setText10(datiBase.getText10());

        // ========== DATA ==========
        content.setData1(datiBase.getData1());
        content.setData2(datiBase.getData2());
        content.setData3(datiBase.getData3());
        content.setData4(datiBase.getData4());
        content.setData5(datiBase.getData5());
        content.setData6(datiBase.getData6());
        content.setData7(datiBase.getData7());
        content.setData8(datiBase.getData8());
        content.setData9(datiBase.getData9());
        content.setData10(datiBase.getData10());

        // ========== VARCHAR ==========
        content.setVarchar1(datiBase.getVarchar1());
        content.setVarchar2(datiBase.getVarchar2());
        content.setVarchar3(datiBase.getVarchar3());
        content.setVarchar4(datiBase.getVarchar4());
        content.setVarchar5(datiBase.getVarchar5());
        content.setVarchar6(datiBase.getVarchar6());
        content.setVarchar7(datiBase.getVarchar7());
        content.setVarchar8(datiBase.getVarchar8());
        content.setVarchar9(datiBase.getVarchar9());
        content.setVarchar10(datiBase.getVarchar10());

        // ========== NUMBER ==========
        content.setNumber1(datiBase.getNumber1());
        content.setNumber2(datiBase.getNumber2());
        content.setNumber3(datiBase.getNumber3());
        content.setNumber4(datiBase.getNumber4());
        content.setNumber5(datiBase.getNumber5());
        content.setNumber6(datiBase.getNumber6());
        content.setNumber7(datiBase.getNumber7());
        content.setNumber8(datiBase.getNumber8());
        content.setNumber9(datiBase.getNumber9());
        content.setNumber10(datiBase.getNumber10());

        // ========== LOG ==========
        content.setLog1(datiBase.getLog1());
        content.setLog2(datiBase.getLog2());
        content.setLog3(datiBase.getLog3());

        // ========== NUMERATORI ==========
        content.setNumeratore1(datiBase.getNumeratore1Long() != null ? datiBase.getNumeratore1Long().intValue() : 0);
        content.setNumeratore2(datiBase.getNumeratore2Long() != null ? datiBase.getNumeratore2Long().intValue() : 0);
        content.setNumeratore3(datiBase.getNumeratore3Long() != null ? datiBase.getNumeratore3Long().intValue() : 0);
        content.setNumeratore4(datiBase.getNumeratore4Long() != null ? datiBase.getNumeratore4Long().intValue() : 0);
        content.setNumeratore5(datiBase.getNumeratore5Long() != null ? datiBase.getNumeratore5Long().intValue() : 0);

        // ========== EXTRA TAG ==========
        content.setExtratag1(datiBase.getExtraTag1());
        content.setExtratag2(datiBase.getExtraTag2());
        content.setExtratag3(datiBase.getExtraTag3());
        content.setExtratag4(datiBase.getExtraTag4());
        content.setExtratag5(datiBase.getExtraTag5());
        content.setExtratag6(datiBase.getExtraTag6());
        content.setExtratag7(datiBase.getExtraTag7());
        content.setExtratag8(datiBase.getExtraTag8());
        content.setExtratag9(datiBase.getExtraTag9());
        content.setExtratag10(datiBase.getExtraTag10());

        content.setExtratagref1(datiBase.getExtraTagRef1());
        content.setExtratagref2(datiBase.getExtraTagRef2());
        content.setExtratagref3(datiBase.getExtraTagRef3());
        content.setExtratagref4(datiBase.getExtraTagRef4());
        content.setExtratagref5(datiBase.getExtraTagRef5());
        content.setExtratagref6(datiBase.getExtraTagRef6());
        content.setExtratagref7(datiBase.getExtraTagRef7());
        content.setExtratagref8(datiBase.getExtraTagRef8());
        content.setExtratagref9(datiBase.getExtraTagRef9());
        content.setExtratagref10(datiBase.getExtraTagRef10());

        content.setOrdineextratag(datiBase.getOrdineExtraTag());
        content.setMaxextratag(datiBase.getMaxExtraTag());
        content.setRegolaextratag1(datiBase.getRegolaExtraTag1());
        content.setRegolaextratag2(datiBase.getRegolaExtraTag2());

        // ========== i18n ==========
        datiBase.setLocale("it_IT"); // Default hardcoded

        return content;
    }
}
