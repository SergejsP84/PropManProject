package lv.emendatus.Destiny_PropMan.controllers;
import lv.emendatus.Destiny_PropMan.annotation.property_amenity_controller.*;
import lv.emendatus.Destiny_PropMan.domain.entity.PropertyAmenity;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaAmenityService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaPropertyAmenityService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaPropertyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;
@RestController
@RequestMapping("/propertyamenity")
public class PropertyAmenityController {
    private final JpaPropertyAmenityService service;
    private final JpaPropertyService propertyService;
    private final JpaAmenityService amenityService;
    public PropertyAmenityController(JpaPropertyAmenityService service, JpaPropertyService propertyService, JpaAmenityService amenityService) {
        this.service = service;
        this.propertyService = propertyService;
        this.amenityService = amenityService;
    }
    @PostMapping("/add")
    @PropertyAmenity_Add
    public ResponseEntity<Void> addPropertyAmenity(@RequestBody PropertyAmenity propertyAmenity) {
        service.addPropertyAmenity(propertyAmenity);
        System.out.println("Added new property-amenity link: property " + propertyAmenity.getProperty_id() + ", amenity " +propertyAmenity.getAmenity_id());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @GetMapping("/getall")
    @PropertyAmenity_GetAll
    public ResponseEntity<List<PropertyAmenity>> getAllPropertyAmenities() {
        List<PropertyAmenity> links = service.getAllPropertyAmenities();
        return new ResponseEntity<>(links, HttpStatus.OK);
    }
    @GetMapping("/getPropertyAmenityById/{id}")
    @PropertyAmenity_GetByID
    public ResponseEntity<PropertyAmenity> getPropertyAmenityById(@PathVariable Long id) {
        Optional<PropertyAmenity> result = service.getPropertyAmenityById(id);
        return result.map(link -> new ResponseEntity<>(link, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @DeleteMapping("/deletePropertyAmenity/{id}")
    @PropertyAmenity_Delete
    public void deletePropertyAmenityByID(@PathVariable Long id) {
        if (service.getPropertyAmenityById(id).isPresent()) {
            System.out.println("Deleting link " + id);
            service.deletePropertyAmenity(id);
        } else {
            System.out.println("No property to amenity relation with the ID " + id + "exists in the database!");
        }
    }
    @GetMapping("/getPropertyAmenityByProperty/{prop_id}")
    @PropertyAmenity_GetByProperty
    public ResponseEntity<List<PropertyAmenity>> getPropertyAmenityByProperty(@PathVariable Long prop_id) {
        if (propertyService.getPropertyById(prop_id).isPresent()) {
            Set<PropertyAmenity> links = service.getPropertyAmenitiesByProperty(prop_id);
            return new ResponseEntity<>(links.stream().toList(), HttpStatus.OK);
        } else {
            System.out.println("No property with the ID " + prop_id + " exists in the database!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/getPropertyAmenityByAmenity/{amen_id}")
    @PropertyAmenity_GetByAmenity
    public ResponseEntity<List<PropertyAmenity>> getPropertyAmenityByAmenity(@PathVariable Long amen_id) {
        if (amenityService.getAmenityById(amen_id).isPresent()) {
            List<PropertyAmenity> links = service.getPropertyAmenitiesByAmenity(amen_id);
            return new ResponseEntity<>(links.stream().toList(), HttpStatus.OK);
        } else {
            System.out.println("No amenity with the ID " + amen_id + " exists in the database!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
