package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
import lv.emendatus.Destiny_PropMan.repository.interfaces.CurrencyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JpaCurrencyService implements lv.emendatus.Destiny_PropMan.service.interfaces.CurrencyService {

    private final CurrencyRepository currencyRepository;

    public JpaCurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @Override
    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }

//    @Override
//    public Optional<Currency> getCurrencyById(Long id) {
//        return currencyRepository.findById(id);
//    }
    @Override
    public Optional<Currency> getCurrencyById(Long id) {
    System.out.println("Fetching currency with ID: " + id);
    Optional<Currency> currency = currencyRepository.findById(id);
    System.out.println("Currency found: " + currency.orElse(null));
    return currency;
}
    @Override
    public void addCurrency(Currency currency) {
        currencyRepository.save(currency);
    }

    @Override
    public void deleteCurrency(Long id) {
        currencyRepository.deleteById(id);
    }
}
