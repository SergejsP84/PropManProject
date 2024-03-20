package lv.emendatus.Destiny_PropMan.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaLeasingHistoryService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaPropertyService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaTenantService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class LeasingHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JpaLeasingHistoryService service;
    @MockBean
    private JpaPropertyService propertyService;
    @MockBean
    private JpaTenantService tenantService;


    private String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    public void testGetAllLeasingHistories() throws Exception {
        mockMvc.perform(get("/leasinghistory/getall"))
                .andExpect(status().isOk());
    }
    @Test
    public void testGetHistoryById() throws Exception {
        LeasingHistory history = new LeasingHistory();
        history.setId(1L);
        when(service.getLeasingHistoryById(anyLong())).thenReturn(Optional.of(history));
        mockMvc.perform(get("/leasinghistory/getLeasingHistoryById/{id}", 1L))
                .andExpect(status().isOk());
    }
    @Test
    public void testAddHistory() throws Exception {
        LeasingHistory history = new LeasingHistory();
        history.setId(1L);
        mockMvc.perform(post("/leasinghistory/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(history)))
                .andExpect(status().isCreated());
    }
    @Test
    public void testDeleteHistoryById() throws Exception {
        LeasingHistory history = new LeasingHistory();
        history.setId(1L);
        when(service.getLeasingHistoryById(eq(1L))).thenReturn(Optional.of(history));
        mockMvc.perform(delete("/leasinghistory/deleteLeasingHistory/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetHistoryByTime() throws Exception {
        LeasingHistory leasingHistory1 = new LeasingHistory();
        leasingHistory1.setId(1L);
        leasingHistory1.setStartDate(Timestamp.valueOf("2020-02-05 00:00:00"));
        leasingHistory1.setEndDate(Timestamp.valueOf("2020-02-10 00:00:00"));
        LeasingHistory leasingHistory2 = new LeasingHistory();
        leasingHistory2.setId(2L);
        leasingHistory2.setStartDate(Timestamp.valueOf("2020-02-15 00:00:00"));
        leasingHistory2.setEndDate(Timestamp.valueOf("2020-02-20 00:00:00"));
        List<LeasingHistory> mockLeasingHistories = Arrays.asList(leasingHistory1, leasingHistory2);
        LocalDateTime startDateTime = leasingHistory1.getStartDate().toLocalDateTime();
        LocalDateTime endDateTime = leasingHistory2.getEndDate().toLocalDateTime();
//        System.out.println("Start Date Time: " + startDateTime);
//        System.out.println("End Date Time: " + endDateTime);
//        when(service.getAllLeasingHistories()).thenReturn(mockLeasingHistories);
        when(service.getLeasingHistoryByTimePeriod(
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(mockLeasingHistories);
//        System.out.println("Mock getAllLeasingHistories called");
//        System.out.println("Mock getLeasingHistoryByTimePeriod called");
        mockMvc.perform(get("/leasinghistory/getByTime")
                        .param("start", "2020-02-04")
                        .param("end", "2020-02-26"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    public void testGetHistoryByProperty() throws Exception {
        Property property1 = new Property();
        property1.setId(1L);
        Property property2 = new Property();
        property2.setId(2L);
        LeasingHistory leasingHistory1 = new LeasingHistory();
        leasingHistory1.setId(1L);
        leasingHistory1.setPropertyId(1L);
        LeasingHistory leasingHistory2 = new LeasingHistory();
        leasingHistory2.setId(2L);
        leasingHistory2.setPropertyId(2L);
        LeasingHistory leasingHistory3 = new LeasingHistory();
        leasingHistory3.setId(3L);
        leasingHistory3.setPropertyId(2L);
        List<LeasingHistory> resulting = Arrays.asList(leasingHistory2, leasingHistory3);
        when(propertyService.getPropertyById(2L)).thenReturn(Optional.of(property2));
        when(service.getLeasingHistoryByProperty(
                any(Property.class)
        )).thenReturn(resulting);
        mockMvc.perform(get("/leasinghistory/getByProperty/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[1].id").value(3L));
    }

    @Test
    public void testGetHistoryByTenant() throws Exception {
        Tenant tenant1 = new Tenant();
        tenant1.setId(1L);
        Tenant tenant2 = new Tenant();
        tenant2.setId(2L);
        LeasingHistory leasingHistory1 = new LeasingHistory();
        leasingHistory1.setId(1L);
        leasingHistory1.setTenant(tenant1);
        LeasingHistory leasingHistory2 = new LeasingHistory();
        leasingHistory2.setId(2L);
        leasingHistory2.setTenant(tenant2);
        LeasingHistory leasingHistory3 = new LeasingHistory();
        leasingHistory3.setId(3L);
        leasingHistory3.setTenant(tenant2);
        List<LeasingHistory> resulting = Arrays.asList(leasingHistory2, leasingHistory3);
        when(tenantService.getTenantById(2L)).thenReturn(Optional.of(tenant2));
        when(service.getLeasingHistoryByTenant(
                any(Tenant.class)
        )).thenReturn(resulting);
        mockMvc.perform(get("/leasinghistory/getByTenant/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[1].id").value(3L));
    }

}
