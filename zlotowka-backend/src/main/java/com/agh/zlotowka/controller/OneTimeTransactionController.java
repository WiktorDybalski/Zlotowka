package com.agh.zlotowka.controller;

import com.agh.zlotowka.dto.OneTimeTransactionDTO;
import com.agh.zlotowka.dto.OneTimeTransactionRequest;
import com.agh.zlotowka.model.OneTimeTransaction;
import com.agh.zlotowka.service.OneTimeTransactionService;
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
@RequestMapping("/onetimetransaction")
@RequiredArgsConstructor
public class OneTimeTransactionController {
    private final OneTimeTransactionService oneTimeTransactionService;
//    private final OneTimeTransactionTempService oneTimeTransactionService;

    @PostMapping
    public ResponseEntity<OneTimeTransactionDTO> createOneTimeTransaction(@Valid @RequestBody OneTimeTransactionRequest request) {
        OneTimeTransactionDTO oneTimeTransaction = oneTimeTransactionService.createTransaction(request);
        return ResponseEntity.ok(oneTimeTransaction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OneTimeTransactionDTO> getOneTimeTransaction(@PathVariable Integer id) {
        OneTimeTransactionDTO oneTimeTransaction = oneTimeTransactionService.getTransaction(id);
        return ResponseEntity.ok(oneTimeTransaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OneTimeTransactionDTO> updateOneTimeTransaction(@PathVariable Integer id, @Valid @RequestBody OneTimeTransactionRequest request) {
        OneTimeTransactionDTO updatedTransaction = oneTimeTransactionService.updateOneTimeTransaction(request, id);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeOneTimeTransaction(@PathVariable Integer id) {
        oneTimeTransactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<List<OneTimeTransactionDTO>> getAllOneTimeTransactions(@PathVariable Integer userId) {
        List<OneTimeTransactionDTO> oneTimeTransactions = oneTimeTransactionService.getAllTransactionsByUserId(userId);
        return ResponseEntity.ok(oneTimeTransactions);
    }
}
