package com.agh.zlotowka.service;

import com.agh.zlotowka.exception.CurrencyConversionException;
import com.agh.zlotowka.model.Currency;
import com.agh.zlotowka.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {

    @Value("${CURRENCY_API_URL}")
    private String apiUrl;
    private final CurrencyRepository currencyRepository;

    @Transactional
    public void addCurrencies() {
        Currency currencyPLN = Currency.builder().isoCode("PLN").build();
        Currency currencyUSD = Currency.builder().isoCode("USD").build();
        Currency currencyEUR = Currency.builder().isoCode("EUR").build();

        currencyRepository.save(currencyPLN);
        currencyRepository.save(currencyUSD);
        currencyRepository.save(currencyEUR);
    }

    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }

    public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) throws CurrencyConversionException {
        if (fromCurrency.equals(toCurrency)) return amount;
        try {
            BigDecimal exchangeRate = fetchExchangeRate(fromCurrency.toLowerCase(), toCurrency.toLowerCase());
            BigDecimal converted = amount.multiply(exchangeRate);
            return converted.setScale(2, RoundingMode.HALF_UP);
        } catch (IOException | CurrencyConversionException e) {
            log.error("CurrencyService: Currency conversion failed: ", e);
            throw new CurrencyConversionException("CurrencyService: Currency conversion failed");
        }
    }

    private BigDecimal fetchExchangeRate(String fromCurrency, String toCurrency) throws IOException, CurrencyConversionException {
        String urlString = String.format("%s/%s.json", apiUrl, fromCurrency);
        URL url = new URL(urlString);
        HttpURLConnection connection = getHttpURLConnection(url);
        JSONObject fromCurrencyData = getJsonObject(fromCurrency, connection);
        if (fromCurrencyData != null && fromCurrencyData.has(toCurrency)) {
            Object exchangeRateObj = fromCurrencyData.get(toCurrency);

            return BigDecimal.valueOf(((Number) exchangeRateObj).doubleValue());
        } else {
            log.error("CurrencyService: No currencies available");
            throw new CurrencyConversionException("CurrencyService: No such currency available");
        }
    }

    private static JSONObject getJsonObject(String fromCurrency, HttpURLConnection connection) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse.optJSONObject(fromCurrency);
    }

    private static HttpURLConnection getHttpURLConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Failed to fetch exchange rate. HTTP response code: " + responseCode);
        }
        return connection;
    }
}
