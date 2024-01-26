package lv.emendatus.Destiny_PropMan.controllerTests;
import com.fasterxml.jackson.databind.ObjectMapper;
import lv.emendatus.Destiny_PropMan.controllers.AmenityController;
import lv.emendatus.Destiny_PropMan.controllers.CurrencyController;
import lv.emendatus.Destiny_PropMan.domain.entity.Amenity;
import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaAmenityService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaCurrencyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Optional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(AmenityController.class)
public class AmenityControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JpaAmenityService amenityService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllAmenities() throws Exception {
        mockMvc.perform(get("/amenities/getall"))
                .andExpect(status().isOk());
    }
    @Test
    public void testGetAmenityById() throws Exception {
        Amenity expectedAmenity = new Amenity();
        expectedAmenity.setId(1L);
        expectedAmenity.setDescription("Rooftop Pool");
        when(amenityService.getAmenityById(anyLong())).thenReturn(Optional.of(expectedAmenity));
        mockMvc.perform(get("/amenities/getAmenityById/{id}", 1L))
                .andExpect(status().isOk());
    }
    @Test
    public void testAddAmenity() throws Exception {
        Amenity newAmenity = new Amenity();
        newAmenity.setId(1L);
        newAmenity.setDescription("Rooftop Pool");
        ResultActions resultActions = mockMvc.perform(post("/amenities/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newAmenity)));
        resultActions.andExpect(status().isCreated());
    }
    @Test
    public void testDeleteCurrencyById() throws Exception {
        Amenity amenityToDelete = new Amenity();
        amenityToDelete.setId(1L);
        amenityToDelete.setDescription("Rooftop Pool");
        when(amenityService.getAmenityById(eq(1L))).thenReturn(Optional.of(amenityToDelete));
        mockMvc.perform(delete("/amenities/deleteAmenityById/{id}", 1L))
                .andExpect(status().isOk());
    }

    private String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
}
