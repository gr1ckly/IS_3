package org.example.lab1.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.lab1.entities.dao.Coordinates;
import org.example.lab1.entities.dto.CoordinatesDTO;
import org.example.lab1.entities.dto.FilterOption;
import org.example.lab1.exceptions.BadDataException;
import org.example.lab1.model.CoordinatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/coordinates")
@Slf4j
public class CoordinatesController {
    private CoordinatesService coordinatesService;

    @Autowired
    public CoordinatesController(CoordinatesService coordinatesService) {
        this.coordinatesService = coordinatesService;
    }

    @PostMapping("/get_count")
    public ResponseEntity<Integer> getCountCoordinates(@RequestBody(required = false) FilterOption... options) throws Exception {
        log.info("getCountCoordinates called with options: {}", (Object) options);
        int count = this.coordinatesService.getCoordinatesCount(options);
        log.info("getCountCoordinates result: {}", count);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/search_coordinates")
    public ResponseEntity<List<CoordinatesDTO>> searchCoordinates(@RequestParam(name="offset") int offset, @RequestParam(name="limit") int limit, @RequestBody(required = false) List<FilterOption> options ) throws Exception {
        log.info("searchCoordinates called with offset: {}, limit: {}, options: {}", offset, limit, options);
        List<Coordinates> coordinates = this.coordinatesService.searchCoordinates(offset, limit, options.toArray(new FilterOption[0]));
        List<CoordinatesDTO> dtos  = new LinkedList<>();
        for  (Coordinates coord : coordinates) {
            dtos.add(coord.toDTO());
        }
        log.info("searchCoordinates result count: {}", dtos.size());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/create_coordinates")
    public ResponseEntity<Long>  createCoordinates(@RequestBody CoordinatesDTO coordinatesDTO) throws Exception {
        log.info("createCoordinates called with DTO: {}", coordinatesDTO);
        long id = this.coordinatesService.createCoordinates(coordinatesDTO.toDAO());
        log.info("createCoordinates result id: {}", id);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<CoordinatesDTO> getCoordinatesById(@PathVariable("id") long id) throws Exception {
        log.info("getCoordinatesById called with id: {}", id);
        Coordinates currCoords  = this.coordinatesService.getCoordinatesById(id);
        if (currCoords != null) {
            log.info("getCoordinatesById found coordinates");
            return ResponseEntity.ok(currCoords.toDTO());
        } else {
            throw new BadDataException("coordinates with id: " + id + "doesn't exist");
        }
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<Integer> updateCoordinates(@PathVariable("id") long id, @RequestBody CoordinatesDTO coordinatesDTO) throws Exception {
        log.info("updateCoordinates called with id: {}, DTO: {}", id, coordinatesDTO);
        int result = this.coordinatesService.updateCoordinates(id, coordinatesDTO.toDAO());
        log.info("updateCoordinates result: {}", result);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Integer> deleteLocation(@PathVariable("id") long id) throws Exception {
        log.info("deleteLocation called with id: {}", id);
        int result = this.coordinatesService.deleteCoordinates(id);
        log.info("deleteLocation result: {}", result);
        return ResponseEntity.ok(result);
    }
}
