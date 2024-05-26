package com.example.dispatcher.controller;

import com.example.dispatcher.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Log4j
@RequiredArgsConstructor
public class UpdateController {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update){
        if(update != null){
            log.error("Received update is null");
            return;
        }

        if(update.getMessage() != null){
            distributeMessagesByType(update);
        } else {
            log.error("Received unsupported message type");
        }

    }

    private void distributeMessagesByType(Update update) {
        Message message = update.getMessage();

        if(message.getText() != null){
            processTextMessage(message);
        } else if(message.getDocument() != null){
            processDocMessage(message);
        } else if(message.getPhoto() != null){
            processPhotoMessage(message);
        } else {
            setUnsupportedMessageTypeView(update);
        }

    }

    private void setUnsupportedMessageTypeView(Update update) {
        SendMessage sendMessage = messageUtils.generateSendMessageWithText(update,
                "Unsupported message type");
        setView(sendMessage);
    }

    private void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    private void processPhotoMessage(Message message) {
    }

    private void processDocMessage(Message message) {
    }

    private void processTextMessage(Message message) {
    }
}
