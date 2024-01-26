package lv.emendatus.Destiny_PropMan.serviceTests;


import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.repository.interfaces.TenantPaymentRepository;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaTenantPaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantPaymentServiceBasicTest {
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
    @Test
    void getPaymentsByProperty() {
        Property property = new Property();
        property.setId(1L);
        TenantPayment tenantPayment1 = new TenantPayment();
        TenantPayment tenantPayment2 = new TenantPayment();
        TenantPayment tenantPayment3 = new TenantPayment();
        tenantPayment1.setAssociatedPropertyId(1L);
        tenantPayment2.setAssociatedPropertyId(2L);
        tenantPayment3.setAssociatedPropertyId(1L);
        Mockito.when(tenantPaymentRepository.findAll()).thenReturn(List.of(tenantPayment1, tenantPayment2, tenantPayment3));
        List<TenantPayment> result = jpaTenantPaymentService.getPaymentsByProperty(1L);
        assertEquals(2, result.size());
        assertTrue(result.contains(tenantPayment1));
        assertTrue(result.contains(tenantPayment3));
        Mockito.verify(tenantPaymentRepository, Mockito.times(1)).findAll();
    }
    @Test
    void getPaymentsByManager() {
        Manager manager = new Manager();
        manager.setId(1L);
        TenantPayment tenantPayment1 = new TenantPayment();
        TenantPayment tenantPayment2 = new TenantPayment();
        TenantPayment tenantPayment3 = new TenantPayment();
        tenantPayment1.setManagerId(1L);
        tenantPayment2.setManagerId(2L);
        tenantPayment3.setManagerId(1L);
        Mockito.when(tenantPaymentRepository.findAll()).thenReturn(List.of(tenantPayment1, tenantPayment2, tenantPayment3));
        List<TenantPayment> result = jpaTenantPaymentService.getPaymentsByManager(1L);
        assertEquals(2, result.size());
        assertTrue(result.contains(tenantPayment1));
        assertTrue(result.contains(tenantPayment3));
        Mockito.verify(tenantPaymentRepository, Mockito.times(1)).findAll();
    }

    @Test
    void getPaymentsByTenant() {
        Tenant tenant1 = new Tenant();
        tenant1.setId(1L);
        Tenant tenant2 = new Tenant();
        tenant2.setId(2L);
        TenantPayment tenantPayment1 = new TenantPayment();
        TenantPayment tenantPayment2 = new TenantPayment();
        TenantPayment tenantPayment3 = new TenantPayment();
        tenantPayment1.setTenant(tenant1);
        tenantPayment2.setTenant(tenant2);
        tenantPayment3.setTenant(tenant1);
        Mockito.when(tenantPaymentRepository.findAll()).thenReturn(List.of(tenantPayment1, tenantPayment2, tenantPayment3));
        List<TenantPayment> result = jpaTenantPaymentService.getPaymentsByTenant(1L);
        assertEquals(2, result.size());
        assertTrue(result.contains(tenantPayment1));
        assertTrue(result.contains(tenantPayment3));
        Mockito.verify(tenantPaymentRepository, Mockito.times(1)).findAll();
    }
    @Test
    void getUnsettledPayments() {
        TenantPayment tenantPayment1 = new TenantPayment();
        TenantPayment tenantPayment2 = new TenantPayment();
        TenantPayment tenantPayment3 = new TenantPayment();
        tenantPayment1.setId(1L);
        tenantPayment2.setId(2L);
        tenantPayment3.setId(3L);
        tenantPayment1.setReceivedFromTenant(false);
        tenantPayment1.setFeePaidToManager(false);
        tenantPayment2.setReceivedFromTenant(true);
        tenantPayment2.setFeePaidToManager(true);
        tenantPayment3.setReceivedFromTenant(true);
        tenantPayment3.setFeePaidToManager(false);
        Mockito.when(tenantPaymentRepository.findAll()).thenReturn(List.of(tenantPayment1, tenantPayment2, tenantPayment3));
        List<TenantPayment> result = jpaTenantPaymentService.getUnsettledPayments();
        assertEquals(2, result.size());
        assertTrue(result.contains(tenantPayment1));
        assertTrue(result.contains(tenantPayment3));
        Mockito.verify(tenantPaymentRepository, Mockito.times(1)).findAll();
    }
}

