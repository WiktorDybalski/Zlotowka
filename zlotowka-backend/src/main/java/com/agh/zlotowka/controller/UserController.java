package com.agh.zlotowka.controller;

import com.agh.zlotowka.model.User;
import com.agh.zlotowka.repository.UserRepository;
import com.agh.zlotowka.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.persistence.EntityNotFoundException;



@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User u = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return ResponseEntity.ok(u);
    }

}
