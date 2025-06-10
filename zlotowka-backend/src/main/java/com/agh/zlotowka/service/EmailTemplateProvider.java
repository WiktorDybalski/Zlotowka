package com.agh.zlotowka.service;

import org.springframework.stereotype.Component;

@Component
public class EmailTemplateProvider {


    private String buildTemplate(String headingColor,
                                 String headingText,
                                 String bodyHtml) {
        return String.format("""
            <html>
              <body style="font-family: sans-serif; background: #f9f9f9; padding: 20px;">
                <div style="background: #ffffff; border-radius: 8px; padding: 20px;
                            max-width: 600px; margin: auto;">
                  <h2 style="color: %s;">%s</h2>
                  %s
                  <hr/>
                  <p style="font-size:0.9em; color:#777;">Zespół Złotówka</p>
                </div>
              </body>
            </html>
            """, headingColor, headingText, bodyHtml);
    }

    public String getBalanceWarningHtml(String firstName,
                                        String day,
                                        String formattedBalance) {
        String color = formattedBalance.trim().startsWith("-")
                ? "#e63946"
                : "#2a9d8f";
        String body = String.format("""
            <p>Cześć %s,</p>
            <p>Twoje saldo %s wyniesie: <strong>%s PLN</strong>.</p>
            <p>Prosimy o uważne zarządzanie finansami, aby uniknąć niedoboru środków.</p>
            """, firstName, day, formattedBalance);
        return buildTemplate(color, "Ostrzeżenie o stanie konta", body);
    }

    public String getPlanDeadlineHtml(String firstName,
                                      String planName,
                                      String messageSuffix) {
        String body = String.format("""
            <p>Cześć %s,</p>
            <p>Termin Twojego marzenia <strong>"%s"</strong> jest <strong>%s</strong>.</p>
            <p>Prosimy o podjęcie odpowiednich działań.</p>
            """, firstName, planName, messageSuffix);
        return buildTemplate("#457b9d", "Ostrzeżenie dotyczące marzenia", body);
    }

    public String getSubplanDeadlineHtml(String firstName,
                                         String subplanName,
                                         String messageSuffix) {
        String body = String.format("""
            <p>Cześć %s,</p>
            <p>Termin Twojej części marzenia <strong>"%s"</strong> jest <strong>%s</strong>.</p>
            <p>Prosimy o podjęcie odpowiednich działań.</p>
            """, firstName, subplanName, messageSuffix);
        return buildTemplate("#2a9d8f", "Ostrzeżenie dotyczące części marzenia", body);
    }

    public String getUserWelcomeHtml(String firstName) {
        String body = String.format("""
            <p>Cześć %s!</p>
            <p>Dziękujemy za rejestrację w <strong>Złotówce</strong>.</p>
            <p>Jeśli masz pytania lub sugestie, napisz do nas!</p>
            """, firstName);
        return buildTemplate("#2a9d8f", "Witamy w Złotówce!", body);
    }

    public String getPasswordChangedHtml(String firstName) {
        String body = String.format("""
            <p>Cześć %s,</p>
            <p>To potwierdzenie, że Twoje hasło zostało właśnie zmienione.</p>
            """, firstName);
        return buildTemplate("#f4a261", "Hasło zostało zmienione", body);
    }

    public String getEmailChangeNotificationHtml(String firstName) {
        String body = String.format("""
            <p>Cześć %s, od teraz otrzymujesz powiadomienia e-mail.</p>
            """, firstName);
        return buildTemplate("#2a9d8f", "Powiadomienia e-mail aktywowane", body);
    }

    public String getForgotPasswordTokenHtml(String firstName, String token) {
        String body = String.format("""
            <p>Cześć %s!</p>
            <p>Twój kod do resetu hasła to: <strong>%s</strong></p>
            """, firstName, token);
        return buildTemplate("#f4a261", "Reset hasła w Złotówce", body);
    }
}
