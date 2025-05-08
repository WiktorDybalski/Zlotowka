package com.agh.zlotowka.repository;

import com.agh.zlotowka.model.Subplan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SubPlanRepository extends JpaRepository<Subplan, Integer> {
    @Query("SELECT sp FROM Subplan sp WHERE sp.plan.planId = :planId")
    List<Subplan> findAllSubPlansByPlanId(@Param("planId") Integer planId);

    @Query("SELECT COALESCE(SUM(sp.requiredAmount), 0) FROM Subplan sp WHERE sp.plan.planId = :planId")
    BigDecimal getTotalSubPlanAmount(@Param("planId") Integer planId);

    @Query("SELECT COALESCE(SUM(sp.requiredAmount), 0) FROM Subplan sp WHERE sp.plan.planId = :planId AND sp.completed = true")
    BigDecimal getTotalSubPlanAmountCompleted(@Param("planId") Integer planId);

    @Query("SELECT COUNT(sp) FROM Subplan sp WHERE sp.plan.planId = :planId AND sp.completed = true")
    Integer getCompletedSubPlanCount(@Param("planId") Integer planId);

    @Query("SELECT COUNT(sp) FROM Subplan sp WHERE sp.plan.planId = :planId")
    Integer getSubplanCount(@Param("planId") Integer planId);

    @Query("SELECT s FROM Subplan s WHERE s.plan.user.userId = :userId AND s.completed = false")
    List<Subplan> findAllUncompletedSubPlansByUserId(@Param("userId") Integer userId);
}
