package lv.emendatus.Destiny_PropMan.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.service.implementation.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class TenantPaymentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JpaTenantPaymentService service;
    @MockBean
    private JpaTenantService tenantService;
    @MockBean
    private JpaManagerService managerService;
    @MockBean
    private JpaPropertyService propertyService;

    private String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    public void testGetAllTenantPayments() throws Exception {
        mockMvc.perform(get("/payments/getall"))
                .andExpect(status().isOk());
    }
    @Test
    public void testGetPaymentById() throws Exception {
        TenantPayment payment = new TenantPayment();
        payment.setId(1L);
        when(service.getTenantPaymentById(anyLong())).thenReturn(Optional.of(payment));
        mockMvc.perform(get("/payments/getPaymentById/{id}", 1L))
                .andExpect(status().isOk());
    }
    @Test
    public void testAddPayment() throws Exception {
        TenantPayment payment = new TenantPayment();
        payment.setId(1L);
        mockMvc.perform(post("/payments/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(payment)))
                .andExpect(status().isCreated());
    }
    @Test
    public void testDeletePaymentById() throws Exception {
        TenantPayment payment = new TenantPayment();
        payment.setId(1L);
        when(service.getTenantPaymentById(eq(1L))).thenReturn(Optional.of(payment));
        mockMvc.perform(delete("/payments/deletePaymentById/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetPaymentsByTenant() throws Exception {
        Tenant tenant1 = new Tenant();
        tenant1.setId(1L);
        Tenant tenant2 = new Tenant();
        tenant2.setId(2L);
        TenantPayment payment1 = new TenantPayment();
        payment1.setId(1L);
        payment1.setTenant(tenant1);
        TenantPayment payment2 = new TenantPayment();
        payment2.setId(2L);
        payment2.setTenant(tenant2);
        TenantPayment payment3 = new TenantPayment();
        payment3.setId(3L);
        payment3.setTenant(tenant2);
        List<TenantPayment> resulting = Arrays.asList(payment2, payment3);
        when(tenantService.getTenantById(2L)).thenReturn(Optional.of(tenant2));
        when(service.getPaymentsByTenant(
                any(Long.class)
        )).thenReturn(resulting);
        mockMvc.perform(get("/payments/getByTenant/{ten_id}", 2L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[1].id").value(3L));
    }

    @Test
    public void testGetPaymentsByManager() throws Exception {
        Manager manager1 = new Manager();
        manager1.setId(1L);
        Manager manager2 = new Manager();
        manager2.setId(2L);
        TenantPayment payment1 = new TenantPayment();
        payment1.setId(1L);
        payment1.setManagerId(1L);
        TenantPayment payment2 = new TenantPayment();
        payment2.setId(2L);
        payment2.setManagerId(2L);
        TenantPayment payment3 = new TenantPayment();
        payment3.setId(3L);
        payment3.setManagerId(2L);
        List<TenantPayment> resulting = Arrays.asList(payment2, payment3);
        when(managerService.getManagerById(2L)).thenReturn(Optional.of(manager2));
        when(service.getPaymentsByManager(
                any(Long.class)
        )).thenReturn(resulting);
        mockMvc.perform(get("/payments/getByManager/{man_id}", 2L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[1].id").value(3L));
    }

    @Test
    public void testGetPaymentsByProperty() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        Property property2 = new Property();
        property2.setId(2L);
        TenantPayment payment1 = new TenantPayment();
        payment1.setId(1L);
        payment1.setAssociatedPropertyId(1L);
        TenantPayment payment2 = new TenantPayment();
        payment2.setId(2L);
        payment2.setAssociatedPropertyId(2L);
        TenantPayment payment3 = new TenantPayment();
        payment3.setId(3L);
        payment3.setAssociatedPropertyId(2L);
        List<TenantPayment> resulting = Arrays.asList(payment2, payment3);
        when(propertyService.getPropertyById(2L)).thenReturn(Optional.of(property2));
        when(service.getPaymentsByProperty(
                any(Long.class)
        )).thenReturn(resulting);
        mockMvc.perform(get("/payments/getByProperty/{prop_id}", 2L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[1].id").value(3L));
    }

    @Test
    public void testGetByDateRange() throws Exception {
        TenantPayment payment1 = new TenantPayment();
        payment1.setId(1L);
        payment1.setReceiptDue(Timestamp.valueOf("2024-02-11 00:00:00"));
        TenantPayment payment2 = new TenantPayment();
        payment2.setId(2L);
        payment2.setReceiptDue(Timestamp.valueOf("2024-02-14 00:00:00"));
        TenantPayment payment3 = new TenantPayment();
        payment3.setId(3L);
        payment3.setReceiptDue(Timestamp.valueOf("2024-02-17 00:00:00"));
        List<TenantPayment> mockPayments = new ArrayList<>();
        mockPayments.add(payment2);
        mockPayments.add(payment3);
        when(service.getPaymentsByDateRange(
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(mockPayments);
        mockMvc.perform(get("/payments/getByDateRange")
                        .param("start", "2024-02-12")
                        .param("end", "2024-02-19"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[1].id").value(3L));
    }

    @Test
    public void testGetUnsettled() throws Exception {
        TenantPayment payment1 = new TenantPayment();
        payment1.setId(1L);
        payment1.setReceivedFromTenant(false);
        payment1.setFeePaidToManager(false);
        TenantPayment payment2 = new TenantPayment();
        payment2.setId(2L);
        payment2.setReceivedFromTenant(true);
        payment2.setFeePaidToManager(false);
        TenantPayment payment3 = new TenantPayment();
        payment3.setId(3L);
        payment3.setReceivedFromTenant(true);
        payment3.setFeePaidToManager(true);
        List<TenantPayment> resulting = Arrays.asList(payment3);
        when(service.getUnsettledPayments()).thenReturn(resulting);
        mockMvc.perform(get("/payments/getUnsettled"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(3L));
    }

    @Test
    public void testSettle() throws Exception {
        TenantPayment tenantPayment = new TenantPayment();
        tenantPayment.setId(1L);
        tenantPayment.setFeePaidToManager(true);
        tenantPayment.setReceivedFromTenant(true);
        when(service.getTenantPaymentById(1L)).thenReturn(Optional.of(tenantPayment));
        mockMvc.perform(post("/payments/settle/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(tenantPayment)))
                .andExpect(status().isOk());
    }

}
