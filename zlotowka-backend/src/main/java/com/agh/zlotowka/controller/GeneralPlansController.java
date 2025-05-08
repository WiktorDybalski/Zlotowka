package com.agh.zlotowka.controller;


import com.agh.zlotowka.dto.GeneralPlanDTO;
import com.agh.zlotowka.service.GeneralPlansService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/general-plans")
@RequiredArgsConstructor
public class GeneralPlansController {
    private final GeneralPlansService generalPlansService;

    @GetMapping("/all/{id}")
    public ResponseEntity<List<GeneralPlanDTO>> getAllPlans(@PathVariable Integer id) {
        List<GeneralPlanDTO> plans = generalPlansService.getAllUncompletedPlans(id);
        return ResponseEntity.ok(plans);
    }
}
