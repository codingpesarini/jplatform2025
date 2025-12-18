package com.studiodomino.jplatform.cms.service;

import com.studiodomino.jplatform.cms.entity.Allegato;
import com.studiodomino.jplatform.cms.entity.DocAllegati;
import com.studiodomino.jplatform.cms.repository.AllegatoRepository;
import com.studiodomino.jplatform.cms.repository.DocAllegatiRepository;
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
import java.util.List;
import java.util.Optional;

/**
 * Service per la gestione completa degli allegati:
 * - CRUD allegati
 * - Upload/Download file
 * - Gestione versioni
 * - Collegamento con documenti
 * - Calcolo SHA1
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AllegatoService {

    private final AllegatoRepository allegatoRepository;
    private final DocAllegatiRepository docAllegatiRepository;

    @Value("${allegati.repository.path:/var/jplatform/repository/}")
    private String repositoryPath;

    // ========================================
    // CRUD ALLEGATI
    // ========================================

    /**
     * Trova allegato per ID
     */
    public Optional<Allegato> findById(Integer id) {
        log.debug("Finding allegato by id: {}", id);
        Optional<Allegato> allegatoOpt = allegatoRepository.findById(id);

        // Carica versioni se esiste
        allegatoOpt.ifPresent(allegato -> {
            if (!"0".equals(allegato.getIdVersion())) {
                allegato.setVersioni(findVersions(allegato.getIdVersion()));
            }
        });

        return allegatoOpt;
    }

    /**
     * Trova allegati per folder
     */
    public List<Allegato> findByFolder(Integer idFolder) {
        log.debug("Finding allegati by folder: {}", idFolder);
        return allegatoRepository.findByFolder(idFolder);
    }

    /**
     * Trova versioni di un allegato
     */
    public List<Allegato> findVersions(String idVersion) {
        log.debug("Finding versions for idVersion: {}", idVersion);
        return allegatoRepository.findVersions(idVersion);
    }

    /**
     * Trova ultima versione
     */
    public Optional<Allegato> findLatestVersion(String idVersion) {
        return allegatoRepository.findLatestVersion(idVersion);
    }

    /**
     * Trova allegati per tipo
     */
    public List<Allegato> findByTipo(Long tipo) {
        return allegatoRepository.findByTipo(tipo);
    }

    /**
     * Trova allegati per anno
     */
    public List<Allegato> findByAnno(String anno) {
        return allegatoRepository.findByAnno(anno);
    }

    // ========================================
    // UPLOAD E SALVATAGGIO FILE
    // ========================================

    /**
     * Salva allegato con file upload
     */
    @Transactional
    public Allegato salvaAllegato(Allegato allegato, MultipartFile file) throws IOException {
        log.info("Saving allegato: {}", allegato.getNome());

        // Imposta dimensione file
        allegato.setSize(String.valueOf(file.getSize()));

        // Calcola SHA1 prima del salvataggio
        allegato.setImprontaSHA1(calculateSHA1(file.getInputStream()));

        // Salva nel database
        Allegato saved = allegatoRepository.save(allegato);

        // Formatta ID con padding (es: 0000000123)
        DecimalFormat formatter = new DecimalFormat("0000000000");
        String paddedId = formatter.format(saved.getId());

        // Aggiorna path relativo
        String relativePath = paddedId + "." + allegato.getType();
        saved.setPath(relativePath);

        // Se è versione 0, imposta idVersion = id
        if ("0".equals(saved.getVersion())) {
            saved.setIdVersion(saved.getId().toString());
        }

        allegatoRepository.save(saved);

        // Salva file fisico
        saveFileToFileSystem(saved, file.getInputStream());

        log.info("Allegato saved with id: {}", saved.getId());
        return saved;
    }

    /**
     * Aggiorna allegato esistente
     */
    @Transactional
    public Allegato updateAllegato(Allegato allegato, MultipartFile file, String modoUpdate) throws IOException {
        log.info("Updating allegato: {} with mode: {}", allegato.getId(), modoUpdate);

        if ("semplice".equals(modoUpdate)) {
            // Aggiornamento solo metadati
            return allegatoRepository.save(allegato);

        } else if ("cambia".equals(modoUpdate)) {
            // Aggiornamento con sostituzione file
            allegato.setSize(String.valueOf(file.getSize()));
            allegato.setImprontaSHA1(calculateSHA1(file.getInputStream()));

            Allegato updated = allegatoRepository.save(allegato);
            saveFileToFileSystem(updated, file.getInputStream());

            return updated;

        } else if ("versione".equals(modoUpdate)) {
            // Crea nuova versione
            int currentVersion = Integer.parseInt(allegato.getVersion());
            allegato.setVersion(String.valueOf(currentVersion + 1));
            allegato.setId(null); // Force insert

            return salvaAllegato(allegato, file);
        }

        return allegato;
    }

    /**
     * Elimina allegato (file + DB)
     */
    @Transactional
    public void deleteAllegato(Integer id) throws IOException {
        log.info("Deleting allegato: {}", id);

        Optional<Allegato> allegatoOpt = allegatoRepository.findById(id);
        if (allegatoOpt.isEmpty()) {
            throw new IllegalArgumentException("Allegato not found: " + id);
        }

        Allegato allegato = allegatoOpt.get();

        // Elimina file fisico
        deleteFileFromFileSystem(allegato);

        // Elimina collegamenti in docallegati
        docAllegatiRepository.deleteByAllegato(id);

        // Elimina versioni associate
        if (!"0".equals(allegato.getIdVersion())) {
            List<Allegato> versioni = findVersions(allegato.getIdVersion());
            for (Allegato versione : versioni) {
                if (!versione.getId().equals(id)) {
                    deleteFileFromFileSystem(versione);
                    allegatoRepository.deleteById(versione.getId());
                }
            }
        }

        // Elimina allegato
        allegatoRepository.deleteById(id);

        log.info("Allegato deleted: {}", id);
    }

    // ========================================
    // GESTIONE COLLEGAMENTI DOCUMENTO-ALLEGATO
    // ========================================

    /**
     * Collega un allegato a un documento
     */
    @Transactional
    public DocAllegati collegaAllegato(Integer idDocumento, Integer idAllegato,
                                       String user, String idUser) {
        log.info("Linking allegato {} to documento {}", idAllegato, idDocumento);

        // Verifica allegato esiste
        if (!allegatoRepository.existsById(idAllegato)) {
            throw new IllegalArgumentException("Allegato not found: " + idAllegato);
        }

        // Calcola ordine (max + 1)
        List<DocAllegati> existing = docAllegatiRepository.findByDocumento(idDocumento);
        int maxOrdine = existing.stream()
                .mapToInt(DocAllegati::getOrdine)
                .max()
                .orElse(0);

        DocAllegati docAllegati = new DocAllegati();
        docAllegati.setIdDocumento(idDocumento);
        docAllegati.setIdAllegato(idAllegato);
        docAllegati.setUser(user);
        docAllegati.setIdUser(idUser);
        docAllegati.setDataInsert(java.time.LocalDateTime.now().toString());
        docAllegati.setOrdine(maxOrdine + 1);

        return docAllegatiRepository.save(docAllegati);
    }

    /**
     * Scollega un allegato da un documento
     */
    @Transactional
    public void scollegaAllegato(Integer idDocAllegati) {
        log.info("Unlinking docAllegati: {}", idDocAllegati);
        docAllegatiRepository.deleteById(idDocAllegati);
    }

    /**
     * Trova tutti gli allegati di un documento (con oggetti Allegato popolati)
     */
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

    /**
     * Conta allegati per documento
     */
    public Long countAllegatiByDocumento(Integer idDocumento) {
        return docAllegatiRepository.countByDocumento(idDocumento);
    }

    // ========================================
    // GESTIONE FILE SYSTEM
    // ========================================

    /**
     * Salva file nel filesystem
     */
    private void saveFileToFileSystem(Allegato allegato, InputStream inputStream) throws IOException {
        // Crea directory se non esiste
        Path directory = Paths.get(repositoryPath);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        // Path completo del file
        Path filePath = directory.resolve(allegato.getPath());

        // Salva file
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

    /**
     * Elimina file dal filesystem
     */
    private void deleteFileFromFileSystem(Allegato allegato) throws IOException {
        Path filePath = Paths.get(repositoryPath, allegato.getPath());

        if (Files.exists(filePath)) {
            Files.delete(filePath);
            log.debug("File deleted: {}", filePath);
        }
    }

    /**
     * Ottiene InputStream per download file
     */
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

    /**
     * Calcola SHA1 di un file
     */
    private String calculateSHA1(InputStream inputStream) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }

            byte[] hash = md.digest();

            // Converti in stringa hex
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

    /**
     * Verifica esistenza allegato
     */
    public boolean exists(Integer id) {
        return allegatoRepository.existsById(id);
    }
}