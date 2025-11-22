package org.example.lab1.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.lab1.entities.dao.Coordinates;
import org.example.lab1.entities.dao.Location;
import org.example.lab1.entities.dao.Person;
import org.example.lab1.entities.dto.FilterOption;
import org.example.lab1.exceptions.BadDataException;
import org.example.lab1.model.interfaces.CoordinatesStorage;
import org.example.lab1.model.interfaces.LocationStorage;
import org.example.lab1.model.interfaces.PersonStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PersonService {
    private PersonStorage personStorage;

    private LocationStorage locationStorage;

    private CoordinatesStorage coordinatesStorage;

    private NotificationService notificationService;

    private static final String personMessage = "person";

    @Autowired
    public PersonService(NotificationService notificationService, PersonStorage personStorage, CoordinatesStorage coordinatesStorage, LocationStorage locationStorage) {
        this.personStorage = personStorage;
        this.locationStorage = locationStorage;
        this.coordinatesStorage = coordinatesStorage;
        this.notificationService = notificationService;
    }

    public long createPerson(Person newPerson, Long locationId, long coordinatesId) throws Exception {
        if (locationId != null && locationId > 0) {
            Location currLocation = this.locationStorage.getLocationByID(locationId);
            if (currLocation == null) {
                throw new BadDataException("Location not found");
            }
            newPerson.setLocation(currLocation);
        }
        Coordinates currCoords = this.coordinatesStorage.getCoordinatesByID(coordinatesId);
        if (currCoords == null) {
            throw new BadDataException("Coordinates not found");
        }
        newPerson.setCoordinates(currCoords);
        long createdId = this.personStorage.createPerson(newPerson);
        notificationService.sendMessage(PersonService.personMessage);
        return createdId;
    }

    public Person getPersonById(long id) throws Exception {
        return this.personStorage.getPersonByID(id);
    }

    public int getPersonCount(FilterOption... options) throws Exception {
        return this.personStorage.getCount(options);
    }

    public List<Person> searchPersons(int offset, int limit, FilterOption... options) throws Exception{
        return this.personStorage.searchPersons(offset, limit, options);
    }

    public int updatePerson(long id, Person newPerson, Long locationId, long coordinatesId) throws Exception {
        if (locationId != null && locationId > 0) {
            Location currLocation = this.locationStorage.getLocationByID(locationId);
            if (currLocation == null) {
                throw new BadDataException("Location not found");
            }
            newPerson.setLocation(currLocation);
        }
        Coordinates currCoords = this.coordinatesStorage.getCoordinatesByID(coordinatesId);
        if (currCoords == null) {
            throw new BadDataException("Coordinates not found");
        }
        newPerson.setCoordinates(currCoords);
        int updated = this.personStorage.updatePerson(id, newPerson);
        if (updated > 0) {
            notificationService.sendMessage(PersonService.personMessage);
        }
        return updated;
    }

    public int deletePersonsByFilters(FilterOption... options) throws Exception {
        int deleted = this.personStorage.deletePersonByFilter(options);
        if (deleted > 0) {
            notificationService.sendMessage(PersonService.personMessage);
        }
        return deleted;
    }
}
