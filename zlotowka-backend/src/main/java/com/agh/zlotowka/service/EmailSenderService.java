package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.EmailDTO;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderService {

    @Autowired
    private final JavaMailSender mailSender;

    @Value("${mail.from}")
    private String fromAddress;

    public void sendHtmlEmail(EmailDTO emailDTO) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(emailDTO.to());
            helper.setSubject(emailDTO.subject());
            helper.setText(emailDTO.body(), true);

            helper.setFrom(fromAddress);

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Błąd podczas wysyłania emaila HTML do {}: {}", emailDTO.to(), e.getMessage(), e);
        }
    }
}
