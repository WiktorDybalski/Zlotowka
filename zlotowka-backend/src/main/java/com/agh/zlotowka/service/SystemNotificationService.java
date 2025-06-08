package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.EmailDTO;
import com.agh.zlotowka.model.*;
import com.agh.zlotowka.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SystemNotificationService {

    private final UserRepository userRepository;
    private final GeneralTransactionService generalTransactionService;
    private final PlanRepository planRepository;
    private final SubPlanRepository subPlanRepository;
    private final EmailSenderService emailSenderService;
    private final SmsSenderService smsSenderService;
    private final SystemNotificationRepository notificationRepository;
    private final AppUserNotificationService appUserNotificationService;

    private final BigDecimal THRESHOLD = BigDecimal.ZERO;

    @Scheduled(cron = "0 0 8 * * ?")
    public void sendBalanceWarningEmail() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        List<User> users = userRepository.findAll();
        for (User user : users) {
            try {
                BigDecimal todayBalance = generalTransactionService.getEstimatedBalanceForDate(user.getUserId(), today);
                if (todayBalance.compareTo(THRESHOLD) < 0) {
                    sendBalanceEmailToUser(user, todayBalance, "dzisiaj");
                    continue;
                }

                BigDecimal tomorrowBalance = generalTransactionService.getEstimatedBalanceForDate(user.getUserId(), tomorrow);
                if (tomorrowBalance.compareTo(THRESHOLD) < 0) {
                    sendBalanceEmailToUser(user, tomorrowBalance, "jutro");
                }
            } catch (Exception e) {
                log.error("Błąd podczas sprawdzania salda użytkownika " + user.getUserId(), e);
            }
        }
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void sendDeadlineWarnings() {
        LocalDate today = LocalDate.now();
        LocalDate weekAhead = today.plusDays(7);

        List<Plan> plans = planRepository.findAll();
        for (Plan plan : plans) {
            if (!plan.getCompleted() && plan.getDate() != null) {
                if (plan.getDate().isBefore(today)) {
                    sendPlanDeadlineEmail(plan, "przekroczony termin");
                } else if (plan.getDate().isEqual(weekAhead)) {
                    sendPlanDeadlineEmail(plan, "termin za tydzień");
                }
            }
        }

        List<Subplan> subplans = subPlanRepository.findAll();
        for (Subplan subplan : subplans) {
            if (!subplan.getCompleted() && subplan.getDate() != null) {
                if (subplan.getDate().isBefore(today)) {
                    sendSubplanDeadlineEmail(subplan, "przekroczony termin");
                } else if (subplan.getDate().isEqual(weekAhead)) {
                    sendSubplanDeadlineEmail(subplan, "termin za tydzień");
                }
            }
        }
    }

    public List<SystemNotificationModel> fetchUserNotifications(User user) {
        return notificationRepository.findAllByUserOrderByCreatedAtDesc(user);
    }

    public void deleteByIdAndUser(Integer id, User user) {
        SystemNotificationModel notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono powiadomienia"));

        if (!notification.getUser().getUserId().equals(user.getUserId())) {
            throw new SecurityException("Brak dostępu");
        }

        notificationRepository.delete(notification);
    }

    private void sendSms(String phoneNumber, String messageText) {
        try {
            smsSenderService.sendSms(phoneNumber, messageText);
        } catch (Exception e) {
            log.error("Wystąpił błąd podczas wysyłania wiadomości SMS do {}: {}", phoneNumber, e.getMessage());
        }
    }

    private String shorten(String text, int maxLength) {
        return (text != null && text.length() > maxLength)
                ? text.substring(0, maxLength - 3) + "..."
                : text;
    }

    private void sendBalanceEmailToUser(User user, BigDecimal balance, String day) {
        boolean sendEmail = Boolean.TRUE.equals(user.getNotificationsByEmail());
        boolean sendSms = Boolean.TRUE.equals(user.getNotificationsByPhone());

        String subject = "Ostrzeżenie o stanie konta";
        DecimalFormat df = new DecimalFormat("#,##0.00");
        String formattedBalance = df.format(balance);

        String htmlBody = """
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
        """.formatted(user.getFirstName(), day, formattedBalance);

        if (sendEmail) {
            emailSenderService.sendHtmlEmail(
                    EmailDTO.builder()
                            .to(user.getEmail())
                            .subject(subject)
                            .body(htmlBody)
                            .build()
            );
        }

        if (sendSms) {
            String smsText = String.format("Złotówka: saldo %s wyniesie %s PLN. Sprawdź budżet!", day, formattedBalance);
            sendSms(user.getPhoneNumber(), smsText);
        }

        createNotification(user, "Saldo", String.format("Saldo %s wyniesie: %s PLN", day, formattedBalance), sendEmail, sendSms);
    }

    private void sendPlanDeadlineEmail(Plan plan, String messageSuffix) {
        User user = plan.getUser();
        boolean sendEmail = Boolean.TRUE.equals(user.getNotificationsByEmail());
        boolean sendSms = Boolean.TRUE.equals(user.getNotificationsByPhone());

        String subject = "Ostrzeżenie dotyczące marzenia";
        String planNameShort = shorten(plan.getName(), 20);
        String planNameFull = plan.getName();

        String htmlBody = """
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
        """.formatted(user.getFirstName(), planNameFull, messageSuffix);

        if (sendEmail) {
            emailSenderService.sendHtmlEmail(
                    EmailDTO.builder()
                            .to(user.getEmail())
                            .subject(subject)
                            .body(htmlBody)
                            .build()
            );
        }

        if (sendSms) {
            String smsText = String.format("Złotówka: marzenia \"%s\" – %s. Sprawdź aplikację.", planNameShort, messageSuffix);
            sendSms(user.getPhoneNumber(), smsText);
        }

        createNotification(user, "Marzenie", String.format("Marzenie \"%s\" – %s", planNameFull, messageSuffix), sendEmail, sendSms);
    }

    private void sendSubplanDeadlineEmail(Subplan subplan, String messageSuffix) {
        User user = subplan.getPlan().getUser();
        boolean sendEmail = Boolean.TRUE.equals(user.getNotificationsByEmail());
        boolean sendSms = Boolean.TRUE.equals(user.getNotificationsByPhone());

        String subject = "Ostrzeżenie dotyczące części marzenia";
        String subplanNameShort = shorten(subplan.getName(), 20);
        String subplanNameFull = subplan.getName();

        String htmlBody = """
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
        """.formatted(user.getFirstName(), subplanNameFull, messageSuffix);

        if (sendEmail) {
            emailSenderService.sendHtmlEmail(
                    EmailDTO.builder()
                            .to(user.getEmail())
                            .subject(subject)
                            .body(htmlBody)
                            .build()
            );
        }

        if (sendSms) {
            String smsText = String.format("Złotówka: marzenia \"%s\" – %s. Sprawdź aplikację.", subplanNameShort, messageSuffix);
            sendSms(user.getPhoneNumber(), smsText);
        }

        createNotification(user, "Marzenia", String.format("Część marzenia \"%s\" – %s", subplanNameFull, messageSuffix), sendEmail, sendSms);
    }

    private void createNotification(User user, String category, String text, boolean byEmail, boolean byPhone) {
        try {
            SystemNotificationModel notification = SystemNotificationModel.builder()
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .category(category)
                    .text(text)
                    .byEmail(byEmail)
                    .byPhone(byPhone)
                    .build();
            notificationRepository.save(notification);
        } catch (Exception e) {
            log.error("Błąd podczas zapisu do system_notification: {}", e.getMessage(), e);
        }

        try {
            appUserNotificationService.createNotification(user, category, text, true, false);
        } catch (Exception e) {
            log.error("Błąd podczas zapisu do app_user_notification: {}", e.getMessage(), e);
        }
    }
}