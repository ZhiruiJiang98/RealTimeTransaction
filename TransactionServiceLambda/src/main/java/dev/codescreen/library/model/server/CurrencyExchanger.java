package dev.codescreen.library.model.server;

import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.CurrencyConversionException;
import javax.money.convert.MonetaryConversions;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CurrencyExchanger {

    public MonetaryAmount exchange(String requiredCurrency, String currentCurrency, String currentAmount) {
        try {
            CurrencyUnit required = Monetary.getCurrency(requiredCurrency);
            CurrencyUnit current = Monetary.getCurrency(currentCurrency);
            MonetaryAmount currentMonetaryAmount = Monetary.getDefaultAmountFactory()
                    .setCurrency(current)
                    .setNumber(Double.valueOf(currentAmount))
                    .create();

            // Perform the currency conversion using the default provider chain
            CurrencyConversion conversion = MonetaryConversions.getConversion(required);

            return currentMonetaryAmount.with(conversion);
        } catch (CurrencyConversionException e) {
            System.err.println("Currency conversion failed: " + e.getMessage());
            return Monetary.getDefaultAmountFactory().setNumber(0).create();
        }
    }

}