package org.example.lab1.entities.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.lab1.entities.dto.CoordinatesDTO;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "coordinates")
@Getter
@Setter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AllArgsConstructor
@NoArgsConstructor
public class Coordinates {
    @Id
    @Column(
            name = "id",
            unique = true,
            nullable = false
    )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "x")
    @Check(constraints = "x > -459")
    private Double x; //Значение поля должно быть больше -459

    @Column(name = "y", nullable = false)
    @Check(constraints = "y > -238")
    private Integer y; //Значение поля должно быть больше -238, Поле не может быть null

    public CoordinatesDTO toDTO() {
        return new CoordinatesDTO(this.id, this.x, this.y);
    }

    public boolean isValid() {
        if (this.x != null && this.x <= -459) {return false;}

        if (this.y == null || this.y <= -238) {return false;}

        return true;
    }
}
