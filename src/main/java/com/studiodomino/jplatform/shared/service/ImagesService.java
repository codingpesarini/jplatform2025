package com.studiodomino.jplatform.shared.service;

import com.studiodomino.jplatform.shared.entity.Images;
import com.studiodomino.jplatform.shared.repository.ImagesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    @Transactional
    public void delete(Integer id) {
        log.debug("Deleting image: {}", id);

        // Trova immagine
        Optional<Images> imageOpt = imagesRepository.findById(id);

        if (imageOpt.isPresent()) {
            Images image = imageOpt.get();

            // Elimina file fisico
            deletePhysicalFile(image.getPathname());

            // Elimina thumbnail
            if (image.getPaththumb() != null) {
                deletePhysicalFile(image.getPaththumb());
            }

            // Elimina da database
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

        // Genera nome unico
        String uniqueName = UUID.randomUUID().toString() + "_" +
                file.getOriginalFilename();

        // Crea path
        Path uploadPath = Paths.get(imagesRepository, idfolder);
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
        image.setFullpath(idfolder + "/" + uniqueName);
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