package com.studiodomino.jplatform.shared.service;

import com.studiodomino.jplatform.shared.entity.Images;
import com.studiodomino.jplatform.shared.repository.ImagesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service per gestione Images
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImagesService {

    private final ImagesRepository imagesRepository;

    /**
     * Trova immagine per ID
     */
    public Optional<Images> findById(Integer id) {
        return imagesRepository.findById(id);
    }

    /**
     * Trova immagine per nome
     */
    public Optional<Images> findByName(String name) {
        return imagesRepository.findByName(name);
    }

    /**
     * Salva immagine
     */
    @Transactional
    public Images save(Images image) {
        return imagesRepository.save(image);
    }

    /**
     * Crea nuova immagine
     */
    @Transactional
    public Images createImage(String name, String type, Long size,
                              String pathname, String fullpath, String idfolder) {
        Images image = new Images();
        image.setName(name);
        image.setType(type);
        image.setSize(size.toString());
        image.setPathname(pathname);
        image.setFullpath(fullpath);
        image.setIdfolder(idfolder != null ? idfolder : "0");
        image.setTmpid(UUID.randomUUID().toString());

        log.debug("Creazione immagine: {} in folder: {}", name, idfolder);
        return imagesRepository.save(image);
    }

    /**
     * Ottieni tutte le immagini in un folder
     */
    public List<Images> getImagesByFolder(String idfolder) {
        return imagesRepository.findByIdfolderOrderByNameAsc(idfolder);
    }

    /**
     * Ottieni immagini pubbliche in un folder
     */
    public List<Images> getPublicImagesByFolder(String idfolder) {
        return imagesRepository.findPublicImagesByFolder(idfolder);
    }

    /**
     * Ottieni tutte le immagini pubbliche
     */
    public List<Images> getPublicImages() {
        return imagesRepository.findPublicImages();
    }

    /**
     * Ottieni tutte le immagini private
     */
    public List<Images> getPrivateImages() {
        return imagesRepository.findPrivateImages();
    }

    /**
     * Cerca immagini per nome
     */
    public List<Images> searchByName(String name) {
        return imagesRepository.findByNameContainingIgnoreCaseOrderByNameAsc(name);
    }

    /**
     * Cerca immagini per didascalia
     */
    public List<Images> searchByDidascalia(String didascalia) {
        return imagesRepository.findByDidascaliaContainingIgnoreCaseOrderByNameAsc(didascalia);
    }

    /**
     * Conta immagini in un folder
     */
    public Long countByFolder(String idfolder) {
        return imagesRepository.countByIdfolder(idfolder);
    }

    /**
     * Calcola dimensione totale folder
     */
    public Long getTotalSizeByFolder(String idfolder) {
        Long total = imagesRepository.sumSizeByFolder(idfolder);
        return total != null ? total : 0L;
    }

    /**
     * Ottieni dimensione totale formattata
     */
    public String getFormattedTotalSize(String idfolder) {
        Long bytes = getTotalSizeByFolder(idfolder);
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        }
    }

    /**
     * Elimina immagine
     */
    @Transactional
    public void deleteImage(Integer id, String repositoryPath) {
        Optional<Images> optionalImage = imagesRepository.findById(id);  // ✅ CORRETTO
        if (optionalImage.isPresent()) {
            Images image = optionalImage.get();

            // Elimina files fisici
            deletePhysicalFiles(image, repositoryPath);

            // Elimina da DB
            imagesRepository.deleteById(id);  // ✅ CORRETTO
            log.info("Immagine eliminata: {}", id);
        }
    }

    /**
     * Elimina array di immagini
     */
    @Transactional
    public void deleteImages(List<Integer> ids, String repositoryPath) {
        for (Integer id : ids) {
            deleteImage(id, repositoryPath);
        }
    }

    /**
     * Elimina tutte le immagini di un folder
     */
    @Transactional
    public void deleteByFolder(String idfolder, String repositoryPath) {
        List<Images> images = imagesRepository.findByIdfolderOrderByNameAsc(idfolder);
        for (Images image : images) {
            deletePhysicalFiles(image, repositoryPath);
        }
        imagesRepository.deleteByIdfolder(idfolder);  // ✅ Questo metodo va aggiunto al repository
        log.info("Eliminate {} immagini dal folder {}", images.size(), idfolder);
    }

    /**
     * Sposta immagine in altro folder
     */
    @Transactional
    public Images moveToFolder(Integer id, String newFolderId) {
        Optional<Images> optionalImage = imagesRepository.findById(id);
        if (optionalImage.isPresent()) {
            Images image = optionalImage.get();
            image.setIdfolder(newFolderId);
            log.info("Immagine {} spostata in folder {}", id, newFolderId);
            return imagesRepository.save(image);
        }
        throw new IllegalArgumentException("Immagine non trovata: " + id);
    }

    /**
     * Imposta come privata/pubblica
     */
    @Transactional
    public Images setPrivacy(Integer id, boolean privato) {
        Optional<Images> optionalImage = imagesRepository.findById(id);
        if (optionalImage.isPresent()) {
            Images image = optionalImage.get();
            image.setPrivato(privato ? "1" : "0");
            return imagesRepository.save(image);
        }
        throw new IllegalArgumentException("Immagine non trovata: " + id);
    }

    /**
     * Aggiorna didascalia
     */
    @Transactional
    public Images updateDidascalia(Integer id, String didascalia) {
        Optional<Images> optionalImage = imagesRepository.findById(id);
        if (optionalImage.isPresent()) {
            Images image = optionalImage.get();
            image.setDidascalia(didascalia);
            return imagesRepository.save(image);
        }
        throw new IllegalArgumentException("Immagine non trovata: " + id);
    }

    /**
     * Ottieni immagini senza thumbnail
     */
    public List<Images> getImagesWithoutThumbnail() {
        return imagesRepository.findImagesWithoutThumbnail();
    }

    /**
     * Ottieni ultime 10 immagini
     */
    public List<Images> getLatestImages() {
        return imagesRepository.findTop10ByOrderByIdDesc();
    }

    /**
     * Trova immagini per estensione
     */
    public List<Images> findByExtension(String extension) {
        return imagesRepository.findByExtension(extension);
    }

    /**
     * Elimina files fisici
     */
    private void deletePhysicalFiles(Images image, String repositoryPath) {
        try {
            // Elimina immagine principale
            if (image.getPathname() != null && !image.getPathname().isEmpty()) {  // ✅ CORRETTO
                Path imagePath = Paths.get(repositoryPath, image.getPathname());
                Files.deleteIfExists(imagePath);
            }

            // Elimina thumbnail
            if (image.getPaththumb() != null && !image.getPaththumb().isEmpty()) {  // ✅ CORRETTO
                Path thumbPath = Paths.get(repositoryPath, image.getPaththumb());
                Files.deleteIfExists(thumbPath);
            }

            log.debug("Files fisici eliminati per immagine: {}", image.getId());
        } catch (IOException e) {
            log.error("Errore eliminazione files per immagine: {}", image.getId(), e);
        }
    }

    /**
     * Verifica se immagine esiste
     */
    public boolean existsById(Integer id) {
        return imagesRepository.existsById(id);
    }

    /**
     * Conta tutte le immagini
     */
    public long countAll() {
        return imagesRepository.count();
    }

    /**
     * Ottieni statistiche folder
     */
    public FolderImageStats getStats(String idfolder) {
        Long count = countByFolder(idfolder);
        Long totalSize = getTotalSizeByFolder(idfolder);
        String formattedSize = getFormattedTotalSize(idfolder);

        return new FolderImageStats(count, totalSize, formattedSize);
    }

    /**
     * Record per statistiche folder
     */
    public record FolderImageStats(Long count, Long totalSize, String formattedSize) {}
}