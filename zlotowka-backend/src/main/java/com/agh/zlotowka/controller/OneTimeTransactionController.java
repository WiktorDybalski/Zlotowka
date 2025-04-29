package com.agh.zlotowka.controller;

import com.agh.zlotowka.dto.OneTimeTransactionDTO;
import com.agh.zlotowka.dto.OneTimeTransactionRequest;
import com.agh.zlotowka.dto.PaginatedTransactionsDTO;
import com.agh.zlotowka.security.CustomUserDetails;
import com.agh.zlotowka.service.OneTimeTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/onetime-transaction")
@RequiredArgsConstructor
public class OneTimeTransactionController {
    private final OneTimeTransactionService oneTimeTransactionService;

    @PostMapping
    public ResponseEntity<OneTimeTransactionDTO> createOneTimeTransaction(
            @Valid @RequestBody OneTimeTransactionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        oneTimeTransactionService.validateUserId(request.userId(), userDetails);
        return ResponseEntity.ok(oneTimeTransactionService.createTransaction(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OneTimeTransactionDTO> getOneTimeTransaction(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        oneTimeTransactionService.validateUserId(oneTimeTransactionService
                .getTransaction(id).userId(), userDetails);
        return ResponseEntity.ok(oneTimeTransactionService.getTransaction(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OneTimeTransactionDTO> updateOneTimeTransaction(
            @PathVariable Integer id,
            @Valid @RequestBody OneTimeTransactionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        oneTimeTransactionService.validateUserId(request.userId(), userDetails);
        return ResponseEntity.ok(oneTimeTransactionService.updateOneTimeTransaction(request, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeOneTimeTransaction(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        oneTimeTransactionService.validateUserId(
                oneTimeTransactionService.getTransaction(id).userId(), userDetails);
        oneTimeTransactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<PaginatedTransactionsDTO> getAllOneTimeTransactions(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer limit,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        oneTimeTransactionService.validateUserId(userId, userDetails);
        PaginatedTransactionsDTO transactions = oneTimeTransactionService.getPageTransactionsByUserId(userId, page, limit);
        return ResponseEntity.ok(transactions);
    }


}
