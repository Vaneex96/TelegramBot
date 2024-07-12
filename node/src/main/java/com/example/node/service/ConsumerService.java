package com.example.node.service;

import com.example.node.entity.MailSentResponse;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.example.commonrabbitmq.RabbitQueue.REGISTRATION_MAIL_SEND_RESULT;

public interface ConsumerService {
    void consumeTextMessagesUpdate(Update update);
    void consumePhotoMessagesUpdate(Update update);
    void consumeDocMessagesUpdate(Update update);

    void consumeRegistrationMailSendResult(MailSentResponse response);
}
