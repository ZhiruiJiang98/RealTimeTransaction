package dev.codescreen.library.model.server;

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
    private final ConcurrentHashMap<String, CurrencyConversion> cache = new ConcurrentHashMap<>();
    private final long cacheDuration = TimeUnit.MINUTES.toMillis(10);  // Cache duration of 10 minutes
    private final ConcurrentHashMap<String, Long> cacheTimes = new ConcurrentHashMap<>();

    public MonetaryAmount exchange(String requiredCurrency, String currentCurrency, String currentAmount) {
        try {
            CurrencyUnit required = Monetary.getCurrency(requiredCurrency);
            CurrencyUnit current = Monetary.getCurrency(currentCurrency);

            MonetaryAmount currentMonetaryAmount = Monetary.getDefaultAmountFactory()
                    .setCurrency(current)
                    .setNumber(new BigDecimal(currentAmount))
                    .create();

            CurrencyConversion conversion = getConversionWithCaching(requiredCurrency);

            return currentMonetaryAmount.with(conversion);
        } catch (CurrencyConversionException e) {
            System.err.println("Currency conversion failed: " + e.getMessage());
            return Monetary.getDefaultAmountFactory().setNumber(0).create();
        }
    }

    private CurrencyConversion getConversionWithCaching(String currencyCode) {
        long currentTime = System.currentTimeMillis();
        cacheTimes.putIfAbsent(currencyCode, 0L);

        if (currentTime - cacheTimes.get(currencyCode) > cacheDuration) {
            CurrencyConversion conversion = MonetaryConversions.getConversion(currencyCode);
            cache.put(currencyCode, conversion);
            cacheTimes.put(currencyCode, currentTime);
        }

        return cache.get(currencyCode);
    }
}