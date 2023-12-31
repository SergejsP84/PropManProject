package lv.emendatus.Destiny_PropMan.serviceTests;


import lv.emendatus.Destiny_PropMan.domain.entity.Amenity;
import lv.emendatus.Destiny_PropMan.domain.entity.NumericalConfig;
import lv.emendatus.Destiny_PropMan.repository.interfaces.NumericalConfigRepository;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaNumericalConfigService;
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
class NumericalConfigServiceTest {
    @InjectMocks
    private JpaNumericalConfigService jpaNumericalConfigService;
    @Mock
    private NumericalConfigRepository numericalConfigRepository;

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
}

