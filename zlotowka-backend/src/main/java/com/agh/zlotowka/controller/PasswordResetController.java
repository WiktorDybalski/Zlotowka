package com.agh.zlotowka.controller;

import com.agh.zlotowka.dto.ForgotPasswordRequest;
import com.agh.zlotowka.dto.ResetPasswordRequest;
import com.agh.zlotowka.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth/password")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;


    @PostMapping("/forgot")
    public ResponseEntity<Map<String,String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest req) {
        String token = passwordResetService.createResetToken(req);
        return ResponseEntity.ok(Map.of(
                "resetToken", token,
                "message", "Wygenerowano token do resetowania hasła. Sprawdź swoją pocztę e-mail."
        ));
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String,String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest req) {
        passwordResetService.resetPassword(req);
        return ResponseEntity.ok(Map.of(
                "message", "Hasło zostało pomyślnie zresetowane"
        ));
    }

}