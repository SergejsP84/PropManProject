package lv.emendatus.Destiny_PropMan.serviceTests;

import lv.emendatus.Destiny_PropMan.domain.entity.Bill;
import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.repository.interfaces.BillRepository;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaBillService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaPropertyService;
import lv.emendatus.Destiny_PropMan.service.interfaces.CurrencyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BillServiceBasicTest {

    @InjectMocks
    private JpaBillService jpaBillService;
//    private final BillRepository billRepositoryMock = Mockito.mock(BillRepository.class);
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
        Mockito.when(billRepository.findAll()).thenReturn(List.of(bill1, bill2));
        List<Bill> result = jpaBillService.getBillsByProperty(property);
        assertEquals(2, result.size());
        assertTrue(result.contains(bill1));
        assertTrue(result.contains(bill2));
        Mockito.verify(billRepository, Mockito.times(1)).findAll();
    }
    @Test
    void getUnpaidBills() {
        Property property = new Property();
        property.setId(1L);
        Bill bill1 = new Bill();
        Bill bill2 = new Bill();
        Bill bill3 = new Bill();
        bill1.setPaid(false);
        bill2.setPaid(true);
        bill3.setPaid(true);
        bill1.setProperty(property);
        bill2.setProperty(property);
        bill3.setProperty(property);
        Mockito.when(billRepository.findAll()).thenReturn(List.of(bill1, bill2, bill3));
        List<Bill> result = jpaBillService.getUnpaidBills(property);
        assertEquals(1, result.size());
        assertTrue(result.contains(bill1));
        Mockito.verify(billRepository, Mockito.times(1)).findAll();
    }
    @Test
    void getPaidBills() {
        Property property = new Property();
        property.setId(1L);
        Bill bill1 = new Bill();
        Bill bill2 = new Bill();
        Bill bill3 = new Bill();
        bill1.setPaid(false);
        bill2.setPaid(true);
        bill3.setPaid(true);
        bill1.setProperty(property);
        bill2.setProperty(property);
        bill3.setProperty(property);
        Mockito.when(billRepository.findAll()).thenReturn(List.of(bill1, bill2, bill3));
        List<Bill> result = jpaBillService.getPaidBills(property);
        assertEquals(2, result.size());
        assertTrue(result.contains(bill2));
        assertTrue(result.contains(bill3));
        Mockito.verify(billRepository, Mockito.times(1)).findAll();
    }

}
