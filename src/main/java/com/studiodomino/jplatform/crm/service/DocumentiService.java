package com.studiodomino.jplatform.crm.service;

import com.studiodomino.jplatform.cms.entity.Content;
import com.studiodomino.jplatform.cms.entity.DatiBase;
import com.studiodomino.jplatform.crm.repository.DocumentiRepository;
import com.studiodomino.jplatform.shared.config.Configurazione;
import com.studiodomino.jplatform.shared.entity.Site;
import com.studiodomino.jplatform.shared.repository.NotificaEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DocumentiService {

    private final DocumentiRepository documentiRepository;
    private final NotificaEmailService notificaEmailService;

    private static final SimpleDateFormat DF_DATETIME = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
    private static final SimpleDateFormat DF_ANNO     = new SimpleDateFormat("yyyy");
    private static final SimpleDateFormat DF_DATE     = new SimpleDateFormat("dd-MM-yyyy");

    static {
        TimeZone tz = TimeZone.getDefault();
        DF_DATETIME.setTimeZone(tz);
        DF_ANNO.setTimeZone(tz);
        DF_DATE.setTimeZone(tz);
    }

    // ─── NEW DTO ────────────────────────────────────────────────────────────
    public DatiBase buildNewDto(String idRoot, String idType) {
        Date dt = new Date();
        DatiBase dto = new DatiBase();

        dto.setId("-1");
        dto.setIdRoot(idRoot);
        dto.setIdType(idType);

        dto.setGalleryString("");
        dto.setGallery(new ArrayList<>());

        dto.setData(DF_DATE.format(dt));
        dto.setS1(DF_DATE.format(dt));
        dto.setS2(DF_DATE.format(dt));

        // Section NON caricabile via JPA (non è entity) -> lascio null
        dto.setSection(null);

        return dto;
    }

    // ─── LOAD DTO ───────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public DatiBase loadDtoByIdOrThrow(String id) {
        Integer iid = Integer.valueOf(id);
        Content c = documentiRepository.findById(iid).orElseThrow();
        return toDto(c);
    }

    // ─── SAVE ───────────────────────────────────────────────────────────────
    @Transactional
    public DatiBase saveFromDto(DatiBase form, Configurazione config) {
        Date now = new Date();
        boolean nuovo = (form.getId() == null || form.getId().isBlank() || "-1".equals(form.getId()));

        Content entity;
        if (nuovo) {
            entity = new Content();
            // se Content ha creato/modificato come string, ok; se non li ha, rimuovi
            try { entity.setCreato(DF_DATETIME.format(now)); } catch (Exception ignored) {}
        } else {
            entity = documentiRepository.findById(Integer.valueOf(form.getId())).orElseThrow();
        }

        Integer idRootInt = safeInt(form.getIdRoot());
        Integer idTypeInt = safeInt(form.getIdType());
        if (idRootInt != null) entity.setIdRoot(idRootInt);
        if (idTypeInt != null) entity.setIdType(idTypeInt);

        entity.setTitolo(form.getTitolo());
        entity.setRiassunto(form.getRiassunto());
        entity.setTesto(form.getTesto());
        entity.setStato(form.getStato());
        entity.setData(form.getData());
        entity.setDataVisualizzata(form.getDataVisualizzata());
        entity.setTag(form.getTag());

        try { entity.setModificato(DF_DATETIME.format(now)); } catch (Exception ignored) {}

        Content saved = documentiRepository.save(entity);

        Site site = (Site) config.getSito();
        notificaEmailService.notificaModificaContenuto(
                site,
                safeString(saved.getTitolo()),
                buildUrl(saved),
                DF_DATETIME.format(now)
        );

        return toDto(saved);
    }

    // ─── ORDINA ─────────────────────────────────────────────────────────────
    @Transactional
    public void normalizzaPosition(String idSezione, String contstato) {
        Integer idRoot = Integer.valueOf(idSezione);

        List<Content> elenco = (contstato != null && !contstato.isBlank())
                ? documentiRepository.findByIdRootAndStatoOrderByPositionAsc(idRoot, contstato)
                : documentiRepository.findByIdRootOrderByPositionAsc(idRoot);

        int i = 1;
        for (Content c : elenco) {
            documentiRepository.updatePosition(c.getId(), i++);
        }
    }

    @Transactional(readOnly = true)
    public List<DatiBase> elencoOrdinaDto(String idSezione, String contstato) {
        Integer idRoot = Integer.valueOf(idSezione);

        List<Content> elenco = (contstato != null && !contstato.isBlank())
                ? documentiRepository.findByIdRootAndStatoOrderByPositionAsc(idRoot, contstato)
                : documentiRepository.findByIdRootOrderByPositionAsc(idRoot);

        List<DatiBase> out = new ArrayList<>();
        for (Content c : elenco) out.add(toDto(c));
        return out;
    }

    // ─── MAPPER ─────────────────────────────────────────────────────────────
    private DatiBase toDto(Content c) {
        DatiBase dto = new DatiBase();

        dto.setId(String.valueOf(c.getId()));
        dto.setIdRoot(c.getIdRoot() != null ? String.valueOf(c.getIdRoot()) : null);
        dto.setIdType(c.getIdType() != null ? String.valueOf(c.getIdType()) : null);

        dto.setTitolo(c.getTitolo());
        dto.setRiassunto(c.getRiassunto());
        dto.setTesto(c.getTesto());
        dto.setStato(c.getStato());
        dto.setPosition(c.getPosition() != null ? String.valueOf(c.getPosition()) : null);

        dto.setData(c.getData());
        dto.setDataVisualizzata(c.getDataVisualizzata());
        dto.setTag(c.getTag());

        try { dto.setCreato(c.getCreato()); } catch (Exception ignored) {}
        try { dto.setModificato(c.getModificato()); } catch (Exception ignored) {}

        dto.setNewsletter3(c.getNewsletter3());

        // Section non gestita via JPA
        dto.setSection(null);

        calcolaAnnoMeseDto(dto);
        return dto;
    }

    private void calcolaAnnoMeseDto(DatiBase doc) {
        Date dt = new Date();
        if (doc.getDataVisualizzata() == null || doc.getDataVisualizzata().isEmpty()) {
            doc.setAnno(Integer.valueOf(DF_ANNO.format(dt)));
            doc.setMese("gennaio");
            return;
        }
        int comma = doc.getDataVisualizzata().indexOf(',');
        if (comma < 0) return;
        doc.setAnno(Integer.valueOf(doc.getDataVisualizzata().substring(comma + 2).trim()));
        doc.setMese(doc.getDataVisualizzata().substring(3, comma).trim().toLowerCase());
    }

    private String buildUrl(Content c) {
        String titolo = c.getTitolo() == null ? "documento" : c.getTitolo();
        String label = titolo.replace(" ", "_")
                .replace("/", "_")
                .replace("\\", "_")
                .replace("\"", "_")
                .replace("'", "_");
        return "/front/" + c.getId() + "/" + label;
    }

    private Integer safeInt(String s) {
        try {
            if (s == null) return null;
            String t = s.trim();
            if (t.isEmpty()) return null;
            return Integer.valueOf(t);
        } catch (Exception e) {
            return null;
        }
    }

    private String safeString(String s) {
        return (s == null) ? "" : s;
    }
}