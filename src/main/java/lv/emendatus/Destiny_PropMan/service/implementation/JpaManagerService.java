package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.Bill;
import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.repository.interfaces.ManagerRepository;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PropertyRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.ManagerService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class JpaManagerService implements ManagerService {
    private final ManagerRepository managerRepository;
    private final PropertyRepository propertyRepository;
    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);
    public JpaManagerService(ManagerRepository managerRepository, PropertyRepository propertyRepository) {
        this.managerRepository = managerRepository;
        this.propertyRepository = propertyRepository;
    }
    @Override
    public List<Manager> getAllManagers() {
        return managerRepository.findAll();
    }
    @Override
    public Optional<Manager> getManagerById(Long id) {
        return managerRepository.findById(id);
    }
    @Override
    public void addManager(Manager manager) {
        managerRepository.save(manager);
    }
    @Override
    public void deleteManager(Long id) {
        managerRepository.deleteById(id);
    }
    @Override
    public void updateManager(Long id, Manager updatedManager) {
        Optional<Manager> optionalManager = managerRepository.findById(id);
        if (optionalManager.isPresent()) {
            Manager existingManager = optionalManager.get();
            existingManager.setManagerName(updatedManager.getManagerName());
            existingManager.setType(updatedManager.getType());
            existingManager.setDescription(updatedManager.getDescription());
            existingManager.setActive(updatedManager.isActive());
            existingManager.setJoinDate(updatedManager.getJoinDate());
            existingManager.setLogin(updatedManager.getLogin());
            existingManager.setPassword(updatedManager.getPassword());
            managerRepository.save(existingManager);
        } else {
            LOGGER.log(Level.ERROR, "No manager with the {} ID exists in the database.", id);
            // TODO: Handle the case where the manager with the given ID is not found
        }
    }
    @Override
    public Set<Property> getManagerProperties(Long managerId) {
        Optional<Manager> optionalManager = getManagerById(managerId);
        if (optionalManager.isPresent()) {
            return optionalManager.get().getProperties();
        } else {
            LOGGER.log(Level.ERROR, "No manager with the {} ID exists in the database.", managerId);
            // TODO: Handle the case where the manager with the given ID is not found
            return Collections.emptySet();
        }
    }
    @Override
    public void addPropertyToManager(Long managerId, Property property) {
        Optional<Manager> optionalManager = getManagerById(managerId);
        if (optionalManager.isPresent()) {
            Set<Property> existingProperties = getManagerProperties(managerId);
            existingProperties.add(property);
            optionalManager.get().setProperties(existingProperties);
            managerRepository.save(optionalManager.get());
        } else {
            LOGGER.log(Level.ERROR, "No manager with the {} ID exists in the database.", managerId);
            // TODO: Handle the case where the manager with the given ID is not found
        }
    }
    @Override
    public void removePropertyFromManager(Long managerId, Long propertyId) {
        Optional<Manager> optionalManager = getManagerById(managerId);
        Optional<Property> optionalProperty = propertyRepository.findById(propertyId);
        if (optionalManager.isPresent()) {
            if (optionalProperty.isPresent()) {
                Set<Property> existingProperties = getManagerProperties(managerId);
                existingProperties.removeIf(property -> property.getId().equals(propertyId));
                managerRepository.save(optionalManager.get());
            } else {
                LOGGER.log(Level.ERROR, "No property with the {} ID exists in the database.", propertyId);
                // TODO: Handle the case where the property with the given ID is not found
            }
        } else {
            LOGGER.log(Level.ERROR, "No manager with the {} ID exists in the database.", managerId);
            // TODO: Handle the case where the manager with the given ID is not found
        }
    }
}
