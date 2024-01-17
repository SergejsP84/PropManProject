package lv.emendatus.Destiny_PropMan.serviceTests;

import lv.emendatus.Destiny_PropMan.domain.entity.Bill;
import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.repository.interfaces.BillRepository;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PropertyRepository;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaBillService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaPropertyService;
import lv.emendatus.Destiny_PropMan.service.interfaces.BillService;
import lv.emendatus.Destiny_PropMan.service.interfaces.CurrencyService;
import lv.emendatus.Destiny_PropMan.util.TestDataInitializer;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan(basePackages = {"lv.emendatus.Destiny_PropMan.util", "lv.emendatus.Destiny_PropMan.service.implementation"})
@ExtendWith(MockitoExtension.class)
class BillServiceTest {
    @Autowired
    private TestDataInitializer testDataInitializer;
    @Autowired
    @InjectMocks
    private JpaBillService jpaBillService;
    private final BillRepository billRepositoryMock = Mockito.mock(BillRepository.class);
    @Mock
    private BillRepository billRepository;
    private JpaPropertyService propertyService;
    @Mock
    private CurrencyService currencyService;


    @Test
    void getAllBills() {
        List<Bill> bills = Arrays.asList(new Bill(), new Bill());
        when(billRepository.findAll()).thenReturn(bills);
        List<Bill> result = jpaBillService.getAllBills();
        assertEquals(bills, result);
    }
    @Test
    void getBillById() {
        Bill eighthBill = new Bill();
        eighthBill.setId(8L);
        eighthBill.setExpenseCategory("Whiskey");
        when(billRepository.findById(8L)).thenReturn(Optional.of(eighthBill));
        Optional<Bill> obtainedBill = jpaBillService.getBillById(8L);
        assertEquals(Optional.of(eighthBill), obtainedBill);
    }
    @Test
    void addBill() {
        Bill newBill = new Bill();
        newBill.setId(9L);
        newBill.setAmount(100.00);
        when(billRepository.save(newBill)).thenReturn(newBill);
        jpaBillService.addBill(newBill);
        verify(billRepository).save(newBill);
    }
    @Test
    void deleteBill() {
        Bill newBill = new Bill();
        newBill.setId(10L);
        newBill.setCurrency(new Currency());
        jpaBillService.deleteBill(10L);
        verify(billRepository).deleteById(10L);
    }

    @Test
    void getBillsByProperty() {
        Property property = new Property();
        property.setId(1L);
        Bill bill1 = new Bill();
        Bill bill2 = new Bill();
        bill1.setProperty(property);
        bill2.setProperty(property);
        Mockito.when(billRepositoryMock.findAll()).thenReturn(List.of(bill1, bill2));
        List<Bill> result = jpaBillService.getBillsByProperty(property);
        assertEquals(2, result.size());
        assertTrue(result.contains(bill1));
        assertTrue(result.contains(bill2));
        Mockito.verify(billRepositoryMock, Mockito.times(1)).findAll();
    }

    @Test
    public void testGetBillsByDueDateRange() {
        // Initialize test data
        testDataInitializer.initializeCurrencies();
        testDataInitializer.initializeManagers();
        testDataInitializer.initializeProperties();
        testDataInitializer.initializeBills();

        // Set the date range for testing
        LocalDate startDate = LocalDate.parse("2023-01-01");
        LocalDate endDate = LocalDate.parse("2023-01-31");

        // Choose a property for testing, e.g., property1
        Property property1 = propertyService.getPropertyById(1L).orElseThrow();

//        Property mockedProperty = new Property();
//        mockedProperty.setId(1L);
//        mockedProperty.setDescription("Frusrated");
//        Manager manager1 = new Manager();
//        manager1.setId(1L);
//        manager1.setManagerName("Wendy Testaburger");
//        mockedProperty.setManager(manager1);
//        Set<Property> tempSet = new HashSet<>();
//        tempSet.add(mockedProperty);
//        manager1.setProperties(tempSet);
//        Mockito.when(propertyService.getPropertyById(1L)).thenReturn(Optional.of(mockedProperty));
//        Property property1 = propertyService.getPropertyById(1L).orElseThrow();

        Manager manager = property1.getManager();
        System.out.println("Trying to retrieve the property's manager...");
        System.out.println("Retrieved a manager: " + manager.getManagerName());

        // Perform the actual test
        List<Bill> billsInRange = jpaBillService.getBillsByDueDateRange(startDate, endDate, property1);

        // Add assertions based on your expectations
        assertEquals(1, billsInRange.size()); // Expecting one bill within the given range

        // Check the details of the retrieved bill
        Bill retrievedBill = billsInRange.get(0);
        assertEquals(1L, retrievedBill.getId()); // Expecting the bill with ID 1
        assertEquals("Heating", retrievedBill.getExpenseCategory()); // Customize based on your data
    }

//    @Test
//    void getBillsByDueDateRange() {
//        Property property = new Property();
//        property.setId(1L);
//        LocalDate startDate = LocalDate.now().minusDays(5);
//        LocalDate endDate = LocalDate.now().plusDays(5);
//        Bill bill1 = new Bill();
//        Bill bill2 = new Bill();
//        bill1.setProperty(property);
//        bill2.setProperty(property);
//        bill1.setDueDate(Timestamp.valueOf(startDate.atStartOfDay().plusDays(2)));
//        bill2.setDueDate(Timestamp.valueOf(endDate.atStartOfDay().minusDays(2)));
//
//        JpaBillService jpaBillService = new JpaBillService();
//
//        System.out.println("Trying to get bills:");
//        List<Bill> result = jpaBillService.getBillsByDueDateRange(startDate, endDate, property);
//        System.out.println(result.size());
//
//        assertEquals(2, result.size());
//        assertTrue(result.contains(bill1));
//        assertTrue(result.contains(bill2));
//    }

    @Test
    void getAllBills_shouldReturnListOfBills() {
        // Arrange
        BillRepository billRepositoryMock = Mockito.mock(BillRepository.class);
        BillService jpaBillService = new JpaBillService(billRepositoryMock, null);

        List<Bill> expectedBills = Collections.singletonList(new Bill()); // You can add sample bills here

        Mockito.when(billRepositoryMock.findAll()).thenReturn(expectedBills);

        // Act
        List<Bill> result = jpaBillService.getAllBills();

        // Assert
        assertEquals(expectedBills, result);
    }

}

