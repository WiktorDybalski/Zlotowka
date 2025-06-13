package com.agh.zlotowka.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SmsSenderService {

    @Value("${brevo.api-key}")
    private String apiKey;

    @Value("${brevo.sender}")
    private String sender;

    private static final String BREVO_SMS_URL = "https://api.brevo.com/v3/transactionalSMS/sms";

    public void sendSms(String to, String messageText) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("sender", sender);
            body.put("recipient", to);
            body.put("content", messageText);
            body.put("type", "transactional");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    BREVO_SMS_URL,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("SMS do {} wysłany pomyślnie", to);
            } else {
                log.error("Błąd podczas wysyłania SMS do {}: {}", to, response.getBody());
            }
        } catch (Exception e) {
            log.error("Wyjątek podczas wysyłania SMS do {}: {}", to, e.getMessage());
        }
    }
}
