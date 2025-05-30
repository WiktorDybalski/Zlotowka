package com.agh.zlotowka.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendTestEmail(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Test maila z Złotówki");
        message.setText("To jest testowa wiadomość wysłana z backendu Złotówki przez Mailtrap.");
        message.setFrom("powiadomienia@zlotowka.pl");

        mailSender.send(message);
    }
}
