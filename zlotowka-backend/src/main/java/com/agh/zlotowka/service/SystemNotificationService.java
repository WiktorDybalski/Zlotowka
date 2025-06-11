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
    private final AppUserNotificationService appUserNotificationService;
    private final EmailTemplateProvider templateProvider;

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

    public void checkUserBalanceAndSendWarning(User user) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        try {
            BigDecimal todayBalance = generalTransactionService.getEstimatedBalanceForDate(user.getUserId(), today);
            if (todayBalance.compareTo(THRESHOLD) < 0) {
                sendBalanceEmailToUser(user, todayBalance, "dzisiaj");
                return;
            }

            BigDecimal tomorrowBalance = generalTransactionService.getEstimatedBalanceForDate(user.getUserId(), tomorrow);
            if (tomorrowBalance.compareTo(THRESHOLD) < 0) {
                sendBalanceEmailToUser(user, tomorrowBalance, "jutro");
            }
        } catch (Exception e) {
            log.error("Błąd podczas sprawdzania salda użytkownika " + user.getUserId(), e);
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
        String formattedBalance = new DecimalFormat("#,##0.00").format(balance);

        String htmlBody = templateProvider.getBalanceWarningHtml(
                user.getFirstName(), day, formattedBalance);

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
            String smsText = String.format(
                    "Złotówka: saldo %s wyniesie %s PLN. Sprawdź budżet!",
                    day, formattedBalance);
            sendSms(user.getPhoneNumber(), smsText);
        }

        createNotification(user, "Saldo",
                String.format("Saldo %s wyniesie: %s PLN", day, formattedBalance),
                sendEmail, sendSms);
    }

    private void sendPlanDeadlineEmail(Plan plan, String messageSuffix) {
        User user = plan.getUser();
        boolean sendEmail = Boolean.TRUE.equals(user.getNotificationsByEmail());
        boolean sendSms = Boolean.TRUE.equals(user.getNotificationsByPhone());

        String subject = "Ostrzeżenie dotyczące marzenia";
        String planNameFull = plan.getName();
        String planNameShort = shorten(planNameFull, 20);

        String htmlBody = templateProvider.getPlanDeadlineHtml(
                user.getFirstName(), planNameFull, messageSuffix);

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
            String smsText = String.format(
                    "Złotówka: marzenia \"%s\" – %s. Sprawdź aplikację.",
                    planNameShort, messageSuffix);
            sendSms(user.getPhoneNumber(), smsText);
        }

        createNotification(user, "Marzenie",
                String.format("Marzenie \"%s\" – %s", planNameFull, messageSuffix),
                sendEmail, sendSms);
    }

    private void sendSubplanDeadlineEmail(Subplan subplan, String messageSuffix) {
        User user = subplan.getPlan().getUser();
        boolean sendEmail = Boolean.TRUE.equals(user.getNotificationsByEmail());
        boolean sendSms = Boolean.TRUE.equals(user.getNotificationsByPhone());

        String subject = "Ostrzeżenie dotyczące części marzenia";
        String subplanNameFull = subplan.getName();
        String subplanNameShort = shorten(subplanNameFull, 20);

        String htmlBody = templateProvider.getSubplanDeadlineHtml(
                user.getFirstName(), subplanNameFull, messageSuffix);

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
            String smsText = String.format(
                    "Złotówka: marzenia \"%s\" – %s. Sprawdź aplikację.",
                    subplanNameShort, messageSuffix);
            sendSms(user.getPhoneNumber(), smsText);
        }

        createNotification(user, "Marzenia",
                String.format("Część marzenia \"%s\" – %s", subplanNameFull, messageSuffix),
                sendEmail, sendSms);
    }

    private void createNotification(User user, String category,
                                    String text, boolean byEmail, boolean byPhone) {
        try {
            appUserNotificationService.createNotification(user, category, text, byEmail, byPhone);
        } catch (Exception e) {
            log.error("Błąd podczas zapisu do bazy: {}", e.getMessage(), e);
        }
    }
}
