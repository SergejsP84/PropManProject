package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ManagerService {
    List<Manager> getAllManagers();
    Optional<Manager> getManagerById(Long id);
    void addManager(Manager manager);
    void deleteManager(Long id);
    void updateManager(Long id, Manager updatedManager);
    Set<Property> getManagerProperties(Long managerId);
    void addPropertyToManager(Long managerId, Property property);
    void removePropertyFromManager(Long managerId, Long propertyId);
    void purgeProperties(Long managerId);
    Manager getManagerByLogin(String login);
    Manager getManagerByEmail(String email);
}
