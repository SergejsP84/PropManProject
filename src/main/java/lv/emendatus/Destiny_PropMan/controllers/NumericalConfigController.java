package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.annotation.numerical_config_controller.*;
import lv.emendatus.Destiny_PropMan.domain.entity.NumericalConfig;
import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaCurrencyService;
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
    private final JpaCurrencyService currencyService;

    @Autowired
    public NumericalConfigController(JpaNumericalConfigService numericalConfigService, JpaCurrencyService currencyService) {
        this.numericalConfigService = numericalConfigService;
        this.currencyService = currencyService;
    }

    @PostMapping("/create")
    @NumericalConfig_Add
    public ResponseEntity<Void> createNumericalConfig(@RequestBody NumericalConfig numericalConfig) {
        numericalConfigService.addNumericalConfig(numericalConfig);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    @NumericalConfig_Update
    public ResponseEntity<Void> updateNumericalConfig(@PathVariable Long id, @RequestBody NumericalConfig updatedConfig) {
        numericalConfigService.updateNumericalConfig(id, updatedConfig);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    @NumericalConfig_GetByID
    public ResponseEntity<NumericalConfig> getNumericalConfigById(@PathVariable Long id) {
        Optional<NumericalConfig> result = numericalConfigService.getNumericalConfigById(id);
        return result.map(config -> new ResponseEntity<>(config, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/getByCurrency/{cur_id}")
    @NumericalConfig_GetByCurrency
    public ResponseEntity<List<NumericalConfig>> getNumericalConfigByCurrency(@PathVariable Long cur_id) {
        Optional<Currency> optionalCurrency = currencyService.getCurrencyById(cur_id);
        if (optionalCurrency.isPresent()) {
            Currency currency = optionalCurrency.get();
            Optional<List<NumericalConfig>> result = Optional.of(numericalConfigService.getNumericalConfigsByCurrency(currency));
            return result.map(config -> new ResponseEntity<>(config, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/getall")
    @NumericalConfig_GetAll
    public ResponseEntity<List<NumericalConfig>> getAllNumericalConfigs() {
        List<NumericalConfig> configs = numericalConfigService.getAllNumericalConfigs();
        return new ResponseEntity<>(configs, HttpStatus.OK);
    }

    @DeleteMapping("/deleteConfigById/{id}")
    @NumericalConfig_Delete
    public void deleteByID(@PathVariable Long id) {
        if (numericalConfigService.getNumericalConfigById(id).isPresent()) {
            System.out.println("Deleting numerical config " + numericalConfigService.getNumericalConfigById(id).get().getId());
            numericalConfigService.deleteNumericalConfig(id);
        } else {
            System.out.println("No numerical config with the ID " + id + "exists in the database!");
        }
    }

    @GetMapping("/getByName/{name}")
    @NumericalConfig_GetByName
    public ResponseEntity<NumericalConfig> getNumericalConfigByName(@PathVariable String name) {
        Optional<NumericalConfig> result = numericalConfigService.getNumericalConfigByName(name);
        return result.map(config -> new ResponseEntity<>(config, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/system-settings")
    @NumericalConfig_GetSystemSettings
    public ResponseEntity<List<NumericalConfig>> getSystemSettings() {
        List<NumericalConfig> result = numericalConfigService.getSystemSettings();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
