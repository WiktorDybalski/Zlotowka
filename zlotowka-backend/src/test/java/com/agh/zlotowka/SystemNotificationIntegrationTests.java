package com.agh.zlotowka;

import com.agh.zlotowka.repository.*;
import com.agh.zlotowka.service.SystemNotificationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@Sql(statements = {
        "INSERT INTO currencies(currency_id, iso_code) VALUES (1, 'PLN') " +
                "ON CONFLICT (currency_id) DO UPDATE SET iso_code = 'PLN'",

        "INSERT INTO users(user_id, currency_id, email, first_name, last_name, password, date_of_joining, current_budget, dark_mode, notifications_by_email, notifications_by_phone, phone_number) " +
                "VALUES (100, 1, 'wojfortuna@gmail.com', 'Tester', 'Testowy', 'testpassword', CURRENT_DATE, -5.00, false, true, true, '+15074105639') " +
                "ON CONFLICT (user_id) DO UPDATE SET email = 'wojfortuna@gmail.com'",

        "INSERT INTO one_time_transactions(transaction_id, user_id, name, amount, currency_id, is_income, date, description) " +
                "VALUES (42, 100, 'Transakcja testowa', 10.00, 1, false, CURRENT_DATE, 'Testowa transakcja') " +
                "ON CONFLICT (transaction_id) DO NOTHING",

        "INSERT INTO plans(plan_id, user_id, currency_id, name, required_amount, date, completed, subplans_completed) " +
                "VALUES (764, 100, 1, 'Testowy Plan', 50.00, CURRENT_DATE - INTERVAL '1 day', false, 0.0) " +
                "ON CONFLICT (plan_id) DO NOTHING",

        "INSERT INTO subplans(subplan_id, plan_id, name, required_amount, date, completed, transaction_id) " +
                "VALUES (455, 764, 'Testowy Subplan', 20.00, CURRENT_DATE - INTERVAL '1 day', false, 42) " +
                "ON CONFLICT (subplan_id) DO NOTHING"
})

@SpringBootTest
public class SystemNotificationIntegrationTests {

    @Autowired
    private SystemNotificationService systemNotificationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private OneTimeTransactionRepository oneTimeTransactionRepository;

    @Autowired
    private SubPlanRepository subPlanRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private SystemNotificationRepository systemNotificationRepository;

    @Autowired
    private AppUserNotificationRepository appUserNotificationRepository;

    @AfterEach
    void cleanup() {
        appUserNotificationRepository.deleteAll();
        systemNotificationRepository.deleteAll();
        subPlanRepository.deleteAll();
        planRepository.deleteAll();
        oneTimeTransactionRepository.deleteAll();
        userRepository.deleteAll();
        currencyRepository.deleteAll();
    }

    @Test
    void testSendRealBalanceWarningEmail() {
        systemNotificationService.sendBalanceWarningEmail();
    }

    @Test
    void testSendRealDeadlineWarningEmail() {
        systemNotificationService.sendDeadlineWarnings();
    }
}