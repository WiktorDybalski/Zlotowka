package com.agh.zlotowka.service;

import com.agh.zlotowka.config.SmsConfig;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsSenderService {

    private final SmsConfig smsConfig;

    public void sendSms(String to, String messageText) {
        try {
            Twilio.init(smsConfig.getAccountSid(), smsConfig.getAuthToken());

            Message.creator(
                    new com.twilio.type.PhoneNumber(to),
                    new com.twilio.type.PhoneNumber(smsConfig.getPhoneNumber()),
                    messageText
            ).create();
        } catch (Exception e) {
            log.error("Error while sending SMS to {}: {}", to, e.getMessage());
        }
    }
}


