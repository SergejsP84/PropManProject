package lv.emendatus.Destiny_PropMan.serviceTests;


import lv.emendatus.Destiny_PropMan.domain.entity.Booking;
import lv.emendatus.Destiny_PropMan.domain.entity.TenantPayment;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;
import lv.emendatus.Destiny_PropMan.repository.interfaces.TenantPaymentRepository;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaTenantPaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantPaymentServiceTest {
    @InjectMocks
    private JpaTenantPaymentService jpaTenantPaymentService;
    @Mock
    private TenantPaymentRepository tenantPaymentRepository;

    @Test
    void getAllTenantPayments() {
        List<TenantPayment> payments = Arrays.asList(new TenantPayment(), new TenantPayment());
        when(tenantPaymentRepository.findAll()).thenReturn(payments);
        List<TenantPayment> result = jpaTenantPaymentService.getAllTenantPayments();
        assertEquals(payments, result);
    }
    @Test
    void getTenantPaymentById() {
        TenantPayment tenthPayment = new TenantPayment();
        tenthPayment.setId(10L);
        tenthPayment.setAmount(100.00);
        when(tenantPaymentRepository.findById(10L)).thenReturn(Optional.of(tenthPayment));
        Optional<TenantPayment> obtainedPayment = jpaTenantPaymentService.getTenantPaymentById(10L);
        assertEquals(Optional.of(tenthPayment), obtainedPayment);
    }
    @Test
    void addTenantPayment() {
        TenantPayment newPayment = new TenantPayment();
        newPayment.setId(11L);
        when(tenantPaymentRepository.save(newPayment)).thenReturn(newPayment);
        jpaTenantPaymentService.addTenantPayment(newPayment);
        verify(tenantPaymentRepository).save(newPayment);
    }
    @Test
    void deleteTenantPayment() {
        TenantPayment newPayment = new TenantPayment();
        newPayment.setId(12L);
        jpaTenantPaymentService.deleteTenantPayment(12L);
        verify(tenantPaymentRepository).deleteById(12L);
    }



}

