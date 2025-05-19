package com.agh.zlotowka.repository;

import com.agh.zlotowka.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {
    @Query("SELECT p FROM Plan p WHERE p.user.userId = :userId")
    List<Plan> findAllByUser(@Param("userId") Integer userId);

    @Query("SELECT p FROM Plan p WHERE p.user.userId = :userId AND p.completed = false")
    List<Plan> findAllUncompletedByUser(@Param("userId") Integer userId);
}
