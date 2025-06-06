package com.agh.zlotowka.service;

import com.agh.zlotowka.config.SmsConfig;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsSenderService {

    private final SmsConfig smsConfig;

    public void sendSms(String to, String messageText) {
        Twilio.init(smsConfig.getAccountSid(), smsConfig.getAuthToken());

        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(to),
                new com.twilio.type.PhoneNumber(smsConfig.getPhoneNumber()),
                messageText
        ).create();

        System.out.println("Wysłano SMS, SID wiadomości: " + message.getSid());
    }
}

