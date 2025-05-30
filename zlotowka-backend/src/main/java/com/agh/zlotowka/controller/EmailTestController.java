package com.agh.zlotowka.controller;

import com.agh.zlotowka.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test-email")
@RequiredArgsConstructor
public class EmailTestController {

    private final EmailService emailService;

    @PostMapping
    public String sendEmail(@RequestParam String to) {
        emailService.sendTestEmail(to);
        return "Wysłano testowego maila na adres: " + to;
    }
}
