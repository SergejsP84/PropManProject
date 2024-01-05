package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaCurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/currency")
public class CurrencyController {

    private final JpaCurrencyService currencyService;

    @Autowired
    public CurrencyController(JpaCurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addCurrency(@RequestBody Currency currency) {
        currencyService.addCurrency(currency);
        System.out.println("Added new currency: " + currency.getDesignation());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/getall")
    public ResponseEntity<List<Currency>> getAllCurrencies() {
        List<Currency> currencies = currencyService.getAllCurrencies();
        return new ResponseEntity<>(currencies, HttpStatus.OK);
    }
}
