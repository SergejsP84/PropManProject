package lv.emendatus.Destiny_PropMan.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lv.emendatus.Destiny_PropMan.controllers.SearchController;
import lv.emendatus.Destiny_PropMan.domain.dto.search.PropertySearchResultDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.search.SearchCriteria;
import lv.emendatus.Destiny_PropMan.service.implementation.PropertySearchService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(SearchController.class)
class SearchControllerTest {

    @MockBean
    private PropertySearchService propertySearchService;
    @InjectMocks
    private SearchController searchController;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Test
    void testSearchProperties() throws Exception {
        PropertySearchResultDTO resultDTO = new PropertySearchResultDTO();
        resultDTO.setId(1L);
        when(propertySearchService.searchProperties(any(SearchCriteria.class)))
                .thenReturn(Arrays.asList(resultDTO));
        SearchCriteria criteria = new SearchCriteria();
        mockMvc.perform(get("/search/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criteria)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}
