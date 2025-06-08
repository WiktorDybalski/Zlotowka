package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.EmailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.agh.zlotowka.model.User;


@Service
@RequiredArgsConstructor
public class UserNotificationService {

    private final EmailSenderService emailSenderService;
    private final AppUserNotificationService appUserNotificationService;
    private final SmsSenderService smsSenderService;

    public void sendUserWelcomeNotification(User user) {
        String htmlBody = """
            <html>
              <body style="font-family: sans-serif; background: #f9f9f9; padding: 20px;">
                <div style="background: #ffffff; border-radius: 8px; padding: 20px; max-width: 600px; margin: auto;">
                  <h2 style="color: #2a9d8f;">Cześć %s!</h2>
                  <p>Dziękujemy za rejestrację w <strong>Złotówce</strong>.</p>
                  <p>Jeśli masz pytania lub sugestie, napisz do nas!</p>
                </div>
              </body>
            </html>
        """.formatted(user.getFirstName());

        emailSenderService.sendHtmlEmail(new EmailDTO(
                user.getEmail(),
                "Witamy w aplikacji Złotówka!",
                htmlBody
        ));

        String category = "WELCOME";
        String body = "Witamy w aplikacji Złotówka! Możesz teraz zarządzać swoim budżetem.";

        appUserNotificationService.createNotification(
                user,
                category,
                body,
                true,
                false
        );
    }

    public void sendUserPasswordChangedEmail(String to, String firstName) {
        String htmlBody = """
            <html>
              <body style="font-family: sans-serif;">
                <h2 style="color: #e76f51;">Twoje hasło zostało zmienione</h2>
                <p>Cześć %s,</p>
                <p>To potwierdzenie, że Twoje hasło zostało właśnie zmienione.</p>
              </body>
            </html>
            """.formatted(firstName);

        emailSenderService.sendHtmlEmail(new EmailDTO(to, "Hasło zmienione", htmlBody));
    }

    public void sendUserEmailChangeNotification(User user) {
        String htmlBody = """
        <html>
          <body>
            <h2>Powiadomienia aktywowane!</h2>
            <p>Cześć %s, od teraz otrzymujesz powiadomienia e-mail.</p>
          </body>
        </html>
        """.formatted(user.getFirstName());

        emailSenderService.sendHtmlEmail(new EmailDTO(
                user.getEmail(),
                "Powiadomienia aktywowane",
                htmlBody
        ));

        String category = "NOTIFICATIONS";
        String text = "Aktywowano powiadomienia e-mail. Będziesz otrzymywać powiadomienia w aplikacji.";

        appUserNotificationService.createNotification(user, category, text, true, false);
    }

    public void sendUserPhoneChangeNotification(User user) {
        String messageText = "Złotówka: Powiadomienia SMS aktywne. Będziesz otrzymywać ważne informacje.";

        smsSenderService.sendSms(user.getPhoneNumber(), messageText);

        String category = "NOTIFICATIONS";
        String text = "Aktywowano powiadomienia SMS. Będziesz otrzymywać powiadomienia w aplikacji.";

        appUserNotificationService.createNotification(user, category, text, false, true);
    }

    public void sendForgotPasswordTokenEmail(String to, String firstName, String token) {
        String htmlBody = """
            <html>
              <body>
                <p>Cześć %s!</p>
                <p>Twój kod do resetu hasła to: <strong>%s</strong></p>
              </body>
            </html>
            """.formatted(firstName, token);

        emailSenderService.sendHtmlEmail(new EmailDTO(to, "Reset hasła w Złotówce", htmlBody));
    }
}