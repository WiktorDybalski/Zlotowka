package com.agh.zlotowka.service;

import org.springframework.stereotype.Component;

@Component
public class EmailTemplateProvider {

    public String getBalanceWarningHtml(String firstName, String day, String formattedBalance) {
        return """
            <html>
              <body style="font-family: sans-serif; background: #f9f9f9; padding: 20px;">
                <div style="background: #ffffff; border-radius: 8px; padding: 20px; max-width: 600px; margin: auto;">
                  <h2 style="color: #e63946;">Ostrzeżenie o stanie konta</h2>
                  <p>Cześć %s,</p>
                  <p>Twoje saldo %s wyniesie: <strong>%s PLN</strong>.</p>
                  <p>Prosimy o uważne zarządzanie finansami, aby uniknąć niedoboru środków.</p>
                  <hr />
                  <p style="font-size: 0.9em; color: #777;">Zespół Złotówka</p>
                </div>
              </body>
            </html>
            """.formatted(firstName, day, formattedBalance);
    }

    public String getPlanDeadlineHtml(String firstName, String planName, String messageSuffix) {
        return """
            <html>
              <body style="font-family: sans-serif; background: #f0f0f0; padding: 20px;">
                <div style="background: #ffffff; border-radius: 8px; padding: 20px; max-width: 600px; margin: auto;">
                  <h2 style="color: #457b9d;">Ostrzeżenie dotyczące marzenia</h2>
                  <p>Cześć %s,</p>
                  <p>Termin Twojego marzenia <strong>"%s"</strong> jest <strong>%s</strong>.</p>
                  <p>Prosimy o podjęcie odpowiednich działań.</p>
                  <hr />
                  <p style="font-size: 0.9em; color: #666;">Zespół Złotówka</p>
                </div>
              </body>
            </html>
            """.formatted(firstName, planName, messageSuffix);
    }

    public String getSubplanDeadlineHtml(String firstName, String subplanName, String messageSuffix) {
        return """
            <html>
              <body style="font-family: sans-serif; background: #eef2f3; padding: 20px;">
                <div style="background: #ffffff; border-radius: 8px; padding: 20px; max-width: 600px; margin: auto;">
                  <h2 style="color: #2a9d8f;">Ostrzeżenie dotyczące części marzenia</h2>
                  <p>Cześć %s,</p>
                  <p>Termin Twojej części marzenia <strong>"%s"</strong> jest <strong>%s</strong>.</p>
                  <p>Prosimy o podjęcie odpowiednich działań.</p>
                  <hr />
                  <p style="font-size: 0.9em; color: #555;">Zespół Złotówka</p>
                </div>
              </body>
            </html>
            """.formatted(firstName, subplanName, messageSuffix);
    }

    public String getUserWelcomeHtml(String firstName) {
        return """
            <html>
              <body style="font-family: sans-serif; background: #f9f9f9; padding: 20px;">
                <div style="background: #ffffff; border-radius: 8px; padding: 20px; max-width: 600px; margin: auto;">
                  <h2 style="color: #2a9d8f;">Cześć %s!</h2>
                  <p>Dziękujemy za rejestrację w <strong>Złotówce</strong>.</p>
                  <p>Jeśli masz pytania lub sugestie, napisz do nas!</p>
                </div>
              </body>
            </html>
            """.formatted(firstName);
    }

    public String getPasswordChangedHtml(String firstName) {
        return """
            <html>
              <body style="font-family: sans-serif;">
                <h2 style="color: #e76f51;">Twoje hasło zostało zmienione</h2>
                <p>Cześć %s,</p>
                <p>To potwierdzenie, że Twoje hasło zostało właśnie zmienione.</p>
              </body>
            </html>
            """.formatted(firstName);
    }

    public String getEmailChangeNotificationHtml(String firstName) {
        return """
            <html>
              <body>
                <h2>Powiadomienia aktywowane!</h2>
                <p>Cześć %s, od teraz otrzymujesz powiadomienia e-mail.</p>
              </body>
            </html>
            """.formatted(firstName);
    }

    public String getForgotPasswordTokenHtml(String firstName, String token) {
        return """
            <html>
              <body>
                <p>Cześć %s!</p>
                <p>Twój kod do resetu hasła to: <strong>%s</strong></p>
              </body>
            </html>
            """.formatted(firstName, token);
    }
}
