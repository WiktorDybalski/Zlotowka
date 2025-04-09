package com.agh.zlotowka.repository;

import com.agh.zlotowka.model.OneTimeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OneTimeTransactionRepository extends JpaRepository<OneTimeTransaction, Integer> {
    @Query("SELECT t FROM OneTimeTransaction t WHERE t.user.userId = :userId AND t.date > :startDate AND t.date <= :endDate")
    List<OneTimeTransaction> getTransactionsInRange(
            @Param("userId") int userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT t FROM OneTimeTransaction t WHERE t.user.userId = :userId AND t.date > :now AND t.isIncome=TRUE ORDER BY t.date ASC LIMIT 1")
    Optional<OneTimeTransaction> getNextIncomeOneTimeTransactionByUser(@Param("userId") int userId, @Param("now") LocalDate now);

    @Query("SELECT t FROM OneTimeTransaction t WHERE t.user.userId = :userId AND t.date > :now AND t.isIncome=FALSE ORDER BY t.date ASC LIMIT 1")
    Optional<OneTimeTransaction> getNextExpenseOneTimeTransactionByUser(@Param("userId") int userId, @Param("now") LocalDate now);

    @Query("SELECT t FROM OneTimeTransaction t WHERE t.date = CURRENT_DATE")
    List<OneTimeTransaction> findTransactionsToday();

    @Query("SELECT t FROM OneTimeTransaction t WHERE t.user.userId = :userId")
    List<OneTimeTransaction> findAllByUser(@Param("userId") Integer userId);
}
