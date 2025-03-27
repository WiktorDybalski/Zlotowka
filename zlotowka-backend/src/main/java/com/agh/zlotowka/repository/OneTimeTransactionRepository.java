package com.agh.zlotowka.repository;

import com.agh.zlotowka.model.OneTimeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OneTimeTransactionRepository extends JpaRepository<OneTimeTransaction, Integer> {
    List<OneTimeTransaction> getTransactionsInRange(int userId, LocalDate startDate, LocalDate endDate);
}
