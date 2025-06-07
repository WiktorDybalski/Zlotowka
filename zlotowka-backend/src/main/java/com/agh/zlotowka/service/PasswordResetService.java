package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.ForgotPasswordRequest;
import com.agh.zlotowka.dto.ResetPasswordRequest;
import com.agh.zlotowka.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserService userService;
    private final EmailService emailService;

    @Value("${jwt.secret}")
    private String base64Secret;

    private static final Duration WINDOW = Duration.ofMinutes(15);
    private static final String HMAC_ALGO = "HmacSHA256";

    public String createResetToken(ForgotPasswordRequest req) {
        User user = userService.findByEmail(req.email());
        String code = generateCode(user.getEmail(), Instant.now());
        emailService.sendForgotPasswordTokenEmail(
                user.getEmail(),
                user.getFirstName(),
                code
        );
        return code;
    }

    public void resetPassword(ResetPasswordRequest req) {
        User user = userService.findByEmail(req.email());
        if (!verifyCode(user.getEmail(), req.token(), Instant.now())) {
            throw new IllegalArgumentException("Nieprawidłowy lub przeterminowany token");
        }
        userService.updatePassword(user, req.newPassword());
        emailService.sendUserPasswordChangedEmail(
                user.getEmail(),
                user.getFirstName()
        );
    }

    private String generateCode(String email, Instant now) {
        long window = now.getEpochSecond() / WINDOW.getSeconds();
        String data = email + ":" + window;
        byte[] keyBytes = Base64.getDecoder().decode(base64Secret);
        byte[] hmac    = hmacSha256(keyBytes, data.getBytes(StandardCharsets.UTF_8));
        int num = ((hmac[0] & 0xFF) << 24 |
                (hmac[1] & 0xFF) << 16 |
                (hmac[2] & 0xFF) << 8  |
                (hmac[3] & 0xFF))
                & 0x7FFFFFFF;
        int code = num % 1_000_000;
        return String.format("%06d", code);
    }

    private boolean verifyCode(String email, String code, Instant now) {
        if (generateCode(email, now).equals(code)) return true;
        Instant older = now.minus(WINDOW);
        return generateCode(email, older).equals(code);
    }

    private byte[] hmacSha256(byte[] key, byte[] data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(key, HMAC_ALGO));
            return mac.doFinal(data);
        } catch (Exception e) {
            throw new IllegalStateException("Błąd podczas generowania HMAC", e);
        }
    }
}
