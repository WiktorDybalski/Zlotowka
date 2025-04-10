package com.agh.zlotowka.repository;

import com.agh.zlotowka.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u.currentBudget FROM User u WHERE u.userId = :userId")
    Optional<BigDecimal> getUserBudget(@Param("userId") int userId);

    @Query("SELECT u.currency.isoCode FROM User u WHERE u.userId = :userId")
    Optional<String> getUserCurrencyName(@Param("userId") int userId);
}
