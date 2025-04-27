package com.agh.zlotowka.controller;

import com.agh.zlotowka.dto.MonthlySummaryDto;
import com.agh.zlotowka.dto.RevenuesAndExpensesResponse;
import com.agh.zlotowka.dto.SinglePlotData;
import com.agh.zlotowka.dto.TransactionBudgetInfo;
import com.agh.zlotowka.dto.UserDataInDateRangeRequest;
import com.agh.zlotowka.service.GeneralTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.agh.zlotowka.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/general-transactions")
@RequiredArgsConstructor
public class GeneralTransactionController {
    private final GeneralTransactionService generalTransactionService;

    @PostMapping("/plot-data")
    public ResponseEntity<List<SinglePlotData>> getUserBudgetInDateRange(
            @Valid @RequestBody UserDataInDateRangeRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        validateUserId(request.userId(), userDetails);
        return ResponseEntity.ok(generalTransactionService.getEstimatedBudgetInDateRange(request));
    }

    @GetMapping("/next-transaction/{userId}")
    public ResponseEntity<TransactionBudgetInfo> getNextTransaction(
            @PathVariable Integer userId,
            @RequestParam Boolean isIncome,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        validateUserId(userId, userDetails);
        return ResponseEntity.ok(generalTransactionService.getNextTransaction(userId, isIncome));
    }

    @GetMapping("/estimated-balance/{userId}")
    public ResponseEntity<Map<String, BigDecimal>> getEstimatedBalanceAtTheEndOfTheMonth(
            @PathVariable Integer userId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        validateUserId(userId, userDetails);
        BigDecimal balance = generalTransactionService.getEstimatedBalanceAtTheEndOfTheMonth(userId);
        return ResponseEntity.ok(Map.of("estimatedBalance", balance));
    }

    @PostMapping("/revenues-expenses-in-range")
    public ResponseEntity<RevenuesAndExpensesResponse> getRevenuesAndExpensesInRange(
            @Valid @RequestBody UserDataInDateRangeRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        validateUserId(request.userId(), userDetails);
        return ResponseEntity.ok(generalTransactionService.getRevenuesAndExpensesInRange(request));
    }

    @GetMapping("/monthly-summary/{userId}")
    public ResponseEntity<MonthlySummaryDto> getMonthlySummary(
            @PathVariable Integer userId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        validateUserId(userId, userDetails);
        return ResponseEntity.ok(generalTransactionService.getMonthlySummary(userId));
    }

    @GetMapping("/current-balance/{userId}")
    public ResponseEntity<Map<String, BigDecimal>> getCurrentBalance(
            @PathVariable Integer userId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        validateUserId(userId, userDetails);
        BigDecimal currentBalance = generalTransactionService.getCurrentBalance(userId);
        return ResponseEntity.ok(Map.of("currentBalance", currentBalance));
    }
    private void validateUserId(Integer userId, CustomUserDetails userDetails) {
        if (!userId.equals(userDetails.getUser().getUserId())) {
            throw new IllegalArgumentException("Access denied");
        }
    }
}

