package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.EmailDTO;
import com.agh.zlotowka.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserNotificationService {

    private final EmailSenderService emailSenderService;
    private final AppUserNotificationService appUserNotificationService;
    private final SmsSenderService smsSenderService;
    private final EmailTemplateProvider templateProvider;

    public void sendUserWelcomeNotification(User user) {
        String htmlBody = templateProvider.getUserWelcomeHtml(user.getFirstName());

        emailSenderService.sendHtmlEmail(EmailDTO.builder()
                .to(user.getEmail())
                .subject("Witamy w aplikacji Złotówka!")
                .body(htmlBody)
                .build()
        );

        appUserNotificationService.createNotification(
                user,
                "Witaj",
                "Witamy w aplikacji Złotówka! Możesz teraz zarządzać swoim budżetem.",
                true,
                false
        );
    }

    public void sendUserPasswordChangedEmail(String to, String firstName) {
        String htmlBody = templateProvider.getPasswordChangedHtml(firstName);

        emailSenderService.sendHtmlEmail(EmailDTO.builder()
                .to(to)
                .subject("Hasło zmienione")
                .body(htmlBody)
                .build()
        );
    }

    public void sendUserEmailChangeNotification(User user) {
        String htmlBody = templateProvider.getEmailChangeNotificationHtml(user.getFirstName());

        emailSenderService.sendHtmlEmail(EmailDTO.builder()
                .to(user.getEmail())
                .subject("Powiadomienia aktywowane")
                .body(htmlBody)
                .build()
        );

        appUserNotificationService.createNotification(
                user,
                "Powiadomienia",
                "Aktywowano powiadomienia e-mail. Będziesz otrzymywać powiadomienia w aplikacji.",
                true,
                false
        );
    }

    public void sendUserPhoneChangeNotification(User user) {
        String messageText = "Złotówka: Powiadomienia SMS aktywne. Będziesz otrzymywać ważne informacje.";

        smsSenderService.sendSms(user.getPhoneNumber(), messageText);

        appUserNotificationService.createNotification(
                user,
                "Powiadomienia",
                "Aktywowano powiadomienia SMS. Będziesz otrzymywać powiadomienia w aplikacji.",
                false,
                true
        );
    }

    public void sendForgotPasswordTokenEmail(String to, String firstName, String token) {
        String htmlBody = templateProvider.getForgotPasswordTokenHtml(firstName, token);

        emailSenderService.sendHtmlEmail(EmailDTO.builder()
                .to(to)
                .subject("Reset hasła w Złotówce")
                .body(htmlBody)
                .build()
        );
    }
}
