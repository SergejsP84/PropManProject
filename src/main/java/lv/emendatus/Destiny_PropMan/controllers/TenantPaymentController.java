package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.domain.entity.Booking;
import lv.emendatus.Destiny_PropMan.domain.entity.TenantPayment;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaTenantPaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/payments")
public class TenantPaymentController {
    private final JpaTenantPaymentService service;

    public TenantPaymentController(JpaTenantPaymentService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addPayment(@RequestBody TenantPayment payment) {
        service.addTenantPayment(payment);
        System.out.println("Added new tenant payment: " + payment.getId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @GetMapping("/getall")
    public ResponseEntity<List<TenantPayment>> getAllPayments() {
        List<TenantPayment> payments = service.getAllTenantPayments();
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }
    @GetMapping("/getPaymentById/{id}")
    public ResponseEntity<TenantPayment> getPaymentById(@PathVariable Long id) {
        Optional<TenantPayment> result = service.getTenantPaymentById(id);
        return result.map(payment -> new ResponseEntity<>(payment, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @DeleteMapping("/deletePaymentById/{id}")
    public void deleteByID(@PathVariable Long id) {
        if (service.getTenantPaymentById(id).isPresent()) {
            System.out.println("Deleting tenant payment " + id);
            service.deleteTenantPayment(id);
        } else {
            System.out.println("No tenant payment with the ID " + id + "exists in the database!");
        }
    }


}
