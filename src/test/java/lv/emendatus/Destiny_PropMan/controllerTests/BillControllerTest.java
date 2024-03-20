package lv.emendatus.Destiny_PropMan.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaBillService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaManagerService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaPropertyService;
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
public class BillControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JpaBillService service;
    @MockBean
    private JpaPropertyService propertyService;

    private String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    public void testGetAllBills() throws Exception {
        mockMvc.perform(get("/bills/getall"))
                .andExpect(status().isOk());
    }
    @Test
    public void testGetBillById() throws Exception {
        Bill bill = new Bill();
        bill.setId(1L);
        when(service.getBillById(anyLong())).thenReturn(Optional.of(bill));
        mockMvc.perform(get("/bills/getBillById/{id}", 1L))
                .andExpect(status().isOk());
    }
    @Test
    public void testAddBill() throws Exception {
        Bill bill = new Bill();
        bill.setId(1L);
        mockMvc.perform(post("/bills/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bill)))
                .andExpect(status().isCreated());
    }
    @Test
    public void testDeleteBillById() throws Exception {
        Bill bill = new Bill();
        bill.setId(1L);
        when(service.getBillById(eq(1L))).thenReturn(Optional.of(bill));
        mockMvc.perform(delete("/bills/deleteBillById/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetBillsByProperty() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        Property property2 = new Property();
        property2.setId(2L);
        Bill bill1 = new Bill();
        bill1.setId(1L);
        bill1.setProperty(property1);
        Bill bill2 = new Bill();
        bill2.setId(2L);
        bill2.setProperty(property2);
        Bill bill3 = new Bill();
        bill3.setId(3L);
        bill3.setProperty(property2);
        List<Bill> resulting = Arrays.asList(bill2, bill3);
        when(propertyService.getPropertyById(2L)).thenReturn(Optional.of(property2));
        when(service.getBillsByProperty(
                any(Property.class)
        )).thenReturn(resulting);
        mockMvc.perform(get("/bills/getByProperty/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[1].id").value(3L));
    }

    @Test
    public void testUnpaidBills() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        Bill bill1 = new Bill();
        bill1.setId(1L);
        bill1.setPaid(false);
        bill1.setProperty(property1);
        Bill bill2 = new Bill();
        bill2.setId(2L);
        bill2.setPaid(true);
        bill2.setProperty(property1);
        Bill bill3 = new Bill();
        bill3.setId(3L);
        bill3.setPaid(false);
        bill3.setProperty(property1);
        List<Bill> resulting = Arrays.asList(bill1, bill3);
        when(propertyService.getPropertyById(1L)).thenReturn(Optional.of(property1));
        when(service.getUnpaidBills(
                any(Property.class)
        )).thenReturn(resulting);
        mockMvc.perform(get("/bills/getUnpaidBills/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(3L));
    }

    @Test
    public void testPaidBills() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        Bill bill1 = new Bill();
        bill1.setId(1L);
        bill1.setPaid(false);
        bill1.setProperty(property1);
        Bill bill2 = new Bill();
        bill2.setId(2L);
        bill2.setPaid(true);
        bill2.setProperty(property1);
        Bill bill3 = new Bill();
        bill3.setId(3L);
        bill3.setPaid(false);
        bill3.setProperty(property1);
        List<Bill> resulting = Arrays.asList(bill2);
        when(propertyService.getPropertyById(1L)).thenReturn(Optional.of(property1));
        when(service.getPaidBills(
                any(Property.class)
        )).thenReturn(resulting);
        mockMvc.perform(get("/bills/getPaidBills/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(2L));
    }

    @Test
    public void testGetBillsByCategory() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        Bill bill1 = new Bill();
        bill1.setId(1L);
        bill1.setExpenseCategory("Food");
        bill1.setProperty(property1);
        Bill bill2 = new Bill();
        bill2.setId(2L);
        bill2.setExpenseCategory("Food");
        bill2.setProperty(property1);
        Bill bill3 = new Bill();
        bill3.setId(3L);
        bill3.setExpenseCategory("Drinks");
        bill3.setProperty(property1);
        List<Bill> resulting = Arrays.asList(bill1, bill2);
        when(propertyService.getPropertyById(1L)).thenReturn(Optional.of(property1));
        when(service.getBillsByExpenseCategory(
                any(Property.class),
                any(String.class)
        )).thenReturn(resulting);
        mockMvc.perform(get("/bills/getByCategory/1/Food"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    public void testGetBillsByDueDate() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        Bill bill1 = new Bill();
        bill1.setId(1L);
        bill1.setDueDate(Timestamp.valueOf("2024-02-11 00:00:00"));
        bill1.setProperty(property1);
        Bill bill2 = new Bill();
        bill2.setId(2L);
        bill2.setDueDate(Timestamp.valueOf("2024-02-15 00:00:00"));
        bill2.setProperty(property1);
        Bill bill3 = new Bill();
        bill3.setId(3L);
        bill3.setDueDate(Timestamp.valueOf("2024-02-20 00:00:00"));
        bill3.setProperty(property1);
        List<Bill> mockBills = new ArrayList<>();
        mockBills.add(bill1);
        mockBills.add(bill2);
        when(propertyService.getPropertyById(1L)).thenReturn(Optional.of(property1));
        when(service.getBillsByDueDateRange(
                any(LocalDate.class),
                any(LocalDate.class),
                any(Property.class)
        )).thenReturn(mockBills);
        mockMvc.perform(get("/bills/getByDueDate/1")
                        .param("start", "2024-02-10")
                        .param("end", "2024-02-18"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    public void testTogglePaidStatus() throws Exception {
        Bill bill1 = new Bill();
        bill1.setId(1L);
        bill1.setPaid(true);
        when(service.getBillById(1L)).thenReturn(Optional.of(bill1));
        mockMvc.perform(patch("/bills/togglePaymentStatus/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bill1)))
                .andExpect(status().isOk());
    }

}
