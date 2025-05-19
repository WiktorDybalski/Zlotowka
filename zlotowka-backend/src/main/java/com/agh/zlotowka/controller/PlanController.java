package com.agh.zlotowka.controller;


import com.agh.zlotowka.dto.PlanDTO;
import com.agh.zlotowka.dto.PlanRequest;
import com.agh.zlotowka.service.PlanService;
import com.agh.zlotowka.validation.DateAfter2000;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/plan")
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;

    @PostMapping
    public ResponseEntity<PlanDTO> createPlan(@Valid @RequestBody PlanRequest request) {
        PlanDTO plan = planService.createPlan(request);
        return ResponseEntity.ok(plan);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanDTO> getPlan(@PathVariable Integer id) {
        PlanDTO plan = planService.getPlan(id);
        return ResponseEntity.ok(plan);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanDTO> updatePlan(@PathVariable Integer id, @Valid @RequestBody PlanRequest request) {
        PlanDTO updatedPlan = planService.updatePlan(request, id);
        return ResponseEntity.ok(updatedPlan);
    }

    @PostMapping("/complete/{id}")
    public ResponseEntity<PlanDTO> completePlan(
            @PathVariable Integer id,
            @RequestParam(required = false) @DateAfter2000 LocalDate completionDate
    ) {
        PlanDTO completedPlan = planService.completePlan(id, completionDate);
        return ResponseEntity.ok(completedPlan);
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<List<PlanDTO>> getAllPlans(@PathVariable Integer userId) {
        List<PlanDTO> plans = planService.getAllPlansByUserId(userId);
        return ResponseEntity.ok(plans);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Integer id) {
        planService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }
}
