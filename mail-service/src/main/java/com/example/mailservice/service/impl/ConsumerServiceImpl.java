package com.example.mailservice.service.impl;

import com.example.commonrabbitmq.RabbitQueue;
import com.example.mailservice.dto.MailParams;
import com.example.mailservice.service.ConsumerService;
import com.example.mailservice.service.MailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {

    private final MailSenderService mailSenderService;

    @Override
    @RabbitListener(queues = RabbitQueue.REGISTRATION_MAIL_MESSAGE)
    public void consumeRegistrationMail(MailParams mailParams) {
        mailSenderService.send(mailParams);
    }
}
