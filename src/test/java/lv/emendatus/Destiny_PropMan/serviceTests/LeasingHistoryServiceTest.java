package lv.emendatus.Destiny_PropMan.serviceTests;


import lv.emendatus.Destiny_PropMan.domain.entity.Amenity;
import lv.emendatus.Destiny_PropMan.domain.entity.LeasingHistory;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.repository.interfaces.LeasingHistoryRepository;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaLeasingHistoryService;
import lv.emendatus.Destiny_PropMan.service.interfaces.LeasingHistoryService;
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
class LeasingHistoryServiceTest {
    @InjectMocks
    private JpaLeasingHistoryService jpaLeasingHistoryService;
    @Mock
    private LeasingHistoryRepository leasingHistoryRepository;

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
}

