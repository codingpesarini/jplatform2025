package com.studiodomino.jplatform.cms.admin.controller;

import com.studiodomino.jplatform.cms.entity.Allegato;
import com.studiodomino.jplatform.cms.service.AllegatoService;
import com.studiodomino.jplatform.shared.config.Configurazione;
import com.studiodomino.jplatform.shared.entity.Folder;
import com.studiodomino.jplatform.shared.entity.Images;
import com.studiodomino.jplatform.shared.service.ConfigurazioneService;
import com.studiodomino.jplatform.shared.service.FolderService;
import com.studiodomino.jplatform.shared.service.ImagesService;
import com.studiodomino.jplatform.shared.util.ViewUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/filemanager")
@RequiredArgsConstructor
@Slf4j
public class FileManagerController {

    private final ConfigurazioneService configurazioneService;
    private final FolderService folderService;
    private final ImagesService imagesService;
    private final AllegatoService allegatoService;

    @Value("${allegati.repository.path:/var/jplatform/repository/}")
    private String repositoryPath;

    @Value("${images.repository.path:/var/jplatform/images/}")
    private String imagesRepositoryPath;

    private static final DateTimeFormatter DF =
            DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");

    // =====================================================================
    // MEDIA LIBRARY — IMMAGINI (pagina principale)
    // =====================================================================

    // =====================================================================
// MEDIA LIBRARY — IMMAGINI
// =====================================================================

    @GetMapping("/images")
    public String mediaLibraryImages(
            @RequestParam(value = "idfolder", defaultValue = "1") String idFolder,
            @RequestParam(value = "sorgente", defaultValue = "") String sorgente,
            @RequestParam(value = "metodo", defaultValue = "tutti") String metodo,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            int idFolderInt = Integer.parseInt(idFolder);
            List<Folder> subfolders   = folderService.getSubfolders(idFolder);
            List<Images> images       = imagesService.findByFolder(idFolder);
            List<Allegato> allegati   = allegatoService.findByFolder(idFolderInt);
            List<Folder> folderPath   = folderService.getFolderPath(idFolderInt);
            List<Folder> allFolders   = folderService.getRootFolders();

            Folder currentFolderObj = folderService.findById(idFolderInt).orElse(new Folder());
            currentFolderObj.setSubfolder(subfolders);
            currentFolderObj.setNumeroFolder(String.valueOf(subfolders.size()));
            currentFolderObj.setNumeroImmagini(String.valueOf(images.size()));
            currentFolderObj.setNumeroAllegati(String.valueOf(allegati.size()));

            model.addAttribute("currentFolderObj", currentFolderObj);
            model.addAttribute("subfolders",   subfolders);
            model.addAttribute("images",       images);
            model.addAttribute("allegati",     allegati);
            model.addAttribute("folderPath",   folderPath);
            model.addAttribute("allFolders",   allFolders);
            model.addAttribute("currentFolder", idFolder);
            model.addAttribute("sorgente",     sorgente);
            model.addAttribute("metodo",       metodo);
            model.addAttribute("config",       config);

        } catch (Exception e) {
            log.error("Errore mediaLibraryImages folder={}", idFolder, e);
        }

        return ViewUtils.resolveProtectedTemplate("filemanager/mediaLibraryImages");
    }

    // URL pulito: /admin/filemanager/images/1
    @GetMapping("/images/{idfolder}")
    public String mediaLibraryImagesByPath(
            @PathVariable String idfolder,
            @RequestParam(value = "sorgente", defaultValue = "") String sorgente,
            @RequestParam(value = "metodo", defaultValue = "tutti") String metodo,
            HttpServletRequest request, Model model) {
        return mediaLibraryImages(idfolder, sorgente, metodo, request, model);
    }

// =====================================================================
// MEDIA LIBRARY — ALLEGATI
// =====================================================================

    @GetMapping("/files")
    public String mediaLibraryFiles(
            @RequestParam(value = "idfolder", defaultValue = "1") String idFolder,
            @RequestParam(value = "sorgente", defaultValue = "") String sorgente,
            @RequestParam(value = "metodo", defaultValue = "tutti") String metodo,
            @RequestParam(value = "docid", defaultValue = "0") String docId,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            int idFolderInt = Integer.parseInt(idFolder);
            List<Folder> subfolders = folderService.getSubfolders(idFolder);
            List<Allegato> allegati = allegatoService.findByFolder(idFolderInt);
            List<Folder> folderPath = folderService.getFolderPath(idFolderInt);
            List<Folder> allFolders = folderService.getRootFolders();

            Folder currentFolderObj = folderService.findById(idFolderInt).orElse(new Folder());
            currentFolderObj.setSubfolder(subfolders);
            currentFolderObj.setNumeroFolder(String.valueOf(subfolders.size()));
            currentFolderObj.setNumeroAllegati(String.valueOf(allegati.size()));

            model.addAttribute("currentFolderObj", currentFolderObj);
            model.addAttribute("subfolders",  subfolders);
            model.addAttribute("allegati",    allegati);
            model.addAttribute("folderPath",  folderPath);
            model.addAttribute("allFolders",  allFolders);
            model.addAttribute("currentFolder", idFolder);
            model.addAttribute("sorgente",    sorgente);
            model.addAttribute("metodo",      metodo);
            model.addAttribute("docId",       docId);
            model.addAttribute("config",      config);

        } catch (Exception e) {
            log.error("Errore mediaLibraryFiles folder={}", idFolder, e);
        }

        return ViewUtils.resolveProtectedTemplate("filemanager/mediaLibraryFiles");
    }

    // URL pulito: /admin/filemanager/files/1
    @GetMapping("/files/{idfolder}")
    public String mediaLibraryFilesByPath(
            @PathVariable String idfolder,
            @RequestParam(value = "sorgente", defaultValue = "") String sorgente,
            @RequestParam(value = "metodo", defaultValue = "tutti") String metodo,
            @RequestParam(value = "docid", defaultValue = "0") String docId,
            HttpServletRequest request, Model model) {
        return mediaLibraryFiles(idfolder, sorgente, metodo, docId, request, model);
    }

    // =====================================================================
    // FORM IMMAGINE — aperto in popup
    // =====================================================================

    @GetMapping("/images/new")
    public String newImage(
            @RequestParam(value = "idfolder", defaultValue = "1") String idFolder,
            @RequestParam(value = "sorgente", defaultValue = "") String sorgente,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        List<Folder> folders = folderService.getAllFolders();
        model.addAttribute("image", new Images());
        model.addAttribute("folders", folders);
        model.addAttribute("currentFolder", idFolder);
        model.addAttribute("sorgente", sorgente);
        model.addAttribute("config", config);

        return ViewUtils.resolveProtectedTemplate("filemanager/images");
    }

    @GetMapping("/images/new/{idfolder}")
    public String newImageByPath(
            @PathVariable String idfolder,
            @RequestParam(value = "sorgente", defaultValue = "") String sorgente,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        List<Folder> folders = folderService.getAllFolders(); // usa getAllFolders invece di getRootFolders
        model.addAttribute("image", new Images());
        model.addAttribute("folders", folders);
        model.addAttribute("currentFolder", idfolder);
        model.addAttribute("sorgente", sorgente);
        model.addAttribute("config", config);

        return ViewUtils.resolveProtectedTemplate("filemanager/images");
    }


    @GetMapping("/images/{id}/edit")
    public String editImage(
            @PathVariable Integer id,
            @RequestParam(value = "idfolder", defaultValue = "1") String idFolder,
            @RequestParam(value = "sorgente", defaultValue = "") String sorgente,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            Images image = imagesService.findById(id).orElseThrow();
            List<Folder> folders = folderService.getAllFolders();
            model.addAttribute("image", image);
            model.addAttribute("folders", folders);
            model.addAttribute("currentFolder", idFolder);
            model.addAttribute("sorgente", sorgente);
            model.addAttribute("config", config);
        } catch (Exception e) {
            log.error("Errore editImage id={}", id, e);
        }

        return ViewUtils.resolveProtectedTemplate("filemanager/images");
    }

    // =====================================================================
    // SAVE IMMAGINE (da form popup)
    // =====================================================================

    @PostMapping("/images/save")
    public String saveImage(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "didascalia", defaultValue = "") String didascalia,
            @RequestParam(value = "idfolder", defaultValue = "1") String idFolder,
            @RequestParam(value = "sorgente", defaultValue = "") String sorgente,
            @RequestParam(value = "filer", required = false) MultipartFile filer,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            Images image;
            if (id == null || id <= 0) {
                // Nuova immagine
                image = imagesService.uploadImage(filer, idFolder, imagesRepositoryPath);
            } else {
                // Aggiorna esistente
                image = imagesService.findById(id).orElseThrow();
                if (filer != null && !filer.isEmpty()) {
                    imagesService.delete(id);
                    image = imagesService.uploadImage(filer, idFolder, imagesRepositoryPath);
                }
            }
            if (!name.isEmpty()) image.setName(name);
            if (!didascalia.isEmpty()) image.setDidascalia(didascalia);
            image.setL4(config.getAmministratore().getNomeCompleto());
            image.setL5(LocalDateTime.now().format(DF));
            imagesService.save(image);

        } catch (Exception e) {
            log.error("Errore saveImage", e);
        }

        // Ricarica il popup sulla cartella corrente
        return "redirect:/admin/filemanager/images/new/" + idFolder;
    }

    // =====================================================================
    // UPLOAD IMMAGINE AJAX (singola)
    // =====================================================================

    @PostMapping("/images/upload")
    @ResponseBody
    public Map<String, Object> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "idfolder", defaultValue = "1") String idFolder,
            @RequestParam(value = "nome", defaultValue = "") String nome,
            @RequestParam(value = "didascalia", defaultValue = "") String didascalia,
            HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);

        if (!config.isLogged()) {
            result.put("success", false);
            result.put("error", "Non autorizzato");
            return result;
        }

        try {
            Images image = imagesService.uploadImage(file, idFolder, imagesRepositoryPath);
            if (!nome.isEmpty()) image.setName(nome);
            if (!didascalia.isEmpty()) image.setDidascalia(didascalia);
            image.setL4(config.getAmministratore().getNomeCompleto());
            image.setL5(LocalDateTime.now().format(DF));
            imagesService.save(image);

            result.put("success", true);
            result.put("id", image.getId());
            result.put("path", image.getFullpath());
            result.put("nome", image.getName());
        } catch (Exception e) {
            log.error("Errore upload immagine", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    // =====================================================================
    // UPLOAD IMMAGINE AJAX (multipla)
    // =====================================================================

    @PostMapping("/images/upload-multiple")
    @ResponseBody
    public Map<String, Object> uploadImageMultiple(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "idfolder", defaultValue = "1") String idFolder,
            HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);

        if (!config.isLogged()) {
            result.put("success", false);
            return result;
        }

        int ok = 0, ko = 0;
        for (MultipartFile file : files) {
            try {
                Images image = imagesService.uploadImage(file, idFolder, imagesRepositoryPath);
                image.setL4(config.getAmministratore().getNomeCompleto());
                image.setL5(LocalDateTime.now().format(DF));
                imagesService.save(image);
                ok++;
            } catch (Exception e) {
                log.error("Errore upload file: {}", file.getOriginalFilename(), e);
                ko++;
            }
        }

        result.put("success", true);
        result.put("uploaded", ok);
        result.put("errors", ko);
        return result;
    }

    // =====================================================================
    // ELIMINA IMMAGINE
    // =====================================================================

    @PostMapping("/images/{id}/delete")
    @ResponseBody
    public ResponseEntity<String> deleteImage(
            @PathVariable Integer id,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return ResponseEntity.status(401).body("KO");

        try {
            imagesService.delete(id);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Errore eliminazione immagine id={}", id, e);
            return ResponseEntity.internalServerError().body("KO");
        }
    }

    // =====================================================================
    // FORM ALLEGATO — aperto in popup
    // =====================================================================

    @GetMapping("/files/new")
    public String newAllegato(
            @RequestParam(value = "idfolder", defaultValue = "1") String idFolder,
            @RequestParam(value = "docid", defaultValue = "0") String docId,
            @RequestParam(value = "sorgente", defaultValue = "") String sorgente,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        Allegato allegato = new Allegato();
        allegato.setIdDocAllegati(docId);
        allegato.setIdFolder(Integer.parseInt(idFolder));

        List<Folder> folders = folderService.getAllFolders();
        model.addAttribute("allegato", allegato);
        model.addAttribute("folders", folders);
        model.addAttribute("currentFolder", idFolder);
        model.addAttribute("sorgente", sorgente);
        model.addAttribute("config", config);

        return ViewUtils.resolveProtectedTemplate("filemanager/allegato");
    }

    @GetMapping("/files/{id}/edit")
    public String editAllegato(
            @PathVariable Integer id,
            @RequestParam(value = "idfolder", defaultValue = "1") String idFolder,
            @RequestParam(value = "sorgente", defaultValue = "") String sorgente,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            Allegato allegato = allegatoService.findById(id).orElseThrow();
            List<Folder> folders = folderService.getAllFolders();
            model.addAttribute("allegato", allegato);
            model.addAttribute("folders", folders);
            model.addAttribute("currentFolder", idFolder);
            model.addAttribute("sorgente", sorgente);
            model.addAttribute("config", config);
        } catch (Exception e) {
            log.error("Errore editAllegato id={}", id, e);
        }

        return ViewUtils.resolveProtectedTemplate("filemanager/allegato");
    }

    // =====================================================================
    // UPLOAD ALLEGATO AJAX
    // =====================================================================

    @PostMapping("/files/upload")
    @ResponseBody
    public Map<String, Object> uploadAllegato(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "idfolder", defaultValue = "1") String idFolder,
            @RequestParam(value = "docid", defaultValue = "0") String docId,
            @RequestParam(value = "nome", defaultValue = "") String nome,
            @RequestParam(value = "annotazioni", defaultValue = "") String annotazioni,
            HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);

        if (!config.isLogged()) {
            result.put("success", false);
            return result;
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf(".") + 1) : "bin";
            String nomeFile = !nome.isEmpty() ? nome
                    : (originalFilename != null
                    ? originalFilename.substring(0, originalFilename.lastIndexOf(".")) : "file");

            Allegato allegato = new Allegato();
            allegato.setIdFolder(Integer.parseInt(idFolder));
            allegato.setL1(nomeFile);
            allegato.setNome(originalFilename);
            allegato.setType(ext);
            allegato.setVersion("0");
            allegato.setAnnotazioni(annotazioni.isEmpty() ? nomeFile : annotazioni);
            allegato.setIdUtente(config.getAmministratore().getId().toString());
            allegato.setApertoDa(config.getAmministratore().getNomeCompleto());
            allegato.setDataInserimento(LocalDateTime.now().format(DF));
            allegato.setDove("file");
            allegato.setIdDocAllegati(docId);

            Allegato saved = allegatoService.salvaAllegato(allegato, file);

            if (!"0".equals(docId)) {
                allegatoService.collegaAllegato(
                        Integer.parseInt(docId), saved.getId(),
                        config.getAmministratore().getNomeCompleto(),
                        config.getAmministratore().getId().toString());
            }

            result.put("success", true);
            result.put("id", saved.getId());
            result.put("l1", saved.getL1());
            result.put("type", saved.getType());
            result.put("url", saved.getUrlRW());
        } catch (Exception e) {
            log.error("Errore upload allegato", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    // =====================================================================
    // DOWNLOAD ALLEGATO
    // =====================================================================

    @GetMapping("/files/{id}/download")
    public ResponseEntity<InputStreamResource> downloadAllegato(
            @PathVariable Integer id,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return ResponseEntity.status(401).build();

        try {
            Allegato allegato = allegatoService.findById(id).orElseThrow();
            InputStream is = allegatoService.getFileInputStream(id);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=" + allegato.getL1() + "." + allegato.getType())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(is));
        } catch (Exception e) {
            log.error("Errore download allegato id={}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // =====================================================================
    // ELIMINA ALLEGATO
    // =====================================================================

    @PostMapping("/files/{id}/delete")
    @ResponseBody
    public ResponseEntity<String> deleteAllegato(
            @PathVariable Integer id,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return ResponseEntity.status(401).body("KO");

        try {
            allegatoService.deleteAllegato(id);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Errore eliminazione allegato id={}", id, e);
            return ResponseEntity.internalServerError().body("KO");
        }
    }

    // =====================================================================
    // SCOLLEGA ALLEGATO
    // =====================================================================

    @PostMapping("/files/{idDocAllegati}/scollega")
    @ResponseBody
    public ResponseEntity<String> scollegaAllegato(
            @PathVariable Integer idDocAllegati,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return ResponseEntity.status(401).body("KO");

        try {
            allegatoService.scollegaAllegato(idDocAllegati);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Errore scollega allegato idDocAllegati={}", idDocAllegati, e);
            return ResponseEntity.internalServerError().body("KO");
        }
    }

    // =====================================================================
    // GESTIONE FOLDER
    // =====================================================================

    @GetMapping("/folder/new")
    public String newFolderForm(
            @RequestParam(value = "idfolder", defaultValue = "1") String idFolder,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        Folder folder = new Folder();
        folder.setIdfolder(idFolder);
        model.addAttribute("folder", folder);
        model.addAttribute("config", config);
        return ViewUtils.resolveProtectedTemplate("filemanager/folder");
    }

    // Nuova cartella con path variable
    @GetMapping("/folder/new/{idfolder}")
    public String newFolderFormByPath(
            @PathVariable String idfolder,
            HttpServletRequest request, Model model) {
        return newFolderForm(idfolder, request, model);
    }

    // Nuovo allegato con path variable
    @GetMapping("/files/new/{idfolder}")
    public String newAllegatoByPath(
            @PathVariable String idfolder,
            @RequestParam(value = "docid", defaultValue = "0") String docId,
            @RequestParam(value = "sorgente", defaultValue = "") String sorgente,
            HttpServletRequest request, Model model) {
        return newAllegato(idfolder, docId, sorgente, request, model);
    }

    @GetMapping("/folder/{id}/edit")
    public String editFolderForm(
            @PathVariable Integer id,
            HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            Folder folder = folderService.findById(id).orElseThrow();
            model.addAttribute("folder", folder);
            model.addAttribute("config", config);
        } catch (Exception e) {
            log.error("Errore editFolderForm id={}", id, e);
        }
        return ViewUtils.resolveProtectedTemplate("filemanager/folder");
    }

    @PostMapping("/folder/save")
    public String saveFolder(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam("nome") String nome,
            @RequestParam(value = "idfolder", defaultValue = "1") String idFolder,
            @RequestParam(value = "idgruppo", defaultValue = "0") String idGruppo,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return "redirect:/login";

        try {
            if (id == null || id <= 0) {
                folderService.createFolder(nome, idFolder, idGruppo);
            } else {
                Folder folder = folderService.findById(id).orElseThrow();
                folder.setNome(nome);
                folder.setIdgruppo(idGruppo);
                folderService.save(folder);
            }
        } catch (Exception e) {
            log.error("Errore saveFolder", e);
        }

        return "redirect:/admin/filemanager/images/" + idFolder;
    }

    @PostMapping("/folder/new")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> newFolderAjax(
            @RequestParam("nome") String nome,
            @RequestParam(value = "idfolder", defaultValue = "1") String idFolder,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        Map<String, Object> result = new HashMap<>();

        if (!config.isLogged()) {
            result.put("success", false);
            return ResponseEntity.status(401).body(result);
        }

        try {
            Folder folder = folderService.createFolder(nome, idFolder, "");
            result.put("success", true);
            result.put("id", folder.getId());
            result.put("nome", folder.getNome());
        } catch (Exception e) {
            log.error("Errore creazione folder", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/folder/{id}/rename")
    @ResponseBody
    public ResponseEntity<String> renameFolder(
            @PathVariable Integer id,
            @RequestParam("nome") String nome,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return ResponseEntity.status(401).body("KO");

        try {
            folderService.renameFolder(id, nome);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Errore rinomina folder id={}", id, e);
            return ResponseEntity.internalServerError().body("KO");
        }
    }

    @PostMapping("/folder/{id}/delete")
    @ResponseBody
    public ResponseEntity<String> deleteFolder(
            @PathVariable Integer id,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return ResponseEntity.status(401).body("KO");

        try {
            folderService.deleteFolder(id);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Errore eliminazione folder id={}", id, e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/folder/{id}/move")
    @ResponseBody
    public ResponseEntity<String> moveFolder(
            @PathVariable Integer id,
            @RequestParam("newParent") String newParent,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return ResponseEntity.status(401).body("KO");

        try {
            folderService.moveFolder(id, newParent);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Errore spostamento folder id={}", id, e);
            return ResponseEntity.internalServerError().body("KO");
        }
    }

    // =====================================================================
    // AVATAR
    // =====================================================================

    @PostMapping("/avatar/upload")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam("idUtente") String idUtente,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        Map<String, Object> result = new HashMap<>();

        if (!config.isLogged()) {
            result.put("success", false);
            return ResponseEntity.status(401).body(result);
        }

        try {
            Path avatarDir = Paths.get(imagesRepositoryPath, "imageProfile");
            Files.createDirectories(avatarDir);
            Path avatarPath = avatarDir.resolve("pfImage" + idUtente + ".jpg");
            file.transferTo(avatarPath.toFile());
            result.put("success", true);
            result.put("path", "imageProfile/pfImage" + idUtente + ".jpg");
        } catch (Exception e) {
            log.error("Errore upload avatar utente id={}", idUtente, e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/avatar/upload-base64")
    @ResponseBody
    public ResponseEntity<String> uploadAvatarBase64(
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return ResponseEntity.status(401).body("KO");

        try {
            String imageBase64 = body.get("image").replaceAll("^data:image/\\w+;base64,", "");
            String idUtente    = body.get("idUtente");
            byte[] bytes = java.util.Base64.getDecoder().decode(imageBase64);
            Path avatarDir = Paths.get(imagesRepositoryPath, "imageProfile");
            Files.createDirectories(avatarDir);
            Path avatarPath = avatarDir.resolve("pfImage" + idUtente + ".jpg");
            Files.write(avatarPath, bytes);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Errore upload avatar base64", e);
            return ResponseEntity.internalServerError().body("KO");
        }
    }

    // =====================================================================
    // API JSON
    // =====================================================================

    @GetMapping("/api/images")
    @ResponseBody
    public List<Map<String, Object>> apiImages(
            @RequestParam(value = "idfolder", defaultValue = "1") String idFolder,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return List.of();

        return imagesService.findByFolder(idFolder).stream().map(img -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", img.getId());
            m.put("nome", img.getName());
            m.put("path", img.getFullpath());
            m.put("didascalia", img.getDidascalia());
            m.put("size", img.getFormattedSize());
            return m;
        }).toList();
    }

    @GetMapping("/api/folders")
    @ResponseBody
    public List<Map<String, Object>> apiFolders(
            @RequestParam(value = "idfolder", defaultValue = "1") String idFolder,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        Configurazione config = configurazioneService.getConfig(session);
        if (!config.isLogged()) return List.of();

        return folderService.getSubfolders(idFolder).stream().map(f -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", f.getId());
            m.put("nome", f.getNome());
            m.put("idfolder", f.getIdfolder());
            m.put("hasSubfolders", f.hasSubfolders());
            return m;
        }).toList();
    }
}