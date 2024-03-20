package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaBookingService;
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
import java.util.Set;

@RestController
@RequestMapping("/booking")
public class BookingController {
    private final JpaBookingService bookingService;
    private final JpaPropertyService propertyService;
    private final JpaTenantService tenantService;

    public BookingController(JpaBookingService bookingService, JpaPropertyService propertyService, JpaTenantService tenantService) {
        this.bookingService = bookingService;
        this.propertyService = propertyService;
        this.tenantService = tenantService;
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addBooking(@RequestBody Booking booking) {
        bookingService.addBooking(booking);
        System.out.println("Added new booking: " + booking.getId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    /*
    ADDITION BODY:
    {
  "property": {
    "id": 1
  },
  "tenantId": 2,
  "startDate": "2022-01-01T10:00:00Z",
  "endDate": "2022-01-10T10:00:00Z",
  "isPaid": false,
  "status": "PENDING_PAYMENT"
}
     */
    @GetMapping("/getall")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }
    @GetMapping("/getBookingById/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        Optional<Booking> result = bookingService.getBookingById(id);
        return result.map(booking -> new ResponseEntity<>(booking, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @DeleteMapping("/deleteBookingById/{id}")
    public void deleteByID(@PathVariable Long id) {
        if (bookingService.getBookingById(id).isPresent()) {
            System.out.println("Deleting booking " + id);
            bookingService.deleteBooking(id);
        } else {
            System.out.println("No booking with the ID " + id + "exists in the database!");
        }
    }
    @GetMapping("/getByProperty/{prop_id}")
    public ResponseEntity<Set<Booking>> getBookingsByProperty(@PathVariable Long prop_id) {
        Optional<Property> obtained = propertyService.getPropertyById(prop_id);
        if (obtained.isPresent()) {
            Set<Booking> bookings = bookingService.getBookingsByProperty(obtained.get());
            if (!bookings.isEmpty()) {
                return new ResponseEntity<>(bookings, HttpStatus.OK);
            } else {
                System.out.println("No bookings found for the specified property!");
                return new ResponseEntity<>(bookings, HttpStatus.NOT_FOUND);
            }
        } else {
            System.out.println("No property with the specified ID exists!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/getByTenant/{ten_id}")
    public ResponseEntity<Set<Booking>> getBookingsByTenant(@PathVariable Long ten_id) {
        Optional<Tenant> obtained = tenantService.getTenantById(ten_id);
        if (obtained.isPresent()) {
//            System.out.println("Recovered a Tenant with ID " +obtained.get().getId());
            Set<Booking> bookings = bookingService.getBookingsByTenant(obtained.get());
            if (!bookings.isEmpty()) {
                return new ResponseEntity<>(bookings, HttpStatus.OK);
            } else {
                System.out.println("No bookings found for the specified tenant!");
                return new ResponseEntity<>(bookings, HttpStatus.NOT_FOUND);
            }
        } else {
            System.out.println("No tenant with the specified ID exists!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/getByDateRange/")
    public ResponseEntity<List<Booking>> getBookingsByDateRange(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        List<Booking> bookings = bookingService.getBookingsByDateRange(startDateTime.toLocalDate(), endDateTime.toLocalDate());
        if (!bookings.isEmpty()) {
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        } else {
            System.out.println("No bookings have been found within the specified time period!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // http://localhost:8080/booking/getByDateRange/?start=2024-02-10&end=2024-02-18
    }
    @GetMapping("/getByDateRangeWithOverlaps/")
    public ResponseEntity<List<Booking>> getBookingsByDateRangeWithOverlaps(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        List<Booking> bookings = bookingService.getBookingsByDateRangeWithOverlaps(startDateTime.toLocalDate(), endDateTime.toLocalDate());
        if (!bookings.isEmpty()) {
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        } else {
            System.out.println("No bookings have been found within the specified time period!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // http://localhost:8080/booking/getByDateRangeWithOverlaps/?start=2024-02-10&end=2024-02-18
    }
    @GetMapping("/getByStatus")
    public ResponseEntity<List<Booking>> getBookingsByStatus(@RequestParam BookingStatus status) {
        List<Booking> bookings = bookingService.getBookingsByStatus(status);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }
    @PostMapping("/updateStatus/{booking_id}")
    public ResponseEntity<Void> updateStatus(@PathVariable Long booking_id, @RequestParam BookingStatus status) {
        Optional<Booking> bookings = bookingService.getBookingById(booking_id);
        if (bookings.isPresent()) {
            bookingService.updateBookingStatus(booking_id, status);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            System.out.println("No booking with the specified ID exists!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/getPrice/{booking_id}")
    public ResponseEntity<Double> getBookingPrice(@PathVariable Long booking_id) {
        Optional<Booking> bookings = bookingService.getBookingById(booking_id);
        if (bookings.isPresent()) {
            Double result = bookingService.calculateTotalPrice(booking_id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            System.out.println("No booking with the specified ID exists!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
