package com.example.node.service.impl;

import com.example.node.dao.RawDataDAO;
import com.example.node.entity.RawData;
import com.example.node.service.MainService;
import com.example.node.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;


    @Override
    public void processTextMessage(Update update) {
        RawData rawData = RawData.builder()
                        .event(update)
                        .build();

        rawDataDAO.save(rawData);

        Message message = update.getMessage();
        SendMessage sendMessage = SendMessage.builder()
                .chatId(message.getChatId())
                .text("Hello from Node (TEXT)")
                .build();

        producerService.producerService(sendMessage);
    }
}
