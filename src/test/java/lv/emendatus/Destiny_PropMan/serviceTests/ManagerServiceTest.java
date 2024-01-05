package lv.emendatus.Destiny_PropMan.serviceTests;

import lv.emendatus.Destiny_PropMan.domain.entity.Amenity;
import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import lv.emendatus.Destiny_PropMan.repository.interfaces.ManagerRepository;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaManagerService;
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
class ManagerServiceTest {
    @InjectMocks
    private JpaManagerService jpaManagerService;
    @Mock
    private ManagerRepository managerRepository;

    @Test
    void getAllManagers() {
        List<Manager> managers = Arrays.asList(new Manager(), new Manager());
        when(managerRepository.findAll()).thenReturn(managers);
        List<Manager> result = jpaManagerService.getAllManagers();
        assertEquals(managers, result);
    }
    @Test
    void getManagerById() {
        Manager seventhManager = new Manager();
        seventhManager.setId(7L);
        when(managerRepository.findById(7L)).thenReturn(Optional.of(seventhManager));
        Optional<Manager> obtainedManager = jpaManagerService.getManagerById(7L);
        assertEquals(Optional.of(seventhManager), obtainedManager);
    }
    @Test
    void addManager() {
        Manager newManager = new Manager();
        newManager.setId(8L);
        newManager.setManagerName("Wendy Testaburger");
        when(managerRepository.save(newManager)).thenReturn(newManager);
        jpaManagerService.addManager(newManager);
        verify(managerRepository).save(newManager);
    }
    @Test
    void deleteManager() {
        Manager newManager = new Manager();
        newManager.setId(9L);
        newManager.setManagerName("Overlook Hotel");
        jpaManagerService.deleteManager(9L);
        verify(managerRepository).deleteById(9L);
    }

    @Test
    void updateManager() {
        Manager newManager = new Manager();
        newManager.setId(8L);
        newManager.setManagerName("Wendy Testaburger");
        when(managerRepository.save(newManager)).thenReturn(newManager);
        jpaManagerService.addManager(newManager);
        Manager updatedManager = new Manager();
        updatedManager.setId(8L);
        updatedManager.setManagerName("Wendy Marsh");
        jpaManagerService.updateManager(8L, updatedManager);
        verify(managerRepository).save(updatedManager);
    }

    @Test
    void getManagerProperties() {

    }

    @Test
    void addPropertyToManager() {

    }

    @Test
    void removePropertyFromManager() {

    }
}

