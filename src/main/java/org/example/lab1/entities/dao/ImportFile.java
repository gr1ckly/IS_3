package org.example.lab1.entities.dao;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.lab1.entities.dto.ImportFileDTO;
import org.hibernate.annotations.Check;

import java.util.Date;

@Entity
@Table(name = "import_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImportFile {
    @Id
    @Column(
            unique = true,
            nullable = false
    )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "name",
            nullable = false,
            length = 255
    )
    @Check(constraints = "name <> ''")
    private String name;

    @Column(name = "creation_date", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date creationDate;

    @Column(name = "eye_color", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ImportStatus status;

    @Column(name = "added_persons")
    @Check(constraints = "added_persons >= 0")
    private int addedPersons;

    @PrePersist
    public void creationDate() {
        if (this.creationDate == null) {
            this.creationDate = new Date();
        }
    }

    public ImportFileDTO toDTO() {
        return new ImportFileDTO(id, name, creationDate, status, addedPersons);
    }
}
