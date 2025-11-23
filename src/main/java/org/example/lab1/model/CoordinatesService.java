package org.example.lab1.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.lab1.entities.dao.Coordinates;
import org.example.lab1.entities.dto.FilterOption;
import org.example.lab1.model.interfaces.CoordinatesStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CoordinatesService {
    private CoordinatesStorage coordinatesStorage;

    private NotificationService notificationService;

    private static final String coordinatesEvent = "coordinates";

    @Autowired
    public CoordinatesService(NotificationService notificationService, CoordinatesStorage coordinatesStorage) {
        this.coordinatesStorage = coordinatesStorage;
        this.notificationService = notificationService;
    }


    public long createCoordinates(Coordinates newCoordinates) throws Exception {
        long createdId = this.coordinatesStorage.createCoordinates(newCoordinates);
        notificationService.sendEvent(CoordinatesService.coordinatesEvent);
        return createdId;
    }

    public Coordinates getCoordinatesById(long id) throws Exception {
        return this.coordinatesStorage.getCoordinatesByID(id);
    }

    public int getCoordinatesCount(FilterOption... options) throws Exception {
        return this.coordinatesStorage.getCount(options);
    }

    public List<Coordinates> searchCoordinates(int offset, int limit, FilterOption... options) throws Exception{
        return this.coordinatesStorage.searchCoordinates(offset, limit, options);
    }

    public int updateCoordinates(long id, Coordinates newCoordinates) throws Exception {
        int updated = this.coordinatesStorage.updateCoordinates(id, newCoordinates);
        if (updated > 0) {
            notificationService.sendEvent(CoordinatesService.coordinatesEvent);
        }
        return updated;
    }

    public int deleteCoordinates(long id) throws Exception {
        int deleted = this.coordinatesStorage.deleteCoordinates(id);
        if (deleted > 0) {
            notificationService.sendEvent(CoordinatesService.coordinatesEvent);
        }
        return deleted;
    }
}
