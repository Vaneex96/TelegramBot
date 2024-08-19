package com.example.node.service.impl;

import com.example.node.dao.enums.UserState;
import com.example.node.dto.TransferDataBetweenNodeAndParserDto;
import com.example.node.entity.MailSentResponse;
import com.example.node.service.ConsumerService;
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
    private final MainServiceImpl mainService;

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessagesUpdate(Update update) {
//        if(update.hasCallbackQuery()){
//            log.debug("NODE: Text message is received: " + update.getCallbackQuery().getMessage());
//        }
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

    @Override
    @RabbitListener(queues = REGISTRATION_MAIL_SEND_RESULT)
    public void consumeRegistrationMailSendResult(MailSentResponse response) {
        log.debug("NODE: REGISTRATION_MAIL_SEND_RESULT message is received");
        mainService.sendAnswer(response.getResponse(), String.valueOf(response.getChatId()));
    }

    @Override
    @RabbitListener(queues = SEARCHED_SERIES_RELEASE_TO_PARSE_RESPONSE)
    public void consumeSearchedSeriesReleaseToParseResponse(TransferDataBetweenNodeAndParserDto dto) {
        try{
            mainService.processSearchedSeriesResponse(dto);
        }catch(NullPointerException e){
            System.out.println(e.getMessage());
            log.error(e);
        }
    }

}
