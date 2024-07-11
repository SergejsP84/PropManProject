package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.annotation.property_controller.*;
import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyType;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaPropertyService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/property")
public class PropertyController {

    private final JpaPropertyService propertyService;

    public PropertyController(JpaPropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping("/add")
    @Property_Add
    public ResponseEntity<Void> addProperty(@RequestBody Property property) {
        propertyService.addProperty(property);
        System.out.println("Added new property: " + property.getId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @GetMapping("/getall")
    @Property_GetAll
    public ResponseEntity<List<Property>> getAllProperties() {
        List<Property> properties = propertyService.getAllProperties();
        return new ResponseEntity<>(properties, HttpStatus.OK);
    }
    @GetMapping("/getPropertyById/{id}")
    @Property_GetByID
    public ResponseEntity<Property> getPropertyById(@PathVariable Long id) {
        Optional<Property> result = propertyService.getPropertyById(id);
        return result.map(property -> new ResponseEntity<>(property, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @DeleteMapping("/deletePropertyById/{id}")
    @Property_Delete
    public void deleteByID(@PathVariable Long id) {
        if (propertyService.getPropertyById(id).isPresent()) {
            System.out.println("Deleting property " + id);
            propertyService.deleteProperty(id);
        } else {
            System.out.println("No property with the ID " + id + "exists in the database!");
        }
    }

    @GetMapping("/getPropertiesByLocation/{location}")
    @Property_GetByLocation
    public ResponseEntity<List<Property>> getPropertiesByLocation(@PathVariable String location) {
        List<Property> result = propertyService.getPropertiesByLocation(location);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/getPropertiesByType/{type}")
    @Property_GetByType
    public ResponseEntity<List<Property>> getPropertiesByType(@PathVariable PropertyType type) {
        List<Property> result = propertyService.getPropertiesByType(type);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/getPriceRangeDay/{min_price}/{max_price}")
    @Property_GetByDailyPriceRange
    public ResponseEntity<List<Property>> getPropertiesByDailyPriceRange(@PathVariable Double min_price, @PathVariable Double max_price) {
        List<Property> result = propertyService.getPropertiesByDailyPriceRange(min_price, max_price);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @GetMapping("/getPriceRangeWeek/{min_price}/{max_price}")
    @Property_GetByWeeklyPriceRange
    public ResponseEntity<List<Property>> getPropertiesByWeeklyPriceRange(@PathVariable Double min_price, @PathVariable Double max_price) {
        List<Property> result = propertyService.getPropertiesByWeeklyPriceRange(min_price, max_price);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @GetMapping("/getPriceRangeMonth/{min_price}/{max_price}")
    @Property_GetByMonthlyPriceRange
    public ResponseEntity<List<Property>> getPropertiesByMonthlyPriceRange(@PathVariable Double min_price, @PathVariable Double max_price) {
        List<Property> result = propertyService.getPropertiesByMonthlyPriceRange(min_price, max_price);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/getAvailableProperties")
    @Property_GetAvailableProperties
    public ResponseEntity<List<Property>> getAvailableProperties(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        List<Property> properties = propertyService.getAvailableProperties(startDateTime.toLocalDate(), endDateTime.toLocalDate());
        if (!properties.isEmpty()) {
            return new ResponseEntity<>(properties, HttpStatus.OK);
        } else {
            System.out.println("No properties have been found within the specified time period!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // http://localhost:8080/property/getAvailableProperties?start=2024-02-10&end=2024-02-18
    }

    @GetMapping("/getPropertiesWithAmenities")
    @Property_GetPropertiesWithAmenities
    public ResponseEntity<Set<Property>> getPropertiesWithAmenities(@RequestParam List<Long> amenityIds) {
        Set<Property> properties = propertyService.getPropertiesWithAmenities(amenityIds);
        if (!properties.isEmpty()) {
            return new ResponseEntity<>(properties, HttpStatus.OK);
        } else {
            System.out.println("No properties found with the specified amenities!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // http://localhost:8080/property/getPropertiesWithAmenities?amenityIds=1,3
    }

    @PutMapping("/update_address/{id}")
    @Property_UpdateAddress
    public ResponseEntity<Void> updateAddress(@PathVariable Long id, @RequestBody String newAddress) {
        propertyService.updatePropertyAddress(id, newAddress);
        return new ResponseEntity<>(HttpStatus.OK);
    } // http://localhost:8080/property/update_address/8 (type the new one as a raw address in the body, nuthin else needed)

    @PutMapping("/update_settlement/{id}")
    @Property_UpdateSettlement
    public ResponseEntity<Void> updateSettlement(@PathVariable Long id, @RequestBody String newSettlement) {
        propertyService.updatePropertySettlement(id, newSettlement);
        return new ResponseEntity<>(HttpStatus.OK);
    } // http://localhost:8080/property/update_settlement/9 (type the new one as a raw address in the body, nuthin else needed)

    @PutMapping("/update_country/{id}")
    @Property_UpdateCountry
    public ResponseEntity<Void> updateCountry(@PathVariable Long id, @RequestBody String newCountry) {
        propertyService.updatePropertyCountry(id, newCountry);
        return new ResponseEntity<>(HttpStatus.OK);
    } // http://localhost:8080/property/update_address/9 (type the new one as a raw address in the body, nuthin else needed)

    @PutMapping("/update_price_day/{id}/{newPrice}")
    @Property_UpdateDailyPrice
    public ResponseEntity<Void> updatePriceDay(@PathVariable Long id, @PathVariable double newPrice) {
        propertyService.updatePropertyPricePerDay(id, newPrice);
        return new ResponseEntity<>(HttpStatus.OK);
    } // http://localhost:8080/property/update_price_day/4/40

    @PutMapping("/update_price_week/{id}/{newPrice}")
    @Property_UpdateWeeklyPrice
    public ResponseEntity<Void> updatePriceWeek(@PathVariable Long id, @PathVariable double newPrice) {
        propertyService.updatePropertyPricePerWeek(id, newPrice);
        return new ResponseEntity<>(HttpStatus.OK);
    } // http://localhost:8080/property/update_price_week/4/220

    @PutMapping("/update_price_month/{id}/{newPrice}")
    @Property_UpdateMonthlyPrice
    public ResponseEntity<Void> updatePriceMonth(@PathVariable Long id, @PathVariable double newPrice) {
        propertyService.updatePropertyPricePerMonth(id, newPrice);
        return new ResponseEntity<>(HttpStatus.OK);
    } // http://localhost:8080/property/update_price_month/4/500

    @PostMapping("/addAmenity/{prop_id}/{amen_id}")
    @Property_AddAmenity
    public ResponseEntity<Void> addAmenityToProperty(@PathVariable Long prop_id, @PathVariable Long amen_id) {
        propertyService.addAmenityToProperty(prop_id, amen_id);
        return new ResponseEntity<>(HttpStatus.OK);
    } // http://localhost:8080/property/addAmenity/12/5

    @DeleteMapping("/removeAmenity/{prop_id}/{amen_id}")
    @Property_RemoveAmenity
    public ResponseEntity<Void> removeAmenityFromProperty(@PathVariable Long prop_id, @PathVariable Long amen_id) {
        propertyService.removeAmenityFromProperty(prop_id, amen_id);
        return new ResponseEntity<>(HttpStatus.OK);
    } // http://localhost:8080/property/removeAmenity/12/5

    @PutMapping("/update_property_manager/{prop_id}/{man_id}")
    @Property_UpdateManager
    public ResponseEntity<Void> updatePropertyManager(@PathVariable Long prop_id, @PathVariable Long man_id) {
        propertyService.updateManager(prop_id, man_id);
        return new ResponseEntity<>(HttpStatus.OK);
    } // http://localhost:8080/property/update_property_manager/13/2

    @PutMapping("/set_property_status/{prop_id}/{status}")
    @Property_UpdateStatus
    public ResponseEntity<Void> updateStatus(@PathVariable Long prop_id, @PathVariable PropertyStatus status) {
        propertyService.setStatus(prop_id, status);
        return new ResponseEntity<>(HttpStatus.OK);
    } // http://localhost:8080/property/set_property_status/10/BUSY

    @PutMapping("/update_size/{id}/{newSize}")
    @Property_UpdateSize
    public ResponseEntity<Void> updateSize(@PathVariable Long id, @PathVariable Float newSize) {
        propertyService.updateSize(id, newSize);
        return new ResponseEntity<>(HttpStatus.OK);
    } // http://localhost:8080/property/update_size/10/26.0

    @PutMapping("/update_rating/{id}/{newRating}")
    @Property_UpdateRating
    public ResponseEntity<Void> updateRating(@PathVariable Long id, @PathVariable Float newRating) {
        propertyService.updateRating(id, newRating);
        return new ResponseEntity<>(HttpStatus.OK);
    } // http://localhost:8080/property/update_rating/12/3.6

    @PutMapping("/update_description/{id}")
    @Property_UpdateDescription
    public ResponseEntity<Void> updateDescription(@PathVariable Long id, @RequestBody String newDescription) {
        propertyService.updateDescription(id, newDescription);
        return new ResponseEntity<>(HttpStatus.OK);
    } // http://localhost:8080/property/update_description/4 (type the new one as raw text in the body)


    @PostMapping("/assign_tenant/{prop_id}/{ten_id}")
    @Property_AssignTenant
    public ResponseEntity<Void> assignTenantToProperty(@PathVariable Long prop_id, @PathVariable Long ten_id) {
        propertyService.assignTenantToProperty(prop_id, ten_id);
        return new ResponseEntity<>(HttpStatus.OK);
    } // http://localhost:8080/property/assign_tenant/12/9

    @GetMapping("/get_tenant/{prop_id}")
    @Property_GetCurrentTenant
    public ResponseEntity<Void> getCurrentTenant (@PathVariable Long prop_id) {
        propertyService.getCurrentTenant(prop_id);
        return new ResponseEntity<>(HttpStatus.OK);
    } // http://localhost:8080/property/get_tenant/12

    @DeleteMapping("/remove_tenant/{prop_id}")
    @Property_RemoveTenantFromProperty
    public ResponseEntity<Void> removeTenantFromProperty(@PathVariable Long prop_id) {
        propertyService.removeTenantFromProperty(prop_id);
        return new ResponseEntity<>(HttpStatus.OK);
    } // http://localhost:8080/property/remove_tenant/12

    @GetMapping("/get_bills/{prop_id}")
    @Property_GetPropertyBills
    public ResponseEntity<Set<Bill>> getPropertyBills(@PathVariable Long prop_id) {
        Set<Bill> bills = propertyService.getPropertyBills(prop_id);
        return new ResponseEntity<>(bills, HttpStatus.OK);
    } // http://localhost:8080/property/get_bills/12

    @PostMapping("/add_bill/{prop_id}/{bill_id}")
    @Property_AddBillToProperty
    public ResponseEntity<Void> addBillToProperty(@PathVariable Long prop_id, @PathVariable Long bill_id) {
        propertyService.addBillToProperty(prop_id, bill_id);
        return new ResponseEntity<>(HttpStatus.OK);
    } // http://localhost:8080/property/add_bill/12/1

    @PostMapping("/remove_bill/{prop_id}/{bill_id}")
    @Property_RemoveBillFromProperty
    public ResponseEntity<Void> removeBillFromProperty(@PathVariable Long prop_id, @PathVariable Long bill_id) {
        propertyService.removeBillFromProperty(prop_id, bill_id);
        return new ResponseEntity<>(HttpStatus.OK);
    } // http://localhost:8080/property/remove_bill/12/5

    @GetMapping("/get_bookings/{prop_id}")
    @Property_GetPropertyBookings
    public ResponseEntity<Set<Booking>> getPropertyBookings(@PathVariable Long prop_id) {
        Set<Booking> bookings = propertyService.getPropertyBookings(prop_id);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    } // http://localhost:8080/property/get_bookings/8

    @PostMapping("/add_booking/{prop_id}/{booking_id}")
    @Property_AddBookingToProperty
    public ResponseEntity<Void> addBookingToProperty(@PathVariable Long prop_id, @PathVariable Long booking_id) {
        propertyService.addBookingToProperty(prop_id, booking_id);
        return new ResponseEntity<>(HttpStatus.OK);
    } // http://localhost:8080/property/add_booking/12/7

    @PostMapping("/remove_booking/{prop_id}/{booking_id}")
    @Property_RemoveBookingFromProperty
    public ResponseEntity<Void> removeBookingFromProperty(@PathVariable Long prop_id, @PathVariable Long booking_id) {
        propertyService.removeBookingFromProperty(prop_id, booking_id);
        return new ResponseEntity<>(HttpStatus.OK);
    } // http://localhost:8080/property/remove_booking/8/9

}

