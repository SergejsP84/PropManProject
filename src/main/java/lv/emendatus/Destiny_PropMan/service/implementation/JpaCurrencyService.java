package lv.emendatus.Destiny_PropMan.service.implementation;

import jakarta.annotation.PostConstruct;
import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
import lv.emendatus.Destiny_PropMan.repository.interfaces.CurrencyRepository;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JpaCurrencyService implements lv.emendatus.Destiny_PropMan.service.interfaces.CurrencyService {

    private final CurrencyRepository currencyRepository;

    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);

    public JpaCurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @Override
    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }

    @Override
    public Optional<Currency> getCurrencyById(Long id) {
        return currencyRepository.findById(id);
    }

    @Override
    public void addCurrency(Currency currency) {
        currencyRepository.save(currency);
    }

    @Override
    public void deleteCurrency(Long id) {
        if (getAllCurrencies().size()<2) {
            System.out.println("Cannot delete the only remaining currency!");
            LOGGER.log(Level.WARN, "Cannot delete the only remaining currency!");
        } else if (getCurrencyById(id).isPresent()) {
            if (getCurrencyById(id).get().getIsBaseCurrency()) {
                System.out.println("Cannot delete the base currency! Set a different base currency first.");
                LOGGER.log(Level.WARN, "Cannot delete the base currency! Set a different base currency first.");
            } else currencyRepository.deleteById(id);
        } else {
            System.out.println("Unable to find the specified currency.");
            LOGGER.log(Level.WARN, "Unable to find the specified currency.");
        }
    }

    @Override
    public void setBaseCurrency(Long id) {
        for (Currency currency : getAllCurrencies()) {
            if (!currency.getId().equals(id)) {
                if (currency.getIsBaseCurrency()) {
                    currency.setIsBaseCurrency(false);
                    addCurrency(currency);
                }
            } else {
                currency.setIsBaseCurrency(true);
                addCurrency(currency);
            }
        }
    }

    @Override
    @PostConstruct
    public void createDefaultCurrency() {
        if (getAllCurrencies().size()<1) {
            Currency currency = new Currency();
            currency.setDesignation("EUR");
            currency.setIsBaseCurrency(true);
            currency.setRateToBase(1.0);
            addCurrency(currency);
            System.out.println("Default currency created - EUR");
            LOGGER.log(Level.INFO, "Default currency created - EUR");
        }
    }

    @Override
    public Currency returnBaseCurrency() {
        for (Currency currency : getAllCurrencies()) {
            if (currency.getIsBaseCurrency()) return currency;
            }
        System.out.println("Could not identify the base currency!");
        LOGGER.log(Level.ERROR, "Could not identify the base currency!");
        return null;
    }

}
