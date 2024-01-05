package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.domain.entity.NumericalConfig;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaNumericalConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/numerical-configs")
public class NumericalConfigController {

    private final JpaNumericalConfigService numericalConfigService;

    @Autowired
    public NumericalConfigController(JpaNumericalConfigService numericalConfigService) {
        this.numericalConfigService = numericalConfigService;
    }

    @PostMapping
    public ResponseEntity<Void> createNumericalConfig(@RequestBody NumericalConfig numericalConfig) {
        numericalConfigService.addNumericalConfig(numericalConfig);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateNumericalConfig(@PathVariable Long id, @RequestBody NumericalConfig updatedConfig) {
        numericalConfigService.updateNumericalConfig(id, updatedConfig);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NumericalConfig> getNumericalConfigById(@PathVariable Long id) {
        Optional<NumericalConfig> result = numericalConfigService.getNumericalConfigById(id);
        return result.map(config -> new ResponseEntity<>(config, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<NumericalConfig>> getAllNumericalConfigs() {
        List<NumericalConfig> configs = numericalConfigService.getAllNumericalConfigs();
        return new ResponseEntity<>(configs, HttpStatus.OK);
    }
}
