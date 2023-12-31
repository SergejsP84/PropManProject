package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
import lv.emendatus.Destiny_PropMan.domain.entity.NumericalConfig;

import java.util.List;
import java.util.Optional;

public interface NumericalConfigService {
    List<NumericalConfig> getAllNumericalConfigs();
    Optional<NumericalConfig> getNumericalConfigById(Long id);
    void addNumericalConfig(NumericalConfig numericalConfig);
    void updateNumericalConfig(Long id, NumericalConfig updatedNumericalConfig);
    void deleteNumericalConfig(Long id);
    List<NumericalConfig> getNumericalConfigsByCurrency(Currency currency);

}