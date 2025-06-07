package com.agh.zlotowka.repository;

import com.agh.zlotowka.model.SystemNotificationModel;
import com.agh.zlotowka.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemNotificationRepository extends JpaRepository<SystemNotificationModel, Integer> {
    List<SystemNotificationModel> findAllByUserOrderByCreatedAtDesc(User user);
}
