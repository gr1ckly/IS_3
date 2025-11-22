package org.example.lab1.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.lab1.entities.dao.Location;
import org.example.lab1.entities.dto.FilterOption;
import org.example.lab1.model.interfaces.LocationStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LocationService {
    private LocationStorage locationStorage;

    private NotificationService notificationService;

    private static final String locationMessage = "location";

    @Autowired
    public LocationService(NotificationService notificationService, LocationStorage locationStorage) {
        this.locationStorage = locationStorage;
        this.notificationService = notificationService;
    }

    public long createLocation(Location newLocation) throws Exception {
        long createdId = this.locationStorage.createLocation(newLocation);
        notificationService.sendMessage(LocationService.locationMessage);
        return createdId;
    }

    public Location getLocationById(long id) throws Exception {
        return this.locationStorage.getLocationByID(id);
    }

    public int getLocationCount(FilterOption... options) throws Exception {
        return this.locationStorage.getCount(options);
    }

    public List<Location> searchLocations(int offset, int limit, FilterOption... options) throws Exception{
        return this.locationStorage.searchLocations(offset, limit, options);
    }

    public int updateLocation(long id, Location newLocation) throws Exception {
        int updated = this.locationStorage.updateLocation(id, newLocation);
        if (updated > 0) {
            notificationService.sendMessage(LocationService.locationMessage);
        }
        return updated;
    }

    public int deleteLocation(long id) throws Exception {
        int deleted = this.locationStorage.deleteLocation(id);
        if (deleted > 0) {
            notificationService.sendMessage(LocationService.locationMessage);
        }
        return deleted;
    }
}
