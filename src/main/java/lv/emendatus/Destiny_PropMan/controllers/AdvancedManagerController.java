package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.domain.dto.managerial.*;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.TenantDTO_Profile;
import lv.emendatus.Destiny_PropMan.domain.dto.reference.PropertyDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.BookingDTO_Reservation;
import lv.emendatus.Destiny_PropMan.domain.entity.Bill;
import lv.emendatus.Destiny_PropMan.domain.entity.Booking;
import lv.emendatus.Destiny_PropMan.domain.entity.PropertyDiscount;
import lv.emendatus.Destiny_PropMan.exceptions.EntityNotFoundException;
import lv.emendatus.Destiny_PropMan.exceptions.FileStorageException;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaBillService;
import lv.emendatus.Destiny_PropMan.service.interfaces.AdvancedManagerService;
import lv.emendatus.Destiny_PropMan.service.interfaces.AdvancedTenantService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/managerial")
public class AdvancedManagerController {

    private final AdvancedManagerService service;

    public AdvancedManagerController(AdvancedManagerService service) {
        this.service = service;
    }

    @GetMapping("/getProfile/{managerId}")
    public ResponseEntity<ManagerProfileDTO> getManagerProfile(@PathVariable Long managerId) {
        ManagerProfileDTO managerProfileDTO = service.getManagerProfile(managerId);
        return ResponseEntity.ok(managerProfileDTO);
    }

    @PutMapping("/updateProfile/{managerId}")
    public ResponseEntity<Void> updateManagerProfile(@PathVariable Long managerId,
                                                    @RequestBody ManagerProfileDTO updatedManagerInfo) {
        service.updateManagerProfile(managerId, updatedManagerInfo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getPropertyPortfolio/{managerId}")
    public ResponseEntity<List<ManagerPropertyDTO>> getManagerPropertyPortfolio(@PathVariable Long managerId) {
        List<ManagerPropertyDTO> propertyPortfolio = service.getManagerPropertyPortfolio(managerId);
        return ResponseEntity.ok(propertyPortfolio);
    }

    @GetMapping("/property/{propertyId}/bookings")
    public ResponseEntity<List<BookingDTO_Reservation>> getBookingsForProperty(@PathVariable Long propertyId) {
        List<BookingDTO_Reservation> bookings = service.getBookingsForProperty(propertyId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/manager/{managerId}/bookings")
    public ResponseEntity<List<BookingDTO_Reservation>> getBookingsForManager(@PathVariable Long managerId) {
        List<BookingDTO_Reservation> bookings = service.getBookingsForManager(managerId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/properties/{propertyId}/unpaid-bills")
    public ResponseEntity<List<Bill>> viewUnpaidBillsForProperty(@PathVariable Long propertyId) {
        List<Bill> unpaidBills = service.getUnpaidBillsForProperty(propertyId);
        return ResponseEntity.ok(unpaidBills);
    }

    @GetMapping("/managers/{managerId}/unpaid-bills")
    public ResponseEntity<List<Bill>> viewUnpaidBillsForManager(@PathVariable Long managerId) {
        List<Bill> unpaidBills = service.getUnpaidBillsForManager(managerId);
        return ResponseEntity.ok(unpaidBills);
    }

    @GetMapping("/managers/{managerId}/financial-statement")
    public ResponseEntity<FinancialStatementDTO> generateFinancialStatement(
            @PathVariable Long managerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd) {
        FinancialStatementDTO financialStatement = service.generateFinancialStatement(periodStart, periodEnd, managerId);
        return ResponseEntity.ok(financialStatement);
    }

    @PostMapping("/addProperty")
    public ResponseEntity<String> addProperty(@RequestBody PropertyAdditionDTO propertyDTO) {
        service.addProperty(propertyDTO);
        return ResponseEntity.ok("Property added successfully");
    }

    @PostMapping("/discount")
    public ResponseEntity<PropertyDiscount> setDiscountOrSurcharge(@RequestBody PropertyDiscountDTO propertyDiscountDTO) {
        PropertyDiscount propertyDiscount = service.setDiscountOrSurcharge(propertyDiscountDTO);
        return ResponseEntity.ok(propertyDiscount);
    }

    @PostMapping("/discount/reset")
    public ResponseEntity<Void> resetDiscountsAndSurcharges(@RequestParam Long propertyId, @RequestParam LocalDate periodStart, @RequestParam LocalDate periodEnd) {
        service.resetDiscountsAndSurcharges(propertyId, periodStart, periodEnd);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/bookings/pending-approval")
    public ResponseEntity<List<Booking>> getBookingsPendingApproval(@RequestParam Long managerId) {
        List<Booking> pendingBookings = service.getBookingsPendingApproval(managerId);
        return ResponseEntity.ok(pendingBookings);
    }

    @PostMapping("/bookings/approve")
    public ResponseEntity<Void> approveBooking(@RequestParam Long bookingId) {
        service.approveBooking(bookingId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bookings/decline")
    public ResponseEntity<Void> declineBooking(@RequestParam Long bookingId) {
        service.declineBooking(bookingId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete_property/{propertyId}")
    public ResponseEntity<String> removeProperty(@PathVariable Long propertyId) {
        service.removeProperty(propertyId);
        return ResponseEntity.ok("Property removed successfully.");
    }

    @PostMapping("/addPhotos/{propertyId}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<String> uploadPhotos(@PathVariable Long propertyId, @RequestParam("files") MultipartFile[] files) {
        try {
            service.uploadPhotos(propertyId, files);
            return ResponseEntity.ok("Photos uploaded successfully.");
        } catch (FileStorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload photos: " + e.getMessage());
        }
    }

    @DeleteMapping("/removePhoto/{propertyId}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<String> removePhoto(@PathVariable Long propertyId, @RequestParam String photoUrl) {
        try {
            service.removePhoto(propertyId, photoUrl);
            return ResponseEntity.ok("Photo removed successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (FileStorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove photo: " + e.getMessage());
        }
    }

}
