package lv.emendatus.Destiny_PropMan.serviceTests;

import lv.emendatus.Destiny_PropMan.domain.entity.Bill;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.domain.entity.TenantPayment;
import lv.emendatus.Destiny_PropMan.repository.interfaces.BookingRepository;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PropertyRepository;
import lv.emendatus.Destiny_PropMan.repository.interfaces.TenantPaymentRepository;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaBookingService;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaTenantPaymentService;
import lv.emendatus.Destiny_PropMan.util.TestDataInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan(basePackages = {"lv.emendatus.Destiny_PropMan.util", "lv.emendatus.Destiny_PropMan.service.implementation"})
@ExtendWith(MockitoExtension.class)
class TenantPaymentServiceIntegrationTest {
    @Autowired
    private TestDataInitializer testDataInitializer;
    @Autowired
    @InjectMocks
    private JpaTenantPaymentService tenantPaymentService;
    @Mock
    private TenantPaymentRepository tenantPaymentRepository;
    @Autowired
    private PropertyRepository propertyRepository;

    @BeforeEach
    public void init() {
        testDataInitializer.initializeData();
    }

    @Test
    public void getPaymentsByDateRange() {
        LocalDate startDate = LocalDate.parse("2024-01-15");
        LocalDate endDate = LocalDate.parse("2024-01-30");
        List<TenantPayment> paymentsInRange = tenantPaymentService.getPaymentsByDateRange(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
        assertEquals(2, paymentsInRange.size());
        TenantPayment payment2 = paymentsInRange.get(0);
        TenantPayment payment3 = paymentsInRange.get(1);
        assertEquals(2L, payment2.getId());
        assertEquals(3L, payment3.getId());
    }

    @Test
    public void settlePayment() {
        tenantPaymentService.settlePayment(3L);
        Optional<TenantPayment> extractedPayment = tenantPaymentService.getTenantPaymentById(3L);
        boolean value = false;
        if (extractedPayment.isPresent()) {
            TenantPayment processedPayment = extractedPayment.get();
            value = processedPayment.isFeePaidToManager() && processedPayment.isReceivedFromTenant();
        } else {
            System.out.println(" --- Did not find the payment!!! --- ");
        }
        assertTrue(value);
    }

}
