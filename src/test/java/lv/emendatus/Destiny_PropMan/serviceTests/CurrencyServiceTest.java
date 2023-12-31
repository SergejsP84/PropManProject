package lv.emendatus.Destiny_PropMan.serviceTests;

import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
import lv.emendatus.Destiny_PropMan.repository.interfaces.CurrencyRepository;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaCurrencyService;
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
class CurrencyServiceTest {
    @InjectMocks
    private JpaCurrencyService jpaCurrencyService;
    @Mock
    private CurrencyRepository currencyRepository;

    @Test
    void getAllCurrencies() {
        // Arrange
        List<Currency> currencies = Arrays.asList(new Currency(), new Currency()); // creating mock instances here
        when(currencyRepository.findAll()).thenReturn(currencies);
        // Act
        List<Currency> result = jpaCurrencyService.getAllCurrencies(); // calling the test method
        // Assert
        // Verify that the method behaved as expected
        assertEquals(currencies, result);
    }

    @Test
    void getCurrencyById() {
        // Arrange
        Currency thirdCurrency = new Currency(); // create some mock currency
        thirdCurrency.setId(3L);
        when(currencyRepository.findById(3L)).thenReturn(Optional.of(thirdCurrency));
        // Act
        Optional<Currency> obtainedCurrency = jpaCurrencyService.getCurrencyById(3L);
        // Assert
        assertEquals(Optional.of(thirdCurrency), obtainedCurrency);
    }

    @Test
    void addCurrency() {
        // Arrange
        Currency newCurrency = new Currency();
        newCurrency.setId(4L);
        newCurrency.setDesignation("EUR");
        when(currencyRepository.save(newCurrency)).thenReturn(newCurrency);
        // Act
        jpaCurrencyService.addCurrency(newCurrency); // Call the method you want to test
        // Assert
        verify(currencyRepository).save(newCurrency);
    }

    @Test
    void deleteCurrency() {
        // Arrange
        Currency newCurrency = new Currency();
        newCurrency.setId(1L); // Set an ID for the new currency
        newCurrency.setDesignation("EUR");
        currencyRepository.save(newCurrency);
        // Act
        jpaCurrencyService.deleteCurrency(1L);// Call the method you want to test
        // Assert
        verify(currencyRepository).deleteById(1L);
    }
}

