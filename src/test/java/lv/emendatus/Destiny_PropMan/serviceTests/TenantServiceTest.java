package lv.emendatus.Destiny_PropMan.serviceTests;

import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.repository.interfaces.TenantRepository;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaTenantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {
    @InjectMocks
    @Spy
    private JpaTenantService jpaTenantService;
    @Mock
    private TenantRepository tenantRepository;

    @Test
    void getAllTenants() {
        List<Tenant> tenants = Arrays.asList(new Tenant(), new Tenant());
        when(tenantRepository.findAll()).thenReturn(tenants);
        List<Tenant> result = jpaTenantService.getAllTenants();
        assertEquals(tenants, result);
    }
    @Test
    void getTenantById() {
        Tenant fourthTenant = new Tenant();
        fourthTenant.setId(4L);
        when(tenantRepository.findById(4L)).thenReturn(Optional.of(fourthTenant));
        Optional<Tenant> obtainedTenant = jpaTenantService.getTenantById(4L);
        assertEquals(Optional.of(fourthTenant), obtainedTenant);
    }
    @Test
    void addTenant() {
        Tenant newTenant = new Tenant();
        newTenant.setId(5L);
        newTenant.setFirstName("Kenny");
        newTenant.setLastName("McCormick");
        when(tenantRepository.save(newTenant)).thenReturn(newTenant);
        jpaTenantService.addTenant(newTenant);
        verify(tenantRepository).save(newTenant);
    }
    @Test
    void deleteTenant() {
        Tenant newTenant = new Tenant();
        newTenant.setId(6L);
        newTenant.setFirstName("Kenny");
        newTenant.setLastName("McCormick");
        jpaTenantService.deleteTenant(6L);
        verify(tenantRepository).deleteById(6L);
    }
    @Test
    void getTenantsByFirstNameOrLastName() {
        Tenant tenant1 = new Tenant();
        Tenant tenant2 = new Tenant();
        Tenant tenant3 = new Tenant();
        Tenant tenant4 = new Tenant();
        tenant1.setId(1L);
        tenant2.setId(2L);
        tenant3.setId(3L);
        tenant4.setId(4L);
        tenant1.setFirstName("Randy");
        tenant1.setLastName("Marsh");
        tenant2.setFirstName("Andy");
        tenant2.setLastName("Tucker");
        tenant3.setFirstName("Pete");
        tenant3.setLastName("Sandy");
        tenant4.setFirstName("Eric");
        tenant4.setLastName("Cartman");
        tenantRepository.save(tenant1);
        tenantRepository.save(tenant2);
        tenantRepository.save(tenant3);
        tenantRepository.save(tenant4);
        when(tenantRepository.findAll()).thenReturn(Arrays.asList(tenant1, tenant2, tenant3, tenant4));
        List<Tenant> obtainedList = jpaTenantService.getTenantsByFirstNameOrLastName("andy");
        List<Tenant> expected = Arrays.asList(tenant1, tenant2, tenant3);
        assertIterableEquals(expected, obtainedList);
    }
}

