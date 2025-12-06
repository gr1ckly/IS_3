package org.example.lab1.entities.dao;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.lab1.entities.dto.PersonDTO;
import org.hibernate.annotations.*;
import jakarta.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "person")
@Getter
@Setter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    @Id
    @Column(
            name = "id",
            unique = true,
            nullable = false
    )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Check(constraints = "id > 0")
    private Long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    @Column(name = "name", nullable = false, length=1000)
    @Check(constraints = "name != ''")
    private String name; //Поле не может быть null, Строка не может быть пустой

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coordinates_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Coordinates coordinates; //Поле не может быть null

    @Column(name = "creation_date", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически

    @Column(name = "eye_color")
    @Enumerated(EnumType.ORDINAL)
    private Color eyeColor; //Поле может быть null

    @Column(name = "hair_color",nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Color hairColor; //Поле не может быть null

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "location_id", nullable = true)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Location location; //Поле может быть null

    @Column(name = "height",nullable = false)
    @Check(constraints = "height > 0")
    private Float height; //Поле не может быть null, Значение поля должно быть больше 0

    @Column(name = "birthday",nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private java.time.LocalDateTime birthday; //Поле не может быть null

    @Column(name = "weight")
    @Check(constraints = "weight > 0")
    private Float weight; //Поле может быть null, Значение поля должно быть больше 0

    @Column(name = "nationality", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Country nationality; //Поле не может быть null

    @PrePersist
    public void creationDate() {
        if (this.creationDate == null) {
            this.creationDate = new Date();
        }
    }

    public boolean isValid() {
        if (this.name == null || this.name.length() > 1000 || this.name == "") return false;

        if (this.coordinates == null) {
            return false;
        } else if (!this.coordinates.isValid()) return false;

        if (this.birthday == null) return false;

        if (this.hairColor == null) return false;

        if (this.location != null && !this.location.isValid()) return false;

        if (this.height == null || this.height <= 0) return false;

        if (this.birthday == null) return false;

        if (this.weight != null && this.weight <= 0) return false;

        if (this.nationality == null) return false;

        return true;
    }

    public PersonDTO toDTO() {
        Long coordinatesId = this.coordinates != null ? this.coordinates.getId() : null;
        Long locationId = this.location != null ? this.location.getId() : null;

        return new PersonDTO(
                this.id,
                this.name,
                coordinatesId,
                this.creationDate,
                this.eyeColor,
                this.hairColor,
                locationId,
                this.height,
                this.birthday,
                this.weight,
                this.nationality
        );
    }
}
