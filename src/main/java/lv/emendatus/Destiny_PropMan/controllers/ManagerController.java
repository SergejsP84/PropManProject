package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.annotation.manager_controller.*;
import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
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

    @PostMapping("/add") // Same-same, this has to be a root post method
    @Manager_Add
    // See the general principles for restful service construction
    public ResponseEntity<Void> addManager(@RequestBody Manager manager) {
        managerService.addManager(manager);
        System.out.println("Added new manager: " + manager.getManagerName());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @GetMapping("/getall") // GetAlls must be implemented with no extra path parameters ("/managers")
    @Manager_GetAll
    public ResponseEntity<List<Manager>> getAllManagers() {
        List<Manager> managers = managerService.getAllManagers();
        return new ResponseEntity<>(managers, HttpStatus.OK);
    }
    @GetMapping("/getManagerById/{id}")
    @Manager_GetByID
    public ResponseEntity<Manager> getManagerById(@PathVariable Long id) {
        Optional<Manager> result = managerService.getManagerById(id);
        return result.map(manager -> new ResponseEntity<>(manager, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @DeleteMapping("/deleteManagerById/{id}")
    @Manager_Delete
    public void deleteByID(@PathVariable Long id) {
        if (managerService.getManagerById(id).isPresent()) {
            System.out.println("Deleting manager " + managerService.getManagerById(id).get().getManagerName());
            managerService.deleteManager(id);
        } else {
            System.out.println("No manager with the ID " + id + "exists in the database!");
        }
    }
    @PutMapping("/update/{id}")
    @Manager_Update
    public ResponseEntity<Void> updateManager(@PathVariable Long id, @RequestBody Manager updatedManager) {
        managerService.updateManager(id, updatedManager);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/getManagerProperties/{id}")
    @Manager_GetProperties
    public ResponseEntity<Set<Property>> getManagerProperties(@PathVariable Long id) {
    Optional<Manager> managerOptional = managerService.getManagerById(id);
    if (managerOptional.isPresent()) {
        Manager managerInQuestion = managerOptional.get();
        return new ResponseEntity<>(managerInQuestion.getProperties(), HttpStatus.OK);
    } else {
        System.out.println("No manager with the ID " + id + " exists in the database!");
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    }
    @PostMapping("/addProperty/{managerId}")
    @Manager_AddProperty
    public ResponseEntity<Void> addPropertyToManager(@PathVariable Long managerId, @RequestBody Property newProperty) {
        managerService.addPropertyToManager(managerId, newProperty);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @PostMapping("/removeProperty/{managerId}/{propertyId}")
    @Manager_RemoveProperty
    public ResponseEntity<Void> removePropertyFromManager(@PathVariable Long managerId, @PathVariable Long propertyId) {
        managerService.removePropertyFromManager(managerId, propertyId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
