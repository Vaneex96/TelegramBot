package com.example.dispatcher.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MessageUtils {

    public SendMessage generateSendMessageWithText(Update update, String text){
        Message message;
        if(update.hasCallbackQuery()){
            message = update.getCallbackQuery().getMessage();
        } else {
            message = update.getMessage();
        }

        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(text)
                .build();
    }

}
