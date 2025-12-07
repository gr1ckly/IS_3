package org.example.lab1.entities.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.example.lab1.entities.dao.ImportFile;
import org.example.lab1.entities.dao.ImportStatus;

public record ImportFileDTO(Long id,
                            String name,
                            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
                            java.util.Date creationDate,
                            ImportStatus status,
                            int addedPersons) {
    public ImportFile toDAO() {
        return new ImportFile(id, name, creationDate, status, addedPersons, "", "");
    }
}
