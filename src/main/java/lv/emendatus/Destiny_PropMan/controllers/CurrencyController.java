package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.annotation.currency_controller.Currency_Add;
import lv.emendatus.Destiny_PropMan.annotation.currency_controller.Currency_Delete;
import lv.emendatus.Destiny_PropMan.annotation.currency_controller.Currency_GetAll;
import lv.emendatus.Destiny_PropMan.annotation.currency_controller.Currency_GetByID;
import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
import lv.emendatus.Destiny_PropMan.domain.entity.NumericalConfig;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaCurrencyService;
import org.apache.logging.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/currency")
public class CurrencyController {

    private final JpaCurrencyService currencyService;

    @Autowired
    public CurrencyController(JpaCurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @PostMapping("/add")
    @Currency_Add
    public ResponseEntity<Void> addCurrency(@RequestBody Currency currency) {
        currencyService.addCurrency(currency);
        System.out.println("Added new currency: " + currency.getDesignation());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/getall")
    @Currency_GetAll
    public ResponseEntity<List<Currency>> getAllCurrencies() {
        List<Currency> currencies = currencyService.getAllCurrencies();
        return new ResponseEntity<>(currencies, HttpStatus.OK);
    }

    @GetMapping("/getCurrencyById/{id}")
    @Currency_GetByID
    public ResponseEntity<Currency> getCurrencyById(@PathVariable Long id) {
        Optional<Currency> result = currencyService.getCurrencyById(id);
        return result.map(currency -> new ResponseEntity<>(currency, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/deleteCurrencyById/{id}")
    @Currency_Delete
    public void deleteByID(@PathVariable Long id) {
        if (currencyService.getCurrencyById(id).isPresent()) {
            System.out.println("Deleting currency " + currencyService.getCurrencyById(id).get().getDesignation());
            currencyService.deleteCurrency(id);
        } else {
            System.out.println("No currency with the ID " + id + "exists in the database!");
        }
    }
}
