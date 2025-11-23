package org.example.lab1.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.lab1.entities.dao.ImportFile;
import org.example.lab1.entities.dao.ImportStatus;
import org.example.lab1.exceptions.BadDataException;
import org.example.lab1.exceptions.BadFormatException;
import org.example.lab1.model.interfaces.ImportFileStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
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

    @Autowired
    public ImportFileService(ImportFileStorage importFilesStorage, NotificationService notificationService, HandleFileExecutor handleFileExecutor) {
        this.importFileStorage = importFilesStorage;
        this. notificationService = notificationService;
        this.handleFileExecutor = handleFileExecutor;
    }

    public int getImportFilesCount() throws Exception {
        return this.importFileStorage.getCount();
    }

    public List<ImportFile> searchImportFiles(int offset, int limit) throws Exception {
        return this.importFileStorage.searchImportFiles(offset, limit);
    }

    public long createImportFile(ImportFile file) throws Exception {
        long id = this.importFileStorage.createImportFile(file);
        this.notificationService.sendEvent(ImportFileService.importFilesEvent);
        return id;
    }

    @Async("importExecutor")
    public CompletableFuture<Void> startHandleFile(ImportFile newFile, InputStream inputStream) throws Exception {
        try {
            log.info("Start file handling id: " + newFile.getId());
            int count = this.handleFileExecutor.handleFile(inputStream);
            newFile.setStatus(ImportStatus.SUCCESS);
            newFile.setAddedPersons(count);
            this.importFileStorage.updateImportFile(newFile.getId(), newFile);
            this.notificationService.sendEvent(ImportFileService.importFilesEvent);
            this.notificationService.sendEventWithMessage(ImportFileService.importFileStatusEvent, "Файл с id: " + newFile.getId() + " был успешно обработан, было добавлено " + count + " person");
        } catch (BadFormatException bfe) {
            log.info("Bad format file id: " + newFile.getId() + " e: " + bfe.getMessage());
            newFile.setStatus(ImportStatus.BAD_FORMAT);
            this.importFileStorage.updateImportFile(newFile.getId(), newFile);
            this.notificationService.sendEvent(ImportFileService.importFilesEvent);
            this.notificationService.sendEventWithMessage(ImportFileService.importFileStatusEvent, "Некорректный формат файла с id: " + newFile.getId());
        } catch (BadDataException bde) {
            log.info("Bad date file id: " + newFile.getId() + " e: " + bde.getMessage());
            newFile.setStatus(ImportStatus.BAD_DATA);
            this.importFileStorage.updateImportFile(newFile.getId(), newFile);
            this.notificationService.sendEvent(ImportFileService.importFilesEvent);
            this.notificationService.sendEventWithMessage(ImportFileService.importFileStatusEvent, "Невалидные данные в файле с id: " + newFile.getId());
        } catch (Exception e) {
            log.info("Failed file id: " + newFile.getId() + " e: " + e.getMessage());
            newFile.setStatus(ImportStatus.FAILED);
            this.importFileStorage.updateImportFile(newFile.getId(), newFile);
            this.notificationService.sendEvent(ImportFileService.importFilesEvent);
            this.notificationService.sendEventWithMessage(ImportFileService.importFileStatusEvent, "Ошибка при обработке файла с id: " + newFile.getId());
        }
        return CompletableFuture.completedFuture(null);
    }
}
