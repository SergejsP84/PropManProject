package lv.emendatus.Destiny_PropMan.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaLeasingHistoryService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaManagerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class ManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JpaManagerService service;

    private String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    public void testGetAllManagers() throws Exception {
        mockMvc.perform(get("/managers/getall"))
                .andExpect(status().isOk());
    }
    @Test
    public void testGetManagerById() throws Exception {
        Manager manager = new Manager();
        manager.setId(1L);
        when(service.getManagerById(anyLong())).thenReturn(Optional.of(manager));
        mockMvc.perform(get("/managers/getManagerById/{id}", 1L))
                .andExpect(status().isOk());
    }
    @Test
    public void testAddManager() throws Exception {
        Manager manager = new Manager();
        manager.setId(1L);
        mockMvc.perform(post("/managers/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(manager)))
                .andExpect(status().isCreated());
    }
    @Test
    public void testDeleteManagerById() throws Exception {
        Manager manager = new Manager();
        manager.setId(1L);
        when(service.getManagerById(eq(1L))).thenReturn(Optional.of(manager));
        mockMvc.perform(delete("/managers/deleteManagerById/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateManager() throws Exception {
        Manager manager1 = new Manager();
        manager1.setManagerName("Eric Cartman");
        Manager manager2 = new Manager();
        manager2.setManagerName("Kyle Broflowski");
        when(service.getManagerById(1L)).thenReturn(Optional.of(manager1));
        mockMvc.perform(put("/managers/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(manager2)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetManagerProperties() throws Exception {
        Manager manager = new Manager();
        manager.setId(1L);
        manager.setManagerName("Stewart Ullman");
        Property property1 = new Property();
        property1.setDescription("Room 217");
        property1.setManager(manager);
        Property property2 = new Property();
        property2.setDescription("Room 218");
        property2.setManager(manager);
        Property property3 = new Property();
        property3.setDescription("Room 219");
        property3.setManager(manager);
        Set<Property> mockProperties = new HashSet<>();
        mockProperties.add(property1);
        mockProperties.add(property2);
        mockProperties.add(property3);
        when(service.getManagerById(
                any(Long.class)
        )).thenReturn(Optional.of(manager));
        when(service.getManagerProperties(
                any(Long.class)
        )).thenReturn(mockProperties);
        mockMvc.perform(get("/managers/getManagerProperties/{id}", 1L))
                .andExpect(status().isOk());
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void testAddPropertyToManager() throws Exception {
        Long managerId = 1L;
        Property newProperty = new Property();
        mockMvc.perform(post("/managers/addProperty/{managerId}", managerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newProperty)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testRemovePropertyFromManager() throws Exception {
        Long managerId = 1L;
        Long propertyId = 2L;
        mockMvc.perform(post("/managers/removeProperty/{managerId}/{propertyId}", managerId, propertyId))
                .andExpect(status().isOk());
    }
}
