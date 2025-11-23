package org.example.lab1.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.example.lab1.entities.dao.Coordinates;
import org.example.lab1.entities.dao.Location;
import org.example.lab1.entities.dao.Person;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Component
@Getter
@Setter
@AllArgsConstructor
public class PersonSimilarity {
    private JaroWinklerSimilarity textSimilarity;

    public PersonSimilarity() {
        this.textSimilarity = new JaroWinklerSimilarity();
    }

    public double similarScore(Person newPerson, Person oldPerson) {
        double nameScore = 0,
                coordinatesYScore = 0,
                hairColorScore = 0,
                heightScore = 0,
                birthdayScore = 0,
                nationalityScore = 0;
        nameScore = this.textScore(newPerson.getName(), oldPerson.getName());
        if (newPerson.getCoordinates() != null && oldPerson.getCoordinates() != null) {
            coordinatesYScore = numberScore(newPerson.getCoordinates().getY(), oldPerson.getCoordinates().getY());
        }
        hairColorScore = this.enumScore(newPerson.getHairColor(), oldPerson.getHairColor());
        heightScore = this.numberScore(newPerson.getHeight(), oldPerson.getHeight());
        birthdayScore = this.localDateTimeScore(newPerson.getBirthday(), oldPerson.getBirthday());
        nationalityScore = this.enumScore(newPerson.getNationality(), oldPerson.getNationality());
        return (nameScore + coordinatesYScore + hairColorScore + heightScore + birthdayScore + nationalityScore) / 6;
    }

    private double numberScore(double number1, double number2) {
        return Math.max(1 - Math.abs(number1 - number2) / (Math.max(Math.abs(number1), Math.abs(number2)) == 0 ? 1 : Math.max(Math.abs(number1), Math.abs(number2))), 0);
    }

    private double textScore(String text1, String text2) {
        return this.textSimilarity.apply(text1, text2);
    }

    private double enumScore(Enum enum1, Enum enum2) {
        return enum1 == enum2 ? 1 : 0 ;
    }

    private double localDateTimeScore(LocalDateTime time1, LocalDateTime time2) {
        if (time1 == null || time2 == null) return 0;
        return time1.equals(time2) ? 1 : 0;
    }

    public Person merge(Person newPerson, Person oldPerson) {
        Person merged = new Person();
        merged.setName(newPerson.getName() == null ? oldPerson.getName() : newPerson.getName());
        merged.setCoordinates(this.mergeCoordinates(newPerson.getCoordinates(), oldPerson.getCoordinates()));
        merged.setEyeColor(newPerson.getEyeColor() == null ? oldPerson.getEyeColor() : newPerson.getEyeColor());
        merged.setHairColor(newPerson.getHairColor() == null ? oldPerson.getHairColor() : newPerson.getHairColor());
        merged.setLocation(this.mergeLocation(newPerson.getLocation(), oldPerson.getLocation()));
        merged.setHeight(newPerson.getHeight() == null ? oldPerson.getHeight() : newPerson.getHeight());
        merged.setBirthday(newPerson.getBirthday() == null ? oldPerson.getBirthday() : newPerson.getBirthday());
        merged.setWeight(newPerson.getWeight() == null ? oldPerson.getWeight() : newPerson.getWeight());
        merged.setNationality(newPerson.getNationality() == null ? oldPerson.getNationality() : newPerson.getNationality());
        return merged;
    }

    private Coordinates mergeCoordinates(Coordinates newCoordinates, Coordinates oldCoordinates) {
        if (newCoordinates == null && oldCoordinates == null) return null;

        if (newCoordinates != null && oldCoordinates == null) return newCoordinates;

        if (oldCoordinates != null && newCoordinates == null) return oldCoordinates;

        newCoordinates.setX(newCoordinates.getX() == null ? oldCoordinates.getX() : newCoordinates.getX());

        return newCoordinates;
    }

    private Location mergeLocation(Location newLocation, Location oldLocation) {
        if (newLocation == null && oldLocation == null) return null;

        if (newLocation != null && oldLocation == null) return newLocation;

        if (oldLocation != null && newLocation == null) return oldLocation;

        newLocation.setName(newLocation.getName() == null || newLocation.getName() == "" ? oldLocation.getName() : newLocation.getName());

        return newLocation;
    }
}
