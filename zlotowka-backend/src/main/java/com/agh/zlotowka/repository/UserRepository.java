package com.agh.zlotowka.repository;

import com.agh.zlotowka.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    int getUserBudget(int userId);
}
