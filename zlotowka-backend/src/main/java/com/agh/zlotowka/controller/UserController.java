package com.agh.zlotowka.controller;

import com.agh.zlotowka.dto.UpdatePasswordRequest;
import com.agh.zlotowka.dto.UserDetailsRequest;
import com.agh.zlotowka.dto.UserResponse;
import com.agh.zlotowka.security.CustomUserDetails;
import com.agh.zlotowka.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/account")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getCurrentUser(userDetails));
    }

    @PutMapping("/password")
    public ResponseEntity<Map<String, String>> updatePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdatePasswordRequest request
    ) {
        userService.updateUserPassword(userDetails, request);
        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }

    @PutMapping("/user-details")
    public ResponseEntity<Map<String, Object>> updateUserDetails(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserDetailsRequest request
    ) {
        UserResponse userResponse = userService.updateUserDetails(userDetails, request);
        return ResponseEntity.ok(Map.of(
                "message", "User details updated successfully",
                "user", userResponse
        ));
    }

}
