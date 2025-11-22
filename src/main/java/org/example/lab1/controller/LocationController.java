package org.example.lab1.controller;

import org.example.lab1.entities.dao.Location;
import org.example.lab1.entities.dto.FilterOption;
import org.example.lab1.entities.dto.LocationDTO;
import org.example.lab1.exceptions.BadDataException;
import org.example.lab1.model.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/location")
public class LocationController {

    private LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping("/get_count")
    public ResponseEntity<Integer> getCountLocations(@RequestBody(required = false) FilterOption... options) throws Exception {
        return ResponseEntity.ok(this.locationService.getLocationCount(options));
    }

    @PostMapping("/search_locations")
    public ResponseEntity<List<LocationDTO>> searchLocations(@RequestParam(name="offset") int offset, @RequestParam(name="limit") int limit, @RequestBody(required = false) FilterOption... options) throws Exception{
        List<Location> locations = this.locationService.searchLocations(offset, limit, options);
        List<LocationDTO> dtos  = new LinkedList<>();
        for  (Location location : locations) {
            dtos.add(location.toDTO());
        }
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/create_location")
    public ResponseEntity<Long>  createLocation(@RequestBody LocationDTO locationDTO) throws Exception {
        return ResponseEntity.ok(this.locationService.createLocation(locationDTO.toDAO()));
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable("id") long id) throws Exception {
        Location currLocation  = this.locationService.getLocationById(id);
        if (currLocation != null) {
            return ResponseEntity.ok(currLocation.toDTO());
        } else {
            throw new BadDataException("Location with id: " + id + " doesn't exist");
        }
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<Integer> updateLocation(@PathVariable("id") long id, @RequestBody LocationDTO locationDTO) throws Exception {
        return ResponseEntity.ok(this.locationService.updateLocation(id, locationDTO.toDAO()));
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Integer> deleteLocation(@PathVariable("id") long id) throws Exception {
        return ResponseEntity.ok(this.locationService.deleteLocation(id));
    }
}
