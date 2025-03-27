package com.agh.zlotowka.repository;

import com.agh.zlotowka.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    int getUserBudget(int userId);

    Optional<String> getUserCurrencyName(int userId);
}
