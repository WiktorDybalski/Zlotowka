package com.agh.zlotowka.controller;


import com.agh.zlotowka.dto.GeneralPlanDTO;
import com.agh.zlotowka.service.GeneralPlansService;
import com.agh.zlotowka.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/general-plans")
@RequiredArgsConstructor
public class GeneralPlansController {
    private final GeneralPlansService generalPlansService;

    @GetMapping("/chart-data/{id}")
    public ResponseEntity<List<GeneralPlanDTO>> getPlansChartData(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        generalPlansService.validateUserId(id, userDetails);
        List<GeneralPlanDTO> plans = generalPlansService.getAllUncompletedPlans(id);
        return ResponseEntity.ok(plans);
    }
}
