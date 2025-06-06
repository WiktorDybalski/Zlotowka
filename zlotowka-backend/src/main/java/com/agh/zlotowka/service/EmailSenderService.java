package com.agh.zlotowka.service;

import com.agh.zlotowka.config.MailConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderService {

    private final JavaMailSender mailSender;
    private final MailConfig mailConfig;

    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            message.setFrom(mailConfig.getFromAddress());

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Error while sending email to " + to, e);
        }
    }
}
