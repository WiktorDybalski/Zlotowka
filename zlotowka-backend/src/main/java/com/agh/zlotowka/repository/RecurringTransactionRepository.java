package com.agh.zlotowka.repository;

import com.agh.zlotowka.model.RecurringTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Integer> {

    @Query("SELECT rt FROM RecurringTransaction rt " +
            "WHERE rt.nextPaymentDate IS NULL OR rt.nextPaymentDate <= CURRENT_DATE")
    List<RecurringTransaction> findDueRecurringTransactions();

    @Query("SELECT rt FROM RecurringTransaction rt " +
            "WHERE rt.user.userId = :userId AND rt.nextPaymentDate >= :now AND rt.isIncome=TRUE " +
            "ORDER BY rt.nextPaymentDate ASC " +
            "LIMIT 1")
    Optional<RecurringTransaction> getNextIncomeRecurringTransactionByUser(
            @Param("userId") int userId,
            @Param("now") LocalDate now
    );

    @Query("SELECT rt FROM RecurringTransaction rt " +
            "WHERE rt.user.userId = :userId AND rt.nextPaymentDate >= :now AND rt.isIncome=FALSE " +
            "ORDER BY rt.nextPaymentDate ASC " +
            "LIMIT 1")
    Optional<RecurringTransaction> getNextExpenseRecurringTransactionByUser(
            @Param("userId") int userId,
            @Param("now") LocalDate now
    );

    @Query("SELECT rt FROM RecurringTransaction rt WHERE rt.user.userId = :userId AND rt.nextPaymentDate >= :startDate AND rt.nextPaymentDate <= :endDate")
    List<RecurringTransaction> getActiveTransactionsByUser(
            @Param("userId") int userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
