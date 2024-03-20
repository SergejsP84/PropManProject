package lv.emendatus.Destiny_PropMan.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lv.emendatus.Destiny_PropMan.domain.entity.Amenity;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.domain.entity.PropertyAmenity;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaAmenityService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaPropertyAmenityService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaPropertyService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaTenantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class PropertyAmenityControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JpaPropertyAmenityService propertyAmenityService;
    @MockBean
    private JpaPropertyService propertyService;
    @MockBean
    private JpaAmenityService amenityService;
    @Autowired
    private ObjectMapper objectMapper;

    private String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    public void testAddPropertyAmenity() throws Exception {
        PropertyAmenity propertyAmenity = new PropertyAmenity();
        propertyAmenity.setId(1L);
        propertyAmenity.setAmenity_id(6L);
        propertyAmenity.setProperty_id(2L);
        mockMvc.perform(post("/propertyamenity/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(propertyAmenity)))
                .andExpect(status().isCreated());
    }
    @Test
    public void testGetPropertyAmenityById() throws Exception {
        PropertyAmenity propertyAmenity = new PropertyAmenity();
        propertyAmenity.setId(1L);
        propertyAmenity.setAmenity_id(6L);
        propertyAmenity.setProperty_id(2L);
        when(propertyAmenityService.getPropertyAmenityById(anyLong())).thenReturn(Optional.of(propertyAmenity));
        mockMvc.perform(get("/propertyamenity/getPropertyAmenityById/{id}", 1L))
                .andExpect(status().isOk());
    }
    @Test
    public void testGetAllPropertyAmenities() throws Exception {
        mockMvc.perform(get("/propertyamenity/getall"))
                .andExpect(status().isOk());
    }
    @Test
    public void testDeletePropertyAmenityById() throws Exception {
        PropertyAmenity propertyAmenity = new PropertyAmenity();
        propertyAmenity.setId(1L);
        propertyAmenity.setAmenity_id(6L);
        propertyAmenity.setProperty_id(2L);
        when(propertyAmenityService.getPropertyAmenityById(eq(1L))).thenReturn(Optional.of(propertyAmenity));
        mockMvc.perform(delete("/propertyamenity/deletePropertyAmenity/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetPropertyAmenityByProperty() throws Exception {
        PropertyAmenity propertyAmenity1 = new PropertyAmenity();
        propertyAmenity1.setId(1L);
        propertyAmenity1.setAmenity_id(2L);
        propertyAmenity1.setProperty_id(3L);
        PropertyAmenity propertyAmenity3 = new PropertyAmenity();
        propertyAmenity3.setId(3L);
        propertyAmenity3.setAmenity_id(1L);
        propertyAmenity3.setProperty_id(3L);
        Set<PropertyAmenity> byPropertyThree = new HashSet<>();
        byPropertyThree.add(propertyAmenity1);
        byPropertyThree.add(propertyAmenity3);
        Property property3 = new Property();
        property3.setId(3L);
        when(propertyService.getPropertyById(eq(3L))).thenReturn(Optional.of(property3));
        when(propertyAmenityService.getPropertyAmenitiesByProperty(3L)).thenReturn(byPropertyThree);
        mockMvc.perform(get("/propertyamenity/getPropertyAmenityByProperty/{prop_id}", 3L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].amenity_id").value(1L))
                .andExpect(jsonPath("$[1].amenity_id").value(2L));
    }

//    @Test
//    public void testGetPropertyAmenityByProperty() throws Exception {
//        PropertyAmenity propertyAmenity1 = new PropertyAmenity();
//        propertyAmenity1.setId(1L);
//        propertyAmenity1.setAmenity_id(2L);
//        propertyAmenity1.setProperty_id(3L);
//        PropertyAmenity propertyAmenity2 = new PropertyAmenity();
//        propertyAmenity2.setId(2L);
//        propertyAmenity2.setAmenity_id(2L);
//        propertyAmenity2.setProperty_id(4L);
//        PropertyAmenity propertyAmenity3 = new PropertyAmenity();
//        propertyAmenity3.setId(3L);
//        propertyAmenity3.setAmenity_id(1L);
//        propertyAmenity3.setProperty_id(3L);
//        PropertyAmenity propertyAmenity4 = new PropertyAmenity();
//        propertyAmenity4.setId(4L);
//        propertyAmenity4.setAmenity_id(1L);
//        propertyAmenity4.setProperty_id(4L);
//        Set<PropertyAmenity> byPropertyThree = new HashSet<>();
//        byPropertyThree.add(propertyAmenity1);
//        byPropertyThree.add(propertyAmenity3);
//        when(propertyAmenityService.getPropertyAmenitiesByProperty(3L)).thenReturn(byPropertyThree);
//        mockMvc.perform(get("/propertyamenity/getPropertyAmenityByProperty/{prop_id}", 3L))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].amenity_id").value(2L))
//                .andExpect(jsonPath("$[1].amenity_id").value(1L));
//    }
    @Test
    public void testGetPropertyAmenityByAmenity() throws Exception {
        PropertyAmenity propertyAmenity2 = new PropertyAmenity();
        propertyAmenity2.setId(2L);
        propertyAmenity2.setAmenity_id(2L);
        propertyAmenity2.setProperty_id(4L);
        PropertyAmenity propertyAmenity3 = new PropertyAmenity();
        propertyAmenity3.setId(3L);
        propertyAmenity3.setAmenity_id(2L);
        propertyAmenity3.setProperty_id(3L);
        List<PropertyAmenity> byAmenityTwo = new ArrayList<>();
        byAmenityTwo.add(propertyAmenity2);
        byAmenityTwo.add(propertyAmenity3);
        Amenity amenity2 = new Amenity();
        amenity2.setId(2L);
        when(amenityService.getAmenityById(eq(2L))).thenReturn(Optional.of(amenity2));
        when(propertyAmenityService.getPropertyAmenitiesByAmenity(2L)).thenReturn(byAmenityTwo);
        mockMvc.perform(get("/propertyamenity/getPropertyAmenityByAmenity/{amen_id}", 2L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].property_id").value(4L))
                .andExpect(jsonPath("$[1].property_id").value(3L));
    }
}
