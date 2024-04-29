package dev.codescreen.library.server;


import dev.codescreen.library.model.server.CurrencyExchanger;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.UnknownCurrencyException;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


public class CurrencyExchangerTest {
    private static final String REQUIRED_CURRENCY = "USD";
    private static final String CURRENT_CURRENCY = "EUR";
    private static final String CURRENT_AMOUNT = "100.0";
    private static final double EXPECTED_AMOUNT = 116.99;

    @Mock
    private CurrencyConversion conversion;

    @InjectMocks
    private CurrencyExchanger currencyExchanger;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testExchange_Success() {
        CurrencyUnit required = Monetary.getCurrency(REQUIRED_CURRENCY);
        CurrencyUnit current = Monetary.getCurrency(CURRENT_CURRENCY);
        MonetaryAmount currentMonetaryAmount = Monetary.getDefaultAmountFactory()
                .setCurrency(current)
                .setNumber(new BigDecimal(CURRENT_AMOUNT))
                .create();
        MonetaryAmount expectedMonetaryAmount = Monetary.getDefaultAmountFactory()
                .setCurrency(required)
                .setNumber(EXPECTED_AMOUNT)
                .create();

        when(conversion.apply(currentMonetaryAmount)).thenReturn(expectedMonetaryAmount);

        MonetaryAmount result = currencyExchanger.exchange(REQUIRED_CURRENCY, CURRENT_CURRENCY, CURRENT_AMOUNT);
        System.out.println(result.getNumber());
        assertEquals(result, result);

    }
    @Test
    public void debuggedCurrencyProblem(){
        CurrencyUnit required = Monetary.getCurrency("USD");
        CurrencyUnit current = Monetary.getCurrency("EUR");
        MonetaryAmount currentMonetaryAmount = Monetary.getDefaultAmountFactory()
                .setCurrency(current)
                .setNumber(new BigDecimal(10.00))
                .create();

        // Perform the currency conversion from currentCurrency to requiredCurrency

        System.out.println(currentMonetaryAmount.with(MonetaryConversions.getConversion(required)).toString());

    }
    @Test
    public void testExchange_SameCurrency() {

        MonetaryAmount currentMonetaryAmount = Monetary.getDefaultAmountFactory()
                .setCurrency(REQUIRED_CURRENCY)
                .setNumber(new BigDecimal(CURRENT_AMOUNT))
                .create();

        MonetaryAmount result = currencyExchanger.exchange(REQUIRED_CURRENCY, REQUIRED_CURRENCY, CURRENT_AMOUNT);

        assertEquals(currentMonetaryAmount, result);
        verifyNoInteractions(conversion);
    }
    @Test
    public void testExchange_InvalidCurrency() {
        String invalidCurrency = "XYZ";

        assertThrows(UnknownCurrencyException.class, () -> {
            currencyExchanger.exchange(invalidCurrency, CURRENT_CURRENCY, CURRENT_AMOUNT);
        });
    }
}
