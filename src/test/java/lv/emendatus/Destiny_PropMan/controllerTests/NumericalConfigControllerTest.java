package lv.emendatus.Destiny_PropMan.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
import lv.emendatus.Destiny_PropMan.domain.entity.NumericalConfig;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.domain.entity.PropertyAmenity;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaCurrencyService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaNumericalConfigService;
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
public class NumericalConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JpaNumericalConfigService service;
    @MockBean
    private JpaCurrencyService currencyService;


    private String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    public void testGetAllNumericalConfigs() throws Exception {
        mockMvc.perform(get("/numerical-configs/getall"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetConfigByIdById() throws Exception {
        NumericalConfig config = new NumericalConfig();
        Currency currency = new Currency();
        currency.setId(1L);
        currency.setDesignation("EUR");
        config.setId(1L);
        config.setCurrency(currency);
        config.setName("Default");
        config.setValue(1.00);
        when(service.getNumericalConfigById(anyLong())).thenReturn(Optional.of(config));
        mockMvc.perform(get("/numerical-configs/get/{id}", 1L))
                .andExpect(status().isOk());
    }
    @Test
    public void testAddConfig() throws Exception {
        NumericalConfig config = new NumericalConfig();
        Currency currency = new Currency();
        currency.setId(1L);
        currency.setDesignation("EUR");
        config.setId(1L);
        config.setCurrency(currency);
        config.setName("Default");
        config.setValue(1.00);
        mockMvc.perform(post("/numerical-configs/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(config)))
                .andExpect(status().isCreated());
    }
    @Test
    public void testDeleteConfigById() throws Exception {
        NumericalConfig config = new NumericalConfig();
        Currency currency = new Currency();
        currency.setId(1L);
        currency.setDesignation("EUR");
        config.setId(1L);
        config.setCurrency(currency);
        config.setName("Default");
        config.setValue(1.00);
        when(service.getNumericalConfigById(eq(1L))).thenReturn(Optional.of(config));
        mockMvc.perform(delete("/numerical-configs/deleteConfigById/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetConfigByCurrency() throws Exception {
        NumericalConfig config1 = new NumericalConfig();
        Currency currency1 = new Currency();
        currency1.setId(1L);
        currency1.setDesignation("EUR");
        config1.setId(1L);
        config1.setCurrency(currency1);
        config1.setName("Default");
        config1.setValue(1.00);
        NumericalConfig config2 = new NumericalConfig();
        Currency currency2 = new Currency();
        currency1.setId(2L);
        currency1.setDesignation("THB");
        config2.setId(2L);
        config2.setCurrency(currency2);
        config2.setName("Thai Tourist Surcharge");
        config2.setValue(1.10);
        NumericalConfig config3 = new NumericalConfig();
        config3.setId(3L);
        config3.setCurrency(currency2);
        config3.setName("Thai Farang Discount");
        config3.setValue(0.90);
        List<NumericalConfig> thingsThai = new ArrayList<>();
        thingsThai.add(config2);
        thingsThai.add(config3);
        when(currencyService.getCurrencyById(eq(2L))).thenReturn(Optional.of(currency2));
        when(service.getNumericalConfigsByCurrency(currency2)).thenReturn(thingsThai);
        mockMvc.perform(get("/numerical-configs/getByCurrency/{сur_id}", 2L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].value").value(1.10))
                .andExpect(jsonPath("$[1].value").value(0.90));
    }

    @Test
    public void testUpdateConfig() throws Exception {
        NumericalConfig config1 = new NumericalConfig();
        Currency currency1 = new Currency();
        currency1.setId(1L);
        currency1.setDesignation("EUR");
        config1.setId(1L);
        config1.setCurrency(currency1);
        config1.setName("Initial");
        config1.setValue(1.00);
        NumericalConfig config2 = new NumericalConfig();
        config2.setId(1L);
        config2.setCurrency(currency1);
        config2.setName("Altered");
        config2.setValue(0.90);
        when(service.getNumericalConfigById(1L)).thenReturn(Optional.of(config1));
        mockMvc.perform(put("/numerical-configs/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(config2)))
                .andExpect(status().isOk());
    }

}
