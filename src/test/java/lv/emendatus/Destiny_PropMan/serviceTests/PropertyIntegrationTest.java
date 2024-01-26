package lv.emendatus.Destiny_PropMan.serviceTests;

import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyType;
import lv.emendatus.Destiny_PropMan.repository.interfaces.*;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaBookingService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaPropertyService;
import lv.emendatus.Destiny_PropMan.util.TestDataInitializer;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan(basePackages = {"lv.emendatus.Destiny_PropMan.util", "lv.emendatus.Destiny_PropMan.service.implementation"})
@ExtendWith(MockitoExtension.class)
class PropertyIntegrationTest {
    @Autowired
    private TestDataInitializer testDataInitializer;
    @Autowired
    @InjectMocks
    private JpaPropertyService propertyService;
//    @Mock
//    private BookingRepository bookingRepository;
    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private AmenityRepository amenityRepository;
    @Autowired
    private PropertyAmenityRepository propertyAmenityRepository;

    @Autowired
    private ManagerRepository managerRepository;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private BillRepository billRepository;
    @BeforeEach
    public void init() {
        testDataInitializer.initializeData();
    }

    @Test
    public void getPropertiesByLocation() {
        List<Property> list1 = propertyService.getPropertiesByLocation("Overlook");
        List<Property> list2 = propertyService.getPropertiesByLocation("Jurmala");
        List<Property> list3 = propertyService.getPropertiesByLocation("Thailand");
        assertEquals(1L, list1.get(0).getId());
        assertEquals(2L, list2.get(0).getId());
        assertEquals(3L, list3.get(0).getId());
    }
    @Test
    public void getPropertiesByType() {
        List<Property> list1 = propertyService.getPropertiesByType(PropertyType.HOTEL_ROOM);
        List<Property> list2 = propertyService.getPropertiesByType(PropertyType.APARTMENT);
        List<Property> list3 = propertyService.getPropertiesByType(PropertyType.HOUSE);
        assertEquals(1L, list1.get(0).getId());
        assertEquals(2L, list2.get(0).getId());
        assertEquals(3L, list3.get(0).getId());
    }
    @Test
    public void getPropertiesByDailyPriceRange() {
        List<Property> retrievedProperties = propertyService.getPropertiesByDailyPriceRange(70.0, 100.0);
        assertEquals(2, retrievedProperties.size());
        assertEquals(1L, retrievedProperties.get(0).getId());
        assertEquals(2L, retrievedProperties.get(1).getId());
    }
    @Test
    public void getPropertiesByWeeklyPriceRange() {
        List<Property> retrievedProperties = propertyService.getPropertiesByWeeklyPriceRange(700.0, 900.0);
        assertEquals(2, retrievedProperties.size());
        assertEquals(1L, retrievedProperties.get(0).getId());
        assertEquals(3L, retrievedProperties.get(1).getId());
    }
    @Test
    public void getPropertiesByMonthlyPriceRange() {
        List<Property> retrievedProperties = propertyService.getPropertiesByMonthlyPriceRange(2000.0, 3500.0);
        assertEquals(3, retrievedProperties.size());
        assertEquals(1L, retrievedProperties.get(0).getId());
        assertEquals(2L, retrievedProperties.get(1).getId());
        assertEquals(3L, retrievedProperties.get(2).getId());
    }

    @Test
    public void getAvailableProperties() {
        LocalDate startDate = LocalDate.parse("2022-12-01");
        LocalDate endDate = LocalDate.parse("2022-12-10");
        List<Property> availableProperties = propertyService.getAvailableProperties(startDate, endDate);
        assertEquals(2, availableProperties.size());
        assertEquals(1L, availableProperties.get(0).getId());
        assertEquals(3L, availableProperties.get(1).getId());
    }

    @Test
    public void getPropertiesWithAmenities() {
        List<Long> amenities = new ArrayList<>();
        amenities.add(1L);
        Optional<Property> property = propertyRepository.findById(1L);
        Set<Property> retrievedProperties = propertyService.getPropertiesWithAmenities(amenities);
        assertEquals(1, retrievedProperties.size());
        assertTrue(retrievedProperties.contains(property.get()));
    }
    @Test
    public void addAmenityToProperty() {
        Optional<Property> property = propertyRepository.findById(1L);
        boolean amenityAdded = false;
        if (property.isPresent()) {
            PropertyAmenity newRelation = new PropertyAmenity();
            newRelation.setProperty_id(property.get().getId());
            newRelation.setAmenity_id(3L);
            propertyAmenityRepository.save(newRelation);
            List<Long> amenities = new ArrayList<>();
            amenities.add(3L);
            Set<Property> retrievedProperties = propertyService.getPropertiesWithAmenities(amenities);
            if (retrievedProperties.contains(property.get())) amenityAdded = true;
        } else {
            System.out.println("--- Failed in retrieving the property ---");
        }
        assertTrue(amenityAdded);
    }
    @Test
    public void removeAmenityFromProperty() {
        Optional<Property> property = propertyRepository.findById(2L);
        boolean amenityRemoved = false;
        if (property.isPresent()) {
            propertyService.removeAmenityFromProperty(2L, 2L);
            List<Long> amenities = new ArrayList<>();
            amenities.add(2L);
            Set<Property> retrievedProperties = propertyService.getPropertiesWithAmenities(amenities);
            if (retrievedProperties.isEmpty()) amenityRemoved = true;
        } else {
            System.out.println("--- Failed in retrieving the property ---");
        }
        assertTrue(amenityRemoved);
    }
    @Test
    public void updatePropertyAddress() {
        propertyService.updatePropertyAddress(1L, "Presidential Suite, Hotel Overlook");
        Assert.assertEquals("Presidential Suite, Hotel Overlook", propertyService.getPropertyById(1L).get().getAddress());
    }
    @Test
    public void updatePropertySettlement() {
        propertyService.updatePropertySettlement(2L, "Riga");
        Assert.assertEquals("Riga", propertyService.getPropertyById(2L).get().getSettlement());
    }
    @Test
    public void updatePropertyCountry() {
        propertyService.updatePropertyCountry(3L, "Vietnam");
        Assert.assertEquals("Vietnam", propertyService.getPropertyById(3L).get().getCountry());
    }

    @Test
    public void updatePropertyPricePerDay() {
        propertyService.updatePropertyPricePerDay(1L, 999.0);
        Assert.assertEquals(999.0, propertyService.getPropertyById(1L).get().getPricePerDay(), 0.0);
    }
    @Test
    public void updatePropertyPricePerWeek() {
        propertyService.updatePropertyPricePerWeek(1L, 999.0);
        Assert.assertEquals(999.0, propertyService.getPropertyById(1L).get().getPricePerWeek(), 0.0);
    }
    @Test
    public void updatePropertyPricePerMonth() {
        propertyService.updatePropertyPricePerMonth(1L, 999.0);
        Assert.assertEquals(999.0, propertyService.getPropertyById(1L).get().getPricePerMonth(), 0.0);
    }

    @Test
    public void updateManager() {
        propertyService.updateManager(1L, 3L);
        assertEquals(managerRepository.findById(3L).get(), propertyService.getPropertyById(1L).get().getManager());
    }
    @Test
    public void updateSize() {
        propertyService.updateSize(2L, 100.0F);
        assertEquals(100.0F, propertyService.getPropertyById(2L).get().getSizeM2());
    }
    @Test
    public void updateRating() {
        propertyService.updateRating(3L, 3.9F);
        assertEquals(3.9F, propertyService.getPropertyById(3L).get().getRating());
    }
    @Test
    public void updateDescription() {
        propertyService.updateDescription(1L, "RedRum");
        assertEquals("RedRum", propertyService.getPropertyById(1L).get().getDescription());
    }

    @Test
    public void setStatus() {
        propertyService.setStatus(1L, PropertyStatus.BUSY);
        assertEquals(PropertyStatus.BUSY, propertyService.getPropertyById(1L).get().getStatus());
    }

    @Test
    public void assignTenantToProperty() {
        propertyService.assignTenantToProperty(1L, 3L);
        assertEquals(propertyService.getPropertyById(1L).get().getTenant(), tenantRepository.findById(3L).get());
    }
    @Test
    public void getCurrentTenant() {
        propertyService.assignTenantToProperty(1L, 3L);
        Tenant tenant = propertyService.getCurrentTenant(1L);
        assertEquals(tenantRepository.findById(3L).get(), tenant);
    }
    @Test
    public void removeTenantFromProperty() {
        propertyService.assignTenantToProperty(1L, 3L);
        Tenant tenant = propertyService.getCurrentTenant(1L);
        propertyService.removeTenantFromProperty(1L);
        boolean empty = false;
        if (tenant != null) {
            try {
                Assertions.assertNull(propertyService.getCurrentTenant(1L));
            } catch (NullPointerException e) {
                empty = true;
            }
        } else {
            Assertions.fail("Tenant is still present. Those who do not leave the Overlook on time... They tend to have their stay, hmm... extended.");
        }
        assertTrue(empty);
    }

    @Test
    public void addBookingToProperty() {
        propertyService.addBookingToProperty(2L, 3L);
        Set<Booking> control = new HashSet<>();
        control.add(bookingRepository.findById(2L).get());
        control.add(bookingRepository.findById(3L).get());
        assertEquals(control, propertyService.getPropertyBookings(2L));
    }
    @Test
    public void getPropertyBookings() {
        Set<Booking> control = new HashSet<>();
        control.add(bookingRepository.findById(1L).get());
        assertEquals(control, propertyService.getPropertyBookings(1L));
    }
    @Test
    public void removeBookingFromProperty() {
        propertyService.removeBookingFromProperty(1L, 1L);
        Set<Booking> control = propertyService.getPropertyBookings(1L);
        assertTrue(control.isEmpty());
    }

    @Test
    public void addBillToProperty() {
        propertyService.addBillToProperty(3L, 1L);
        Set<Bill> control = new HashSet<>();
        control.add(billRepository.findById(1L).get());
        control.add(billRepository.findById(3L).get());
        assertEquals(control, propertyService.getPropertyBills(3L));
    }
    @Test
    public void getPropertyBills() {
        Set<Bill> control = new HashSet<>();
        control.add(billRepository.findById(1L).get());
        assertEquals(control, propertyService.getPropertyBills(1L));
    }
    @Test
    public void removeBillFromProperty() {
        System.out.println("Initial disposition:");
        Property property = propertyService.getPropertyById(2L).get();
        System.out.println("Retrieved property No. 2. Trying to get its bills...");
        System.out.println("The following bills have been found:");
        boolean removed = false;
        for (Bill bill : propertyService.getPropertyBills(2L)) {
            System.out.println("Bill " + bill.getId());
        }

        propertyService.removeBillFromProperty(2L, 2L);
        try {
            Set<Bill> control = propertyService.getPropertyBills(2L);
        } catch (NullPointerException e) {
            removed = true;
        }
        assertTrue(removed);
    }

}
