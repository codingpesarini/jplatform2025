package com.studiodomino.jplatform.cms.service;

import com.studiodomino.jplatform.cms.entity.Content;
import com.studiodomino.jplatform.cms.entity.DatiBase;
import com.studiodomino.jplatform.cms.entity.Section;
import com.studiodomino.jplatform.cms.entity.SectionType;
import com.studiodomino.jplatform.cms.front.dto.Tag;
import com.studiodomino.jplatform.cms.mapper.ContentToDatiBaseMapper;
import com.studiodomino.jplatform.cms.mapper.ContentToSectionMapper;
import com.studiodomino.jplatform.cms.repository.ContentRepository;
import com.studiodomino.jplatform.cms.repository.SectionTypeRepository;
import com.studiodomino.jplatform.shared.entity.UtenteEsterno;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service per la gestione dei contenuti e delle sezioni del CMS.
 *
 * Questo service funge da layer intermedio tra i controller e il repository,
 * gestendo la conversione tra entity (Content) e DTO (Section/DatiBase)
 * e implementando la business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ContentService {

    private final ContentRepository contentRepository;
    private final ContentToSectionMapper sectionMapper;
    private final ContentToDatiBaseMapper datiBaseMapper;
    private final SectionTypeRepository sectionTypeRepository;

    // ========================================
    // METODI PER SEZIONI
    // ========================================

    /**
     * Trova tutte le sezioni di un sito
     */
    public List<Section> findAllSections(String idSite) {
        log.debug("Finding all sections for site: {}", idSite);
        List<Content> contents = contentRepository.findSectionsBySite(idSite);
        return sectionMapper.toSectionList(contents);
    }

    /**
     * Trova le sezioni radice (primo livello) di un sito
     */
    public List<Section> findRootSections(String idSite) {
        log.debug("Finding root sections for site: {}", idSite);
        List<Content> contents = contentRepository.findRootSectionsBySite(idSite);
        return sectionMapper.toSectionList(contents);
    }

    /**
     * Trova le sotto-sezioni di una sezione parent
     */
    public List<Section> findSubsections(String idSite, String idParent) {
        log.debug("Finding subsections for site: {} and parent: {}", idSite, idParent);
        List<Content> contents = contentRepository.findSubsectionsByParent(idSite, idParent);
        return sectionMapper.toSectionList(contents);
    }

    /**
     * Trova una sezione per ID
     */
    public Optional<Section> findSectionById(Integer id, String idSite) {
        log.debug("Finding section by id: {} and site: {}", id, idSite);
        return contentRepository.findByIdAndSite(id, idSite)
                .filter(Content::isSection)
                .map(sectionMapper::toSection);
    }

    /**
     * Trova una sezione per ID (senza filtro sito)
     */
    public Optional<Section> findSectionById(Integer id) {
        log.debug("Finding section by id: {}", id);
        return contentRepository.findById(id)
                .filter(Content::isSection)
                .map(sectionMapper::toSection);
    }

    /**
     * Trova una sezione per label
     */
    public Optional<Section> findSectionByLabel(String idSite, String label) {
        log.debug("Finding section by label: {} for site: {}", label, idSite);
        return contentRepository.findSectionByLabel(idSite, label)
                .map(sectionMapper::toSection);
    }

    /**
     * Trova sezioni per tipo
     */
    public List<Section> findSectionsByType(String idSite, Integer idType) {
        log.debug("Finding sections by type: {} for site: {}", idType, idSite);
        List<Content> contents = contentRepository.findSectionsByType(idSite, idType);
        return sectionMapper.toSectionList(contents);
    }

    /**
     * Trova una sezione completa con tutte le relazioni caricate
     * (subsections, contenuti, parent chain)
     */
    public Optional<Section> findSectionComplete(Integer id, String idSite) {
        log.debug("Finding complete section by id: {} and site: {}", id, idSite);

        Optional<Section> sectionOpt = findSectionById(id, idSite);
        if (sectionOpt.isEmpty()) {
            return Optional.empty();
        }

        Section section = sectionOpt.get();

        // Carica sotto-sezioni
        section.setSubsection(findSubsections(idSite, id.toString()));

        // Carica contenuti della sezione
        section.setContenuti(findContentsBySection(idSite, id));

        // Carica catena dei parent (per breadcrumb)
        section.setParentSection(buildParentChain(section, idSite));

        return Optional.of(section);
    }

    /**
     * Costruisce la catena dei parent per il breadcrumb
     */
    private List<Section> buildParentChain(Section section, String idSite) {
        List<Section> chain = new ArrayList<>();
        String currentParentId = section.getIdParent();

        while (currentParentId != null && !currentParentId.isEmpty()
                && !"0".equals(currentParentId)) {
            try {
                Integer parentId = Integer.parseInt(currentParentId);
                Optional<Section> parentOpt = findSectionById(parentId, idSite);

                if (parentOpt.isPresent()) {
                    Section parent = parentOpt.get();
                    chain.add(0, parent); // Inserisci all'inizio
                    currentParentId = parent.getIdParent();
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                log.warn("Invalid parent ID format: {}", currentParentId);
                break;
            }
        }

        return chain;
    }

    /**
     * Salva o aggiorna una sezione
     */
    @Transactional
    public Section saveSection(Section section) {
        log.debug("Saving section: {}", section.getId());

        Content content = sectionMapper.toContent(section);
        Content saved = contentRepository.save(content);

        return sectionMapper.toSection(saved);
    }

    /**
     * Elimina una sezione
     */
    @Transactional
    public void deleteSection(Integer id) {
        log.debug("Deleting section: {}", id);
        contentRepository.deleteById(id);
    }

    // ========================================
    // METODI PER CONTENUTI
    // ========================================

    /**
     * Trova tutti i contenuti di una sezione
     */
    public List<DatiBase> findContentsBySection(String idSite, Integer idRoot) {
        log.debug("Finding contents for section: {} and site: {}", idRoot, idSite);
        List<Content> contents = contentRepository.findContentsBySection(idSite, idRoot);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    /**
     * Trova contenuti di una sezione con filtri custom
     */
    public List<DatiBase> findContentsBySection(
            Integer idSito,
            Integer idRoot,
            String whereCondition,
            String orderBy,
            Integer limit) {

        log.debug("Finding contents for section {} with custom filters", idRoot);

        // Per ora usa il metodo base e applica filtri in memoria
        // TODO: Creare query custom nel repository per performance migliori
        List<Content> contents = contentRepository.findPublishedContentsBySection(
                idSito.toString(),
                idRoot
        );

        // Applica ordinamento
        if (orderBy != null && !orderBy.isEmpty()) {
            contents = sortContents(contents, orderBy);
        }

        // Applica limit
        if (limit != null && limit > 0) {
            contents = contents.stream()
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        return datiBaseMapper.toDatiBaseList(contents);
    }

    /**
     * Trova contenuti pubblicati di una sezione
     */
    public List<DatiBase> findPublishedContentsBySection(String idSite, Integer idRoot) {
        log.debug("Finding published contents for section: {} and site: {}", idRoot, idSite);
        List<Content> contents = contentRepository.findPublishedContentsBySection(idSite, idRoot);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    /**
     * Trova un contenuto per ID
     */
    public Optional<DatiBase> findContentById(Integer id, String idSite) {
        log.debug("Finding content by id: {} and site: {}", id, idSite);
        return contentRepository.findByIdAndSite(id, idSite)
                .filter(Content::isContent)
                .map(datiBaseMapper::toDatiBase);
    }

    /**
     * Trova un DatiBase per ID (senza filtro sito)
     * Alias di findContentById per compatibilità
     */
    public Optional<DatiBase> findDatiBaseById(Integer id) {
        log.debug("Finding DatiBase by id: {}", id);
        return contentRepository.findById(id)
                .filter(Content::isContent)
                .map(datiBaseMapper::toDatiBase);
    }

    /**
     * Trova un contenuto per label in una sezione
     */
    public Optional<DatiBase> findContentByLabel(String idSite, Integer idRoot, String label) {
        log.debug("Finding content by label: {} in section: {} for site: {}",
                label, idRoot, idSite);
        return contentRepository.findContentByLabel(idSite, idRoot, label)
                .map(datiBaseMapper::toDatiBase);
    }

    /**
     * Trova contenuti per stato
     */
    public List<DatiBase> findContentsByStato(String idSite, String stato) {
        log.debug("Finding contents by stato: {} for site: {}", stato, idSite);
        List<Content> contents = contentRepository.findByStato(idSite, stato);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    /**
     * Trova contenuti pubblici
     */
    public List<DatiBase> findPublicContents(String idSite) {
        log.debug("Finding public contents for site: {}", idSite);
        List<Content> contents = contentRepository.findPublicContents(idSite);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    /**
     * Trova contenuti privati per gruppo
     */
    public List<DatiBase> findPrivateContentsByGroup(String idSite, String idGruppo) {
        log.debug("Finding private contents for group: {} and site: {}", idGruppo, idSite);
        List<Content> contents = contentRepository.findPrivateContentsByGroup(idSite, idGruppo);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    /**
     * Salva o aggiorna un contenuto
     */
    @Transactional
    public DatiBase saveContent(DatiBase datiBase) {
        log.debug("Saving content: {}", datiBase.getId());

        Content content = datiBaseMapper.toContent(datiBase);
        Content saved = contentRepository.save(content);

        return datiBaseMapper.toDatiBase(saved);
    }

    /**
     * Elimina un contenuto
     */
    @Transactional
    public void deleteContent(Integer id) {
        log.debug("Deleting content: {}", id);
        contentRepository.deleteById(id);
    }

    // ========================================
    // METODI PER SCHEDULING (s1, s2, s3)
    // ========================================

    /**
     * Trova contenuti schedulati
     */
    public List<DatiBase> findScheduledContents(String idSite) {
        log.debug("Finding scheduled contents for site: {}", idSite);
        List<Content> contents = contentRepository.findScheduledContents(idSite);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    /**
     * Pubblica automaticamente i contenuti schedulati
     * (da chiamare con uno scheduled task)
     */
    @Transactional
    public int publishScheduledContents(String idSite) {
        log.info("Publishing scheduled contents for site: {}", idSite);

        LocalDate today = LocalDate.now();
        List<Content> toPublish = contentRepository.findContentsToPublish(idSite, today);

        int count = 0;
        for (Content content : toPublish) {
            content.setStato("1"); // Pubblica
            contentRepository.save(content);
            count++;
        }

        log.info("Published {} scheduled contents", count);
        return count;
    }

    /**
     * Nasconde automaticamente i contenuti scaduti
     * (da chiamare con uno scheduled task)
     */
    @Transactional
    public int hideExpiredContents(String idSite) {
        log.info("Hiding expired contents for site: {}", idSite);

        LocalDate today = LocalDate.now();
        List<Content> toHide = contentRepository.findContentsToHide(idSite, today);

        int count = 0;
        for (Content content : toHide) {
            content.setStato("0"); // Nascondi
            contentRepository.save(content);
            count++;
        }

        log.info("Hidden {} expired contents", count);
        return count;
    }

    // ========================================
    // METODI PER ARCHIVIO
    // ========================================

    /**
     * Trova contenuti per anno
     */
    public List<DatiBase> findContentsByYear(String idSite, Integer idRoot, Integer anno) {
        log.debug("Finding contents by year: {} for section: {} and site: {}",
                anno, idRoot, idSite);
        List<Content> contents = contentRepository.findContentsByYear(idSite, idRoot, anno);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    /**
     * Trova contenuti per anno e mese
     */
    public List<DatiBase> findContentsByYearMonth(String idSite, Integer idRoot,
                                                  Integer anno, String mese) {
        log.debug("Finding contents by year/month: {}/{} for section: {} and site: {}",
                anno, mese, idRoot, idSite);
        List<Content> contents = contentRepository.findContentsByYearMonth(
                idSite, idRoot, anno, mese);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    /**
     * Trova anni distinti per una sezione (per archivio)
     */
    public List<Integer> findDistinctYearsBySection(String idSite, Integer idRoot) {
        log.debug("Finding distinct years for section: {} and site: {}", idRoot, idSite);
        return contentRepository.findDistinctYearsBySection(idSite, idRoot);
    }

    // ========================================
    // METODI PER TAG
    // ========================================

    /**
     * Trova contenuti per tag
     */
    public List<DatiBase> findContentsByTag(String idSite, String tag) {
        log.debug("Finding contents by tag: {} for site: {}", tag, idSite);
        List<Content> contents = contentRepository.findContentsByTag(idSite, tag);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    /**
     * Trova contenuti per extra tag
     */
    public List<DatiBase> findContentsByExtraTag(String idSite, String extraTag) {
        log.debug("Finding contents by extraTag: {} for site: {}", extraTag, idSite);
        List<Content> contents = contentRepository.findContentsByExtraTag(idSite, extraTag);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    // ========================================
    // METODI PER FIRST PAGE
    // ========================================

    /**
     * Trova contenuti per first page
     */
    public List<DatiBase> findFirstPageContents(String idSite) {
        log.debug("Finding first page contents for site: {}", idSite);
        List<Content> contents = contentRepository.findFirstPageContents(idSite);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    // ========================================
    // METODI PER RICERCA
    // ========================================

    /**
     * Ricerca full text (LIKE-based, per ricerche semplici)
     */
    public List<DatiBase> searchFullText(String idSite, String searchTerm) {
        log.debug("Searching full text: {} for site: {}", searchTerm, idSite);
        List<Content> contents = contentRepository.searchFullText(idSite, searchTerm);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    // ========================================
    // METODI PER STATISTICHE
    // ========================================

    /**
     * Conta contenuti per sezione
     */
    public Long countContentsBySection(String idSite, Integer idRoot) {
        log.debug("Counting contents for section: {} and site: {}", idRoot, idSite);
        return contentRepository.countContentsBySection(idSite, idRoot);
    }

    /**
     * Conta contenuti pubblicati per sezione
     */
    public Long countPublishedContentsBySection(String idSite, Integer idRoot) {
        log.debug("Counting published contents for section: {} and site: {}", idRoot, idSite);
        return contentRepository.countPublishedContentsBySection(idSite, idRoot);
    }

    /**
     * Incrementa il click counter per un contenuto
     */
    @Transactional
    public void incrementClick(Integer id, String idSite) {
        log.debug("Incrementing click for content: {} and site: {}", id, idSite);

        contentRepository.findByIdAndSite(id, idSite).ifPresent(content -> {
            int currentClick = content.getClick() != null ? content.getClick() : 0;
            content.setClick(currentClick + 1);
            contentRepository.save(content);
        });
    }

    // ========================================
    // METODI UTILITY
    // ========================================

    /**
     * Trova tutti i contenuti di un sito (sia sezioni che contenuti)
     */
    public List<Content> findAllBySite(String idSite) {
        log.debug("Finding all contents (sections + items) for site: {}", idSite);
        return contentRepository.findAllBySite(idSite);
    }

    /**
     * Trova contenuti per tipo (sia sezioni che contenuti)
     */
    public List<DatiBase> findContentsByType(String idSite, Integer idType) {
        log.debug("Finding contents by type: {} for site: {}", idType, idSite);
        List<Content> contents = contentRepository.findContentsByType(idSite, idType);

        // Separa e mappa correttamente sezioni vs contenuti
        return contents.stream()
                .filter(Content::isContent)
                .map(datiBaseMapper::toDatiBase)
                .collect(Collectors.toList());
    }

    /**
     * Verifica se un contenuto esiste
     */
    public boolean exists(Integer id) {
        return contentRepository.existsById(id);
    }

    /**
     * Ottiene un Content entity raw (per operazioni avanzate)
     */
    public Optional<Content> findContentEntityById(Integer id) {
        return contentRepository.findById(id);
    }

    // ========================================
    // METODI PER FRONT-END PORTAL
    // ========================================

    /**
     * Ordina tutti i contenuti del sito per position
     */
    @Transactional
    public void ordinaContenuti(Integer idSito) {
        log.debug("Ordinamento contenuti per sito: {}", idSito);
        // TODO: Implementare riordinamento automatico
        // Per ora non fa nulla - ordine gestito già dalle query
    }

    /**
     * Ottieni sezione home del sito
     */
    public Section getSezioneHome(Integer idSito) {
        log.debug("Caricamento sezione home per sito: {}", idSito);

        // Cerca sezione con id_root = "0" o id_parent = "0"
        Optional<Content> homeOpt = contentRepository.findRootSectionsBySite(idSito.toString())
                .stream()
                .filter(c -> "0".equals(c.getIdParent()) || "-1".equals(c.getIdRoot()))
                .findFirst();

        return homeOpt.map(sectionMapper::toSection)
                .orElse(new Section()); // Ritorna sezione vuota se non trovata
    }

    /**
     * Ottieni struttura menu gerarchica
     */
    public List<Section> getStrutturaMenu(
            Integer idSito,
            String idRoot,
            String privato,
            String stato,
            String idParent,
            String orderBy,
            boolean loadSubsections) {

        log.debug("Caricamento struttura menu: sito={}, idRoot={}, privato={}, stato={}",
                idSito, idRoot, privato, stato);

        // Trova sezioni radice del menu
        List<Content> sections = contentRepository.findMenuSections(
                idSito.toString(),
                stato,
                privato
        );

        List<Section> menuSections = sectionMapper.toSectionList(sections);

        // Carica ricorsivamente le sottosezioni se richiesto
        if (loadSubsections) {
            for (Section section : menuSections) {
                loadSubsectionsRecursive(section, idSito.toString());
            }
        }

        return menuSections;
    }

    /**
     * Carica ricorsivamente le sottosezioni
     */
    private void loadSubsectionsRecursive(Section section, String idSito) {
        List<Section> subsections = findSubsections(idSito, section.getId().toString());
        section.setSubsection(subsections);

        // Ricorsione sulle sottosezioni
        for (Section sub : subsections) {
            loadSubsectionsRecursive(sub, idSito);
        }
    }

    /**
     * Ottieni tag cloud con conteggi
     */
    /**
     * Ottieni tag cloud con conteggi
     */
    public List<Tag> getTagCloud(String idSite) {
        log.debug("Caricamento tag cloud per sito: {}", idSite);

        // Trova tutti i tag distinti con conteggio
        List<Object[]> tagCounts = contentRepository.findTagCloud(idSite);

        List<Tag> tagCloud = new ArrayList<>();
        for (Object[] row : tagCounts) {
            String tagName = (String) row[0];
            Long count = (Long) row[1];

            Tag tag = Tag.builder()
                    .nome(tagName)
                    .occorrenze(count.intValue())
                    .peso(calculateTagWeight(count.intValue()))
                    .cssClass(calculateTagCssClass(count.intValue()))
                    .build();

            tagCloud.add(tag);
        }

        return tagCloud;
    }

    /**
     * Calcola peso tag per visualizzazione
     */
    private int calculateTagWeight(int occorrenze) {
        if (occorrenze > 20) return 5;
        if (occorrenze > 15) return 4;
        if (occorrenze > 10) return 3;
        if (occorrenze > 5) return 2;
        return 1;
    }

    /**
     * Calcola CSS class per tag
     */
    private String calculateTagCssClass(int occorrenze) {
        return "tag-weight-" + calculateTagWeight(occorrenze);
    }

    /**
     * Ottieni contenuti front-end con filtri avanzati
     */
    public List<DatiBase> getContenutiFront(
            Integer idSito,
            String fieldFilter,     // es: "id_root", "id"
            String valueFilter,     // es: "5", "10,20,30"
            String extraCondition,  // es: " or l2='1' "
            String orderBy,         // es: "titolo", "data"
            String maxResults) {    // es: "12"

        log.debug("Caricamento contenuti front: sito={}, field={}, value={}, order={}, max={}",
                idSito, fieldFilter, valueFilter, orderBy, maxResults);

        List<Content> contents;

        if ("id_root".equals(fieldFilter)) {
            // Contenuti per sezione
            try {
                Integer idRoot = Integer.parseInt(valueFilter);
                contents = contentRepository.findPublishedContentsBySection(
                        idSito.toString(), idRoot
                );
            } catch (NumberFormatException e) {
                log.warn("Invalid id_root: {}", valueFilter);
                return List.of();
            }

        } else if ("id".equals(fieldFilter)) {
            // Contenuti per lista ID (es: profilo navigazione)
            String[] ids = valueFilter.split(",");
            contents = new ArrayList<>();

            for (String idStr : ids) {
                try {
                    Integer id = Integer.parseInt(idStr.trim());
                    contentRepository.findByIdAndSite(id, idSito.toString())
                            .ifPresent(contents::add);
                } catch (NumberFormatException e) {
                    log.warn("Invalid id: {}", idStr);
                }
            }

        } else {
            // Fallback: tutti i contenuti pubblicati
            contents = contentRepository.findPublicContents(idSito.toString());
        }

        // Applica extra condition se presente
        if (extraCondition != null && !extraCondition.isEmpty()) {
            // TODO: Implementare filtro extra condition
            // Per ora ignoriamo - richiede query custom
        }

        // Ordina
        contents = sortContents(contents, orderBy);

        // Limita risultati
        if (maxResults != null && !maxResults.isEmpty()) {
            try {
                int max = Integer.parseInt(maxResults);
                contents = contents.stream().limit(max).collect(Collectors.toList());
            } catch (NumberFormatException e) {
                log.warn("Invalid maxResults: {}", maxResults);
            }
        }

        return datiBaseMapper.toDatiBaseList(contents);
    }

    /**
     * Ordina lista contenuti per campo
     */
    private List<Content> sortContents(List<Content> contents, String orderBy) {
        if (orderBy == null || orderBy.isEmpty()) {
            return contents;
        }

        return contents.stream()
                .sorted((c1, c2) -> {
                    return switch (orderBy.toLowerCase()) {
                        case "titolo" -> compareStrings(c1.getTitolo(), c2.getTitolo());
                        case "data" -> compareStrings(c2.getData(), c1.getData()); // Decrescente
                        case "data desc" -> compareStrings(c2.getData(), c1.getData());
                        case "data asc" -> compareStrings(c1.getData(), c2.getData());
                        case "position" -> compareIntegers(c1.getPosition(), c2.getPosition());
                        case "id" -> compareIntegers(c2.getId(), c1.getId()); // Decrescente
                        case "id desc" -> compareIntegers(c2.getId(), c1.getId());
                        case "id asc" -> compareIntegers(c1.getId(), c2.getId());
                        default -> 0;
                    };
                })
                .collect(Collectors.toList());
    }

    private int compareStrings(String s1, String s2) {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return 1;
        if (s2 == null) return -1;
        return s1.compareTo(s2);
    }

    private int compareIntegers(Integer i1, Integer i2) {
        if (i1 == null && i2 == null) return 0;
        if (i1 == null) return 1;
        if (i2 == null) return -1;
        return i1.compareTo(i2);
    }
    // ========================================
// METODI PER MENU E NAVIGAZIONE
// ========================================

    /**
     * Costruisce il menu pubblico principale
     * Equivalente legacy: getStrutturaMenu() per menu pubblico
     *
     * Logica:
     * 1. Carica sezioni di primo livello (idParent=NULL, menu1!=0)
     * 2. Verifica date pubblicazione programmata (S1/S2/S3)
     * 3. Carica ricorsivamente TUTTE le sottosezioni
     * 4. Ritorna struttura completa del menu
     *
     * @param idSite ID del sito
     * @return Lista completa delle sezioni del menu con sottosezioni
     */
    public List<Section> findPublicMenu(String idSite) {
        log.debug("=== COSTRUZIONE MENU PUBBLICO === sito: {}", idSite);

        try {
            // ===== 1. CARICA SEZIONI ROOT (PRIMO LIVELLO) =====
            List<Content> rootContents = contentRepository.findPublicMenu(idSite);

            if (rootContents.isEmpty()) {
                log.warn("Nessuna sezione trovata per menu pubblico, sito: {}", idSite);
                return new ArrayList<>();
            }

            log.debug("Trovate {} sezioni root per menu", rootContents.size());

            List<Section> menuSections = new ArrayList<>();
            LocalDate today = LocalDate.now();

            // ===== 2. PROCESSA OGNI SEZIONE ROOT =====
            for (Content content : rootContents) {

                // ===== 3. VERIFICA DATE PUBBLICAZIONE PROGRAMMATA =====
                // Se S3='1' → verifica che oggi sia tra S1 (inizio) e S2 (fine)
                // ===== 3. VERIFICA DATE PUBBLICAZIONE PROGRAMMATA =====
                if ("1".equals(content.getS(3))) {
                    try {
                        LocalDate startDate = this.parseScheduleDate(content.getS(1));  // ← this.
                        LocalDate endDate = this.parseScheduleDate(content.getS(2));    // ← this.

                        if (startDate != null && endDate != null) {
                            if (today.isBefore(startDate) || today.isAfter(endDate)) {
                                log.debug("Sezione {} esclusa: fuori periodo pubblicazione [{} - {}]",
                                        content.getId(), startDate, endDate);
                                continue;
                            }
                            log.debug("Sezione {} inclusa: nel periodo pubblicazione [{} - {}]",
                                    content.getId(), startDate, endDate);
                        }
                    } catch (Exception e) {
                        log.warn("Errore parsing date pubblicazione per sezione root {}: {}",
                                content.getId(), e.getMessage());
                        continue;
                    }
                }

                // ===== 4. CONVERTI A SECTION =====
                Section section = sectionMapper.toSection(content);

                // ===== 5. CARICA RICORSIVAMENTE TUTTE LE SOTTOSEZIONI =====
                // maxDepth=10 (10 livelli massimo), currentDepth=0 (inizia da root)
                loadMenuSubsections(section, idSite, 10, 0);

                menuSections.add(section);
            }

            log.debug("Menu pubblico costruito: {} sezioni root (dopo filtri date)",
                    menuSections.size());

            // Log dettagliato della struttura del menu
            if (log.isDebugEnabled()) {
                logMenuStructure(menuSections, 0);
            }

            return menuSections;

        } catch (Exception e) {
            log.error("Errore costruzione menu pubblico per sito {}: {}",
                    idSite, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Carica ricorsivamente TUTTE le sottosezioni del menu
     * Equivalente legacy: getStrutturaMenu() - ricorsione completa
     *
     * Logica:
     * 1. Carica sottosezioni pubblicate (stato=1, privato=0)
     * 2. Verifica date di pubblicazione programmata (S1/S2/S3)
     * 3. Ricorsivamente carica sottosezioni di ogni sottosezione
     * 4. Limita profondità a maxDepth per sicurezza
     *
     * @param section Sezione parent da popolare
     * @param idSite ID del sito
     * @param maxDepth Profondità massima ricorsione (default: 10)
     * @param currentDepth Profondità corrente (inizia da 0)
     */
    private void loadMenuSubsectionsOld(Section section, String idSite, int maxDepth, int currentDepth) {
        // Protezione da ricorsione infinita
        if (currentDepth >= maxDepth) {
            log.warn("Raggiunta profondità massima ricorsione: {} per sezione: {}",
                    maxDepth, section.getId());
            section.setSubsection(new ArrayList<>());
            return;
        }

        try {
            // ===== 1. CARICA SOTTOSEZIONI PUBBLICATE =====
            List<Content> subsectionContents = contentRepository.findPublishedSubsections(
                    idSite,
                    section.getId().toString(),
                    "1",  // stato = 1 (pubblicato)
                    "0"   // privato = 0 (pubblico)
            );

            if (subsectionContents.isEmpty()) {
                section.setSubsection(new ArrayList<>());
                return;
            }

            log.debug("Trovate {} sottosezioni per sezione {} (livello {})",
                    subsectionContents.size(), section.getId(), currentDepth + 1);

            List<Section> subsections = new ArrayList<>();
            LocalDate today = LocalDate.now();

            // ===== 2. PROCESSA OGNI SOTTOSEZIONE =====
            for (Content content : subsectionContents) {

                // ===== 3. VERIFICA DATE DI PUBBLICAZIONE PROGRAMMATA =====
                // Se S3='1' → verifica che oggi sia tra S1 (inizio) e S2 (fine)
                if ("1".equals(content.getS(3))) {
                    try {
                        LocalDate startDate = parseScheduleDate(content.getS(1));
                        LocalDate endDate = parseScheduleDate(content.getS(2));

                        if (startDate != null && endDate != null) {
                            // Verifica se oggi è nel range [startDate, endDate]
                            if (today.isBefore(startDate) || today.isAfter(endDate)) {
                                log.debug("Sottosezione {} esclusa: fuori periodo pubblicazione",
                                        content.getId());
                                continue; // Salta questa sezione
                            }
                        }
                    } catch (Exception e) {
                        log.warn("Errore parsing date pubblicazione per sottosezione {}: {}",
                                content.getId(), e.getMessage());
                        continue;
                    }
                }

                // ===== 4. CONVERTI A SECTION =====
                Section subsection = sectionMapper.toSection(content);

                // ===== 5. RICORSIONE: CARICA SOTTOSEZIONI DELLE SOTTOSEZIONI =====
                loadMenuSubsections(subsection, idSite, maxDepth, currentDepth + 1);

                subsections.add(subsection);
            }

            section.setSubsection(subsections);
            log.debug("Caricate {} sottosezioni valide per sezione {} (livello {})",
                    subsections.size(), section.getId(), currentDepth + 1);

        } catch (Exception e) {
            log.error("Errore caricamento sottosezioni per sezione {} (livello {}): {}",
                    section.getId(), currentDepth, e.getMessage(), e);
            section.setSubsection(new ArrayList<>());
        }
    }



    /**
     * Log ricorsivo della struttura del menu (solo in DEBUG)
     * Utile per verificare che il menu sia stato costruito correttamente
     *
     * @param sections Lista di sezioni
     * @param level Livello di indentazione
     */
    private void logMenuStructure(List<Section> sections, int level) {
        if (sections == null || sections.isEmpty()) {
            return;
        }

        String indent = "  ".repeat(level);

        for (Section section : sections) {
            log.debug("{}├─ [{}] {} (menu1={}, subsections={})",
                    indent,
                    section.getId(),
                    section.getTitolo(),
                    section.getMenu1(),
                    section.getSubsection() != null ? section.getSubsection().size() : 0);

            // Ricorsione per sottosezioni
            if (section.getSubsection() != null && !section.getSubsection().isEmpty()) {
                logMenuStructure(section.getSubsection(), level + 1);
            }
        }
    }

    /**
     * Trova il menu privato del sito per utente autenticato
     * Sezioni con: stato='1', privato='1', gruppi compatibili con utente
     */
    public List<Section> findPrivateMenu(String idSite, UtenteEsterno utente) {
        log.debug("Loading private menu for site: {} and user: {}",
                idSite, utente.getUsername());

        if (utente == null) {
            return new ArrayList<>();
        }

        // Ottieni i gruppi dell'utente
        String gruppiSql = utente.GruppiSqlCond();

        List<Content> menuContents = contentRepository.findPrivateMenu(idSite, gruppiSql);
        List<Section> menuSections = sectionMapper.toSectionList(menuContents);

        // Carica ricorsivamente le sottosezioni del menu
        for (Section section : menuSections) {
            loadMenuSubsections(section, idSite, 2, 0);
        }

        log.debug("Private menu loaded: {} sections", menuSections.size());
        return menuSections;
    }

    /**
     * Trova la sezione home page del sito
     * Prima sezione con: idRoot=-1, idParent='0' o '0', firstPage='1'
     */
    public Section findHomePage(String idSite) {
        log.debug("Loading home page for site: {}", idSite);

        // Cerca sezione marcata come firstPage
        Optional<Content> homeOpt = contentRepository.findHomePage(idSite);

        if (homeOpt.isPresent()) {
            Section home = sectionMapper.toSection(homeOpt.get());

            // Carica contenuti della home
            home.setContenuti(findPublishedContentsBySection(idSite, home.getId()));

            log.debug("Home page loaded: {}", home.getTitolo());
            return home;
        }

        // Fallback: prima sezione radice
        log.warn("No home page found, using first root section");
        List<Content> rootSections = contentRepository.findRootSectionsBySite(idSite);

        if (!rootSections.isEmpty()) {
            Section home = sectionMapper.toSection(rootSections.get(0));
            home.setContenuti(findPublishedContentsBySection(idSite, home.getId()));
            return home;
        }

        // Fallback finale: sezione vuota
        log.warn("No sections found for site: {}", idSite);
        return new Section();
    }

    /**
     * Carica ricorsivamente TUTTE le sottosezioni del menu
     * Equivalente legacy: getStrutturaMenu() - ricorsione completa
     *
     * Logica:
     * 1. Carica sottosezioni pubblicate (stato=1, privato=0)
     * 2. Verifica date di pubblicazione programmata (S1/S2/S3)
     * 3. Ricorsivamente carica sottosezioni di ogni sottosezione
     * 4. Limita profondità a maxDepth per sicurezza
     *
     * @param section Sezione parent da popolare
     * @param idSite ID del sito
     * @param maxDepth Profondità massima ricorsione (default: 10)
     * @param currentDepth Profondità corrente (inizia da 0)
     */
    private void loadMenuSubsections(Section section, String idSite, int maxDepth, int currentDepth) {
        // Protezione da ricorsione infinita
        if (currentDepth >= maxDepth) {
            log.warn("Raggiunta profondità massima ricorsione: {} per sezione: {}",
                    maxDepth, section.getId());
            return;
        }

        try {
            // ===== 1. CARICA SOTTOSEZIONI PUBBLICATE =====
            List<Content> subsectionContents = contentRepository.findPublishedSubsections(
                    idSite,
                    section.getId().toString(),
                    "1",  // stato = 1 (pubblicato)
                    "0"   // privato = 0 (pubblico)
            );

            if (subsectionContents.isEmpty()) {
                section.setSubsection(new ArrayList<>());
                return;
            }

            List<Section> subsections = new ArrayList<>();
            LocalDate today = LocalDate.now();

            // ===== 2. PROCESSA OGNI SOTTOSEZIONE =====
            for (Content content : subsectionContents) {

                // ===== 3. VERIFICA DATE DI PUBBLICAZIONE PROGRAMMATA =====
                // Se S3='1' → verifica che oggi sia tra S1 (inizio) e S2 (fine)
                if ("1".equals(content.getS(3))) {
                    try {
                        LocalDate startDate = parseScheduleDate(content.getS(1));
                        LocalDate endDate = parseScheduleDate(content.getS(2));

                        if (startDate != null && endDate != null) {
                            // Verifica se oggi è nel range [startDate, endDate]
                            if (today.isBefore(startDate) || today.isAfter(endDate)) {
                                log.debug("Sezione {} esclusa: fuori periodo pubblicazione",
                                        content.getId());
                                continue; // Salta questa sezione
                            }
                        }
                    } catch (Exception e) {
                        log.warn("Errore parsing date pubblicazione per sezione {}: {}",
                                content.getId(), e.getMessage());
                        continue;
                    }
                }

                // ===== 4. CONVERTI A SECTION =====
                Section subsection = sectionMapper.toSection(content);

                // ===== 5. RICORSIONE: CARICA SOTTOSEZIONI DELLE SOTTOSEZIONI =====
                loadMenuSubsections(subsection, idSite, maxDepth, currentDepth + 1);

                subsections.add(subsection);
            }

            section.setSubsection(subsections);

        } catch (Exception e) {
            log.error("Errore caricamento sottosezioni per sezione {}: {}",
                    section.getId(), e.getMessage(), e);
            section.setSubsection(new ArrayList<>());
        }
    }

    /**
     * Parse date da campo S1/S2 (formati: yyyy-MM-dd, dd/MM/yyyy, dd-MM-yyyy)
     */
    private LocalDate parseScheduleDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        try {
            // Prova formato ISO (yyyy-MM-dd)
            return LocalDate.parse(dateString.trim());
        } catch (Exception e1) {
            try {
                // Prova formato europeo (dd/MM/yyyy)
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                return LocalDate.parse(dateString.trim(), formatter);
            } catch (Exception e2) {
                try {
                    // Prova formato con trattini (dd-MM-yyyy)
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    return LocalDate.parse(dateString.trim(), formatter);
                } catch (Exception e3) {
                    log.warn("Formato data non riconosciuto: {}", dateString);
                    return null;
                }
            }
        }
    }

    // ========================================
    // METODI PER NEWSLETTER E SMS
    // ========================================

    /**
     * Trova contenuti per newsletter
     */
    public List<DatiBase> findContentsByNewsletter(String idSite, String newsletterId) {
        log.debug("Finding contents by newsletter: {} for site: {}", newsletterId, idSite);
        List<Content> contents = contentRepository.findContentsByNewsletter(idSite, newsletterId);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    /**
     * Trova contenuti per SMS
     */
    public List<DatiBase> findContentsBySms(String idSite, String smsId) {
        log.debug("Finding contents by SMS: {} for site: {}", smsId, idSite);
        List<Content> contents = contentRepository.findContentsBySms(idSite, smsId);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    // ========================================
    // METODI PER CAMPI CUSTOM (TEXT, VARCHAR, NUMBER, DATA)
    // ========================================

    /**
     * Cerca contenuti nei campi TEXT custom
     */
    public List<DatiBase> searchInTextFields(String idSite, String searchTerm) {
        log.debug("Searching in TEXT fields: {} for site: {}", searchTerm, idSite);
        List<Content> contents = contentRepository.searchInTextFields(idSite, searchTerm);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    /**
     * Cerca contenuti nei campi VARCHAR custom
     */
    public List<DatiBase> searchInVarcharFields(String idSite, String searchTerm) {
        log.debug("Searching in VARCHAR fields: {} for site: {}", searchTerm, idSite);
        List<Content> contents = contentRepository.searchInVarcharFields(idSite, searchTerm);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    /**
     * Trova contenuti con NUMBER in range
     */
    public List<DatiBase> findContentsByNumberRange(String idSite, Double min, Double max) {
        log.debug("Finding contents by NUMBER range: {}-{} for site: {}", min, max, idSite);
        List<Content> contents = contentRepository.findContentsByNumberRange(idSite, min, max);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    /**
     * Trova contenuti per range di date (DATA fields)
     */
    public List<DatiBase> findContentsByDateRange(String idSite, LocalDate startDate, LocalDate endDate) {
        log.debug("Finding contents by DATE range: {}-{} for site: {}", startDate, endDate, idSite);
        List<Content> contents = contentRepository.findContentsByDateRange(idSite, startDate, endDate);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    // ========================================
    // METODI HELPER PER ACCESSO CAMPI ESTESI
    // ========================================

    /**
     * Ottieni valore campo L per numero (1-15)
     */
    public String getCampoL(Integer contentId, int numeroL) {
        Optional<Content> contentOpt = contentRepository.findById(contentId);
        if (contentOpt.isEmpty()) {
            return null;
        }

        Content content = contentOpt.get();
        return switch (numeroL) {
            case 1 -> content.getL1();
            case 2 -> content.getL2();
            case 3 -> content.getL3();
            case 4 -> content.getL4();
            case 5 -> content.getL5();
            case 6 -> content.getL6();
            case 7 -> content.getL7();
            case 8 -> content.getL8();
            case 9 -> content.getL9();
            case 10 -> content.getL10();
            case 11 -> content.getL11();
            case 12 -> content.getL12();
            case 13 -> content.getL13();
            case 14 -> content.getL14();
            case 15 -> content.getL15();
            default -> null;
        };
    }

    /**
     * Incrementa numeratore
     */
    @Transactional
    public Integer incrementNumeratore(Integer contentId, int numeroNumeratore) {
        log.debug("Incrementing numeratore{} for content: {}", numeroNumeratore, contentId);

        Optional<Content> contentOpt = contentRepository.findById(contentId);
        if (contentOpt.isEmpty()) {
            return null;
        }

        Content content = contentOpt.get();
        Integer currentValue = switch (numeroNumeratore) {
            case 1 -> content.getNumeratore1();
            case 2 -> content.getNumeratore2();
            case 3 -> content.getNumeratore3();
            case 4 -> content.getNumeratore4();
            case 5 -> content.getNumeratore5();
            default -> 0;
        };

        Integer newValue = (currentValue != null ? currentValue : 0) + 1;

        switch (numeroNumeratore) {
            case 1 -> content.setNumeratore1(newValue);
            case 2 -> content.setNumeratore2(newValue);
            case 3 -> content.setNumeratore3(newValue);
            case 4 -> content.setNumeratore4(newValue);
            case 5 -> content.setNumeratore5(newValue);
        }

        contentRepository.save(content);
        return newValue;
    }

    /**
     * Ottieni numeratore formattato (zerofill 8 cifre)
     */
    public String getNumeratoreFormatted(Integer contentId, int numeroNumeratore) {
        Optional<Content> contentOpt = contentRepository.findById(contentId);
        if (contentOpt.isEmpty()) {
            return "00000000";
        }

        Content content = contentOpt.get();
        Integer value = switch (numeroNumeratore) {
            case 1 -> content.getNumeratore1();
            case 2 -> content.getNumeratore2();
            case 3 -> content.getNumeratore3();
            case 4 -> content.getNumeratore4();
            case 5 -> content.getNumeratore5();
            default -> 0;
        };

        return String.format("%08d", value != null ? value : 0);
    }
    /**
     * Trova SectionType per ID
     */
    public SectionType getSectionTypeById(Integer id) {
        log.debug("Finding SectionType by id: {}", id);
        return sectionTypeRepository.findById(id).orElse(null);
    }

}