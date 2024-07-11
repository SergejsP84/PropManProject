package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.*;
import lv.emendatus.Destiny_PropMan.exceptions.AuthenticationFailedException;
import lv.emendatus.Destiny_PropMan.exceptions.EntityNotFoundException;
import lv.emendatus.Destiny_PropMan.exceptions.ManagerNotFoundException;
import lv.emendatus.Destiny_PropMan.repository.interfaces.ManagerRepository;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PropertyRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.ManagerService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.*;

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
    public void addManager(Manager manager)
    {
        Manager savedManager = managerRepository.save(manager);
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
            throw new ManagerNotFoundException("No manager found with ID: " + id);
        }
    }
    @Override
    public Set<Property> getManagerProperties(Long managerId) {
        Optional<Manager> optionalManager = getManagerById(managerId);
        if (optionalManager.isPresent()) {
            Set<Property> result = new HashSet<>();
            List<Property> allProperties = propertyRepository.findAll();
            for (Property property : allProperties) {
                if (property.getManager().getId().equals(managerId)) result.add(property);
            }
            return result;
        } else {
            LOGGER.log(Level.ERROR, "No manager with the {} ID exists in the database.", managerId);
            throw new ManagerNotFoundException("No manager found with ID: " + managerId);
        }
    }
    @Override
    public void addPropertyToManager(Long managerId, Property property) {
        Optional<Manager> optionalManager = getManagerById(managerId);
        if (optionalManager.isPresent()) {
            Set<Property> existingProperties = getManagerProperties(managerId);
            if (existingProperties == null) {
                existingProperties = new HashSet<>();
            }
            existingProperties.add(property);
            optionalManager.get().setProperties(existingProperties);
            managerRepository.save(optionalManager.get());
            property.setManager(optionalManager.get());
            propertyRepository.save(property);
        } else {
            LOGGER.log(Level.ERROR, "No manager with the {} ID exists in the database.", managerId);
            throw new ManagerNotFoundException("No manager found with ID: " + managerId);
        }
    }

    @Override
    public void purgeProperties(Long managerId) {  // Internal-use method, better not use in logics
        Optional<Manager> optionalManager = getManagerById(managerId);
        if (optionalManager.isPresent()) {
            Set<Property> existingProperties = optionalManager.get().getProperties();
            if (existingProperties == null) {
                existingProperties = new HashSet<>();
            }
//            System.out.println("Set of properties before purging:");
//            for (Property property : existingProperties) {
//                System.out.println(property.getId());
//            }
            existingProperties.clear();
            System.out.println("Properties purged!");
//            for (Property property : existingProperties) {
//                System.out.println(property.getId());
//            }
            optionalManager.get().setProperties(existingProperties);
            managerRepository.save(optionalManager.get());
        } else {
            LOGGER.log(Level.ERROR, "No manager with the {} ID exists in the database.", managerId);
            throw new ManagerNotFoundException("No manager found with ID: " + managerId);
        }
    }

    @Override
    public Manager getManagerByLogin(String login) {
            for (Manager manager : getAllManagers()) {
                if (manager.getLogin().equals(login)) return manager;
            }
            return null;
    }

    @Override
    public Manager getManagerByEmail(String email) {
        for (Manager manager : getAllManagers()) {
            if (manager.getEmail().equals(email)) return manager;
        }
        return null;
    }

    @Override
    public Manager getManagerByConfirmationToken(String confirmationToken) {
        for (Manager manager : getAllManagers()) {
            if (manager.getConfirmationToken().equals(confirmationToken)) return manager;
        }
        return null;
    }

    @Override
    public boolean isTokenValid(Manager manager, String token) {
        return manager != null && token.equals(manager.getConfirmationToken());
    }

    @Override
    public void removePropertyFromManager(Long managerId, Long propertyId) {
        Optional<Manager> optionalManager = getManagerById(managerId);
        Optional<Property> optionalProperty = propertyRepository.findById(propertyId);
        if (optionalManager.isPresent() && optionalProperty.isPresent()) {
            Manager manager = optionalManager.get();
            Property property = optionalProperty.get();
            Set<Property> existingProperties = manager.getProperties();
            existingProperties.removeIf(p -> p.getId().equals(propertyId));
            manager.setProperties(existingProperties);
            property.setManager(null);
            propertyRepository.save(property);
            managerRepository.save(manager);
        } else {
            LOGGER.log(Level.ERROR, "No manager or property with the given IDs exist in the database.");
            throw new EntityNotFoundException("Either the manager or the property could not be found");
        }
    }
}
