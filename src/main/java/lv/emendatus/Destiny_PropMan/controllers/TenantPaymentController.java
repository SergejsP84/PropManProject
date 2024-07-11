package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.annotation.tenant_payment_controller.*;
import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaManagerService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaPropertyService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaTenantPaymentService;
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
@RequestMapping("/payments")
public class TenantPaymentController {
    private final JpaTenantPaymentService service;
    private final JpaTenantService tenantService;
    private final JpaManagerService managerService;
    private final JpaPropertyService propertyService;

    public TenantPaymentController(JpaTenantPaymentService service, JpaTenantService tenantService, JpaManagerService managerService, JpaPropertyService propertyService) {
        this.service = service;
        this.tenantService = tenantService;
        this.managerService = managerService;
        this.propertyService = propertyService;
    }

    @PostMapping("/add")
    @TenantPayment_Add
    public ResponseEntity<Void> addPayment(@RequestBody TenantPayment payment) {
        service.addTenantPayment(payment);
        System.out.println("Added new tenant payment: " + payment.getId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @GetMapping("/getall")
    @TenantPayment_GetAll
    public ResponseEntity<List<TenantPayment>> getAllPayments() {
        List<TenantPayment> payments = service.getAllTenantPayments();
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }
    @GetMapping("/getPaymentById/{id}")
    @TenantPayment_GetByID
    public ResponseEntity<TenantPayment> getPaymentById(@PathVariable Long id) {
        Optional<TenantPayment> result = service.getTenantPaymentById(id);
        return result.map(payment -> new ResponseEntity<>(payment, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @DeleteMapping("/deletePaymentById/{id}")
    @TenantPayment_Delete
    public void deleteByID(@PathVariable Long id) {
        if (service.getTenantPaymentById(id).isPresent()) {
            System.out.println("Deleting tenant payment " + id);
            service.deleteTenantPayment(id);
        } else {
            System.out.println("No tenant payment with the ID " + id + "exists in the database!");
        }
    }
    @GetMapping("/getByTenant/{ten_id}")
    @TenantPayment_GetByTenant
    public ResponseEntity<List<TenantPayment>> getPaymentsByTenant(@PathVariable Long ten_id) {
        Optional<Tenant> obtained = tenantService.getTenantById(ten_id);
        if (obtained.isPresent()) {
            List<TenantPayment> payments = service.getPaymentsByTenant(ten_id);
            if (!payments.isEmpty()) {
                return new ResponseEntity<>(payments, HttpStatus.OK);
            } else {
                System.out.println("No payments found for the specified tenant!");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            System.out.println("No tenant with the specified ID exists!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/getByManager/{man_id}")
    @TenantPayment_GetByManager
    public ResponseEntity<List<TenantPayment>> getPaymentsByManager(@PathVariable Long man_id) {
        Optional<Manager> obtained = managerService.getManagerById(man_id);
        if (obtained.isPresent()) {
            List<TenantPayment> payments = service.getPaymentsByManager(man_id);
            if (!payments.isEmpty()) {
                return new ResponseEntity<>(payments, HttpStatus.OK);
            } else {
                System.out.println("No payments found for the specified manager!");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            System.out.println("No manager with the specified ID exists!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/getByProperty/{prop_id}")
    @TenantPayment_GetByProperty
    public ResponseEntity<List<TenantPayment>> getPaymentsByProperty(@PathVariable Long prop_id) {
        Optional<Property> obtained = propertyService.getPropertyById(prop_id);
        if (obtained.isPresent()) {
            List<TenantPayment> payments = service.getPaymentsByProperty(prop_id);
            if (!payments.isEmpty()) {
                return new ResponseEntity<>(payments, HttpStatus.OK);
            } else {
                System.out.println("No payments found for the specified property!");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            System.out.println("No property with the specified ID exists!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/getUnsettled")
    @TenantPayment_GetUnsettled
    public ResponseEntity<List<TenantPayment>> getUnsettled() {
        List<TenantPayment> unsettledPayments = service.getUnsettledPayments();
        return new ResponseEntity<>(unsettledPayments, HttpStatus.OK);
    }
    @PostMapping("/settle/{id}")
    @TenantPayment_Settle
    public ResponseEntity<Void> settlePayment(@PathVariable Long id) {
        service.settlePayment(id);
        System.out.println("Settled payment: " + id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/getByDateRange")
    @TenantPayment_GetByDueDateRange
    public ResponseEntity<List<TenantPayment>> getPaymentsByDueDateRange(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        List<TenantPayment> payments = service.getPaymentsByDateRange(startDateTime, endDateTime);
        if (!payments.isEmpty()) {
            return new ResponseEntity<>(payments, HttpStatus.OK);
        } else {
            System.out.println("No bookings have been found within the specified time period!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // http://localhost:8080/payments/getByDateRange/?start=2024-02-10&end=2024-02-15
    }

}
