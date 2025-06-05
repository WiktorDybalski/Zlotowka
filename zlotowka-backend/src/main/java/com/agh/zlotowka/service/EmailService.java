package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.EmailDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;


    public void sendEmail(EmailDTO email) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("mail@example.com");
            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            helper.setText(email.getBody(), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Błąd przy wysyłaniu maila: " + e.getMessage(), e);
        }
    }


    public void sendUserWelcomeEmail(String to, String firstName) {
        String htmlBody = """
            <html>
              <body style="font-family: sans-serif; background: #f9f9f9; padding: 20px;">
                <div style="background: #ffffff; border-radius: 8px; padding: 20px; max-width: 600px; margin: auto;">
                  <h2 style="color: #2a9d8f;">Cześć %s!</h2>
                  <p>Dziękujemy za rejestrację w <strong>Złotówce</strong>. Od teraz możesz łatwiej zarządzać swoim budżetem.</p>
                  <p>Jeśli masz pytania lub sugestie, napisz do nas!</p>
                  <hr />
                  <p style="font-size: 0.9em; color: #999;">To jest wiadomość automatyczna – prosimy na nią nie odpowiadać.</p>
                </div>
              </body>
            </html>
            """.formatted(firstName);

        sendEmail(EmailDTO.builder()
                .to(to)
                .subject("Witamy w aplikacji Złotówka!")
                .body(htmlBody)
                .build());
    }

    public void sendUserPasswordChangedEmail(String to, String firstName) {
        String htmlBody = """
            <html>
              <body style="font-family: sans-serif; background: #fdfdfd; padding: 20px;">
                <div style="background: #fff; border: 1px solid #eee; border-radius: 8px; padding: 20px; max-width: 600px; margin: auto;">
                  <h2 style="color: #e76f51;">Twoje hasło zostało zmienione</h2>
                  <p>Cześć %s,</p>
                  <p>To potwierdzenie, że Twoje hasło w aplikacji Złotówka zostało właśnie zmienione.</p>
                  <p>Jeśli to nie Ty dokonałeś zmiany – <strong>skontaktuj się z nami natychmiast</strong>.</p>
                  <hr />
                  <p style="font-size: 0.9em; color: #aaa;">Zespół Złotówka</p>
                </div>
              </body>
            </html>
            """.formatted(firstName);

        sendEmail(EmailDTO.builder()
                .to(to)
                .subject("Hasło zmienione")
                .body(htmlBody)
                .build());
    }

    public void sendUserOptInWelcomeEmail(String to, String firstName) {
        String htmlBody = """
            <html>
              <body style="font-family: sans-serif; background: #f0f8ff; padding: 20px;">
                <div style="background: #ffffff; border-left: 5px solid #2a9d8f; padding: 20px; max-width: 600px; margin: auto;">
                  <h2 style="color: #2a9d8f;">Powiadomienia aktywowane!</h2>
                  <p>Cześć %s,</p>
                  <p>Właśnie aktywowałeś powiadomienia e-mail w aplikacji Złotówka.</p>
                  <p>Od teraz będziesz otrzymywać automatyczne przypomnienia i ważne informacje o Twoich finansach.</p>
                  <p>Możesz je wyłączyć w każdej chwili w ustawieniach profilu.</p>
                  <hr />
                  <p style="font-size: 0.9em; color: #777;">Dziękujemy, że jesteś z nami!</p>
                </div>
              </body>
            </html>
            """.formatted(firstName);

        sendEmail(EmailDTO.builder()
                .to(to)
                .subject("Powiadomienia e-mail zostały aktywowane")
                .body(htmlBody)
                .build());
    }
    public void sendForgotPasswordTokenEmail(String to, String firstName, String token) {
        String htmlBody = """
        <html>
          <body style="font-family: sans-serif; background: #f9f9f9; padding: 20px;">
            <div style="background: #ffffff; border-radius: 8px; padding: 20px; max-width: 600px; margin: auto;">
              <h2 style="color: #264653;">Cześć %s!</h2>
              <p>Otrzymaliśmy prośbę o zresetowanie hasła.</p>
              <p>Twój kod to: <strong style="font-size:1.5em;">%s</strong></p>
              <p>Kod jest ważny 15 minut.</p>
              <hr/>
              <p style="font-size:0.9em; color:#999;">Jeśli nie prosiłeś o zmianę, zignoruj ten email.</p>
            </div>
          </body>
        </html>
        """.formatted(firstName, token);

        sendEmail(EmailDTO.builder()
                .to(to)
                .subject("Reset hasła w Złotówce")
                .body(htmlBody)
                .build()
        );
    }
}
