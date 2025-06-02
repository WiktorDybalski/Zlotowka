package com.agh.zlotowka.controller;


import com.agh.zlotowka.dto.SubplanDTO;
import com.agh.zlotowka.dto.SubplanRequest;
import com.agh.zlotowka.service.SubplanService;
import com.agh.zlotowka.validation.DateAfter2000;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/subplan")
@RequiredArgsConstructor
public class SubplanController {
    private final SubplanService subplanService;

    @PostMapping
    public ResponseEntity<SubplanDTO> createSubplan(@Valid @RequestBody SubplanRequest request) {
        SubplanDTO subplan = subplanService.createSubplan(request);
        return ResponseEntity.ok(subplan);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubplanDTO> getSubplan(@PathVariable Integer id) {
        SubplanDTO subplan = subplanService.getSubplan(id);
        return ResponseEntity.ok(subplan);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubplanDTO> updateSubplan(@PathVariable Integer id, @Valid @RequestBody SubplanRequest request) {
        SubplanDTO updatedSubplan = subplanService.updateSubplan(request, id);
        return ResponseEntity.ok(updatedSubplan);
    }

    @PostMapping("/complete/{id}")
    public ResponseEntity<SubplanDTO> completeSubplan(
            @PathVariable Integer id,
            @RequestParam(required = false) @DateAfter2000 LocalDate completionDate
            ) {
        SubplanDTO completedSubplan = subplanService.completeSubplan(id, completionDate);
        return ResponseEntity.ok(completedSubplan);
    }

    @GetMapping("/all/{planId}")
    public ResponseEntity<List<SubplanDTO>> getAllSubplans(@PathVariable Integer planId) {
        List<SubplanDTO> subplans = subplanService.getAllSubplansByPlanId(planId);
        return ResponseEntity.ok(subplans);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubplan(@PathVariable Integer id) {
        subplanService.deleteSubplan(id);
        return ResponseEntity.noContent().build();
    }
}
