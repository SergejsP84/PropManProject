package lv.emendatus.Destiny_PropMan.serviceTests;

import lv.emendatus.Destiny_PropMan.domain.dto.search.PropertySearchResultDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.search.SearchCriteria;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.PropertyType;
import lv.emendatus.Destiny_PropMan.repository.interfaces.BookingRepository;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PropertyRepository;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaPropertyService;
import lv.emendatus.Destiny_PropMan.service.implementation.PropertySearchService;
import lv.emendatus.Destiny_PropMan.util.TestDataInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ComponentScan(basePackages = {"lv.emendatus.Destiny_PropMan.util", "lv.emendatus.Destiny_PropMan.service.implementation"})
@ExtendWith(MockitoExtension.class)
class PropertySearchIntegrationTest {
    @Autowired
    private TestDataInitializer testDataInitializer;
    @Autowired
    @InjectMocks
    private JpaPropertyService propertyService;
    @Mock
    private BookingRepository bookingRepository;
    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertySearchService propertySearchService;

    @BeforeEach
    public void init() {
        testDataInitializer.initializeData();
    }

    @Test
    public void searchTest1() {
        // scenario 1 - Search for a property in Jurmala
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setLocation("Jurmala");
        searchCriteria.setStartDate(LocalDate.now());
        searchCriteria.setEndDate(LocalDate.now().plusDays(7));
        List<PropertySearchResultDTO> searchResults = propertySearchService.searchProperties(searchCriteria);
        assertEquals(2L, (long) searchResults.get(0).getId());
    }

    @Test
    public void searchTest2() {
        // search for a place to stay no smaller than 25 m2 and rated at no less than 4.4
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setRating(4.4F);
        searchCriteria.setMinSizeM2(25.0F);
        searchCriteria.setStartDate(LocalDate.of(2024, 3, 5));
        searchCriteria.setEndDate(LocalDate.of(2024, 3, 18));
        List<PropertySearchResultDTO> searchResults = propertySearchService.searchProperties(searchCriteria);
        assertEquals(2, searchResults.size());
        assertEquals(1L, (long) searchResults.get(0).getId());
        assertEquals(3L, (long) searchResults.get(1).getId());
    }

    @Test
    public void searchTest3() {
        // search for a hotel room in USA worth no more than 120 a day
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setLocation("USA");
        searchCriteria.setType(PropertyType.HOTEL_ROOM);
        searchCriteria.setStartDate(LocalDate.of(2024, 4, 9));
        searchCriteria.setEndDate(LocalDate.of(2024, 4, 15));
        searchCriteria.setMaxPrice(120.0);
        List<PropertySearchResultDTO> searchResults = propertySearchService.searchProperties(searchCriteria);
        assertEquals(1, searchResults.size());
        assertEquals(1L, (long) searchResults.get(0).getId());
    }

}
