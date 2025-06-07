package com.agh.zlotowka.controller;

import com.agh.zlotowka.model.AppUserNotification;
import com.agh.zlotowka.model.User;
import com.agh.zlotowka.security.CustomUserDetails;
import com.agh.zlotowka.service.AppUserNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class AppUserNotificationController {
    private final AppUserNotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<AppUserNotification>> getAll(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        List<AppUserNotification> list = notificationService.fetchUserNotifications(user);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteOne(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer id
    ) {
        User user = userDetails.getUser();
        notificationService.deleteByIdAndUser(id, user);
        return ResponseEntity.ok(Map.of("message", "Powiadomienie usunięte"));
    }
}
