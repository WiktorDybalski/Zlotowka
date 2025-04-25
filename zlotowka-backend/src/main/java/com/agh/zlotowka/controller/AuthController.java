package com.agh.zlotowka.controller;

import com.agh.zlotowka.dto.LoginRequest;
import com.agh.zlotowka.dto.RegistrationRequest;
import com.agh.zlotowka.security.JWTUtil;
import com.agh.zlotowka.security.CustomUserDetails;
import com.agh.zlotowka.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
        );
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(Map.of(
                "token", token,
                "message", "Login completed successfully!",
                "user", userDetails.getUser()
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest registrationRequest) {
        var newUser = userService.registerUser(registrationRequest);
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(newUser.getEmail(), registrationRequest.password())
        );
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(Map.of(
                "token", token,
                "message", "Registration completed successfully!",
                "user", userDetails.getUser()
        ));
    }
}
