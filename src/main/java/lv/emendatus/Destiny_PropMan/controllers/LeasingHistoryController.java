package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.domain.entity.LeasingHistory;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaLeasingHistoryService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaPropertyService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaTenantService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/leasinghistory")
public class LeasingHistoryController {
    private final JpaLeasingHistoryService service;
    private final JpaPropertyService propertyService;
    private final JpaTenantService tenantService;
    public LeasingHistoryController(JpaLeasingHistoryService service, JpaPropertyService propertyService, JpaTenantService tenantService) {
        this.service = service;
        this.propertyService = propertyService;
        this.tenantService = tenantService;
    }
    @PostMapping("/add")
    public ResponseEntity<Void> addLeasingHistory(@RequestBody LeasingHistory leasingHistory) {
        service.addLeasingHistory(leasingHistory);
        System.out.println("Added new leasing history with the ID " + leasingHistory.getId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @GetMapping("/getall")
    public ResponseEntity<List<LeasingHistory>> getAllLeasingHistories() {
        List<LeasingHistory> histories = service.getAllLeasingHistories();
        return new ResponseEntity<>(histories, HttpStatus.OK);
    }
    @GetMapping("/getLeasingHistoryById/{id}")
    public ResponseEntity<LeasingHistory> getLeasingHistoryById(@PathVariable Long id) {
        Optional<LeasingHistory> result = service.getLeasingHistoryById(id);
        return result.map(history -> new ResponseEntity<>(history, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @DeleteMapping("/deleteLeasingHistory/{id}")
    public void deleteByID(@PathVariable Long id) {
        if (service.getLeasingHistoryById(id).isPresent()) {
            System.out.println("Deleting leasing history with the ID " + id);
            service.deleteLeasingHistory(id);
        } else {
            System.out.println("No tenant with the ID " + id + "exists in the database!");
        }
    }
    @GetMapping("/getByTime")
    public ResponseEntity<List<LeasingHistory>> getLeasingHistoryByTimePeriod(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        List<LeasingHistory> histories = service.getLeasingHistoryByTimePeriod(startDateTime, endDateTime);
        if (!histories.isEmpty()) {
            return new ResponseEntity<>(histories, HttpStatus.OK);
        } else {
            System.out.println("No leasing histories found within the specified time period!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    } // http://localhost:8080/leasinghistory/getByTime?start=2020-02-02&end=2020-02-17

    @GetMapping("/getByProperty/{prop_id}")
    public ResponseEntity<List<LeasingHistory>> getLeasingHistoryByProperty(@PathVariable Long prop_id) {
        Optional<Property> obtained = propertyService.getPropertyById(prop_id);
        if (obtained.isPresent()) {
        List<LeasingHistory> histories = service.getLeasingHistoryByProperty(obtained.get());
        if (!histories.isEmpty()) {
            return new ResponseEntity<>(histories, HttpStatus.OK);
        } else {
            System.out.println("No leasing histories found for the specified property!");
            return new ResponseEntity<>(histories, HttpStatus.NOT_FOUND);
        }
        } else {
            System.out.println("No property with the specified ID exists!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/getByTenant/{ten_id}")
    public ResponseEntity<List<LeasingHistory>> getLeasingHistoryByTenant(@PathVariable Long ten_id) {
        Optional<Tenant> obtained = tenantService.getTenantById(ten_id);
        if (obtained.isPresent()) {
            List<LeasingHistory> histories = service.getLeasingHistoryByTenant(obtained.get());
            if (!histories.isEmpty()) {
                return new ResponseEntity<>(histories, HttpStatus.OK);
            } else {
                System.out.println("No leasing histories found for the specified tenant!");
                return new ResponseEntity<>(histories, HttpStatus.NOT_FOUND);
            }
        } else {
            System.out.println("No tenant with the specified ID exists!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
