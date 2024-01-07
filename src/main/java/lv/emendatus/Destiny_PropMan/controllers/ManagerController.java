package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.domain.entity.Amenity;
import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import lv.emendatus.Destiny_PropMan.domain.entity.NumericalConfig;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaManagerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/managers")
public class ManagerController {
    private final JpaManagerService managerService;
    public ManagerController(JpaManagerService managerService) {
        this.managerService = managerService;
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addManager(@RequestBody Manager manager) {
        managerService.addManager(manager);
        System.out.println("Added new manager: " + manager.getManagerName());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @GetMapping("/getall")
    public ResponseEntity<List<Manager>> getAllManagers() {
        List<Manager> managers = managerService.getAllManagers();
        return new ResponseEntity<>(managers, HttpStatus.OK);
    }
    @GetMapping("/getManagerById/{id}")
    public ResponseEntity<Manager> getManagerById(@PathVariable Long id) {
        Optional<Manager> result = managerService.getManagerById(id);
        return result.map(manager -> new ResponseEntity<>(manager, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @DeleteMapping("/deleteManagerById/{id}")
    public void deleteByID(@PathVariable Long id) {
        if (managerService.getManagerById(id).isPresent()) {
            System.out.println("Deleting manager " + managerService.getManagerById(id).get().getManagerName());
            managerService.deleteManager(id);
        } else {
            System.out.println("No manager with the ID " + id + "exists in the database!");
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateManager(@PathVariable Long id, @RequestBody Manager updatedManager) {
        managerService.updateManager(id, updatedManager);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/getManagerProperties/{id}")
    public ResponseEntity<Set<Property>> getManagerProperties(@PathVariable Long id) {
        if (managerService.getManagerById(id).isPresent()) {
            Optional<Manager> manager = managerService.getManagerById(id);
            return manager.map(managerInQuestion -> new ResponseEntity<>(managerInQuestion.getProperties(), HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } else {
            System.out.println("No manager with the ID " + id + "exists in the database!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/addProperty/{managerId}")
    public ResponseEntity<Void> addPropertyToManager(@PathVariable Long managerId, @RequestBody Property newProperty) {
        managerService.addPropertyToManager(managerId, newProperty);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @PostMapping("/removeProperty/{managerId}/{propertyId}")
    public ResponseEntity<Void> removePropertyFromManager(@PathVariable Long managerId, @PathVariable Long propertyId) {
        managerService.removePropertyFromManager(managerId, propertyId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
