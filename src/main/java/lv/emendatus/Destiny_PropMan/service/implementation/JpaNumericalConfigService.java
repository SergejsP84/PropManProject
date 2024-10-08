package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
import lv.emendatus.Destiny_PropMan.domain.entity.NumericalConfig;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.NumConfigType;
import lv.emendatus.Destiny_PropMan.exceptions.NumericalConfigNotFoundException;
import lv.emendatus.Destiny_PropMan.repository.interfaces.NumericalConfigRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.NumericalConfigService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JpaNumericalConfigService implements NumericalConfigService {
    private final NumericalConfigRepository repository;

    private final JpaTenantService tenantService;
    private final JpaManagerService managerService;
    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);
    public JpaNumericalConfigService(NumericalConfigRepository repository, JpaTenantService tenantService, JpaManagerService managerService) {
        this.repository = repository;
        this.tenantService = tenantService;
        this.managerService = managerService;
    }
    @Override
    public List<NumericalConfig> getAllNumericalConfigs() {
        return repository.findAll();
    }
    @Override
    public Optional<NumericalConfig> getNumericalConfigById(Long id) {
        return repository.findById(id);
    }
    @Override
    public void addNumericalConfig(NumericalConfig numericalConfig)
    {
        NumericalConfig savedConfig = repository.save(numericalConfig);
    }
    @Override
    public void deleteNumericalConfig(Long id) {
        repository.deleteById(id);
    }
    @Override
    @Transactional
    public void updateNumericalConfig(Long id, NumericalConfig updatedNumericalConfig) {
        Optional<NumericalConfig> optionalNumericalConfig = repository.findById(id);
        if (optionalNumericalConfig.isPresent()) {
            NumericalConfig existingNumericalConfig = optionalNumericalConfig.get();
            existingNumericalConfig.setName(updatedNumericalConfig.getName());
            existingNumericalConfig.setValue(updatedNumericalConfig.getValue());
            existingNumericalConfig.setCurrency(updatedNumericalConfig.getCurrency());
            existingNumericalConfig.setType(updatedNumericalConfig.getType());
            repository.save(existingNumericalConfig);
        } else {
            LOGGER.log(Level.ERROR, "No config with the {} ID exists in the database.", id);
            throw new NumericalConfigNotFoundException("No config found with ID: " + id);
        }
    }
    @Override
    public List<NumericalConfig> getNumericalConfigsByCurrency(Currency currency) {
        List<NumericalConfig> allConfigs = getAllNumericalConfigs();
        return allConfigs.stream()
                .filter(numericalConfig -> {
                    Currency configCurrency = numericalConfig.getCurrency();
                    return configCurrency != null && configCurrency.equals(currency);
                })
                .toList();
    }

    @Override
    public List<NumericalConfig> getSystemSettings() {
        List<NumericalConfig> result = new ArrayList<>();
        for (NumericalConfig config : getAllNumericalConfigs()) {
           if (config.getType().equals(NumConfigType.SYSTEM_SETTING)) result.add(config);
        }
        return result;
    }

    @Override
    public Optional<NumericalConfig> getNumericalConfigByName(String name) {
        for (NumericalConfig config : getAllNumericalConfigs()) {
            if (config.getName().equals(name)) return Optional.of(config);
        }
        return Optional.empty();
    }

    // AUXILIARY METHODS
    public Long getLastTenantId() {
        Optional<NumericalConfig> fetchedConfig = getNumericalConfigByName("LastRegisteredTenantID");
        if (fetchedConfig.isPresent()) {
            return fetchedConfig.get().getValue().longValue();
        } else {
            NumericalConfig newConfig = new NumericalConfig();
            newConfig.setName("LastRegisteredTenantID");
            newConfig.setType(NumConfigType.COUNTER);
            if (!tenantService.getAllTenants().isEmpty()) {
                newConfig.setValue(tenantService.getAllTenants().get(tenantService.getAllTenants().size()-1).getId().doubleValue());
            } else {
                newConfig.setValue(1.00);
            }
            addNumericalConfig(newConfig);
            return newConfig.getValue().longValue();
        }
    }

    public Long getLastManagerId() {
        Optional<NumericalConfig> fetchedConfig = getNumericalConfigByName("LastRegisteredManagerID");
        if (fetchedConfig.isPresent()) {
            return fetchedConfig.get().getValue().longValue();
        } else {
            NumericalConfig newConfig = new NumericalConfig();
            newConfig.setName("LastRegisteredManagerID");
            newConfig.setType(NumConfigType.COUNTER);
            if (!managerService.getAllManagers().isEmpty()) {
                newConfig.setValue(managerService.getAllManagers().get(managerService.getAllManagers().size() - 1).getId().doubleValue());
            } else {
                newConfig.setValue(1.00);
            }
            addNumericalConfig(newConfig);
            return newConfig.getValue().longValue();
        }
    }


}
