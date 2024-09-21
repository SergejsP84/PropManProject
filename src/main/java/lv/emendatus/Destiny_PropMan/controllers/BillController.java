package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.annotation.bill_controller.*;
import lv.emendatus.Destiny_PropMan.domain.entity.Bill;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaBillService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaPropertyService;
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
@RequestMapping("/bills")
public class BillController {
    private final JpaBillService billService;
    private final JpaPropertyService propertyService;

    public BillController(JpaBillService billService, JpaPropertyService propertyService) {
        this.billService = billService;
        this.propertyService = propertyService;
    }

    @PostMapping("/add")
    @Bill_Add
    public ResponseEntity<Void> addBill(@RequestBody Bill bill) {
        billService.addBill(bill);
        System.out.println("Added new bill: " + bill.getId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @GetMapping("/getall")
    @Bill_GetAll
    public ResponseEntity<List<Bill>> getAllBills() {
        List<Bill> bills = billService.getAllBills();
        return new ResponseEntity<>(bills, HttpStatus.OK);
    }
    @GetMapping("/getBillById/{id}")
    @Bill_GetByID
    public ResponseEntity<Bill> getBillById(@PathVariable Long id) {
        Optional<Bill> result = billService.getBillById(id);
        return result.map(bill -> new ResponseEntity<>(bill, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @DeleteMapping("/deleteBillById/{id}")
    @Bill_Delete
    public void deleteByID(@PathVariable Long id) {
        if (billService.getBillById(id).isPresent()) {
            System.out.println("Deleting bill " + id);
            billService.deleteBill(id);
        } else {
            System.out.println("No bill with the ID " + id + "exists in the database!");
        }
    }
    @GetMapping("/getByProperty/{prop_id}")
    @Bill_GetByProperty
    public ResponseEntity<List<Bill>> getBillsByProperty(@PathVariable Long prop_id) {
        Optional<Property> obtained = propertyService.getPropertyById(prop_id);
        if (obtained.isPresent()) {
            List<Bill> bills = billService.getBillsByProperty(obtained.get());
            if (!bills.isEmpty()) {
                return new ResponseEntity<>(bills, HttpStatus.OK);
            } else {
                System.out.println("No bills found for the specified property!");
                return new ResponseEntity<>(bills, HttpStatus.NOT_FOUND);
            }
        } else {
            System.out.println("No property with the specified ID exists!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/getUnpaidBills/{prop_id}")
    @Bill_GetUnpaidByProperty
    public ResponseEntity<List<Bill>> getUnpaidBills(@PathVariable Long prop_id) {
        Optional<Property> obtained = propertyService.getPropertyById(prop_id);
        if (obtained.isPresent()) {
            List<Bill> bills = billService.getUnpaidBills(obtained.get());
            if (!bills.isEmpty()) {
                return new ResponseEntity<>(bills, HttpStatus.OK);
            } else {
                System.out.println("No unpaid bills found for the specified property!");
                return new ResponseEntity<>(bills, HttpStatus.NOT_FOUND);
            }
        } else {
            System.out.println("No property with the specified ID exists!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/getPaidBills/{prop_id}")
    @Bill_GetPaidByProperty
    public ResponseEntity<List<Bill>> getPaidBills(@PathVariable Long prop_id) {
        Optional<Property> obtained = propertyService.getPropertyById(prop_id);
        if (obtained.isPresent()) {
            List<Bill> bills = billService.getPaidBills(obtained.get());
            if (!bills.isEmpty()) {
                return new ResponseEntity<>(bills, HttpStatus.OK);
            } else {
                System.out.println("No paid bills found for the specified property!");
                return new ResponseEntity<>(bills, HttpStatus.NOT_FOUND);
            }
        } else {
            System.out.println("No property with the specified ID exists!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getByCategory/{prop_id}/{category}")
    @Bill_GetByExpenseCategory
    public ResponseEntity<List<Bill>> getBillsByExpenseCategory(@PathVariable Long prop_id, @PathVariable String category) {
        Optional<Property> obtained = propertyService.getPropertyById(prop_id);
        if (obtained.isPresent()) {
            List<Bill> bills = billService.getBillsByExpenseCategory(obtained.get(), category);
            if (!bills.isEmpty()) {
                return new ResponseEntity<>(bills, HttpStatus.OK);
            } else {
                System.out.println("No bills matching the category " + category + " found for the specified property!");
                return new ResponseEntity<>(bills, HttpStatus.NOT_FOUND);
            }
        } else {
            System.out.println("No property with the specified ID exists!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/getByDueDate/{prop_id}")
    @Bill_GetByDueDateRange
    public ResponseEntity<List<Bill>> getBillsByDueDateRange(
            @PathVariable Long prop_id,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        Optional<Property> obtained = propertyService.getPropertyById(prop_id);
        if (obtained.isPresent()) {
            LocalDateTime startDateTime = start.atStartOfDay();
            LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
            List<Bill> bills = billService.getBillsByDueDateRange(startDateTime.toLocalDate(), endDateTime.toLocalDate(), obtained.get());
            if (!bills.isEmpty()) {
                return new ResponseEntity<>(bills, HttpStatus.OK);
            } else {
                System.out.println("No bills with due dates within the specified time period have been found!");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            System.out.println("No property with the specified ID exists!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    } // http://localhost:8080/bills/getByDueDate/4?start=2024-02-10&end=2024-02-18
    @PatchMapping("/togglePaymentStatus/{billId}")
    @Bill_TogglePaymentStatus
    public ResponseEntity<Void> toggleBillPaymentStatus(@PathVariable Long billId) {
        try {
            billService.togglePaidStatus(billId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
