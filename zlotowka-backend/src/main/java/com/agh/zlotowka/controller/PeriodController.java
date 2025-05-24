package com.agh.zlotowka.controller;

import com.agh.zlotowka.dto.PeriodDTO;
import com.agh.zlotowka.service.PeriodService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/period")
@AllArgsConstructor
public class PeriodController {
    private final PeriodService periodService;

    @GetMapping("/all")
    public ResponseEntity<List<PeriodDTO>> getAllCurrencies() {
        return ResponseEntity.ok(periodService.getPeriods());
    }
}
