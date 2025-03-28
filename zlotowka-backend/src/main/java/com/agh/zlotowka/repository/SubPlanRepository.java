package com.agh.zlotowka.repository;

import com.agh.zlotowka.model.Subplan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubPlanRepository extends JpaRepository<Subplan, Integer> {
}
