package com.example.node.service.impl;

import com.example.node.service.ConsumerService;
import com.example.node.service.MainService;
import com.example.node.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.example.commonrabbitmq.RabbitQueue.*;

@Service
@Log4j
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {

    private final ProducerService producerService;
    private final MainService mainService;

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessagesUpdate(Update update) {
        log.debug("NODE: Text message is received");
        mainService.processTextMessage(update);
    }

    @Override
    @RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
    public void consumePhotoMessagesUpdate(Update update) {
        log.debug("NODE: Photo message is received");
        mainService.processPhotoMessage(update);
    }

    @Override
    @RabbitListener(queues = DOC_MESSAGE_UPDATE)
    public void consumeDocMessagesUpdate(Update update) {
        log.debug("NODE: Doc message is received");
        mainService.processDocMessage(update);
    }
}
