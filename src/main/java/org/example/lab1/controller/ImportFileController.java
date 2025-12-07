package org.example.lab1.controller;

import org.example.lab1.entities.dao.ImportFile;
import org.example.lab1.entities.dao.ImportStatus;
import org.example.lab1.entities.dto.ImportFileDTO;
import org.example.lab1.entities.dto.PersonDTO;
import org.example.lab1.exceptions.BadDataException;
import org.example.lab1.exceptions.BadFormatException;
import org.example.lab1.model.ImportFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/import_files")
public class ImportFileController {
    public ImportFileService importFileService;

    @Autowired
    public ImportFileController(ImportFileService importFileService) {
        this.importFileService = importFileService;
    }

    @GetMapping("/get_count")
    public ResponseEntity<Integer> getCountImportFiles() throws Exception {
        return ResponseEntity.ok(this.importFileService.getImportFilesCount());
    }

    @GetMapping("/search_files")
    public ResponseEntity<List<ImportFileDTO>> searchImportFiles(@RequestParam(name="offset") int offset, @RequestParam(name="limit") int limit) throws Exception {
        List<ImportFile> importFiles = this.importFileService.searchImportFiles(offset, limit);
        List<ImportFileDTO> dtos  = new LinkedList<>();
        for  (ImportFile importFile : importFiles) {
            dtos.add(importFile.toDTO());
        }
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id:\\d+}/download")
    public ResponseEntity<String> updatePerson(@PathVariable("id") long id) throws Exception {
        return ResponseEntity.ok(this.importFileService.getDownloadLink(id));
    }

    @PostMapping("/import")
    public ResponseEntity<Long> importFile(@RequestParam("file") MultipartFile file) throws Exception {
        ImportFile newFile = new ImportFile();
        newFile.setName(StringUtils.hasText(file.getOriginalFilename()) ? StringUtils.cleanPath(file.getOriginalFilename()) : "unnamed");
        newFile.setStatus(ImportStatus.IN_PROGRESS);
        newFile.setContentType(file.getContentType());
        Long id = this.importFileService.createImportFile(newFile);
        this.importFileService.startHandleFile(newFile, file.getInputStream());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(id);
    }
}
