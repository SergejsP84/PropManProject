package lv.emendatus.Destiny_PropMan.serviceTests;

import lv.emendatus.Destiny_PropMan.domain.entity.Bill;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.repository.interfaces.BillRepository;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PropertyRepository;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaBillService;
import lv.emendatus.Destiny_PropMan.service.interfaces.CurrencyService;
import lv.emendatus.Destiny_PropMan.util.TestDataInitializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ComponentScan(basePackages = {"lv.emendatus.Destiny_PropMan.util", "lv.emendatus.Destiny_PropMan.service.implementation"})
@ExtendWith(MockitoExtension.class)
class BillServiceIntegrationTest {
    @Autowired
    private TestDataInitializer testDataInitializer;
    @Autowired
    @InjectMocks
    private JpaBillService jpaBillService;
    private BillRepository billRepositoryMock = Mockito.mock(BillRepository.class);

    @Mock
    private BillRepository billRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Mock
    private CurrencyService currencyService;


    @Test
    public void testGetBillsByDueDateRange() {
        testDataInitializer.initializeData();
        LocalDate startDate = LocalDate.parse("2023-01-01");
        LocalDate endDate = LocalDate.parse("2023-01-31");
        System.out.println("Trying to retrieve a property...");
        Optional<Property> property1 = propertyRepository.findById(1L);
        List<Bill> billsInRange = new ArrayList<>();
        if (property1.isPresent()) {
            billsInRange = jpaBillService.getBillsByDueDateRange(startDate, endDate, property1.get());
        } else {
            System.out.println(" --- Did not find the property!!! --- ");
        }
        assertEquals(1, billsInRange.size()); // Expecting one bill within the given range
        Bill retrievedBill = billsInRange.get(0);
        assertEquals(1L, retrievedBill.getId()); // Expecting the bill with ID 1
        assertEquals("Heating", retrievedBill.getExpenseCategory());
    }

    @Test
    public void testTogglePaidStatus() {
        testDataInitializer.initializeData();
        jpaBillService.togglePaidStatus(2L);
        Optional<Bill> extractedBill = jpaBillService.getBillById(2L);
        boolean value = false;
        if (extractedBill.isPresent()) {
            Bill processedBill = extractedBill.get();
            value = processedBill.isPaid();
        } else {
            System.out.println(" --- Did not find the bill!!! --- ");
        }
        assertTrue(value);
    }

    @Test
    public void testGetBillsByExpenseCategory() {
        testDataInitializer.initializeData();
        System.out.println("Trying to retrieve a property...");
        Optional<Property> property3 = propertyRepository.findById(3L);
        List<Bill> obtainedBills = new ArrayList<>();
        if (property3.isPresent()) {
            obtainedBills = jpaBillService.getBillsByExpenseCategory(property3.get(), "Catering");
        } else {
            System.out.println(" --- Did not find the property!!! --- ");
        }
        assertEquals(1, obtainedBills.size());
        Bill retrievedBill = obtainedBills.get(0);
        assertEquals(3L, retrievedBill.getId());
        assertEquals("Catering", retrievedBill.getExpenseCategory());
    }

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

//    @Test
//    void getAllBills_shouldReturnListOfBills() {
//        // Arrange
//        BillRepository billRepositoryMock = Mockito.mock(BillRepository.class);
//        BillService jpaBillService = new JpaBillService(billRepositoryMock, null);
//
//        List<Bill> expectedBills = Collections.singletonList(new Bill()); // You can add sample bills here
//
//        Mockito.when(billRepositoryMock.findAll()).thenReturn(expectedBills);
//
//        // Act
//        List<Bill> result = jpaBillService.getAllBills();
//
//        // Assert
//        assertEquals(expectedBills, result);
//    }


