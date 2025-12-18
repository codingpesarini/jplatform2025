package com.studiodomino.jplatform.cms.service;

import com.studiodomino.jplatform.cms.entity.Content;
import com.studiodomino.jplatform.cms.entity.DatiBase;
import com.studiodomino.jplatform.cms.entity.Section;
import com.studiodomino.jplatform.cms.mapper.ContentToDatiBaseMapper;
import com.studiodomino.jplatform.cms.mapper.ContentToSectionMapper;
import com.studiodomino.jplatform.cms.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
}