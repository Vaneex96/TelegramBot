package com.example.dispatcher.service.impl;

import com.example.dispatcher.controller.UpdateProcessor;
import com.example.dispatcher.service.AnswerConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.example.dispatcher.configuration.RabbitQueue.ANSWER_MESSAGE;

@Service
@RequiredArgsConstructor
public class AnswerConsumerImpl implements AnswerConsumer {

    private final UpdateProcessor updateController;

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consume(SendMessage sendMessage) {
        updateController.setView(sendMessage);
    }
}
