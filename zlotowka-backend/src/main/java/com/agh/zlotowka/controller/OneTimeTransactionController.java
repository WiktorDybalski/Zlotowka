package com.agh.zlotowka.controller;

import com.agh.zlotowka.dto.OneTimeTransactionRequest;
import com.agh.zlotowka.model.OneTimeTransaction;
import com.agh.zlotowka.service.OneTimeTransactionTempService;
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

import java.util.List;

@RestController
@RequestMapping("/onetimetransactions")
@RequiredArgsConstructor
public class OneTimeTransactionController {
//    private final OneTimeTransactionService oneTimeTransactionService;
    private final OneTimeTransactionTempService oneTimeTransactionService;

    @PostMapping
    public ResponseEntity<OneTimeTransaction> addOneTimeTransaction(@Valid @RequestBody OneTimeTransactionRequest request) {
        OneTimeTransaction oneTimeTransaction = oneTimeTransactionService.createTransaction(request);
        return ResponseEntity.ok(oneTimeTransaction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OneTimeTransaction> getOneTimeTransaction(@PathVariable Integer id) {
        OneTimeTransaction oneTimeTransaction = oneTimeTransactionService.getTransaction(id);
        return ResponseEntity.ok(oneTimeTransaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OneTimeTransaction> updateOneTimeTransaction(@Valid @RequestBody OneTimeTransactionRequest request) {
        OneTimeTransaction updatedTransaction = oneTimeTransactionService.updateTransaction(request);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeOneTimeTransaction(@PathVariable Integer id) {
        oneTimeTransactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<OneTimeTransaction>> getAllOneTimeTransactions(Integer userId) {
        List<OneTimeTransaction> oneTimeTransactions = oneTimeTransactionService.getAllTransactions(userId);
        return ResponseEntity.ok(oneTimeTransactions);
    }
}
