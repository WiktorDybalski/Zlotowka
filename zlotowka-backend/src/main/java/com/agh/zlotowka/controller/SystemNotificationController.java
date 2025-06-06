package com.agh.zlotowka.controller;

import com.agh.zlotowka.model.SystemNotificationModel;
import com.agh.zlotowka.security.CustomUserDetails;
import com.agh.zlotowka.service.SystemNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/system-notifications")
public class SystemNotificationController {

    private final SystemNotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<SystemNotificationModel>> getAll(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(
                notificationService.fetchUserNotifications(userDetails.getUser())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteOne(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer id
    ) {
        notificationService.deleteByIdAndUser(id, userDetails.getUser());
        return ResponseEntity.ok(Map.of("message", "Powiadomienie usunięte"));
    }
}

