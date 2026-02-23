package com.studiodomino.jplatform.cms.service;

import com.studiodomino.jplatform.cms.entity.Content;
import com.studiodomino.jplatform.cms.entity.DatiBase;
import com.studiodomino.jplatform.cms.entity.Section;
import com.studiodomino.jplatform.cms.entity.SectionType;
import com.studiodomino.jplatform.cms.front.dto.Breadcrumb;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service per la gestione dei contenuti e delle sezioni del CMS.
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

    public List<Section> findAllSections(String idSite) {
        log.debug("Finding all sections for site: {}", idSite);
        List<Content> contents = contentRepository.findSectionsBySite(idSite);
        return sectionMapper.toSectionList(contents);
    }

    public List<SectionType> findAllSectionTypes() {
        return sectionTypeRepository.findAll();
    }

    public List<Section> findRootSections(String idSite) {
        log.debug("Finding root sections for site: {}", idSite);
        List<Content> contents = contentRepository.findRootSectionsBySite(idSite);
        return sectionMapper.toSectionList(contents);
    }

    public List<Section> findSubsections(String idSite, String idParent) {
        log.debug("Finding subsections for site: {} and parent: {}", idSite, idParent);
        List<Content> contents = contentRepository.findSubsectionsByParent(idSite, idParent);
        return sectionMapper.toSectionList(contents);
    }

    public Optional<Section> findSectionById(Integer id, String idSite) {
        log.debug("Finding section by id: {} and site: {}", id, idSite);
        return contentRepository.findByIdAndSite(id, idSite)
                .filter(Content::isSection)
                .map(sectionMapper::toSection);
    }

    public Optional<Section> findSectionById(Integer id) {
        log.debug("Finding section by id: {}", id);
        return contentRepository.findById(id)
                .filter(Content::isSection)
                .map(sectionMapper::toSection);
    }

    public Optional<Section> findSectionByLabel(String idSite, String label) {
        log.debug("Finding section by label: {} for site: {}", label, idSite);
        return contentRepository.findSectionByLabel(idSite, label)
                .map(sectionMapper::toSection);
    }

    public List<Section> findSectionsByType(String idSite, Integer idType) {
        log.debug("Finding sections by type: {} for site: {}", idType, idSite);
        List<Content> contents = contentRepository.findSectionsByType(idSite, idType);
        return sectionMapper.toSectionList(contents);
    }

    public Optional<Section> findSectionComplete(Integer id, String idSite) {
        log.debug("Finding complete section by id: {} and site: {}", id, idSite);

        Optional<Section> sectionOpt = findSectionById(id, idSite);
        if (sectionOpt.isEmpty()) return Optional.empty();

        Section section = sectionOpt.get();

        // Carica sotto-sezioni
        section.setSubsection(findSubsections(idSite, id.toString()));

        // Carica contenuti della sezione
        section.setContenuti(findContentsBySection(idSite, id));

        // Carica catena dei parent (per breadcrumb)
        section.setParentSection(buildParentChain(section, idSite));

        return Optional.of(section);
    }

    private List<Section> buildParentChain(Section section, String idSite) {
        List<Section> chain = new ArrayList<>();
        String currentParentId = section.getIdParent();

        while (currentParentId != null && !currentParentId.isEmpty() && !"0".equals(currentParentId)) {
            try {
                Integer parentId = Integer.parseInt(currentParentId);
                Optional<Section> parentOpt = findSectionById(parentId, idSite);

                if (parentOpt.isPresent()) {
                    Section parent = parentOpt.get();
                    chain.add(0, parent);
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

    public Breadcrumb buildBreadcrumbForSection(Section section, String idSite) {
        Breadcrumb breadcrumb = new Breadcrumb();
        if (section == null) return breadcrumb;

        // parent chain (Home > ... > Parent)
        List<Section> chain = buildParentChain(section, idSite);

        // aggiungo i parent
        for (Section s : chain) {
            // getUrl() deve già restituire qualcosa tipo: /front/1547/slug
            String url = s.getUrl();

            // (opzionale) se per qualche motivo url è null/vuoto, fallback a /front/{id}
            if (url == null || url.isEmpty()) {
                url = "/front/" + s.getId();
            }

            breadcrumb.add(s.getTitolo(), url, String.valueOf(s.getId()));
        }

        // item corrente (non linkato)
        String currentUrl = section.getUrl();
        if (currentUrl == null || currentUrl.isEmpty()) {
            currentUrl = "/front/" + section.getId();
        }

        breadcrumb.setCurrentItem(section.getTitolo(), currentUrl, String.valueOf(section.getId()));
        return breadcrumb;
    }

    public Breadcrumb buildBreadcrumbForContent(DatiBase post, String idSite) {
        Breadcrumb breadcrumb = new Breadcrumb();
        if (post == null) return breadcrumb;

        // sezione padre = idRoot
        Integer idRootInt = safeInt(post.getIdRoot());
        if (idRootInt != null && idRootInt > 0) {
            Optional<Section> secOpt = findSectionById(idRootInt, idSite);
            if (secOpt.isPresent()) {
                Section section = secOpt.get();
                List<Section> chain = buildParentChain(section, idSite);

                // parent chain
                for (Section s : chain) {
                    String url = s.getUrl();
                    if (url == null || url.isEmpty()) {
                        url = "/front/" + s.getId();
                    }
                    breadcrumb.add(s.getTitolo(), url, String.valueOf(s.getId()));
                }

                // aggiungo anche la sezione stessa prima del documento
                String secUrl = section.getUrl();
                if (secUrl == null || secUrl.isEmpty()) {
                    secUrl = "/front/" + section.getId();
                }
                breadcrumb.add(section.getTitolo(), secUrl, String.valueOf(section.getId()));
            }
        }

        // item corrente = documento
        String postUrl = post.getUrl();   // deve essere: /front/{id}/{slug}
        if (postUrl == null || postUrl.isEmpty()) {
            // fallback: almeno /front/{id}
            postUrl = "/front/" + post.getId();
        }

        breadcrumb.setCurrentItem(post.getTitolo(), postUrl, post.getId());
        return breadcrumb;
    }

    private Integer safeInt(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;
        try {
            return Integer.parseInt(t);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Transactional
    public Section saveSection(Section section) {
        log.debug("Saving section: {}", section.getId());
        Content content = sectionMapper.toContent(section);
        Content saved = contentRepository.save(content);
        return sectionMapper.toSection(saved);
    }

    @Transactional
    public void deleteSection(Integer id) {
        log.debug("Deleting section: {}", id);
        contentRepository.deleteById(id);
    }

    // ========================================
    // METODI PER CONTENUTI
    // ========================================

    public List<DatiBase> findContentsBySection(String idSite, Integer idRoot) {
        log.debug("Finding contents for section: {} and site: {}", idRoot, idSite);
        List<Content> contents = contentRepository.findContentsBySection(idSite, idRoot);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    public List<DatiBase> findContentsBySection(
            Integer idSito,
            Integer idRoot,
            String whereCondition,
            String orderBy,
            Integer limit) {

        log.debug("Finding contents for section {} with custom filters", idRoot);

        List<Content> contents = contentRepository.findPublishedContentsBySection(
                idSito.toString(), idRoot
        );

        contents = sortContents(contents, orderBy);

        if (limit != null && limit > 0) {
            contents = contents.stream().limit(limit).collect(Collectors.toList());
        }

        return datiBaseMapper.toDatiBaseList(contents);
    }

    public List<DatiBase> findPublishedContentsBySection(String idSite, Integer idRoot) {
        log.debug("Finding published contents for section: {} and site: {}", idRoot, idSite);
        List<Content> contents = contentRepository.findPublishedContentsBySection(idSite, idRoot);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    public Optional<DatiBase> findContentById(Integer id, String idSite) {
        log.debug("Finding content by id: {} and site: {}", id, idSite);
        return contentRepository.findByIdAndSite(id, idSite)
                .filter(Content::isContent)
                .map(datiBaseMapper::toDatiBase);
    }

    public Optional<DatiBase> findDatiBaseById(Integer id) {
        log.debug("Finding DatiBase by id: {}", id);
        return contentRepository.findById(id)
                .filter(Content::isContent)
                .map(datiBaseMapper::toDatiBase);
    }

    public Optional<DatiBase> findContentByLabel(String idSite, Integer idRoot, String label) {
        log.debug("Finding content by label: {} in section: {} for site: {}",
                label, idRoot, idSite);
        return contentRepository.findContentByLabel(idSite, idRoot, label)
                .map(datiBaseMapper::toDatiBase);
    }

    public List<DatiBase> findContentsByStato(String idSite, String stato) {
        log.debug("Finding contents by stato: {} for site: {}", stato, idSite);
        List<Content> contents = contentRepository.findByStato(idSite, stato);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    public List<DatiBase> findPublicContents(String idSite) {
        log.debug("Finding public contents for site: {}", idSite);
        List<Content> contents = contentRepository.findPublicContents(idSite);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    public List<DatiBase> findPrivateContentsByGroup(String idSite, String idGruppo) {
        log.debug("Finding private contents for group: {} and site: {}", idGruppo, idSite);
        List<Content> contents = contentRepository.findPrivateContentsByGroup(idSite, idGruppo);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    @Transactional
    public DatiBase saveContent(DatiBase datiBase) {
        log.debug("Saving content: {}", datiBase.getId());
        Content content = datiBaseMapper.toContent(datiBase);
        Content saved = contentRepository.save(content);
        return datiBaseMapper.toDatiBase(saved);
    }

    @Transactional
    public void deleteContent(Integer id) {
        log.debug("Deleting content: {}", id);
        contentRepository.deleteById(id);
    }

    // ========================================
    // METODI PER SCHEDULING (s1, s2, s3)
    // ========================================

    public List<DatiBase> findScheduledContents(String idSite) {
        log.debug("Finding scheduled contents for site: {}", idSite);
        List<Content> contents = contentRepository.findScheduledContents(idSite);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    @Transactional
    public int publishScheduledContents(String idSite) {
        log.info("Publishing scheduled contents for site: {}", idSite);

        LocalDate today = LocalDate.now();
        List<Content> toPublish = contentRepository.findContentsToPublish(idSite, today);

        int count = 0;
        for (Content content : toPublish) {
            content.setStato("1");
            contentRepository.save(content);
            count++;
        }
        log.info("Published {} scheduled contents", count);
        return count;
    }

    @Transactional
    public int hideExpiredContents(String idSite) {
        log.info("Hiding expired contents for site: {}", idSite);

        LocalDate today = LocalDate.now();
        List<Content> toHide = contentRepository.findContentsToHide(idSite, today);

        int count = 0;
        for (Content content : toHide) {
            content.setStato("0");
            contentRepository.save(content);
            count++;
        }
        log.info("Hidden {} expired contents", count);
        return count;
    }

    // ========================================
    // METODI PER ARCHIVIO
    // ========================================

    public List<DatiBase> findContentsByYear(String idSite, Integer idRoot, Integer anno) {
        log.debug("Finding contents by year: {} for section: {} and site: {}",
                anno, idRoot, idSite);
        List<Content> contents = contentRepository.findContentsByYear(idSite, idRoot, anno);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    public List<DatiBase> findContentsByYearMonth(String idSite, Integer idRoot, Integer anno, String mese) {
        log.debug("Finding contents by year/month: {}/{} for section: {} and site: {}",
                anno, mese, idRoot, idSite);
        List<Content> contents = contentRepository.findContentsByYearMonth(idSite, idRoot, anno, mese);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    public List<Integer> findDistinctYearsBySection(String idSite, Integer idRoot) {
        log.debug("Finding distinct years for section: {} and site: {}", idRoot, idSite);
        return contentRepository.findDistinctYearsBySection(idSite, idRoot);
    }

    // ========================================
    // METODI PER TAG
    // ========================================

    public List<DatiBase> findContentsByTag(String idSite, String tag) {
        log.debug("Finding contents by tag: {} for site: {}", tag, idSite);
        List<Content> contents = contentRepository.findContentsByTag(idSite, tag);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    public List<DatiBase> findContentsByExtraTag(String idSite, String extraTag) {
        log.debug("Finding contents by extraTag: {} for site: {}", extraTag, idSite);
        List<Content> contents = contentRepository.findContentsByExtraTag(idSite, extraTag);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    // ========================================
    // METODI PER FIRST PAGE
    // ========================================

    public List<DatiBase> findFirstPageContents(String idSite) {
        log.debug("Finding first page contents for site: {}", idSite);
        List<Content> contents = contentRepository.findFirstPageContents(idSite);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    // ========================================
    // METODI PER RICERCA
    // ========================================

    public List<DatiBase> searchFullText(String idSite, String searchTerm) {
        log.debug("Searching full text: {} for site: {}", searchTerm, idSite);
        List<Content> contents = contentRepository.searchFullText(idSite, searchTerm);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    // ========================================
    // METODI PER STATISTICHE
    // ========================================

    public Long countContentsBySection(String idSite, Integer idRoot) {
        log.debug("Counting contents for section: {} and site: {}", idRoot, idSite);
        return contentRepository.countContentsBySection(idSite, idRoot);
    }

    public Long countPublishedContentsBySection(String idSite, Integer idRoot) {
        log.debug("Counting published contents for section: {} and site: {}", idRoot, idSite);
        return contentRepository.countPublishedContentsBySection(idSite, idRoot);
    }

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

    public List<Content> findAllBySite(String idSite) {
        log.debug("Finding all contents (sections + items) for site: {}", idSite);
        return contentRepository.findAllBySite(idSite);
    }

    public List<DatiBase> findContentsByType(String idSite, Integer idType) {
        log.debug("Finding contents by type: {} for site: {}", idType, idSite);
        List<Content> contents = contentRepository.findContentsByType(idSite, idType);

        return contents.stream()
                .filter(Content::isContent)
                .map(datiBaseMapper::toDatiBase)
                .collect(Collectors.toList());
    }

    public boolean exists(Integer id) {
        return contentRepository.existsById(id);
    }

    public Optional<Content> findContentEntityById(Integer id) {
        return contentRepository.findById(id);
    }

    // ========================================
    // METODI PER FRONT-END PORTAL
    // ========================================

    @Transactional
    public void ordinaContenuti(Integer idSito) {
        log.debug("Ordinamento contenuti per sito: {}", idSito);
        // TODO
    }

    public Section getSezioneHome(Integer idSito) {
        log.debug("Caricamento sezione home per sito: {}", idSito);

        Optional<Content> homeOpt = contentRepository.findRootSectionsBySite(idSito.toString())
                .stream()
                .filter(c -> "0".equals(c.getIdParent()) || "-1".equals(c.getIdRoot()))
                .findFirst();

        return homeOpt.map(sectionMapper::toSection).orElse(new Section());
    }

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

        List<Content> sections = contentRepository.findMenuSections(
                idSito.toString(),
                stato,
                privato
        );

        List<Section> menuSections = sectionMapper.toSectionList(sections);

        if (loadSubsections) {
            for (Section section : menuSections) {
                loadSubsectionsRecursive(section, idSito.toString());
            }
        }

        return menuSections;
    }

    private void loadSubsectionsRecursive(Section section, String idSito) {
        List<Section> subsections = findSubsections(idSito, section.getId().toString());
        section.setSubsection(subsections);

        for (Section sub : subsections) {
            loadSubsectionsRecursive(sub, idSito);
        }
    }

    public List<Tag> getTagCloud(String idSite) {
        log.debug("Caricamento tag cloud per sito: {}", idSite);

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

    private int calculateTagWeight(int occorrenze) {
        if (occorrenze > 20) return 5;
        if (occorrenze > 15) return 4;
        if (occorrenze > 10) return 3;
        if (occorrenze > 5) return 2;
        return 1;
    }

    private String calculateTagCssClass(int occorrenze) {
        return "tag-weight-" + calculateTagWeight(occorrenze);
    }

    /**
     * Ottieni contenuti front-end con filtri avanzati
     *
     * FIX IMPORTANTE:
     * - prima accettava solo id_root singolo (parseInt(valueFilter))
     * - ora supporta anche liste tipo "346,364,367"
     */
    public List<DatiBase> getContenutiFront(
            Integer idSito,
            String fieldFilter,     // es: "id_root", "id"
            String valueFilter,     // es: "5" oppure "10,20,30"
            String extraCondition,  // es: " or l2='1' "
            String orderBy,         // es: "data desc", "id asc"
            String maxResults) {    // es: "12"

        log.debug("Caricamento contenuti front: sito={}, field={}, value={}, order={}, max={}",
                idSito, fieldFilter, valueFilter, orderBy, maxResults);

        if (idSito == null || fieldFilter == null) {
            return List.of();
        }

        List<Content> contents;

        if ("id_root".equalsIgnoreCase(fieldFilter)) {

            // Supporta sia "562" che "562,563,561"
            contents = new ArrayList<>();
            if (valueFilter != null && !valueFilter.trim().isEmpty()) {
                String[] parts = valueFilter.split(",");
                for (String p : parts) {
                    String t = (p != null ? p.trim() : "");
                    if (t.isEmpty()) continue;

                    try {
                        Integer idRoot = Integer.parseInt(t);
                        List<Content> one = contentRepository.findPublishedContentsBySection(
                                idSito.toString(), idRoot
                        );
                        contents.addAll(one);
                    } catch (NumberFormatException e) {
                        log.warn("Invalid id_root token: {}", t);
                    }
                }
            }

        } else if ("id".equalsIgnoreCase(fieldFilter)) {

            contents = new ArrayList<>();
            if (valueFilter != null && !valueFilter.trim().isEmpty()) {
                String[] ids = valueFilter.split(",");
                for (String idStr : ids) {
                    try {
                        Integer id = Integer.parseInt(idStr.trim());
                        contentRepository.findByIdAndSite(id, idSito.toString())
                                .ifPresent(contents::add);
                    } catch (NumberFormatException e) {
                        log.warn("Invalid id: {}", idStr);
                    }
                }
            }

        } else {
            contents = contentRepository.findPublicContents(idSito.toString());
        }

        // TODO: extraCondition (ignorata come prima)

        // Ordina
        contents = sortContents(contents, orderBy);

        // Deduplica (se id_root multipli possono portare lo stesso contenuto più volte)
        contents = dedupeById(contents);

        // Limita risultati
        if (maxResults != null && !maxResults.isEmpty()) {
            try {
                int max = Integer.parseInt(maxResults);
                if (max > 0) contents = contents.stream().limit(max).collect(Collectors.toList());
            } catch (NumberFormatException e) {
                log.warn("Invalid maxResults: {}", maxResults);
            }
        }

        return datiBaseMapper.toDatiBaseList(contents);
    }

    private List<Content> dedupeById(List<Content> contents) {
        if (contents == null || contents.isEmpty()) return contents;
        Map<Integer, Content> map = new LinkedHashMap<>();
        for (Content c : contents) {
            if (c != null && c.getId() != null) map.putIfAbsent(c.getId(), c);
        }
        return new ArrayList<>(map.values());
    }

    private List<Content> sortContents(List<Content> contents, String orderBy) {
        if (contents == null || contents.isEmpty()) return contents;
        if (orderBy == null || orderBy.isEmpty()) return contents;

        String ob = orderBy.trim().toLowerCase();

        return contents.stream()
                .sorted((c1, c2) -> switch (ob) {
                    case "titolo" -> compareStrings(c1.getTitolo(), c2.getTitolo());
                    case "data" -> compareStrings(c2.getData(), c1.getData()); // desc
                    case "data desc" -> compareStrings(c2.getData(), c1.getData());
                    case "data asc" -> compareStrings(c1.getData(), c2.getData());
                    case "position" -> compareIntegers(c1.getPosition(), c2.getPosition());
                    case "id" -> compareIntegers(c2.getId(), c1.getId()); // desc
                    case "id desc" -> compareIntegers(c2.getId(), c1.getId());
                    case "id asc" -> compareIntegers(c1.getId(), c2.getId());
                    default -> 0;
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

    public List<Section> findPublicMenu(String idSite) {
        log.debug("=== COSTRUZIONE MENU PUBBLICO === sito: {}", idSite);

        try {
            List<Content> rootContents = contentRepository.findPublicMenu(idSite);

            if (rootContents.isEmpty()) {
                log.warn("Nessuna sezione trovata per menu pubblico, sito: {}", idSite);
                return new ArrayList<>();
            }

            log.debug("Trovate {} sezioni root per menu", rootContents.size());

            List<Section> menuSections = new ArrayList<>();
            LocalDate today = LocalDate.now();

            for (Content content : rootContents) {

                if ("1".equals(content.getS(3))) {
                    try {
                        LocalDate startDate = this.parseScheduleDate(content.getS(1));
                        LocalDate endDate = this.parseScheduleDate(content.getS(2));

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

                Section section = sectionMapper.toSection(content);
                loadMenuSubsections(section, idSite, 10, 0);
                menuSections.add(section);
            }

            log.debug("Menu pubblico costruito: {} sezioni root (dopo filtri date)", menuSections.size());

            if (log.isDebugEnabled()) logMenuStructure(menuSections, 0);

            return menuSections;

        } catch (Exception e) {
            log.error("Errore costruzione menu pubblico per sito {}: {}", idSite, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private void logMenuStructure(List<Section> sections, int level) {
        if (sections == null || sections.isEmpty()) return;

        String indent = "  ".repeat(level);
        for (Section section : sections) {
            log.debug("{}├─ [{}] {} (menu1={}, subsections={})",
                    indent,
                    section.getId(),
                    section.getTitolo(),
                    section.getMenu1(),
                    section.getSubsection() != null ? section.getSubsection().size() : 0);

            if (section.getSubsection() != null && !section.getSubsection().isEmpty()) {
                logMenuStructure(section.getSubsection(), level + 1);
            }
        }
    }

    public List<Section> findPrivateMenu(String idSite, UtenteEsterno utente) {
        log.debug("Loading private menu for site: {} and user: {}", idSite, utente != null ? utente.getUsername() : null);

        if (utente == null) return new ArrayList<>();

        String gruppiSql = utente.GruppiSqlCond();

        List<Content> menuContents = contentRepository.findPrivateMenu(idSite, gruppiSql);
        List<Section> menuSections = sectionMapper.toSectionList(menuContents);

        for (Section section : menuSections) {
            loadMenuSubsections(section, idSite, 10, 0);
        }

        log.debug("Private menu loaded: {} sections", menuSections.size());
        return menuSections;
    }

    public Section findHomePage(String idSite) {
        log.debug("Loading home page for site: {}", idSite);

        Optional<Content> homeOpt = contentRepository.findHomePage(idSite);

        if (homeOpt.isPresent()) {
            Section home = sectionMapper.toSection(homeOpt.get());
            home.setContenuti(findPublishedContentsBySection(idSite, home.getId()));
            log.debug("Home page loaded: {}", home.getTitolo());
            return home;
        }

        log.warn("No home page found, using first root section");
        List<Content> rootSections = contentRepository.findRootSectionsBySite(idSite);

        if (!rootSections.isEmpty()) {
            Section home = sectionMapper.toSection(rootSections.get(0));
            home.setContenuti(findPublishedContentsBySection(idSite, home.getId()));
            return home;
        }

        log.warn("No sections found for site: {}", idSite);
        return new Section();
    }

    private void loadMenuSubsections(Section section, String idSite, int maxDepth, int currentDepth) {
        if (currentDepth >= maxDepth) {
            log.warn("Raggiunta profondità massima ricorsione: {} per sezione: {}", maxDepth, section.getId());
            return;
        }

        try {
            List<Content> subsectionContents = contentRepository.findPublishedSubsections(
                    idSite,
                    section.getId().toString(),
                    "1",
                    "0"
            );

            if (subsectionContents.isEmpty()) {
                section.setSubsection(new ArrayList<>());
                return;
            }

            List<Section> subsections = new ArrayList<>();
            LocalDate today = LocalDate.now();

            for (Content content : subsectionContents) {

                if ("1".equals(content.getS(3))) {
                    try {
                        LocalDate startDate = parseScheduleDate(content.getS(1));
                        LocalDate endDate = parseScheduleDate(content.getS(2));

                        if (startDate != null && endDate != null) {
                            if (today.isBefore(startDate) || today.isAfter(endDate)) {
                                log.debug("Sezione {} esclusa: fuori periodo pubblicazione", content.getId());
                                continue;
                            }
                        }
                    } catch (Exception e) {
                        log.warn("Errore parsing date pubblicazione per sezione {}: {}", content.getId(), e.getMessage());
                        continue;
                    }
                }

                Section subsection = sectionMapper.toSection(content);
                loadMenuSubsections(subsection, idSite, maxDepth, currentDepth + 1);
                subsections.add(subsection);
            }

            section.setSubsection(subsections);

        } catch (Exception e) {
            log.error("Errore caricamento sottosezioni per sezione {}: {}", section.getId(), e.getMessage(), e);
            section.setSubsection(new ArrayList<>());
        }
    }

    private LocalDate parseScheduleDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) return null;

        String s = dateString.trim();
        try {
            return LocalDate.parse(s);
        } catch (Exception e1) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                return LocalDate.parse(s, formatter);
            } catch (Exception e2) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    return LocalDate.parse(s, formatter);
                } catch (Exception e3) {
                    log.warn("Formato data non riconosciuto: {}", s);
                    return null;
                }
            }
        }
    }

    // ========================================
    // METODI PER NEWSLETTER E SMS
    // ========================================

    public List<DatiBase> findContentsByNewsletter(String idSite, String newsletterId) {
        log.debug("Finding contents by newsletter: {} for site: {}", newsletterId, idSite);
        List<Content> contents = contentRepository.findContentsByNewsletter(idSite, newsletterId);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    public List<DatiBase> findContentsBySms(String idSite, String smsId) {
        log.debug("Finding contents by SMS: {} for site: {}", smsId, idSite);
        List<Content> contents = contentRepository.findContentsBySms(idSite, smsId);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    // ========================================
    // METODI PER CAMPI CUSTOM
    // ========================================

    public List<DatiBase> searchInTextFields(String idSite, String searchTerm) {
        log.debug("Searching in TEXT fields: {} for site: {}", searchTerm, idSite);
        List<Content> contents = contentRepository.searchInTextFields(idSite, searchTerm);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    public List<DatiBase> searchInVarcharFields(String idSite, String searchTerm) {
        log.debug("Searching in VARCHAR fields: {} for site: {}", searchTerm, idSite);
        List<Content> contents = contentRepository.searchInVarcharFields(idSite, searchTerm);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    public List<DatiBase> findContentsByNumberRange(String idSite, Double min, Double max) {
        log.debug("Finding contents by NUMBER range: {}-{} for site: {}", min, max, idSite);
        List<Content> contents = contentRepository.findContentsByNumberRange(idSite, min, max);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    public List<DatiBase> findContentsByDateRange(String idSite, LocalDate startDate, LocalDate endDate) {
        log.debug("Finding contents by DATE range: {}-{} for site: {}", startDate, endDate, idSite);
        List<Content> contents = contentRepository.findContentsByDateRange(idSite, startDate, endDate);
        return datiBaseMapper.toDatiBaseList(contents);
    }

    // ========================================
    // METODI HELPER PER ACCESSO CAMPI ESTESI
    // ========================================

    public String getCampoL(Integer contentId, int numeroL) {
        Optional<Content> contentOpt = contentRepository.findById(contentId);
        if (contentOpt.isEmpty()) return null;

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

    @Transactional
    public Integer incrementNumeratore(Integer contentId, int numeroNumeratore) {
        log.debug("Incrementing numeratore{} for content: {}", numeroNumeratore, contentId);

        Optional<Content> contentOpt = contentRepository.findById(contentId);
        if (contentOpt.isEmpty()) return null;

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

    public String getNumeratoreFormatted(Integer contentId, int numeroNumeratore) {
        Optional<Content> contentOpt = contentRepository.findById(contentId);
        if (contentOpt.isEmpty()) return "00000000";

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

    public SectionType getSectionTypeById(Integer id) {
        log.debug("Finding SectionType by id: {}", id);
        return sectionTypeRepository.findById(id).orElse(null);
    }
}