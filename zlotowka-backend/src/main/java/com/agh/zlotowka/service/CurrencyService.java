package com.agh.zlotowka.service;

import com.agh.zlotowka.exception.CurrencyConversionException;
import com.agh.zlotowka.model.Currency;
import com.agh.zlotowka.model.User;
import com.agh.zlotowka.repository.CurrencyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {

    @Value("${CURRENCY_API_URL}")
    private static String API_URL;
    private final CurrencyRepository currencyRepository;

    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }

    public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) throws Exception {
        if (fromCurrency.equals(toCurrency)) return amount;
        try {
            BigDecimal exchangeRate = fetchExchangeRate(amount, fromCurrency, toCurrency);
            return amount.multiply(exchangeRate);
        } catch (IOException e) {
            log.error("CurrencyService: Currency conversion failed: ", e);
            throw new IOException("CurrencyService: Currency conversion failed", e);
        }
    }

    private BigDecimal fetchExchangeRate(BigDecimal amount, String fromCurrency, String toCurrency) throws IOException, CurrencyConversionException {
        String urlString = String.format("%s/%s.json", API_URL, fromCurrency);
        URL url = new URL(urlString);
        HttpURLConnection connection = getHttpURLConnection(url);
        JSONObject fromCurrencyData = getJsonObject(fromCurrency, connection);
        if (fromCurrencyData != null && fromCurrencyData.has(toCurrency)) {
            Object exchangeRateObj = fromCurrencyData.get(toCurrency);
            BigDecimal exchangeRate = BigDecimal.valueOf(((Number) exchangeRateObj).doubleValue());

            return amount.multiply(exchangeRate);
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
