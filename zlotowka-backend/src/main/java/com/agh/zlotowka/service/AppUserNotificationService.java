package com.agh.zlotowka.service;

import com.agh.zlotowka.model.AppUserNotification;
import com.agh.zlotowka.repository.AppUserNotificationRepository;
import com.agh.zlotowka.model.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserNotificationService {
    private final AppUserNotificationRepository repo;


    public void createNotification(
            User user,
            String category,
            String text,
            boolean byEmail,
            boolean byPhone
    ) {
        AppUserNotification n = AppUserNotification.builder()
                .user(user)
                .createdAt(LocalDateTime.now())
                .category(category)
                .text(text)
                .byEmail(byEmail)
                .byPhone(byPhone)
                .build();
        repo.save(n);
    }


    public List<AppUserNotification> fetchUserNotifications(User user) {
        return repo.findByUserOrderByCreatedAtDesc(user);
    }


    public void deleteByIdAndUser(Integer id, User user) {
        AppUserNotification existing = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono powiadomienia"));
        if (!existing.getUser().getUserId().equals(user.getUserId())) {
            throw new SecurityException("Brak dostępu do togo powiadomienia");
        }
        repo.delete(existing);
    }
}
