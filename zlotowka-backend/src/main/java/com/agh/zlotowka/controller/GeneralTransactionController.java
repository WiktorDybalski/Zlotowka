package com.agh.zlotowka.controller;

import com.agh.zlotowka.dto.RevenuesAndExpensesResponse;
import com.agh.zlotowka.dto.SinglePlotData;
import com.agh.zlotowka.dto.TransactionBudgetInfo;
import com.agh.zlotowka.dto.UserDataInDateRangeRequest;
import com.agh.zlotowka.service.GeneralTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/generaltransactions")
@RequiredArgsConstructor
public class GeneralTransactionController {
    private final GeneralTransactionService generalTransactionService;

    @PostMapping("/plotData")
    public ResponseEntity<List<SinglePlotData>> getUserBudgetInDateRange(@Valid @RequestBody UserDataInDateRangeRequest request) {
        List<SinglePlotData> budgetList = generalTransactionService.getEstimatedBudgetInDateRange(request);
        return ResponseEntity.ok(budgetList);
    }

    @GetMapping("/nextTransaction/{userId}")
    public ResponseEntity<TransactionBudgetInfo> getNextTransaction(@PathVariable Integer userId, @RequestParam Boolean isIncome) {
        TransactionBudgetInfo transactionBudgetInfo = generalTransactionService.getNextTransaction(userId, isIncome);
        return ResponseEntity.ok(transactionBudgetInfo);
    }

    @GetMapping("/estimatedBalance/{userId}")
    public ResponseEntity<Map<String, BigDecimal>> getEstimatedBalanceAtTheEndOfTheMonth(@PathVariable Integer userId) {
        BigDecimal estimatedBalance = generalTransactionService.getEstimatedBalanceAtTheEndOfTheMonth(userId);
        return ResponseEntity.ok(Map.of("estimatedBalance:", estimatedBalance));
    }

    @PostMapping("/revenuesAndExpensesInRange")
    public ResponseEntity<RevenuesAndExpensesResponse> getRevenuesAndExpensesInRange(@Valid @RequestBody UserDataInDateRangeRequest request) {
        RevenuesAndExpensesResponse expensesAndRevenuesResponse = generalTransactionService.getRevenuesAndExpensesInRange(request);
        return ResponseEntity.ok(expensesAndRevenuesResponse);
    }
}

