package com.agh.zlotowka.repository;

import com.agh.zlotowka.model.RecurringTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Integer> {

    @Query("SELECT rt FROM RecurringTransaction rt " +
            "WHERE FUNCTION('DATE_ADD', rt.lastPaymentDate, rt.interval, 'DAY') <= CURRENT_DATE")
    List<RecurringTransaction> findDueRecurringTransactions();

    List<RecurringTransaction> getTransactionsByUser(int userId);
}
