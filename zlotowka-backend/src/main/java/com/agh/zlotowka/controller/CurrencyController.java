package com.agh.zlotowka.controller;

import com.agh.zlotowka.model.Currency;
import com.agh.zlotowka.service.CurrencyService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/currency")
@AllArgsConstructor
public class CurrencyController {
    private final CurrencyService currencyService;

    @GetMapping("/all")
    public ResponseEntity<List<Currency>> getAllCurrencies() {
        List<Currency> currencyList = currencyService.getAllCurrencies();
        return ResponseEntity.ok(currencyList);
    }
}
