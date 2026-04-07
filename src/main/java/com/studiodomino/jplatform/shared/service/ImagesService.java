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

    /**
     * Trova tutte le immagini
     */
    public List<Images> findAll() {
        return imagesRepository.findAll();
    }

    /**
     * Trova immagine per ID
     */
    public Optional<Images> findById(Integer id) {
        return imagesRepository.findById(id);
    }

    /**
     * Trova immagini per folder
     */
    public List<Images> findByFolder(String idfolder) {
        return imagesRepository.findByIdfolder(idfolder);
    }

    /**
     * Trova immagini pubbliche
     */
    public List<Images> findPublicImages() {
        return imagesRepository.findByPrivato("0");
    }

    /**
     * Salva immagine
     */
    @Transactional
    public Images save(Images image) {
        log.debug("Saving image: {}", image.getName());
        return imagesRepository.save(image);
    }

    /**
     * Elimina immagine
     */

    /**
     * Sposta un'immagine in una nuova cartella
     */
    @Transactional
    public void moveImage(String imageId, String targetFolderId) {
        // 1. Recupero l'immagine (usa l'ID corretto, solitamente Long o Integer)
        Images image = imagesRepository.findById(Integer.parseInt(imageId))
                .orElseThrow(() -> new RuntimeException("Immagine non trovata"));

        // 2. Aggiorno l'ID della cartella di destinazione
        // Nota: assicurati che il campo nell'entità Images si chiami 'idfolder' o 'folder'
        image.setIdfolder(targetFolderId);

        // 3. Salvo l'immagine nella nuova posizione
        imagesRepository.save(image);
    }

    @Value("${upload.path}")
    private String uploadPath;
    @Transactional
    public void delete(Integer id) {
        log.debug("Deleting image: {}", id);

        Optional<Images> imageOpt = imagesRepository.findById(id);
        if (imageOpt.isPresent()) {
            Images image = imageOpt.get();

            // fullpath è "2026/uuid_nome.jpg" — costruisci il path assoluto
            if (image.getFullpath() != null && !image.getFullpath().isEmpty()) {
                try {
                    Path filePath = Paths.get(uploadPath, image.getFullpath());
                    Files.deleteIfExists(filePath);
                    log.debug("Deleted file: {}", filePath);
                } catch (IOException e) {
                    log.error("Error deleting file: {}", image.getFullpath(), e);
                }
            }

            imagesRepository.deleteById(id);
        }
    }

    /**
     * Elimina file fisico
     */
    private void deletePhysicalFile(String filepath) {
        if (filepath == null || filepath.isEmpty()) {
            return;
        }

        try {
            Path path = Paths.get(filepath);
            Files.deleteIfExists(path);
            log.debug("Deleted file: {}", filepath);
        } catch (IOException e) {
            log.error("Error deleting file: {}", filepath, e);
        }
    }

    /**
     * Upload immagine
     */
    @Transactional
    public Images uploadImage(MultipartFile file, String idfolder,
                              String imagesRepository) throws IOException {

        log.debug("Uploading image: {}", file.getOriginalFilename());

        // Cartella per anno (es: 2026)
        String anno = String.valueOf(java.time.LocalDate.now().getYear());

        // Genera nome unico
        String uniqueName = UUID.randomUUID().toString() + "_" +
                file.getOriginalFilename();

        // Crea path con solo l'anno come sottocartella
        Path uploadPath = Paths.get(imagesRepository, anno);
        Files.createDirectories(uploadPath);

        // Salva file
        Path filePath = uploadPath.resolve(uniqueName);
        Files.copy(file.getInputStream(), filePath);

        // Crea entity
        Images image = new Images();
        image.setName(file.getOriginalFilename());
        image.setType(file.getContentType());
        image.setSize(String.valueOf(file.getSize()));
        image.setIdfolder(idfolder);
        image.setPathname(uniqueName);
        image.setFullpath(anno + "/" + uniqueName);  // solo il path relativo
        image.setPrivato("0");

        return save(image);
    }

    /**
     * Conta immagini per folder
     */
    public Long countByFolder(String idfolder) {
        return imagesRepository.countByIdfolder(idfolder);
    }

    /**
     * Calcola dimensione totale folder
     */
    public Long getTotalSizeByFolder(String idfolder) {
        Long size = imagesRepository.sumSizeByFolder(idfolder);
        return size != null ? size : 0L;
    }

    /**
     * Cerca immagini
     */
    public List<Images> searchImages(String query) {
        return imagesRepository.searchByName(query);
    }
}