package com.agh.zlotowka.controller;

import com.agh.zlotowka.service.PeriodService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/period")
@AllArgsConstructor
public class PeriodController {
    private final PeriodService periodService;

    @GetMapping("/all")
    public ResponseEntity<List<Map<String, String>>> getAllCurrencies() {
        List<Map<String, String>> periodList = periodService.getPeriods();
        return ResponseEntity.ok(periodList);
    }
}
