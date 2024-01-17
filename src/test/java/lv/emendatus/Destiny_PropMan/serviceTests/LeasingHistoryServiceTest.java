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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
    void getLeasingHistoryByProperty() {
        Property property1 = new Property();
        property1.setId(1L);
        Property property2 = new Property();
        property2.setId(2L);
        LeasingHistory leasingHistory1 = new LeasingHistory();
        LeasingHistory leasingHistory2 = new LeasingHistory();
        LeasingHistory leasingHistory3 = new LeasingHistory();
        leasingHistory1.setPropertyId(1L);
        leasingHistory2.setPropertyId(1L);
        leasingHistory3.setPropertyId(2L);
        Mockito.when(propertyRepository.findById(1L)).thenReturn(Optional.of(property1));
        Mockito.when(leasingHistoryRepository.findAll()).thenReturn(List.of(leasingHistory1, leasingHistory2, leasingHistory3));
        List<LeasingHistory> result = jpaLeasingHistoryService.getLeasingHistoryByProperty(property1);
        assertEquals(2, result.size());
        Mockito.verify(propertyRepository, Mockito.times(2)).findById(1L);
        Mockito.verify(propertyRepository, Mockito.times(1)).findById(2L);
        Mockito.verify(leasingHistoryRepository, Mockito.times(1)).findAll();
    }

    @Test
    void getLeasingHistoryByTenant() {
        Tenant tenant1 = new Tenant();
        tenant1.setId(1L);
        Tenant tenant2 = new Tenant();
        tenant2.setId(2L);
        LeasingHistory leasingHistory1 = new LeasingHistory();
        LeasingHistory leasingHistory2 = new LeasingHistory();
        LeasingHistory leasingHistory3 = new LeasingHistory();
        leasingHistory1.setTenant(tenant1);
        leasingHistory2.setTenant(tenant1);
        leasingHistory3.setTenant(tenant2);
        Mockito.when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant1));
        Mockito.when(leasingHistoryRepository.findAll()).thenReturn(List.of(leasingHistory1, leasingHistory2, leasingHistory3));
        List<LeasingHistory> result = jpaLeasingHistoryService.getLeasingHistoryByTenant(tenant1);
        assertEquals(2, result.size());
        Mockito.verify(tenantRepository, Mockito.times(2)).findById(1L);
        Mockito.verify(tenantRepository, Mockito.times(1)).findById(2L);
        Mockito.verify(leasingHistoryRepository, Mockito.times(1)).findAll();
    }


    public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
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
        LocalDateTime startDateTime = convertToLocalDateTimeViaInstant(startDate);
        LocalDateTime endDateTime = convertToLocalDateTimeViaInstant(endDate);
        List<LeasingHistory> control = new ArrayList<>();
        control.add(history2);
        control.add(history3);
        List<LeasingHistory> found = jpaLeasingHistoryService.getLeasingHistoryByTimePeriod(startDateTime, endDateTime);
        assertEquals(control, found);
    }

}

