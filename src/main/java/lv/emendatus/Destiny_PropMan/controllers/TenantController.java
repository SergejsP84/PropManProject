package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.annotation.tenant_controller.*;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaTenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tenants")
public class TenantController {
    private final JpaTenantService tenantService;

    public TenantController(JpaTenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PostMapping("/add")
    @Tenant_Add
    public ResponseEntity<Void> addTenant(@RequestBody Tenant tenant) {
        tenantService.addTenant(tenant);
        System.out.println("Added new tenant: " + tenant.getFirstName() + " " + tenant.getLastName());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @GetMapping("/getall")
    @Tenant_GetAll
    public ResponseEntity<List<Tenant>> getAllTenants() {
        List<Tenant> tenants = tenantService.getAllTenants();
        return new ResponseEntity<>(tenants, HttpStatus.OK);
    }
    @GetMapping("/getTenantById/{id}")
    @Tenant_GetByID
    public ResponseEntity<Tenant> getTenantById(@PathVariable Long id) {
        Optional<Tenant> result = tenantService.getTenantById(id);
        return result.map(tenant -> new ResponseEntity<>(tenant, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @DeleteMapping("/deleteTenant/{id}")
    @Tenant_Delete
    public void deleteByID(@PathVariable Long id) {
        if (tenantService.getTenantById(id).isPresent()) {
            System.out.println("Deleting tenant " + tenantService.getTenantById(id).get().getFirstName() + " " + tenantService.getTenantById(id).get().getLastName());
            tenantService.deleteTenant(id);
        } else {
            System.out.println("No tenant with the ID " + id + "exists in the database!");
        }
    }
    @GetMapping("/getTenantsByName/{name}")
    @Tenant_GetByName
    public ResponseEntity<List<Tenant>> getTenantsByFirstNameOrLastName(@PathVariable String name) {
        List<Tenant> result = tenantService.getTenantsByFirstNameOrLastName(name);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
