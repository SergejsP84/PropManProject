package lv.emendatus.Destiny_PropMan.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lv.emendatus.Destiny_PropMan.controllers.CurrencyController;
import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
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

@WebMvcTest(CurrencyController.class) //  focus the testing only on the CurrencyController class
public class CurrencyControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JpaCurrencyService currencyService;
    @Autowired
    private ObjectMapper objectMapper;
    @Test
    public void testGetAllCurrencies() throws Exception {
        mockMvc.perform(get("/currency/getall"))
                .andExpect(status().isOk());
    }
    @Test
    public void testGetCurrencyById() throws Exception {
        Currency expectedCurrency = new Currency();
        expectedCurrency.setId(1L);
        expectedCurrency.setDesignation("THB");
        when(currencyService.getCurrencyById(anyLong())).thenReturn(Optional.of(expectedCurrency));
        mockMvc.perform(get("/currency/getCurrencyById")
                        .param("id", "1"))
                .andExpect(status().isOk());
    }
    @Test
    public void testAddCurrency() throws Exception {
        // Create a new currency object for the test
        Currency newCurrency = new Currency();
        newCurrency.setId(1L);
        newCurrency.setDesignation("USD");
        // Perform the HTTP POST request to add the currency
        ResultActions resultActions = mockMvc.perform(post("/currency/add") // simulate a POST request
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newCurrency))); // converts the newCurrency object to a JSON string
        // Verify that the response has a status of 201 (CREATED)
        resultActions.andExpect(status().isCreated());
    }
    @Test
    public void testDeleteCurrencyById() throws Exception {
        Currency currencyToDelete = new Currency();
        currencyToDelete.setId(1L);
        currencyToDelete.setDesignation("THB");
        when(currencyService.getCurrencyById(eq(1L))).thenReturn(Optional.of(currencyToDelete));
        mockMvc.perform(delete("/currency/deleteCurrencyById/{id}", 1L))
                .andExpect(status().isOk());
    }

    // Utility method to convert an object to JSON string
    private String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
}