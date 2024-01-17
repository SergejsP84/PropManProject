package lv.emendatus.Destiny_PropMan.serviceTests;

import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.repository.interfaces.ManagerRepository;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PropertyRepository;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaManagerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentMatcher;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

@ExtendWith(MockitoExtension.class)
class ManagerServiceTest {
    @InjectMocks
    private JpaManagerService jpaManagerService;
    @Mock
    private ManagerRepository managerRepository;

    @Mock
    private PropertyRepository propertyRepository;

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
        Long id = 1L;
        Manager existingManager = new Manager();
        Manager updatedManager = new Manager();

        existingManager.setId(1L);
        existingManager.setManagerName("Kenny McCormick");
        updatedManager.setId(1L);
        updatedManager.setManagerName("Wendy Testaburger");

        Mockito.when(managerRepository.findById(id)).thenReturn(Optional.of(existingManager));
        Mockito.when(managerRepository.save(any(Manager.class))).thenAnswer(invocation -> invocation.getArgument(0));
        assertDoesNotThrow(() -> jpaManagerService.updateManager(id, updatedManager));
        Mockito.verify(managerRepository, Mockito.times(1)).findById(eq(id));
        Mockito.verify(managerRepository, Mockito.times(1)).save(any(Manager.class));
    }


//    public class PropertyMatcher implements ArgumentMatcher<Property> {
//        private final Property expected;
//        public PropertyMatcher(Property expected) {
//            this.expected = expected;
//        }
//        @Override
//        public boolean matches(Property actual) {
//            return actual.getId().equals(expected.getId());
//        }
//    }
    @Test
    void getManagerProperties() {
        Manager manager = new Manager();
        manager.setId(1L);
        Property property1 = new Property();
        property1.setId(101L);
        Property property2 = new Property();
        property2.setId(102L);
        ManagerRepository managerRepositoryMock = Mockito.mock(ManagerRepository.class);
        PropertyRepository propertyRepositoryMock = Mockito.mock(PropertyRepository.class);

        Mockito.when(managerRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(manager));
        JpaManagerService jpaManagerService = new JpaManagerService(managerRepositoryMock, propertyRepositoryMock);
        jpaManagerService.addPropertyToManager(1L, property1);
        jpaManagerService.addPropertyToManager(1L, property2);
//        propertyRepositoryMock.save(property1);
//        propertyRepositoryMock.save(property2);
//        managerRepositoryMock.save(manager);
        Set<Property> result = jpaManagerService.getManagerProperties(1L);
        assertEquals(2, result.size());
        long managerId = 0;
        int counter = 0;
        long[] propertyIds = new long[2];
        for (Property prop : result) {
            propertyIds[counter] = prop.getId();
            counter++;
            managerId = prop.getManager().getId();
        }
        Mockito.verify(managerRepositoryMock, Mockito.times(5)).findById(any(Long.class));
        assertTrue(managerId == 1L);
        assertTrue(propertyIds[0] == 101L);
        assertTrue(propertyIds[1] == 102L);
        //        System.out.println(result.contains(property1));
//        System.out.println(result.contains(property2));
//        assertTrue(result.contains(property2) && result.contains(property1));
//        assertTrue(result.contains(property2));
//        assertThat(result, contains(property1));
//        assertThat(result, contains(property2));
    }

    @Test
    void addPropertyToManager() {
        Long managerId = 1L;
        Long propertyId = 101L;
        Manager manager = new Manager();
        manager.setId(managerId);
        Property property = new Property();
        property.setId(propertyId);
        Mockito.when(managerRepository.findById(managerId)).thenReturn(Optional.of(manager));
        jpaManagerService.addPropertyToManager(managerId, property);
        Mockito.verify(managerRepository, Mockito.times(1)).save(manager);
        Mockito.verify(propertyRepository, Mockito.times(1)).save(property);
    }

    @Test
    void purgeProperties() {
        Long managerId = 1L;
        Manager manager = new Manager();
        manager.setId(managerId);
        Mockito.when(managerRepository.findById(managerId)).thenReturn(Optional.of(manager));
        jpaManagerService.purgeProperties(managerId);
        Mockito.verify(managerRepository, Mockito.times(1)).save(manager);
    }

    @Test
    void removePropertyFromManager() {
        Long managerId = 1L;
        Long propertyId = 101L;
        Manager manager = new Manager();
        manager.setId(managerId);
        Property property = new Property();
        property.setId(propertyId);
        property.setManager(manager);
        Set<Property> properties = new HashSet<>();
        properties.add(property);
        manager.setProperties(properties);
        Mockito.when(managerRepository.findById(managerId)).thenReturn(Optional.of(manager));
        Mockito.when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        jpaManagerService.removePropertyFromManager(managerId, propertyId);
        Mockito.verify(managerRepository, Mockito.times(1)).save(manager);
        Mockito.verify(propertyRepository, Mockito.times(1)).save(property);
    }
}

