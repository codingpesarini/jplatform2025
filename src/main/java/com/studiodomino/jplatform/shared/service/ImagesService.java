package com.studiodomino.jplatform.shared.service;

import com.studiodomino.jplatform.shared.entity.Images;
import com.studiodomino.jplatform.shared.repository.ImagesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ImagesService {

    private final ImagesRepository imagesRepository;

    @Value("${upload.path}")
    private String uploadPath;

    // =====================================================================
    // FIND
    // =====================================================================

    public List<Images> findAll() {
        return imagesRepository.findAll();
    }

    public Optional<Images> findById(Integer id) {
        return imagesRepository.findById(id);
    }

    public List<Images> findByFolder(String idfolder) {
        return imagesRepository.findByIdfolder(idfolder);
    }

    public List<Images> findPublicImages() {
        return imagesRepository.findByPrivato("0");
    }

    public List<Images> searchImages(String query) {
        return imagesRepository.searchByName(query);
    }

    public Long countByFolder(String idfolder) {
        return imagesRepository.countByIdfolder(idfolder);
    }

    public Long getTotalSizeByFolder(String idfolder) {
        Long size = imagesRepository.sumSizeByFolder(idfolder);
        return size != null ? size : 0L;
    }

    // =====================================================================
    // SAVE
    // =====================================================================

    @Transactional
    public Images save(Images image) {
        log.debug("Saving image: {}", image.getName());
        return imagesRepository.save(image);
    }

    // =====================================================================
    // UPLOAD
    // =====================================================================

    @Transactional
    public Images uploadImage(MultipartFile file, String idfolder,
                              String imagesRepositoryPath) throws IOException {

        log.debug("Uploading image: {}", file.getOriginalFilename());

        // Sottocartella per anno (es: 2026)
        String anno = String.valueOf(java.time.LocalDate.now().getYear());

        // Nome file univoco
        String uniqueName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // Crea directory se non esiste
        Path uploadDir = Paths.get(imagesRepositoryPath, anno);
        Files.createDirectories(uploadDir);

        // Salva file su disco
        Path filePath = uploadDir.resolve(uniqueName);
        Files.copy(file.getInputStream(), filePath);

        log.info("Image saved to disk: {}", filePath);

        // Crea entity
        Images image = new Images();
        image.setName(file.getOriginalFilename());
        image.setType(file.getContentType());
        image.setSize(String.valueOf(file.getSize()));
        image.setIdfolder(idfolder);
        image.setPathname(uniqueName);
        image.setFullpath(anno + "/" + uniqueName);
        image.setPrivato("0");

        return save(image);
    }

    // =====================================================================
    // DELETE
    // =====================================================================

    @Transactional
    public void delete(Integer id) {
        log.debug("Deleting image id: {}", id);

        imagesRepository.findById(id).ifPresent(image -> {
            // Elimina file fisico dal disco
            deletePhysicalFile(image.getFullpath());
            // Elimina record dal DB
            imagesRepository.deleteById(id);
            log.info("Image deleted: id={}, path={}", id, image.getFullpath());
        });
    }

    // =====================================================================
    // MOVE
    // =====================================================================

    @Transactional
    public void moveImage(String imageId, String targetFolderId) {
        Images image = imagesRepository.findById(Integer.parseInt(imageId))
                .orElseThrow(() -> new RuntimeException("Immagine non trovata: " + imageId));
        image.setIdfolder(targetFolderId);
        imagesRepository.save(image);
        log.info("Image moved: id={} -> folder={}", imageId, targetFolderId);
    }

    // =====================================================================
    // HELPER PRIVATO — elimina file fisico
    // Usa sempre uploadPath iniettato da Spring (funziona in locale e Docker)
    // =====================================================================

    private void deletePhysicalFile(String fullpath) {
        if (fullpath == null || fullpath.isEmpty()) return;
        try {
            Path filePath = Paths.get(uploadPath, fullpath);
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.info("File fisico eliminato: {}", filePath);
            } else {
                log.warn("File fisico non trovato (già eliminato?): {}", filePath);
            }
        } catch (IOException e) {
            log.error("Errore eliminazione file fisico: {}", fullpath, e);
        }
    }
}