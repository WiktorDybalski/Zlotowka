package com.agh.zlotowka.controller;

import com.agh.zlotowka.dto.RevenuesAndExpensesResponse;
import com.agh.zlotowka.dto.TransactionBudgetInfo;
import com.agh.zlotowka.dto.UserDataInDateRangeRequest;
import com.agh.zlotowka.service.GeneralTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/generaltransactions")
@RequiredArgsConstructor
public class GeneralTransactionController {
    private final GeneralTransactionService generalTransactionService;

    @GetMapping("/plot")
    public ResponseEntity<List<TransactionBudgetInfo>> getUserBudgetInDateRange(UserDataInDateRangeRequest request) {
        List<TransactionBudgetInfo> budgetList = generalTransactionService.getEstimatedBudgetInDateRange(request);
        return ResponseEntity.ok(budgetList);
    }

    @GetMapping("/nextTransaction/{userId}")
    public ResponseEntity<TransactionBudgetInfo> getNextTransaction(@PathVariable Integer userId) {
        TransactionBudgetInfo transactionBudgetInfo = generalTransactionService.getNextTransaction(userId);
        return ResponseEntity.ok(transactionBudgetInfo);
    }

    @GetMapping("/estimatedBalance/{userId}")
    public ResponseEntity<BigDecimal> getEstimatedBalanceAtTheEndOfTheMonth(@PathVariable Integer userId) {
        BigDecimal estimatedBalance = generalTransactionService.getEstimatedBalanceAtTheEndOfTheMonth(userId);
        return ResponseEntity.ok(estimatedBalance);
    }

    @GetMapping("/revenuesAndExpensesInRange")
    public ResponseEntity<RevenuesAndExpensesResponse> getRevenuesAndExpensesInRange(UserDataInDateRangeRequest request) {
        RevenuesAndExpensesResponse expensesAndRevenuesResponse = generalTransactionService.getRevenuesAndExpensesInRange(request);
        return ResponseEntity.ok(expensesAndRevenuesResponse);
    }
}

