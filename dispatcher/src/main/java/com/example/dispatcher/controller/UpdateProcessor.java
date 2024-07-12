package com.example.dispatcher.controller;

import com.example.dispatcher.service.UpdateProducer;
import com.example.dispatcher.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.example.commonrabbitmq.RabbitQueue.*;

@Component
@Log4j
@RequiredArgsConstructor
public class UpdateProcessor {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final UpdateProducer updateProducer;

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update){
        if(update == null){
            log.error("Received update is null");
            return;
        }

        if(update.hasMessage()){
            distributeMessagesByType(update);
        } else {
            log.error("Received unsupported message type");
        }

    }

    private void distributeMessagesByType(Update update) {
        Message message = update.getMessage();

        if(message.hasText()){
            processTextMessage(update);
        } else if(message.hasDocument()){
            processDocMessage(update);
        } else if(message.hasPhoto()){
            processPhotoMessage(update);
        } else {
            setUnsupportedMessageTypeView(update);
        }

    }

    private void setUnsupportedMessageTypeView(Update update) {
        SendMessage sendMessage = messageUtils.generateSendMessageWithText(update,
                "Unsupported message type");
        setView(sendMessage);
    }

    private void setFileIsReceivedView(Update update){
        SendMessage sendMessage = messageUtils.generateSendMessageWithText(update,
                "File received. Processing...");

        setView(sendMessage);
    }

    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    public void processPhotoMessage(Update update) {
        updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }

    public void processDocMessage(Update update) {
        updateProducer.produce(DOC_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }

    public void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
//        setFileIsReceivedView(update);
    }

}
