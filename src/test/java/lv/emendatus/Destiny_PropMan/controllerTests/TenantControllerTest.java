package lv.emendatus.Destiny_PropMan.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lv.emendatus.Destiny_PropMan.controllers.TenantController;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.repository.interfaces.TenantRepository;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaTenantService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest(TenantController.class)
public class TenantControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JpaTenantService tenantService;
    @Autowired
    private ObjectMapper objectMapper;
    @Mock
    private TenantRepository tenantRepository;

    @Test
    public void testGetAllTenants() throws Exception {
        mockMvc.perform(get("/tenants/getall"))
                .andExpect(status().isOk());
    }
    @Test
    public void testGetTenantById() throws Exception {
        Tenant expectedTenant = new Tenant();
        expectedTenant.setId(1L);
        expectedTenant.setFirstName("Wendy");
        expectedTenant.setLastName("Testaburger");
        when(tenantService.getTenantById(anyLong())).thenReturn(Optional.of(expectedTenant));
        mockMvc.perform(get("/tenants/getTenantById/{id}", 1L))
                .andExpect(status().isOk());
    }
    @Test
    public void testAddTenant() throws Exception {
        Tenant newTenant = new Tenant();
        newTenant.setId(1L);
        newTenant.setFirstName("Wendy");
        newTenant.setLastName("Testaburger");
        mockMvc.perform(post("/tenants/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newTenant)))
                .andExpect(status().isCreated());
    }
    @Test
    public void testDeleteTenantById() throws Exception {
        Tenant tenantToDelete = new Tenant();
        tenantToDelete.setId(1L);
        tenantToDelete.setFirstName("Wendy");
        tenantToDelete.setLastName("Testaburger");
        when(tenantService.getTenantById(eq(1L))).thenReturn(Optional.of(tenantToDelete));
        mockMvc.perform(delete("/tenants/deleteTenant/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetTenantsByFirstNameOrLastName() throws Exception {
        Tenant tenant1 = new Tenant();
        tenant1.setId(1L);
        tenant1.setFirstName("Wendy");
        tenant1.setLastName("Testaburger");
        Tenant tenant2 = new Tenant();
        tenant2.setId(2L);
        tenant2.setFirstName("Randy");
        tenant2.setLastName("Marsh");
        List<Tenant> mockTenants = Arrays.asList(tenant1);
        when(tenantService.getTenantsByFirstNameOrLastName("Test")).thenReturn(mockTenants);
        mockMvc.perform(get("/tenants/getTenantsByName/{name}", "Test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value("Wendy"));
    }

    private String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
}
