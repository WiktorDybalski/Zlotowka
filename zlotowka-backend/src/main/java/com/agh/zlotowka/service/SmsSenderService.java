package com.agh.zlotowka.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsSenderService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;

    public void sendSms(String to, String messageText) {
        try {
            Twilio.init(accountSid, authToken);

            Message.creator(
                    new com.twilio.type.PhoneNumber(to),
                    new com.twilio.type.PhoneNumber(twilioPhoneNumber),
                    messageText
            ).create();
        } catch (Exception e) {
            log.error("Błąd podczas wysyłania SMS do {}: {}", to, e.getMessage());
        }
    }
}
