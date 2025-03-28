package com.agh.zlotowka.controller;

import com.agh.zlotowka.dto.RecurringTransactionRequest;
import com.agh.zlotowka.model.OneTimeTransaction;
import com.agh.zlotowka.model.RecurringTransaction;
import com.agh.zlotowka.service.RecurringTransactionService;
import com.agh.zlotowka.service.RecurringTransactionTempService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recurringtransaction")
@RequiredArgsConstructor
public class RecurringTransactionController {
        private final RecurringTransactionService recurringTransactionService;
//    private final RecurringTransactionTempService recurringTransactionService;

    @PostMapping
    public ResponseEntity<RecurringTransaction> addRecurringTransaction(@Valid @RequestBody RecurringTransactionRequest request) {
        RecurringTransaction transaction = recurringTransactionService.createTransaction(request);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecurringTransaction> getRecurringTransactions(@PathVariable Integer id) {
        RecurringTransaction oneTimeTransaction = recurringTransactionService.getTransaction(id);
        return ResponseEntity.ok(oneTimeTransaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecurringTransaction> updateRecurringTransaction(@Valid @RequestBody RecurringTransactionRequest request, @PathVariable Integer id) {
        RecurringTransaction updatedTransaction = recurringTransactionService.updateTransaction(request, id);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeRecurringTransaction(@PathVariable Integer id) {
        recurringTransactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
