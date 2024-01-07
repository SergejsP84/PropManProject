package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
import lv.emendatus.Destiny_PropMan.domain.entity.NumericalConfig;
import lv.emendatus.Destiny_PropMan.repository.interfaces.NumericalConfigRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.NumericalConfigService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class JpaNumericalConfigService implements NumericalConfigService {
    private final NumericalConfigRepository repository;

    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);
    public JpaNumericalConfigService(NumericalConfigRepository repository) {
        this.repository = repository;
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
    @Transactional
    public void addNumericalConfig(NumericalConfig numericalConfig)
    {
        System.out.println("addNumericalConfig method invoked");
        NumericalConfig savedConfig = repository.save(numericalConfig);
        System.out.println("ID of the saved entity: " + savedConfig.getId());
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
            repository.save(existingNumericalConfig);
        } else {
            LOGGER.log(Level.ERROR, "No config with the {} ID exists in the database.", id);
            // TODO: Handle the case where the property with the given ID is not found
        }
    }
    @Override
    @Transactional
    public List<NumericalConfig> getNumericalConfigsByCurrency(Currency currency) {
        List<NumericalConfig> allConfigs = getAllNumericalConfigs();
        return allConfigs.stream()
                .filter(numericalConfig -> {
                    Currency configCurrency = numericalConfig.getCurrency();
                    return configCurrency != null && configCurrency.equals(currency);
                })
                .toList();
    }
}
