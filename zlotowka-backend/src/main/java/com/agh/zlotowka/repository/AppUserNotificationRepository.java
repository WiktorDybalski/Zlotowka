package com.agh.zlotowka.repository;

import com.agh.zlotowka.model.AppUserNotification;
import com.agh.zlotowka.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppUserNotificationRepository extends JpaRepository<AppUserNotification, Integer> {

    List<AppUserNotification> findByUserOrderByCreatedAtDesc(User user);
}
