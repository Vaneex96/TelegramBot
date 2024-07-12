package com.example.mailservice.service.impl;

import com.example.mailservice.dto.MailSentResponse;
import com.example.mailservice.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.example.commonrabbitmq.RabbitQueue.REGISTRATION_MAIL_SEND_RESULT;

@RequiredArgsConstructor
@Service
public class ProducerServiceImpl implements ProducerService {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void produceMailSendResult(MailSentResponse response) {
        rabbitTemplate.convertAndSend(REGISTRATION_MAIL_SEND_RESULT, response);
    }
}
