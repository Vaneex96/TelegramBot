package com.example.node.service.impl;

import com.example.node.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.example.commonrabbitmq.RabbitQueue.ANSWER_MESSAGE;

@Service
@RequiredArgsConstructor
public class ProducerServiceImpl implements ProducerService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void producerService(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
    }
}
