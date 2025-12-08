package org.example.lab1.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.lab1.entities.dao.ImportFile;
import org.example.lab1.entities.dao.ImportStatus;
import org.example.lab1.exceptions.BadDataException;
import org.example.lab1.exceptions.BadFormatException;
import org.example.lab1.model.interfaces.FileStorage;
import org.example.lab1.model.interfaces.ImportFileStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@NoArgsConstructor
@Getter
@Setter
@Slf4j
public class ImportFileService {
    private ImportFileStorage importFileStorage;

    private NotificationService notificationService;

    private static final String importFilesEvent = "import_file";

    private static final String importFileStatusEvent = "import_file_status";

    private HandleFileExecutor handleFileExecutor;

    private FileStorage fileStorage;

    @Autowired
    public ImportFileService(ImportFileStorage importFilesStorage, NotificationService notificationService, HandleFileExecutor handleFileExecutor, FileStorage fileStorage) {
        this.importFileStorage = importFilesStorage;
        this. notificationService = notificationService;
        this.handleFileExecutor = handleFileExecutor;
        this.fileStorage = fileStorage;
    }

    public int getImportFilesCount() throws Exception {
        return this.importFileStorage.getCount();
    }

    public List<ImportFile> searchImportFiles(int offset, int limit) throws Exception {
        return this.importFileStorage.searchImportFiles(offset, limit);
    }

    public String getDownloadLink(Long id) throws Exception{
        ImportFile currFile = this.importFileStorage.getFileByID(id);
        return this.fileStorage.getDownloadLink(currFile.getUuidFile(), currFile.getName());
    }

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public long createImportFile(ImportFile file) throws Exception {
        file.setUuidFile(this.generateUUID() + "-" + file.getName());
        long id = this.importFileStorage.createImportFile(file);
        this.notificationService.sendEvent(ImportFileService.importFilesEvent);
        return id;
    }

    @Async("importExecutor")
    public CompletableFuture<Void> startHandleFile(ImportFile newFile, InputStream inputStream) throws Exception {
        try {
            log.error("Start file handling id: " + newFile.getId());
            int count = this.handleFileExecutor.handleFile(inputStream, newFile);
            newFile = this.importFileStorage.getFileByID(newFile.getId());
            newFile.setStatus(ImportStatus.SUCCESS);
            newFile.setAddedPersons(count);
            this.importFileStorage.updateImportFile(newFile.getId(), newFile);
            this.notificationService.sendEvent(ImportFileService.importFilesEvent);
            this.notificationService.sendEventWithMessage(ImportFileService.importFileStatusEvent, new StringBuilder("File with id: ").append(newFile.getId()).append(" was successfully handled, added ").append(count).append(" person").toString());
        } catch (BadFormatException bfe) {
            log.error("Bad format file id: " + newFile.getId() + " e: " + bfe.getMessage());
            newFile.setStatus(ImportStatus.BAD_FORMAT);
            this.importFileStorage.updateImportFile(newFile.getId(), newFile);
            this.notificationService.sendEvent(ImportFileService.importFilesEvent);
            this.notificationService.sendEventWithMessage(ImportFileService.importFileStatusEvent, new StringBuilder("Incorrect format file with id: ").append(newFile.getId()).toString());
        } catch (BadDataException bde) {
            log.error("Bad date file id: " + newFile.getId() + " e: " + bde.getMessage());
            newFile.setStatus(ImportStatus.BAD_DATA);
            this.importFileStorage.updateImportFile(newFile.getId(), newFile);
            this.notificationService.sendEvent(ImportFileService.importFilesEvent);
            this.notificationService.sendEventWithMessage(ImportFileService.importFileStatusEvent, new StringBuilder("Not valid data in file with id: ").append( newFile.getId()).toString());
        } catch (Exception e) {
            log.error("Failed file id: " + newFile.getId() + " e: " + e.getMessage());
            newFile.setStatus(ImportStatus.FAILED);
            this.importFileStorage.updateImportFile(newFile.getId(), newFile);
            this.notificationService.sendEvent(ImportFileService.importFilesEvent);
            this.notificationService.sendEventWithMessage(ImportFileService.importFileStatusEvent,  new StringBuilder("Failed handling file with id: ").append(newFile.getId()).toString());
        }
        return CompletableFuture.completedFuture(null);
    }
}
