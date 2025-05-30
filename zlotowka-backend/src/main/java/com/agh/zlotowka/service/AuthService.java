package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.LoginRequest;
import com.agh.zlotowka.dto.RegistrationRequest;
import com.agh.zlotowka.dto.UserResponse;
import com.agh.zlotowka.model.User;
import com.agh.zlotowka.security.CustomUserDetails;
import com.agh.zlotowka.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final UserService userService;
    private final EmailService emailService;

    public Map<String, Object> login(LoginRequest loginRequest) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
        );
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();
        String token = jwtUtil.generateToken(userDetails);

        return Map.of(
                "token", token,
                "message", "Logowanie zakończone pomyślnie!",
                "user", mapToUserResponse(user)
        );
    }

    public Map<String, Object> register(RegistrationRequest registrationRequest) {
        var newUser = userService.registerUser(registrationRequest);
        emailService.sendUserWelcomeEmail(newUser.getEmail(), newUser.getFirstName());

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(newUser.getEmail(), registrationRequest.password())
        );
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();
        String token = jwtUtil.generateToken(userDetails);

        return Map.of(
                "token", token,
                "message", "Rejestracja zakończona pomyślnie!",
                "user", mapToUserResponse(user)
        );
    }

    public Map<String, Object> refreshToken(CustomUserDetails userDetails) {
        String newToken = jwtUtil.generateToken(userDetails);
        return Map.of(
                "token", newToken,
                "message", "Token odświeżony pomyślnie!"
        );
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getDateOfJoining(),
                user.getCurrentBudget(),
                user.getCurrency(),
                user.getDarkMode(),
                user.getNotificationsByEmail(),
                user.getNotificationsByPhone()
        );
    }
}
