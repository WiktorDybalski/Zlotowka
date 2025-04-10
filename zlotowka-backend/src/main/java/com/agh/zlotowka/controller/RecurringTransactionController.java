package com.agh.zlotowka.controller;

import com.agh.zlotowka.dto.RecurringTransactionDTO;
import com.agh.zlotowka.dto.RecurringTransactionRequest;
import com.agh.zlotowka.service.RecurringTransactionService;
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
    public ResponseEntity<RecurringTransactionDTO> addRecurringTransaction(@Valid @RequestBody RecurringTransactionRequest request) {
        RecurringTransactionDTO transaction = recurringTransactionService.createTransaction(request);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecurringTransactionDTO> getRecurringTransactions(@PathVariable Integer id) {
        RecurringTransactionDTO oneTimeTransaction = recurringTransactionService.getTransaction(id);
        return ResponseEntity.ok(oneTimeTransaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecurringTransactionDTO> updateRecurringTransaction(@Valid @RequestBody RecurringTransactionRequest request, @PathVariable Integer id) {
        RecurringTransactionDTO updatedTransaction = recurringTransactionService.updateTransaction(request, id);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeRecurringTransaction(@PathVariable Integer id) {
        recurringTransactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
