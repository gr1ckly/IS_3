package org.example.lab1.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.lab1.entities.dao.Person;
import org.example.lab1.entities.dto.FilterOption;
import org.example.lab1.entities.dto.PersonDTO;
import org.example.lab1.exceptions.BadDataException;
import org.example.lab1.model.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/persons")
@Slf4j
public class PersonController {
    private PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping("/get_count")
    public ResponseEntity<Integer> getCountPersons(@RequestBody(required = false) FilterOption... options) throws Exception {
        return ResponseEntity.ok(this.personService.getPersonCount(options));
    }

    @PostMapping("/search_persons")
    public ResponseEntity<List<PersonDTO>> searchPersons(@RequestParam(name="offset") int offset, @RequestParam(name="limit") int limit, @RequestBody(required = false) FilterOption... options) throws Exception{
        log.info("searchPersons called with offset: {}, limit: {}, options: {}", offset, limit, options);
        List<Person> persons  = this.personService.searchPersons(offset, limit, options);
        log.info("searchPersons result: {}", persons);
        List<PersonDTO> dtos  = new LinkedList<>();
        for  (Person pers : persons) {
            dtos.add(pers.toDTO());
        }
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/create_person")
    public ResponseEntity<Long>  createPerson(@RequestBody PersonDTO personDTO) throws Exception {
        log.info("personDto: {}", personDTO);
        return ResponseEntity.ok(this.personService.createPerson(personDTO.toDAO(), personDTO.locationId(), personDTO.coordinatesId()));
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<PersonDTO> getPersonById(@PathVariable("id") long id) throws Exception {
        Person pers  = this.personService.getPersonById(id);
        if (pers != null) {
            return ResponseEntity.ok(pers.toDTO());
        } else {
            throw new BadDataException("Person with id: " + id + " doesn't exist");
        }
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<Integer> updatePerson(@PathVariable("id") long id, @RequestBody PersonDTO personDTO) throws Exception {
        return ResponseEntity.ok(this.personService.updatePerson(id, personDTO.toDAO(), personDTO.locationId(), personDTO.coordinatesId()));
    }

    @DeleteMapping("/")
    public ResponseEntity<Integer> deletePersons(@RequestBody FilterOption... options) throws Exception {
        return ResponseEntity.ok(this.personService.deletePersonsByFilters(options));
    }
}
