package lv.emendatus.Destiny_PropMan.serviceTests;


import lv.emendatus.Destiny_PropMan.domain.entity.Amenity;
import lv.emendatus.Destiny_PropMan.domain.entity.LeasingHistory;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.repository.interfaces.LeasingHistoryRepository;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PropertyRepository;
import lv.emendatus.Destiny_PropMan.repository.interfaces.TenantRepository;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaLeasingHistoryService;
import lv.emendatus.Destiny_PropMan.service.interfaces.LeasingHistoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeasingHistoryServiceTest {

    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private LeasingHistoryRepository leasingHistoryRepository;
    @Mock
    private TenantRepository tenantRepository;
    @InjectMocks
    private JpaLeasingHistoryService jpaLeasingHistoryService;

    @Test
    void getAllLeasingHistories() {
        List<LeasingHistory> histories = Arrays.asList(new LeasingHistory(), new LeasingHistory());
        when(leasingHistoryRepository.findAll()).thenReturn(histories);
        List<LeasingHistory> result = jpaLeasingHistoryService.getAllLeasingHistories();
        assertEquals(histories, result);
    }
    @Test
    void getLeasingHistoryById() {
        LeasingHistory sixthHistory = new LeasingHistory();
        sixthHistory.setId(6L);
        sixthHistory.setTenant(new Tenant());
        when(leasingHistoryRepository.findById(6L)).thenReturn(Optional.of(sixthHistory));
        Optional<LeasingHistory> obtainedHistory = jpaLeasingHistoryService.getLeasingHistoryById(6L);
        assertEquals(Optional.of(sixthHistory), obtainedHistory);
    }
    @Test
    void addLeasingHistory() {
        LeasingHistory newHistory = new LeasingHistory();
        newHistory.setId(7L);
        newHistory.setTenant(new Tenant());
        when(leasingHistoryRepository.save(newHistory)).thenReturn(newHistory);
        jpaLeasingHistoryService.addLeasingHistory(newHistory);
        verify(leasingHistoryRepository).save(newHistory);
    }
    @Test
    void deleteLeasingHistory() {
        LeasingHistory newHistory = new LeasingHistory();
        newHistory.setId(8L);
        newHistory.setTenant(new Tenant());
        jpaLeasingHistoryService.deleteLeasingHistory(8L);
        verify(leasingHistoryRepository).deleteById(8L);
    }

    @Test
    void getLeasingHistoryByProperty() {   // FAILED
        LeasingHistory history1 = new LeasingHistory();
        LeasingHistory history2 = new LeasingHistory();
        LeasingHistory history3 = new LeasingHistory();
        Property property1 = new Property();
        property1.setId(1L);
        Property property2 = new Property();
        property2.setId(2L);
        history1.setPropertyId(1L);
        history2.setPropertyId(1L);
        history3.setPropertyId(2L);
        List<LeasingHistory> histories = new ArrayList<>();
        histories.add(history1);
        histories.add(history2);
        histories.add(history3);
        when(leasingHistoryRepository.findAll()).thenReturn(histories);
        List<LeasingHistory> control = new ArrayList<>();
        control.add(history1);
        control.add(history2);
        List<LeasingHistory> found = jpaLeasingHistoryService.getLeasingHistoryByProperty(property1);
        assertEquals(control, found);
    }

    @Test
    void getLeasingHistoryByTimePeriod() {
        LeasingHistory history1 = new LeasingHistory();
        LeasingHistory history2 = new LeasingHistory();
        LeasingHistory history3 = new LeasingHistory();
        LeasingHistory history4 = new LeasingHistory();

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = dateFormat.parse("20/01/2024");
            endDate = dateFormat.parse("30/01/2024");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        history1.setStartDate(new Timestamp(startDate.getTime()));
        history1.setEndDate(new Timestamp(endDate.getTime()));
        try {
            startDate = dateFormat.parse("22/01/2024");
            endDate = dateFormat.parse("01/02/2024");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        history2.setStartDate(new Timestamp(startDate.getTime()));
        history2.setEndDate(new Timestamp(endDate.getTime()));
        try {
            startDate = dateFormat.parse("24/01/2024");
            endDate = dateFormat.parse("03/02/2024");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        history3.setStartDate(new Timestamp(startDate.getTime()));
        history3.setEndDate(new Timestamp(endDate.getTime()));
        try {
            startDate = dateFormat.parse("26/01/2024");
            endDate = dateFormat.parse("05/02/2024");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        history4.setStartDate(new Timestamp(startDate.getTime()));
        history4.setEndDate(new Timestamp(endDate.getTime()));
        List<LeasingHistory> histories = new ArrayList<>();
        histories.add(history1);
        histories.add(history2);
        histories.add(history3);
        histories.add(history4);
        when(leasingHistoryRepository.findAll()).thenReturn(histories);

        // control
        try {
            startDate = dateFormat.parse("22/01/2024");
            endDate = dateFormat.parse("04/02/2024");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Timestamp start = new Timestamp(startDate.getTime());
        Timestamp end = new Timestamp(endDate.getTime());
        List<LeasingHistory> control = new ArrayList<>();
        control.add(history2);
        control.add(history3);
        List<LeasingHistory> found = jpaLeasingHistoryService.getLeasingHistoryByTimePeriod(start, end);
        assertEquals(control, found);
    }

    @Test
    void getLeasingHistoryByTenant() {   // FAILED
        LeasingHistory history1 = new LeasingHistory();
        LeasingHistory history2 = new LeasingHistory();
        LeasingHistory history3 = new LeasingHistory();
        Tenant tenant1 = new Tenant();
        tenant1.setId(1L);
        Tenant tenant2 = new Tenant();
        tenant2.setId(2L);
        history1.setTenant(tenant1);
        history2.setTenant(tenant2);
        history3.setTenant(tenant1);
        List<LeasingHistory> histories = new ArrayList<>();
        histories.add(history1);
        histories.add(history2);
        histories.add(history3);
        when(leasingHistoryRepository.findAll()).thenReturn(histories);
        List<LeasingHistory> control = new ArrayList<>();
        control.add(history1);
        control.add(history3);
        List<LeasingHistory> found = jpaLeasingHistoryService.getLeasingHistoryByTenant(tenant1);
        assertEquals(control, found);
    }


}

