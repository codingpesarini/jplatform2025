package com.studiodomino.jplatform.shared.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileUtils {

    /**
     * Salva file caricato con nome univoco
     */
    public static String saveUploadedFile(MultipartFile file, String uploadDir) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File vuoto");
        }

        // Crea directory se non esiste
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Genera nome univoco
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String uniqueFilename = UUID.randomUUID().toString() + extension;

        // Salva file
        Path filePath = Paths.get(uploadDir, uniqueFilename);
        Files.copy(file.getInputStream(), filePath);

        return uniqueFilename;
    }

    /**
     * Cancella file
     */
    public static boolean deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            return file.delete();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Ottieni estensione file
     */
    public static String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * Verifica se è un'immagine
     */
    public static boolean isImage(String filename) {
        String ext = getExtension(filename);
        return ext.equals("jpg") || ext.equals("jpeg") ||
                ext.equals("png") || ext.equals("gif") ||
                ext.equals("webp");
    }

    /**
     * Verifica se è un PDF
     */
    public static boolean isPdf(String filename) {
        return "pdf".equals(getExtension(filename));
    }

    /**
     * Ottieni dimensione file formattata
     */
    public static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }
}