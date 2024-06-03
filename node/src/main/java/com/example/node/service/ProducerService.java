package com.example.node.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ProducerService {
    void producerService(SendMessage sendMessage);
}
