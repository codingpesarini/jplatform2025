package com.studiodomino.jplatform.cms.service;

import com.studiodomino.jplatform.cms.entity.Allegato;
import com.studiodomino.jplatform.cms.entity.DocAllegati;
import com.studiodomino.jplatform.cms.repository.AllegatoRepository;
import com.studiodomino.jplatform.cms.repository.DocAllegatiRepository;
import com.studiodomino.jplatform.shared.entity.Folder;
import com.studiodomino.jplatform.shared.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AllegatoService {

    private final AllegatoRepository allegatoRepository;
    private final DocAllegatiRepository docAllegatiRepository;
    private final FolderRepository folderRepository;

    @Value("${allegati.repository.path}")
    private String repositoryPath;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");
    private static final DecimalFormat ID_FORMATTER = new DecimalFormat("0000000000");

    // ========================================
    // CRUD ALLEGATI
    // ========================================

    public Optional<Allegato> findById(Integer id) {
        log.debug("Finding allegato by id: {}", id);
        Optional<Allegato> allegatoOpt = allegatoRepository.findById(id);
        allegatoOpt.ifPresent(allegato -> {
            if (allegato.getIdVersion() != null && !"0".equals(allegato.getIdVersion())) {
                allegato.setVersioni(findVersions(allegato.getIdVersion()));
            }
        });
        return allegatoOpt;
    }

    public List<Allegato> findByFolder(Integer idFolder) {
        log.debug("Finding allegati by folder: {}", idFolder);
        return allegatoRepository.findByFolder(idFolder);
    }

    public List<Allegato> findVersions(String idVersion) {
        log.debug("Finding versions for idVersion: {}", idVersion);
        return allegatoRepository.findVersions(idVersion);
    }

    public Optional<Allegato> findLatestVersion(String idVersion) {
        return allegatoRepository.findLatestVersion(idVersion);
    }

    public List<Allegato> findByTipo(Long tipo) {
        return allegatoRepository.findByTipo(tipo);
    }

    public List<Allegato> findByAnno(String anno) {
        return allegatoRepository.findByAnno(anno);
    }

    /**
     * Sposta una cartella sotto un nuovo genitore
     */
    @Transactional
    public void moveAllegato(String allegatoId, String targetFolderId) {
        Allegato allegato = allegatoRepository.findById(Integer.parseInt(allegatoId))
                .orElseThrow(() -> new RuntimeException("Allegato non trovato"));

        allegato.setIdFolder(Integer.parseInt(targetFolderId));
        allegatoRepository.save(allegato);
    }

    // ========================================
    // UPLOAD E SALVATAGGIO FILE
    // ========================================

    @Transactional
    public Allegato salvaAllegato(Allegato allegato, MultipartFile file) throws IOException {
        log.info("Saving allegato: {}", allegato.getNome());

        allegato.setSize(String.valueOf(file.getSize()));
        allegato.setImprontaSHA1(calculateSHA1(file.getInputStream()));

        Allegato saved = allegatoRepository.save(allegato);

        // Path: anno/0000000123.pdf
        String anno = String.valueOf(LocalDate.now().getYear());
        String paddedId = ID_FORMATTER.format(saved.getId());
        String relativePath = anno + "/" + paddedId + "." + allegato.getType();
        saved.setPath(relativePath);

        if ("0".equals(saved.getVersion())) {
            saved.setIdVersion(saved.getId().toString());
        }

        allegatoRepository.save(saved);
        saveFileToFileSystem(saved, file.getInputStream());

        log.info("Allegato saved with id: {}", saved.getId());
        return saved;
    }

    @Transactional
    public Allegato updateAllegato(Allegato allegato, MultipartFile file, String modoUpdate) throws IOException {
        log.info("Updating allegato: {} with mode: {}", allegato.getId(), modoUpdate);

        if ("semplice".equals(modoUpdate)) {
            return allegatoRepository.save(allegato);

        } else if ("cambia".equals(modoUpdate)) {
            // Sostituzione file — mantieni il path esistente nel DB
            allegato.setSize(String.valueOf(file.getSize()));
            allegato.setImprontaSHA1(calculateSHA1(file.getInputStream()));
            Allegato updated = allegatoRepository.save(allegato);
            saveFileToFileSystem(updated, file.getInputStream());
            return updated;

        } else if ("versione".equals(modoUpdate)) {
            String idVersionOriginale = allegato.getIdVersion();
            int currentVersion = Integer.parseInt(allegato.getVersion());
            String dataOra = LocalDateTime.now().format(DF);

            // Nuovo oggetto distaccato dal contesto Hibernate
            Allegato nuovaVersione = new Allegato();
            nuovaVersione.setL1(allegato.getL1());
            nuovaVersione.setL3(allegato.getL3());
            nuovaVersione.setNome(file.getOriginalFilename());
            nuovaVersione.setType(allegato.getType());
            nuovaVersione.setAnnotazioni(allegato.getAnnotazioni());
            nuovaVersione.setIdFolder(allegato.getIdFolder());
            nuovaVersione.setIdUtente(allegato.getIdUtente());
            nuovaVersione.setApertoDa(allegato.getApertoDa());
            nuovaVersione.setDataInserimento(dataOra);
            nuovaVersione.setDove(allegato.getDove());
            nuovaVersione.setIdDocAllegati(allegato.getIdDocAllegati());
            nuovaVersione.setVersion(String.valueOf(currentVersion + 1));
            nuovaVersione.setIdVersion(idVersionOriginale);
            nuovaVersione.setSize(String.valueOf(file.getSize()));
            nuovaVersione.setImprontaSHA1(calculateSHA1(file.getInputStream()));

            Allegato saved = allegatoRepository.saveAndFlush(nuovaVersione);

            // Path: anno/0000000123.pdf
            String anno = String.valueOf(LocalDate.now().getYear());
            saved.setPath(anno + "/" + ID_FORMATTER.format(saved.getId()) + "." + saved.getType());
            allegatoRepository.save(saved);

            saveFileToFileSystem(saved, file.getInputStream());

            log.info("Nuova versione creata: id={}, version={}, idVersion={}",
                    saved.getId(), saved.getVersion(), saved.getIdVersion());
            return saved;
        }

        return allegato;
    }

    @Transactional
    public void deleteAllegato(Integer id) throws IOException {
        log.info("Deleting allegato: {}", id);

        Optional<Allegato> allegatoOpt = allegatoRepository.findById(id);
        if (allegatoOpt.isEmpty()) {
            throw new IllegalArgumentException("Allegato not found: " + id);
        }

        Allegato allegato = allegatoOpt.get();
        deleteFileFromFileSystem(allegato);
        docAllegatiRepository.deleteByAllegato(id);

        if (allegato.getIdVersion() != null && !"0".equals(allegato.getIdVersion())) {
            List<Allegato> versioni = findVersions(allegato.getIdVersion());
            for (Allegato versione : versioni) {
                if (!versione.getId().equals(id)) {
                    deleteFileFromFileSystem(versione);
                    allegatoRepository.deleteById(versione.getId());
                }
            }
        }

        allegatoRepository.deleteById(id);
        log.info("Allegato deleted: {}", id);
    }

    // ========================================
    // GESTIONE COLLEGAMENTI DOCUMENTO-ALLEGATO
    // ========================================

    @Transactional
    public DocAllegati collegaAllegato(Integer idDocumento, Integer idAllegato,
                                       String user, String idUser) {
        log.info("Linking allegato {} to documento {}", idAllegato, idDocumento);

        if (!allegatoRepository.existsById(idAllegato)) {
            throw new IllegalArgumentException("Allegato not found: " + idAllegato);
        }

        List<DocAllegati> existing = docAllegatiRepository.findByDocumento(idDocumento);
        int maxOrdine = existing.stream().mapToInt(DocAllegati::getOrdine).max().orElse(0);

        DocAllegati docAllegati = new DocAllegati();
        docAllegati.setIdDocumento(idDocumento);
        docAllegati.setIdAllegato(idAllegato);
        docAllegati.setUser(user);
        docAllegati.setIdUser(idUser);
        docAllegati.setDataInsert(LocalDateTime.now().toString());
        docAllegati.setOrdine(maxOrdine + 1);

        return docAllegatiRepository.save(docAllegati);
    }

    @Transactional
    public void scollegaAllegato(Integer idDocAllegati) {
        log.info("Unlinking docAllegati: {}", idDocAllegati);
        docAllegatiRepository.deleteById(idDocAllegati);
    }

    public List<Allegato> findAllegatiByDocumento(Integer idDocumento) {
        log.debug("Finding allegati for documento: {}", idDocumento);
        List<DocAllegati> collegamenti = docAllegatiRepository.findByDocumento(idDocumento);
        return collegamenti.stream()
                .map(doc -> {
                    Optional<Allegato> allegatoOpt = findById(doc.getIdAllegato());
                    if (allegatoOpt.isPresent()) {
                        Allegato allegato = allegatoOpt.get();
                        allegato.setIdDocAllegati(doc.getId().toString());
                        allegato.setOrdine(doc.getOrdine().toString());
                        allegato.setLog(doc.getUser() + " - " + doc.getDataInsert());
                        return allegato;
                    }
                    return null;
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    public Long countAllegatiByDocumento(Integer idDocumento) {
        return docAllegatiRepository.countByDocumento(idDocumento);
    }

    // ========================================
    // GESTIONE FILE SYSTEM
    // ========================================

    private void saveFileToFileSystem(Allegato allegato, InputStream inputStream) throws IOException {
        Path filePath = Paths.get(repositoryPath).resolve(allegato.getPath());

        // Crea directory inclusa sottocartella anno
        Files.createDirectories(filePath.getParent());

        try (InputStream in = inputStream;
             OutputStream out = new FileOutputStream(filePath.toFile())) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        log.debug("File saved to: {}", filePath);
    }

    private void deleteFileFromFileSystem(Allegato allegato) throws IOException {
        Path filePath = Paths.get(repositoryPath, allegato.getPath());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            log.debug("File deleted: {}", filePath);
        }
    }

    public InputStream getFileInputStream(Integer id) throws IOException {
        Optional<Allegato> allegatoOpt = findById(id);
        if (allegatoOpt.isEmpty()) {
            throw new FileNotFoundException("Allegato not found: " + id);
        }

        Allegato allegato = allegatoOpt.get();
        Path filePath = Paths.get(repositoryPath, allegato.getPath());

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        return new FileInputStream(filePath.toFile());
    }

    // ========================================
    // UTILITY
    // ========================================

    private String calculateSHA1(InputStream inputStream) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
            byte[] hash = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("Error calculating SHA1", e);
            return "";
        }
    }

    public boolean exists(Integer id) {
        return allegatoRepository.existsById(id);
    }
}