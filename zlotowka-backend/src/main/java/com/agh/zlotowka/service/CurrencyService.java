package com.agh.zlotowka.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {
    private static final String API_URL = "https://latest.currency-api.pages.dev/v1/currencies";

    public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) throws IOException {
        if (fromCurrency.equals(toCurrency))
            return amount;
        try {

            String urlString = String.format("%s/%s.json", API_URL, fromCurrency);
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("Failed to fetch exchange rate. HTTP response code: " + responseCode);
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject fromCurrencyData = jsonResponse.optJSONObject(fromCurrency);

            if (fromCurrencyData != null && fromCurrencyData.has(toCurrency)) {
                Object exchangeRateObj = fromCurrencyData.get(toCurrency);
                BigDecimal exchangeRate = BigDecimal.valueOf(((Number) exchangeRateObj).doubleValue());

                return amount.multiply(exchangeRate);
            } else {
                throw new IOException("Did not find currencies");
            }

        } catch (IOException e) {
            log.error("Currency conversion failed", e);
            return BigDecimal.ZERO;
        }
    }

//    TODO: To remove in the future; only for testing
    public static void main(String[] args) {
        try {
            BigDecimal amount = new BigDecimal(100);
            String fromCurrency = "EUR";
            String toCurrency = "PLN";

            CurrencyService service = new CurrencyService();
            BigDecimal convertedAmount = service.convertCurrency(amount, fromCurrency.toLowerCase(), toCurrency.toLowerCase());
            System.out.println(amount + " " + fromCurrency + " is " + convertedAmount + " " + toCurrency);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
