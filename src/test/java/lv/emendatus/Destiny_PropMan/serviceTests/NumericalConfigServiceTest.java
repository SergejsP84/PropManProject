package lv.emendatus.Destiny_PropMan.serviceTests;


import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
import lv.emendatus.Destiny_PropMan.domain.entity.NumericalConfig;
import lv.emendatus.Destiny_PropMan.repository.interfaces.NumericalConfigRepository;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaNumericalConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NumericalConfigServiceTest {
    @InjectMocks
    private JpaNumericalConfigService jpaNumericalConfigService;
    @Mock
    private NumericalConfigRepository numericalConfigRepository;

    @BeforeEach
    public void init() {
        NumericalConfig newConfig = new NumericalConfig();
        Currency currency = new Currency();
        currency.setId(1L);
        currency.setDesignation("EUR");
        newConfig.setName("Initial");
        newConfig.setCurrency(currency);
        newConfig.setId(1L);
        when(numericalConfigRepository.save(newConfig)).thenReturn(newConfig);
        jpaNumericalConfigService.addNumericalConfig(newConfig);
        List<NumericalConfig> list = new ArrayList<>(jpaNumericalConfigService.getAllNumericalConfigs());
        for (NumericalConfig config : list) {
            System.out.println(config.getId());
            System.out.println(config.getName());
        }
    }

    @Test
    void getAllNumericalConfigs() {
        List<NumericalConfig> configs = Arrays.asList(new NumericalConfig(), new NumericalConfig());
        when(numericalConfigRepository.findAll()).thenReturn(configs);
        List<NumericalConfig> result = jpaNumericalConfigService.getAllNumericalConfigs();
        assertEquals(configs, result);
    }

    @Test
    void getNumericalConfigById() {
        NumericalConfig secondConfig = new NumericalConfig();
        secondConfig.setId(2L);
        when(numericalConfigRepository.findById(2L)).thenReturn(Optional.of(secondConfig));
        Optional<NumericalConfig> obtainedConfig = jpaNumericalConfigService.getNumericalConfigById(2L);
        assertEquals(Optional.of(secondConfig), obtainedConfig);
    }

    @Test
    void addNumericalConfig() {
        NumericalConfig newConfig = new NumericalConfig();
        newConfig.setId(3L);
        newConfig.setName("TestConfig");
        when(numericalConfigRepository.save(newConfig)).thenReturn(newConfig);
        jpaNumericalConfigService.addNumericalConfig(newConfig);
        verify(numericalConfigRepository).save(newConfig);
    }

    @Test
    void deleteNumericalConfig() {
        NumericalConfig newConfig = new NumericalConfig();
        newConfig.setId(4L);
        newConfig.setName("AnotherConfig");
        jpaNumericalConfigService.deleteNumericalConfig(4L);
        verify(numericalConfigRepository).deleteById(4L);
    }

    @Test
    void updateNumericalConfig() {      // FAILING!!!!
        Currency currency2 = new Currency();
        currency2.setId(2L);
        currency2.setDesignation("USD");
        NumericalConfig updatedConfig = new NumericalConfig();
        updatedConfig.setName("Updated");
        updatedConfig.setCurrency(currency2);
        updatedConfig.setValue(2.00);
        System.out.println("Before Update:");
        jpaNumericalConfigService.getAllNumericalConfigs().forEach(config -> System.out.println(config.getId() + " - " + config.getName()));
        jpaNumericalConfigService.updateNumericalConfig(1L, updatedConfig);
        System.out.println("After Update:");
        jpaNumericalConfigService.getAllNumericalConfigs().forEach(config -> System.out.println(config.getId() + " - " + config.getName()));
        assertEquals(Optional.of(updatedConfig), jpaNumericalConfigService.getNumericalConfigById(1L));
    }

    @Test
    void getNumericalConfigsByCurrency() {
        NumericalConfig config1 = new NumericalConfig();
        NumericalConfig config2 = new NumericalConfig();
        NumericalConfig config3 = new NumericalConfig();
        Currency currency1 = new Currency();
        Currency currency2 = new Currency();
        Currency currency3 = new Currency();
        currency1.setDesignation("EUR");
        currency2.setDesignation("USD");
        currency3.setDesignation("THB");
        config1.setCurrency(currency1);
        config2.setCurrency(currency2);
        config3.setCurrency(currency3);
        List<NumericalConfig> configs = Arrays.asList(config1, config2, config3);
        when(numericalConfigRepository.findAll()).thenReturn(configs);
        List<NumericalConfig> check = new ArrayList<>();
        assertEquals(configs.get(2), jpaNumericalConfigService.getNumericalConfigsByCurrency(currency3).get(0));
    }
}
