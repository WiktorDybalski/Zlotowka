package com.agh.zlotowka.service;

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
                log.error("Error while checking balance for user " + user.getUserId(), e);
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
            log.error("Error while sending SMS to {}: {}", phoneNumber, e.getMessage());
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
        if (!sendEmail && !sendSms) return;

        String subject = "Ostrzeżenie o stanie konta";
        DecimalFormat df = new DecimalFormat("#,##0.00");
        String formattedBalance = df.format(balance);

        String text = String.format(
                "Cześć %s,\n\n" +
                        "Twoje saldo %s wyniesie: %s PLN.\n" +
                        "Prosimy o uważne zarządzanie finansami, aby uniknąć niedoboru środków.\n\n" +
                        "Pozdrawiamy,\nZespół Złotówka",
                user.getFirstName(), day, formattedBalance);

        String smsText = String.format("Złotówka: saldo %s wyniesie %s PLN. Sprawdź budżet!", day, formattedBalance);

        if (sendEmail) {
            emailSenderService.sendEmail(user.getEmail(), subject, text);
        }
        if (sendSms) {
            sendSms(user.getPhoneNumber(), smsText);
        }

        createNotification(user, "Saldo", String.format("Saldo %s wyniesie: %s PLN", day, formattedBalance), sendEmail, sendSms);
    }

    private void sendPlanDeadlineEmail(Plan plan, String messageSuffix) {
        User user = plan.getUser();
        boolean sendEmail = Boolean.TRUE.equals(user.getNotificationsByEmail());
        boolean sendSms = Boolean.TRUE.equals(user.getNotificationsByPhone());

        if (!sendEmail && !sendSms) return;

        String toEmail = user.getEmail();
        String userName = user.getFirstName();
        String subject = "Ostrzeżenie dotyczące planu";
        String text = String.format(
                "Cześć %s,\n\nTermin Twojego planu \"%s\" jest %s.\nProsimy o podjęcie odpowiednich działań.\n\nPozdrawiamy,\nZespół Złotówka",
                userName, plan.getName(), messageSuffix);
        String planName = shorten(plan.getName(), 20);
        String smsText = String.format("Złotówka: plan \"%s\" – %s. Sprawdź aplikację.", planName, messageSuffix);

        if (sendEmail) {
            emailSenderService.sendEmail(toEmail, subject, text);
        }
        if (sendSms) {
            sendSms(user.getPhoneNumber(), smsText);
        }

        createNotification(user, "Plan", String.format("Plan \"%s\" – %s", plan.getName(), messageSuffix), sendEmail, sendSms);
    }

    private void sendSubplanDeadlineEmail(Subplan subplan, String messageSuffix) {
        User user = subplan.getPlan().getUser();
        boolean sendEmail = Boolean.TRUE.equals(user.getNotificationsByEmail());
        boolean sendSms = Boolean.TRUE.equals(user.getNotificationsByPhone());

        if (!sendEmail && !sendSms) return;

        String toEmail = user.getEmail();
        String userName = user.getFirstName();
        String subject = "Ostrzeżenie dotyczące części planu";
        String text = String.format(
                "Cześć %s,\n\nTermin Twojej części planu \"%s\" jest %s.\nProsimy o podjęcie odpowiednich działań.\n\nPozdrawiamy,\nZespół Złotówka",
                userName, subplan.getName(), messageSuffix);
        String name = shorten(subplan.getName(), 20);
        String smsText = String.format("Złotówka: subplan \"%s\" – %s. Sprawdź aplikację.", name, messageSuffix);

        if (sendEmail) {
            emailSenderService.sendEmail(toEmail, subject, text);
        }
        if (sendSms) {
            sendSms(user.getPhoneNumber(), smsText);
        }

        createNotification(user, "Subplan", String.format("Część planu \"%s\" – %s", subplan.getName(), messageSuffix), sendEmail, sendSms);
    }

    private void createNotification(User user, String category, String text, boolean byEmail, boolean byPhone) {
        SystemNotificationModel notification = SystemNotificationModel.builder()
                .user(user)
                .createdAt(LocalDateTime.now())
                .category(category)
                .text(text)
                .byEmail(byEmail)
                .byPhone(byPhone)
                .build();
        notificationRepository.save(notification);

        if (Boolean.TRUE.equals(byEmail)) {
            appUserNotificationService.createNotification(user, category, text, true, false);
        }
    }
}
