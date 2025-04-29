package com.agh.zlotowka.controller;

import com.agh.zlotowka.dto.RecurringTransactionDTO;
import com.agh.zlotowka.dto.RecurringTransactionRequest;
import com.agh.zlotowka.service.RecurringTransactionService;
import com.agh.zlotowka.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recurring-transaction")
@RequiredArgsConstructor
public class RecurringTransactionController {
    private final RecurringTransactionService recurringTransactionService;

    @PostMapping
    public ResponseEntity<RecurringTransactionDTO> addRecurringTransaction(
            @Valid @RequestBody RecurringTransactionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        recurringTransactionService.validateUserId(request.userId(), userDetails);
        RecurringTransactionDTO dto = recurringTransactionService.createTransaction(request);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecurringTransactionDTO> getRecurringTransactions(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        recurringTransactionService.validateOwnershipById(id, userDetails);
        RecurringTransactionDTO dto = recurringTransactionService.getTransaction(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecurringTransactionDTO> updateRecurringTransaction(
            @Valid @RequestBody RecurringTransactionRequest request,
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        recurringTransactionService.validateUserId(request.userId(), userDetails);
        recurringTransactionService.validateOwnershipById(id, userDetails);
        RecurringTransactionDTO dto = recurringTransactionService.updateTransaction(request, id);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeRecurringTransaction(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        recurringTransactionService.validateOwnershipById(id, userDetails);
        recurringTransactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
