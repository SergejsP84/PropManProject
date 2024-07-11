package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.NumericDataMapping;
import lv.emendatus.Destiny_PropMan.domain.entity.NumericalConfig;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;
import lv.emendatus.Destiny_PropMan.repository.interfaces.NumericDataMappingRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.NumericDataMappingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

@Service
public class JpaNumericDataMappingService implements NumericDataMappingService {
    private final NumericDataMappingRepository repository;
    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);
    public JpaNumericDataMappingService(NumericDataMappingRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<NumericDataMapping> getAllMappings() {
        return repository.findAll();
    }

    @Override
    public Optional<NumericDataMapping> getMappingById(Long id) {
        return repository.findById(id);
    }
    @Override
    public SecretKey returnCardNumberSecretKeyById(Long id, UserType userType) {
        List<NumericDataMapping> sameIDMaps = new ArrayList<>();
        for (NumericDataMapping level1Map : repository.findAll()) {
            if (level1Map.getMap().containsKey(id)) sameIDMaps.add(level1Map);
        }
        if (sameIDMaps.size() < 1) {
            LOGGER.error("No user entity with the ID {} found in the database", id);
            System.out.println("No user entity with the ID " + id + " found in the database");
        } else if (sameIDMaps.size() == 1) {
            Map<UserType, Map<Boolean, SecretKey>> level2map = sameIDMaps.get(0).getMap().get(id);
            Map<Boolean, SecretKey> level3map = level2map.get(userType);
            if (level3map.containsKey(true)) {
                return level3map.get(true);
            } else {
                LOGGER.error("Could not retrieve the card number key for user entity with the ID {}", id);
                System.out.println("Could not retrieve the card number key for user entity with the ID " + id);
            }
        } else if (sameIDMaps.size() == 2) {
            Map<UserType, Map<Boolean, SecretKey>> level2map = new HashMap<>();
            if (sameIDMaps.get(0).getMap().get(id).containsKey(userType)) {
                level2map = sameIDMaps.get(0).getMap().get(id);
            } else {
                level2map = sameIDMaps.get(1).getMap().get(id);
            }
            Map<Boolean, SecretKey> level3map = level2map.get(userType);
            if (level3map.containsKey(true)) {
                return level3map.get(true);
            } else {
                LOGGER.error("Could not retrieve the card number key for user entity with the ID {}", id);
                System.out.println("Could not retrieve the card number key for user entity with the ID " + id);
            }
        } else {
            LOGGER.error("Error in the retrieval logic: cannot have more than two Maps for the same user ID");
            System.out.println("Error in the retrieval logic: cannot have more than two Maps for the same user ID");
        }
        return null;
    }

    @Override
    public SecretKey returnCVVSecretKeyById(Long id, UserType userType) {
        List<NumericDataMapping> sameIDMaps = new ArrayList<>();
        for (NumericDataMapping level1Map : repository.findAll()) {
            if (level1Map.getMap().containsKey(id)) sameIDMaps.add(level1Map);
        }
        if (sameIDMaps.size() < 1) {
            LOGGER.error("No user entity with the ID {} found in the database", id);
            System.out.println("No user entity with the ID " + id + " found in the database");
        } else if (sameIDMaps.size() == 1) {
            Map<UserType, Map<Boolean, SecretKey>> level2map = sameIDMaps.get(0).getMap().get(id);
            Map<Boolean, SecretKey> level3map = level2map.get(userType);
            if (level3map.containsKey(false)) {
                return level3map.get(false);
            } else {
                LOGGER.error("Could not retrieve the CVV encryption key for user entity with the ID {}", id);
                System.out.println("Could not retrieve the CVV encryption key for user entity with the ID " + id);
            }
        } else if (sameIDMaps.size() == 2) {
            Map<UserType, Map<Boolean, SecretKey>> level2map = new HashMap<>();
            if (sameIDMaps.get(0).getMap().get(id).containsKey(userType)) {
                level2map = sameIDMaps.get(0).getMap().get(id);
            } else {
                level2map = sameIDMaps.get(1).getMap().get(id);
            }
            Map<Boolean, SecretKey> level3map = level2map.get(userType);
            if (level3map.containsKey(false)) {
                return level3map.get(false);
            } else {
                LOGGER.error("Could not retrieve the CVV encryption key for user entity with the ID {}", id);
                System.out.println("Could not retrieve the CVV encryption key for user entity with the ID " + id);
            }
        } else {
            LOGGER.error("Error in the retrieval logic: cannot have more than two Maps for the same user ID");
            System.out.println("Error in the retrieval logic: cannot have more than two Maps for the same user ID");
        }
        return null;
    }

    @Override
    public void saveCardNumberSecretKey(Long id, UserType userType, SecretKey cardNumberSecretKey) {
        System.out.println("    --------------     1) saveCardNumberSecretKey method invoked");
        List<NumericDataMapping> sameIDMaps = new ArrayList<>();
        Map<Long, Map<UserType, Map<Boolean, SecretKey>>> level1Map = new HashMap<>();
        Map<UserType, Map<Boolean, SecretKey>> level2Map = new HashMap<>();
        Map<Boolean, SecretKey> level3Map = new HashMap<>();
        System.out.println("    --------------     2) Empty starting entities initialized");
        for (NumericDataMapping map : repository.findAll()) {
            if (map.getMap().containsKey(id)) sameIDMaps.add(map);
        }
        System.out.println("    --------------     3) Found " + sameIDMaps.size() + " maps with the same ID");
        if (sameIDMaps.isEmpty()) {
            System.out.println("    --------------     4a) Triggered the case with no existing maps with this ID");
            level3Map.put(true, cardNumberSecretKey);
            System.out.println("    --------------     5a) Level 3 Map generated: key - " + level3Map.keySet().toString() + ", value - " + level3Map.values().toString());
            level2Map.put(userType, level3Map);
            System.out.println("    --------------     6a) Level 2 Map generated: key - " + level2Map.keySet().toString() + ", value - " + level2Map.values().toString());
            level1Map.put(id, level2Map);
            System.out.println("    --------------     7a) Level 1 Map generated: key - " + level1Map.keySet().toString() + ", value - " + level1Map.values().toString());
            NumericDataMapping mapping = new NumericDataMapping();
            mapping.setMap(level1Map);
            System.out.println("    --------------     8a) Created a new Mapping");
            repository.save(mapping);
            System.out.println("    --------------     9a) Saved the new Mapping to the repository");
            LOGGER.info("Secret key for the card number value set successfully for user with ID {}", id);
            System.out.println("Secret key for the card number value set successfully for user with ID " + id);
        } else if (sameIDMaps.size() == 1) {
            NumericDataMapping existingMapping = sameIDMaps.get(0);
            Map<UserType, Map<Boolean, SecretKey>> secondMap = existingMapping.getMap().get(id);

            if (secondMap.containsKey(userType)) {
                Map<Boolean, SecretKey> thirdMap = secondMap.get(userType);
                if (thirdMap.containsKey(true)) {
                    LOGGER.info("A secret key for the card number value already exists for user with ID {}, please use the updateCardNumberSecretKey method instead.", id);
                    System.out.println("A secret key for the card number value already exists for user with ID " + id + ", please use the updateCardNumberSecretKey method instead.");
                } else {
                    thirdMap.put(true, cardNumberSecretKey);
                    secondMap.put(userType, thirdMap);
                    existingMapping.getMap().put(id, secondMap);
                    repository.save(existingMapping);
                    LOGGER.info("Secret key for the card number value set successfully for user with ID {}", id);
                    System.out.println("Secret key for the card number value set successfully for user with ID " + id);
                }
            } else {
                level3Map.put(true, cardNumberSecretKey);
                secondMap.put(userType, level3Map);
                existingMapping.getMap().put(id, secondMap);
                repository.save(existingMapping);
                LOGGER.warn("Secret key for the card number value set successfully for user with ID {}", id);
                System.out.println("Secret key for the card number value set successfully for user with ID " + id);
            }
        } else if (sameIDMaps.size() == 2) {
            LOGGER.info("A secret key for the card number value already exists for user with ID {}, please use the updateCardNumberSecretKey method instead.", id);
            System.out.println("A secret key for the card number value already exists for user with ID " + id + ", please use the updateCardNumberSecretKey method instead.");
        } else {
            LOGGER.error("Error in the retrieval logic: cannot have more than two Maps for the same user ID");
            System.out.println("Error in the retrieval logic: cannot have more than two Maps for the same user ID");
        }
    }

    @Override
    public void saveCVVSecretKey(Long id, UserType userType, SecretKey cVVSecretKey) {
        List<NumericDataMapping> sameIDMaps = new ArrayList<>();
        Map<Long, Map<UserType, Map<Boolean, SecretKey>>> level1Map = new HashMap<>();
        Map<UserType, Map<Boolean, SecretKey>> level2Map = new HashMap<>();
        Map<Boolean, SecretKey> level3Map = new HashMap<>();
        for (NumericDataMapping map : repository.findAll()) {
            if (map.getMap().containsKey(id)) sameIDMaps.add(map);
        }
        if (sameIDMaps.isEmpty()) {
            level3Map.put(false, cVVSecretKey);
            level2Map.put(userType, level3Map);
            level1Map.put(id, level2Map);
            NumericDataMapping mapping = new NumericDataMapping();
            mapping.setMap(level1Map);
            repository.save(mapping);
            LOGGER.info("Secret key for the CVV code set successfully for user with ID {}", id);
            System.out.println("Secret key for the CVV code set successfully for user with ID " + id);
        } else if (sameIDMaps.size() == 1) {
            NumericDataMapping existingMapping = sameIDMaps.get(0);
            Map<UserType, Map<Boolean, SecretKey>> secondMap = existingMapping.getMap().get(id);

            if (secondMap.containsKey(userType)) {
                Map<Boolean, SecretKey> thirdMap = secondMap.get(userType);
                if (thirdMap.containsKey(false)) {
                    LOGGER.info("A secret key for the CVV code already exists for user with ID {}, please use the updateCVVSecretKey method instead.", id);
                    System.out.println("A secret key for the CVV code value already exists for user with ID " + id + ", please use the updateCVVSecretKey method instead.");
                } else {
                    thirdMap.put(false, cVVSecretKey);
                    secondMap.put(userType, thirdMap);
                    existingMapping.getMap().put(id, secondMap);
                    repository.save(existingMapping);
                    LOGGER.info("Secret key for the CVV code set successfully for user with ID {}", id);
                    System.out.println("Secret key for the CVV code set successfully for user with ID " + id);
                }
            } else {
                level3Map.put(false, cVVSecretKey);
                secondMap.put(userType, level3Map);
                existingMapping.getMap().put(id, secondMap);
                repository.save(existingMapping);
                LOGGER.warn("Secret key for the CVV code set successfully for user with ID {}", id);
                System.out.println("Secret key for the CVV code set successfully for user with ID " + id);
            }
        } else if (sameIDMaps.size() == 2) {
            LOGGER.info("A secret key for the CVV code already exists for user with ID {}, please use the updateCVVSecretKey method instead.", id);
            System.out.println("A secret key for the CVV code value already exists for user with ID " + id + ", please use the updateCVVSecretKey method instead.");
        } else {
            LOGGER.error("Error in the retrieval logic: cannot have more than two Maps for the same user ID");
            System.out.println("Error in the retrieval logic: cannot have more than two Maps for the same user ID");
        }
    }

    @Override
    public void deleteMapsByUserID(Long id) {
        List<Long> mappingsForDeletion = new ArrayList<>();
        for (NumericDataMapping mapping : repository.findAll()) {
            if (mapping.getMap().containsKey(id)) mappingsForDeletion.add(mapping.getId());
        }
        if (mappingsForDeletion.isEmpty()) {
            LOGGER.info("No mappings found for user ID {}", id);
            System.out.println("No mappings found for user ID " + id);
        } else {
            for (Long mappingId : mappingsForDeletion) {
                repository.deleteById(mappingId);
            }
            LOGGER.info("Deleted {} mappings for user ID {}", mappingsForDeletion.size(), id);
            System.out.println("Deleted " + mappingsForDeletion.size() + " mappings for user ID " + id);
        }
    }

    @Override
    public void updateCardNumberSecretKey(Long id, UserType userType, SecretKey cardNumberSecretKey) {
        List<NumericDataMapping> sameIDMaps = new ArrayList<>();
        for (NumericDataMapping map : repository.findAll()) {
            if (map.getMap().containsKey(id)) sameIDMaps.add(map);
        }
        if (sameIDMaps.isEmpty()) {
            LOGGER.info("No secret key for the card number value exists for user with ID {}, please use the saveCardNumberSecretKey method instead.", id);
            System.out.println("No secret key for the card number value exists for user with ID " + id + ", please use the saveCardNumberSecretKey method instead.");
            return;
        }
        if (sameIDMaps.size() == 1) {
            NumericDataMapping existingMapping = sameIDMaps.get(0);
            Map<UserType, Map<Boolean, SecretKey>> level2Map = existingMapping.getMap().get(id);
            if (level2Map.containsKey(userType)) {
                Map<Boolean, SecretKey> level3Map = level2Map.get(userType);
                if (level3Map.containsKey(true)) {
                    level3Map.put(true, cardNumberSecretKey);
                    level2Map.put(userType, level3Map);
                    existingMapping.getMap().put(id, level2Map);
                    repository.save(existingMapping);
                    LOGGER.info("Secret key for the card number value successfully updated for user with ID {}", id);
                    System.out.println("Secret key for the card number value successfully updated for user with ID " + id);
                } else {
                    LOGGER.info("No secret key for the card number value exists for user with ID {}, please use the saveCardNumberSecretKey method instead.", id);
                    System.out.println("No secret key for the card number value exists for user with ID " + id + ", please use the saveCardNumberSecretKey method instead.");
                }
            } else {
                LOGGER.info("No secret key for the card number value exists for user with ID {}, please use the saveCardNumberSecretKey method instead.", id);
                System.out.println("No secret key for the card number value exists for user with ID " + id + ", please use the saveCardNumberSecretKey method instead.");
            }
        }
        else if (sameIDMaps.size() == 2) {
            NumericDataMapping existingMapping = sameIDMaps.get(0);
            Map<UserType, Map<Boolean, SecretKey>> level2Map = existingMapping.getMap().get(id);
            if (level2Map.containsKey(userType)) {
                Map<Boolean, SecretKey> level3Map = level2Map.get(userType);
                level3Map.put(true, cardNumberSecretKey);
                level2Map.put(userType, level3Map);
                existingMapping.getMap().put(id, level2Map);
                repository.save(existingMapping);
                LOGGER.info("Secret key for the card number value successfully updated for user with ID {}", id);
                System.out.println("Secret key for the card number value successfully updated for user with ID " + id);
            } else {
                LOGGER.info("No secret key for the card number value exists for user with ID {}, please use the saveCardNumberSecretKey method instead.", id);
                System.out.println("No secret key for the card number value exists for user with ID " + id + ", please use the saveCardNumberSecretKey method instead.");
            }
        }
        else {
            LOGGER.error("Error in the retrieval logic: cannot have more than two Maps for the same user ID");
            System.out.println("Error in the retrieval logic: cannot have more than two Maps for the same user ID");
        }
    }

    @Override
    public void updateCVVSecretKey(Long id, UserType userType, SecretKey cVVSecretKey) {
        List<NumericDataMapping> sameIDMaps = new ArrayList<>();
        for (NumericDataMapping map : repository.findAll()) {
            if (map.getMap().containsKey(id)) sameIDMaps.add(map);
        }
        if (sameIDMaps.isEmpty()) {
            LOGGER.info("No secret key for the CVV code exists for user with ID {}, please use the saveCardNumberSecretKey method instead.", id);
            System.out.println("No secret key for the CVV code exists for user with ID " + id + ", please use the saveCardNumberSecretKey method instead.");
            return;
        }
        if (sameIDMaps.size() == 1) {
            NumericDataMapping existingMapping = sameIDMaps.get(0);
            Map<UserType, Map<Boolean, SecretKey>> level2Map = existingMapping.getMap().get(id);
            if (level2Map.containsKey(userType)) {
                Map<Boolean, SecretKey> level3Map = level2Map.get(userType);
                if (level3Map.containsKey(false)) {
                    level3Map.put(false, cVVSecretKey);
                    level2Map.put(userType, level3Map);
                    existingMapping.getMap().put(id, level2Map);
                    repository.save(existingMapping);
                    LOGGER.info("Secret key for the CVV code successfully updated for user with ID {}", id);
                    System.out.println("Secret key for the CVV code value successfully updated for user with ID " + id);
                } else {
                    LOGGER.info("No secret key for the CVV code exists for user with ID {}, please use the saveCardNumberSecretKey method instead.", id);
                    System.out.println("No secret key for the CVV code value exists for user with ID " + id + ", please use the saveCardNumberSecretKey method instead.");
                }
            } else {
                LOGGER.info("No secret key for the CVV code exists for user with ID {}, please use the saveCardNumberSecretKey method instead.", id);
                System.out.println("No secret key for the CVV code value exists for user with ID " + id + ", please use the saveCardNumberSecretKey method instead.");
            }
        }
        else if (sameIDMaps.size() == 2) {
            NumericDataMapping existingMapping = sameIDMaps.get(0);
            Map<UserType, Map<Boolean, SecretKey>> level2Map = existingMapping.getMap().get(id);
            if (level2Map.containsKey(userType)) {
                Map<Boolean, SecretKey> level3Map = level2Map.get(userType);
                level3Map.put(false, cVVSecretKey);
                level2Map.put(userType, level3Map);
                existingMapping.getMap().put(id, level2Map);
                repository.save(existingMapping);
                LOGGER.info("Secret key for the CVV code successfully updated for user with ID {}", id);
                System.out.println("Secret key for the CVV code value successfully updated for user with ID " + id);
            } else {
                LOGGER.info("No secret key for the CVV code exists for user with ID {}, please use the saveCardNumberSecretKey method instead.", id);
                System.out.println("No secret key for the CVV code exists for user with ID " + id + ", please use the saveCardNumberSecretKey method instead.");
            }
        }
        else {
            LOGGER.error("Error in the retrieval logic: cannot have more than two Maps for the same user ID");
            System.out.println("Error in the retrieval logic: cannot have more than two Maps for the same user ID");
        }
    }
}
