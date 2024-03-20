package lv.emendatus.Destiny_PropMan.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyType;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaBookingService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaPropertyService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class PropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JpaPropertyService service;

    private String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    public void testGetAllProperties() throws Exception {
        mockMvc.perform(get("/property/getall"))
                .andExpect(status().isOk());
    }
    @Test
    public void testGetPropertyById() throws Exception {
        Property property = new Property();
        property.setId(1L);
        when(service.getPropertyById(anyLong())).thenReturn(Optional.of(property));
        mockMvc.perform(get("/property/getPropertyById/{id}", 1L))
                .andExpect(status().isOk());
    }
    @Test
    public void testAddProperty() throws Exception {
        Property property = new Property();
        property.setId(1L);
        mockMvc.perform(post("/property/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(property)))
                .andExpect(status().isCreated());
    }
    @Test
    public void testDeletePropertyById() throws Exception {
        Property property = new Property();
        property.setId(1L);
        when(service.getPropertyById(eq(1L))).thenReturn(Optional.of(property));
        mockMvc.perform(delete("/property/deletePropertyById/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetPropertiesByLocation() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        property1.setAddress("Soi Six 12");
        property1.setSettlement("Pattaya");
        property1.setCountry("Thailand");
        List<Property> resulting = new ArrayList<>();
        resulting.add(property1);
        when(service.getPropertiesByLocation("Pattaya")).thenReturn(resulting);
        mockMvc.perform(get("/property/getPropertiesByLocation/Pattaya"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testGetPropertiesByType() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        property1.setType(PropertyType.APARTMENT);
        List<Property> resulting = new ArrayList<>();
        resulting.add(property1);
        when(service.getPropertiesByType(PropertyType.APARTMENT)).thenReturn(resulting);
        mockMvc.perform(get("/property/getPropertiesByType/APARTMENT"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testGetPropertiesByDailyPrice() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        property1.setPricePerDay(50.0);
        Property property2 = new Property();
        property2.setId(2L);
        property2.setPricePerDay(70.0);
        Property property3 = new Property();
        property3.setId(3L);
        property3.setPricePerDay(90.0);
        List<Property> resulting = new ArrayList<>();
        resulting.add(property1);
        resulting.add(property2);
        when(service.getPropertiesByDailyPriceRange(40.0, 80.0)).thenReturn(resulting);
        mockMvc.perform(get("/property/getPriceRangeDay/40.0/80.0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    public void testGetPropertiesByWeeklyPrice() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        property1.setPricePerWeek(200.0);
        Property property2 = new Property();
        property2.setId(2L);
        property1.setPricePerWeek(300.0);
        Property property3 = new Property();
        property3.setId(3L);
        property1.setPricePerWeek(400.0);
        List<Property> resulting = new ArrayList<>();
        resulting.add(property2);
        resulting.add(property3);
        when(service.getPropertiesByWeeklyPriceRange(300.0, 500.0)).thenReturn(resulting);
        mockMvc.perform(get("/property/getPriceRangeWeek/300.0/500.0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[1].id").value(3L));
    }

    @Test
    public void testGetPropertiesByMonthlyPrice() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        property1.setPricePerMonth(700.0);
        Property property2 = new Property();
        property2.setId(2L);
        property2.setPricePerMonth(800.0);
        Property property3 = new Property();
        property3.setId(3L);
        property1.setPricePerMonth(900.0);
        List<Property> resulting = new ArrayList<>();
        resulting.add(property1);
        resulting.add(property2);
        when(service.getPropertiesByMonthlyPriceRange(600.0, 850.0)).thenReturn(resulting);
        mockMvc.perform(get("/property/getPriceRangeMonth/600.0/850.0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    public void testGetAvailableProperties() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        Property property2 = new Property();
        property2.setId(2L);
        Property property3 = new Property();
        property3.setId(3L);
        List<Property> resulting = new ArrayList<>();
        resulting.add(property1);
        resulting.add(property2);
        when(service.getAvailableProperties(
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(resulting);
        mockMvc.perform(get("/property/getAvailableProperties?start=2024-02-10&end=2024-02-18"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    public void testGetPropertiesWithAmenities() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        Property property2 = new Property();
        property2.setId(2L);
        Property property3 = new Property();
        property3.setId(3L);
        List<Property> resulting = new ArrayList<>();
        resulting.add(property1);
        resulting.add(property2);
        List<Long> amenityIds = Arrays.asList(1L, 3L);
        when(service.getPropertiesWithAmenities(
                ArgumentMatchers.argThat(arg -> arg.containsAll(amenityIds))
        )).thenReturn(new HashSet<>(resulting));
        mockMvc.perform(get("/property/getPropertiesWithAmenities")
                        .param("amenityIds", "1", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[1].id").value(1L));
    }

    @Test
    public void testUpdateAddress() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        property1.setAddress("216 Beach Road");
        Property property2 = new Property();
        property2.setId(2L);
        property2.setAddress("43 Soi Kratong");
        when(service.getPropertyById(1L)).thenReturn(Optional.of(property1));
        mockMvc.perform(put("/property/update_address/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(property2)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateSettlement() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        property1.setSettlement("Hua Hin");
        Property property2 = new Property();
        property2.setId(2L);
        property2.setSettlement("Suratthani");
        when(service.getPropertyById(1L)).thenReturn(Optional.of(property1));
        mockMvc.perform(put("/property/update_settlement/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(property2)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateCountry() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        property1.setCountry("Vietnam");
        Property property2 = new Property();
        property2.setId(2L);
        property2.setCountry("Thailand");
        when(service.getPropertyById(1L)).thenReturn(Optional.of(property1));
        mockMvc.perform(put("/property/update_country/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(property2)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdatePricePerDay() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        property1.setPricePerDay(50.0);
        when(service.getPropertyById(1L)).thenReturn(Optional.of(property1));
        mockMvc.perform(put("/property/update_price_day/{id}/60.0", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(property1)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdatePricePerWeek() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        property1.setPricePerWeek(200.0);
        when(service.getPropertyById(1L)).thenReturn(Optional.of(property1));
        mockMvc.perform(put("/property/update_price_week/{id}/250.0", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(property1)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdatePricePerMonth() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        property1.setPricePerMonth(600.0);
        when(service.getPropertyById(1L)).thenReturn(Optional.of(property1));
        mockMvc.perform(put("/property/update_price_month/{id}/700.0", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(property1)))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddAmenityToProperty() throws Exception {
        Property property = new Property();
        property.setId(1L);
        Amenity amenity = new Amenity();
        amenity.setId(1L);
        mockMvc.perform(post("/property/addAmenity/1/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testRemoveAmenityFromProperty() throws Exception {
        Property property = new Property();
        property.setId(1L);
        when(service.getPropertyById(eq(1L))).thenReturn(Optional.of(property));
        mockMvc.perform(delete("/property/removeAmenity/1/1", 1L))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdatePropertyManager() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        Manager manager1 = new Manager();
        property1.setManager(manager1);
        Set<Property> properties = new HashSet<>();
        properties.add(property1);
        manager1.setProperties(properties);
        mockMvc.perform(put("/property/update_property_manager/1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(property1)))
                .andExpect(status().isOk());
    }

    @Test
    public void testSetPropertyStatus() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        property1.setStatus(PropertyStatus.BLOCKED);
        mockMvc.perform(put("/property/set_property_status/1/AVAILABLE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(property1)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateSize() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        property1.setSizeM2(25.0F);
        mockMvc.perform(put("/property/update_size/1/35.0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(property1)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateRating() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        property1.setRating(4.5F);
        mockMvc.perform(put("/property/update_rating/1/4.6")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(property1)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateDescription() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        property1.setDescription("Old description");
        mockMvc.perform(put("/property/update_description/1")
                        .param("description", "New description")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(property1)))
                .andExpect(status().isOk());
    }

    @Test
    public void testAssignTenantToProperty() throws Exception {
        Property property = new Property();
        property.setId(1L);
        Tenant tenant1 = new Tenant();
        tenant1.setId(1L);
        mockMvc.perform(post("/property/assign_tenant/1/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCurrentTenant() throws Exception {
        Property property = new Property();
        property.setId(1L);
        Tenant tenant1 = new Tenant();
        tenant1.setId(1L);
        when(service.getCurrentTenant(1L)).thenReturn(tenant1);
        mockMvc.perform(get("/property/get_tenant/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testRemoveTenantFromProperty() throws Exception {
        Property property = new Property();
        property.setId(1L);
        when(service.getPropertyById(eq(1L))).thenReturn(Optional.of(property));
        mockMvc.perform(delete("/property/remove_tenant/1", 1L))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddBillToProperty() throws Exception {
        Property property = new Property();
        property.setId(1L);
        Bill bill1 = new Bill();
        bill1.setId(1L);
        mockMvc.perform(post("/property/add_bill/1/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetBills() throws Exception {
        Property property = new Property();
        property.setId(1L);
        Bill bill1 = new Bill();
        bill1.setId(1L);
        Bill bill2 = new Bill();
        bill2.setId(2L);
        Set<Bill> bills = new HashSet<>();
        bills.add(bill1);
        bills.add(bill2);
        when(service.getPropertyBills(1L)).thenReturn(bills);
        mockMvc.perform(get("/property/get_bills/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[1].id").value(1L))
                .andExpect(status().isOk());
    }

    @Test
    public void testRemoveBillFromProperty() throws Exception {
        Long propertyId = 12L;
        Long billId = 5L;
        mockMvc.perform(post("/property/remove_bill/{prop_id}/{bill_id}", propertyId, billId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(service, times(1)).removeBillFromProperty(eq(propertyId), eq(billId));
    }

    @Test
    public void testAddBookingToProperty() throws Exception {
        Property property = new Property();
        property.setId(1L);
        Booking booking1 = new Booking();
        booking1.setId(1L);
        mockMvc.perform(post("/property/add_booking/1/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetBookings() throws Exception {
        Property property = new Property();
        property.setId(1L);
        Booking booking1 = new Booking();
        booking1.setId(1L);
        Booking booking2 = new Booking();
        booking2.setId(2L);
        Set<Booking> bookings = new HashSet<>();
        bookings.add(booking1);
        bookings.add(booking2);
        when(service.getPropertyBookings(1L)).thenReturn(bookings);
        mockMvc.perform(get("/property/get_bookings/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[1].id").value(1L))
                .andExpect(status().isOk());
    }

    @Test
    public void testRemoveBookingFromProperty() throws Exception {
        Long propertyId = 13L;
        Long bookingId = 6L;
        mockMvc.perform(post("/property/remove_booking/{prop_id}/{bill_id}", propertyId, bookingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(service, times(1)).removeBookingFromProperty(eq(propertyId), eq(bookingId));
    }
}
