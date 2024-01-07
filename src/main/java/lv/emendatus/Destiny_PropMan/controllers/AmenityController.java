package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.domain.entity.Amenity;
import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaAmenityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/amenities")
public class AmenityController {
    private final JpaAmenityService jpaAmenityService;
    public AmenityController(JpaAmenityService jpaAmenityService) {
        this.jpaAmenityService = jpaAmenityService;
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addAmenity(@RequestBody Amenity amenity) {
        jpaAmenityService.addAmenity(amenity);
        System.out.println("Added new amenity: " + amenity.getDescription());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/getall")
    public ResponseEntity<List<Amenity>> getAllAmenities() {
        List<Amenity> amenities = jpaAmenityService.getAllAmenities();
        return new ResponseEntity<>(amenities, HttpStatus.OK);
    }

    @GetMapping("/getAmenityById/{id}")
    public ResponseEntity<Amenity> getAmenityById(@PathVariable Long id) {
        Optional<Amenity> result = jpaAmenityService.getAmenityById(id);
        return result.map(amenity -> new ResponseEntity<>(amenity, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/deleteAmenityById/{id}")
    public void deleteByID(@PathVariable Long id) {
        if (jpaAmenityService.getAmenityById(id).isPresent()) {
            System.out.println("Deleting amenity " + jpaAmenityService.getAmenityById(id).get().getDescription());
            jpaAmenityService.deleteAmenity(id);
        } else {
            System.out.println("No amenity with the ID " + id + "exists in the database!");
        }
    }

}
