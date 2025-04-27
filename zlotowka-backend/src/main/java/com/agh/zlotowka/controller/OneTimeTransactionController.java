package com.agh.zlotowka.controller;

import com.agh.zlotowka.dto.OneTimeTransactionDTO;
import com.agh.zlotowka.dto.OneTimeTransactionRequest;
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
import com.agh.zlotowka.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

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
        validateUserId(request.userId(), userDetails);
        return ResponseEntity.ok(oneTimeTransactionService.createTransaction(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OneTimeTransactionDTO> getOneTimeTransaction(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(oneTimeTransactionService.getTransactionWithUserCheck(id, userDetails));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OneTimeTransactionDTO> updateOneTimeTransaction(
            @PathVariable Integer id,
            @Valid @RequestBody OneTimeTransactionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        validateUserId(request.userId(), userDetails);
        return ResponseEntity.ok(oneTimeTransactionService.updateOneTimeTransaction(request, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeOneTimeTransaction(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        oneTimeTransactionService.deleteTransactionWithUserCheck(id, userDetails);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<List<OneTimeTransactionDTO>> getAllOneTimeTransactions(
            @PathVariable Integer userId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (!userId.equals(userDetails.getUser().getUserId())) {
            throw new IllegalArgumentException("Access denied");
        }
        List<OneTimeTransactionDTO> oneTimeTransactions = oneTimeTransactionService.getAllTransactionsByUserId(userId);
        return ResponseEntity.ok(oneTimeTransactions);
    }

    private void validateUserId(Integer userId, CustomUserDetails userDetails) {
        if (!userId.equals(userDetails.getUser().getUserId())) {
            throw new IllegalArgumentException("Access denied");
        }
    }
}
