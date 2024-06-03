package com.example.node.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ConsumerService {
    void consumeTextMessagesUpdate(Update update);
    void consumePhotoMessagesUpdate(Update update);
    void consumeDocMessagesUpdate(Update update);
}
