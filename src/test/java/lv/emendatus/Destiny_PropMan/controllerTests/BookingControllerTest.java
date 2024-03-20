package lv.emendatus.Destiny_PropMan.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaBookingService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaPropertyService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaTenantPaymentService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaTenantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JpaBookingService service;
    @MockBean
    private JpaPropertyService propertyService;
    @MockBean
    private JpaTenantService tenantService;


    private String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    public void testGetAllBookings() throws Exception {
        mockMvc.perform(get("/booking/getall"))
                .andExpect(status().isOk());
    }
    @Test
    public void testGetBookingById() throws Exception {
        Booking booking = new Booking();
        booking.setId(1L);
        when(service.getBookingById(anyLong())).thenReturn(Optional.of(booking));
        mockMvc.perform(get("/booking/getBookingById/{id}", 1L))
                .andExpect(status().isOk());
    }
    @Test
    public void testAddBooking() throws Exception {
        Booking booking = new Booking();
        booking.setId(1L);
        mockMvc.perform(post("/booking/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(booking)))
                .andExpect(status().isCreated());
    }
    @Test
    public void testDeleteBookingById() throws Exception {
        Booking booking = new Booking();
        booking.setId(1L);
        when(service.getBookingById(eq(1L))).thenReturn(Optional.of(booking));
        mockMvc.perform(delete("/booking/deleteBookingById/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetBookingsByProperty() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        Property property2 = new Property();
        property2.setId(2L);
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setProperty(property1);
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setProperty(property2);
        Booking booking3 = new Booking();
        booking3.setId(3L);
        booking3.setProperty(property2);
        Set<Booking> resulting = new HashSet<>();
        resulting.add(booking2);
        resulting.add(booking3);
        when(propertyService.getPropertyById(2L)).thenReturn(Optional.of(property2));
        when(service.getBookingsByProperty(
                any(Property.class)
        )).thenReturn(resulting);
        mockMvc.perform(get("/booking/getByProperty/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[1].id").value(3L));
    }

    @Test
    public void testGetBookingsByTenant() throws Exception {
        Tenant tenant1 = new Tenant();
        tenant1.setId(1L);
        Tenant tenant2 = new Tenant();
        tenant2.setId(1L);
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setTenantId(1L);
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setTenantId(2L);
        Booking booking3 = new Booking();
        booking3.setId(3L);
        booking3.setTenantId(2L);
        Set<Booking> resulting = new HashSet<>();
        resulting.add(booking2);
        resulting.add(booking3);
        when(tenantService.getTenantById(2L)).thenReturn(Optional.of(tenant2));
        when(service.getBookingsByTenant(
                any(Tenant.class)
        )).thenReturn(resulting);
        mockMvc.perform(get("/booking/getByTenant/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[1].id").value(3L));
    }

    @Test
    public void testGetByDateRange() throws Exception {
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStartDate(Timestamp.valueOf("2024-02-10 00:00:00"));
        booking1.setEndDate(Timestamp.valueOf("2024-02-15 00:00:00"));
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStartDate(Timestamp.valueOf("2024-02-13 00:00:00"));
        booking2.setEndDate(Timestamp.valueOf("2024-02-18 00:00:00"));
        Booking booking3 = new Booking();
        booking3.setId(3L);
        booking3.setStartDate(Timestamp.valueOf("2024-02-16 00:00:00"));
        booking3.setEndDate(Timestamp.valueOf("2024-02-21 00:00:00"));
        List<Booking> mockBookings = new ArrayList<>();
        mockBookings.add(booking1);
        when(service.getBookingsByDateRange(
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(mockBookings);
        mockMvc.perform(get("/booking/getByDateRange/?start=2024-02-10&end=2024-02-15"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testGetByDateRangeWithOverlaps() throws Exception {
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStartDate(Timestamp.valueOf("2024-02-10 00:00:00"));
        booking1.setEndDate(Timestamp.valueOf("2024-02-15 00:00:00"));
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStartDate(Timestamp.valueOf("2024-02-13 00:00:00"));
        booking2.setEndDate(Timestamp.valueOf("2024-02-18 00:00:00"));
        Booking booking3 = new Booking();
        booking3.setId(3L);
        booking3.setStartDate(Timestamp.valueOf("2024-02-16 00:00:00"));
        booking3.setEndDate(Timestamp.valueOf("2024-02-21 00:00:00"));
        List<Booking> mockBookings = new ArrayList<>();
        mockBookings.add(booking2);
        mockBookings.add(booking3);
        when(service.getBookingsByDateRangeWithOverlaps(
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(mockBookings);
        mockMvc.perform(get("/booking/getByDateRangeWithOverlaps/?start=2024-02-12&end=2024-02-17"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[1].id").value(3L));
    }

    @Test
    public void testGetByStatus() throws Exception {
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStatus(BookingStatus.CONFIRMED);
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStatus(BookingStatus.CURRENT);
        Booking booking3 = new Booking();
        booking3.setId(3L);
        booking3.setStatus(BookingStatus.PENDING_PAYMENT);
        List<Booking> mockBookings = new ArrayList<>();
        mockBookings.add(booking1);
        when(service.getBookingsByStatus(
                BookingStatus.CONFIRMED
        )).thenReturn(mockBookings);
        mockMvc.perform(get("/booking/getByStatus")
                .param("status", BookingStatus.CONFIRMED.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testUpdateBookingStatus() throws Exception {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        when(service.getBookingById(1L)).thenReturn(Optional.of(booking));
        mockMvc.perform(post("/booking/updateStatus/{booking_id}", 1L)
                        .param("status", BookingStatus.PENDING_PAYMENT.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(booking)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetBookingPrice() throws Exception {
        Long bookingId = 1L;
        Double totalPrice = 100.0;
        when(service.getBookingById(bookingId)).thenReturn(Optional.of(new Booking()));
        when(service.calculateTotalPrice(bookingId)).thenReturn(totalPrice);
        mockMvc.perform(get("/booking/getPrice/{booking_id}", bookingId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(totalPrice));
    }

}

