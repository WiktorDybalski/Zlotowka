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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/general-transactions")
@RequiredArgsConstructor
public class GeneralTransactionController {
    private final GeneralTransactionService generalTransactionService;

    @PostMapping("/plot-data")
    public ResponseEntity<List<SinglePlotData>> getUserBudgetInDateRange(@Valid @RequestBody UserDataInDateRangeRequest request) {
        List<SinglePlotData> budgetList = generalTransactionService.getEstimatedBudgetInDateRange(request);
        return ResponseEntity.ok(budgetList);
    }

    @GetMapping("/next-transaction/{userId}")
    public ResponseEntity<TransactionBudgetInfo> getNextTransaction(@PathVariable Integer userId, @RequestParam Boolean isIncome) {
        TransactionBudgetInfo transactionBudgetInfo = generalTransactionService.getNextTransaction(userId, isIncome);
        return ResponseEntity.ok(transactionBudgetInfo);
    }

    @GetMapping("/estimated-balance/{userId}")
    public ResponseEntity<Map<String, BigDecimal>> getEstimatedBalanceAtTheEndOfTheMonth(@PathVariable Integer userId) {
        BigDecimal estimatedBalance = generalTransactionService.getEstimatedBalanceAtTheEndOfTheMonth(userId);
        return ResponseEntity.ok(Map.of("estimatedBalance", estimatedBalance));
    }

    @PostMapping("/revenues-expenses-in-range")
    public ResponseEntity<RevenuesAndExpensesResponse> getRevenuesAndExpensesInRange(@Valid @RequestBody UserDataInDateRangeRequest request) {
        RevenuesAndExpensesResponse expensesAndRevenuesResponse = generalTransactionService.getRevenuesAndExpensesInRange(request);
        return ResponseEntity.ok(expensesAndRevenuesResponse);
    }

    @GetMapping("/monthly-summary/{userId}")
    public ResponseEntity<MonthlySummaryDto> getMonthlySummary(@PathVariable Integer userId) {
        MonthlySummaryDto monthlySummaryDto = generalTransactionService.getMonthlySummary(userId);
        return ResponseEntity.ok(monthlySummaryDto);
    }

    @GetMapping("/current-balance/{userId}")
    public ResponseEntity<Map<String, BigDecimal>> getCurrentBalance(@PathVariable Integer userId) {
        BigDecimal currentBalance = generalTransactionService.getCurrentBalance(userId);
        return ResponseEntity.ok(Map.of("currentBalance", currentBalance));
    }
}

