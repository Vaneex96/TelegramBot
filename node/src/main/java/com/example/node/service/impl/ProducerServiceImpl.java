package com.example.node.service.impl;

import com.example.node.dto.TransferDataBetweenNodeAndParserDto;
import com.example.node.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.example.commonrabbitmq.RabbitQueue.*;

@Service
@RequiredArgsConstructor
public class ProducerServiceImpl implements ProducerService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void producerService(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
    }

    @Override
    public void produceSearchingSeries(TransferDataBetweenNodeAndParserDto searchingSeriesToParseDto) {
        rabbitTemplate.convertAndSend(SEARCHING_SERIES_RELEASE_TO_PARSE, searchingSeriesToParseDto);
    }

    @Override
    public void produceParsingFollowSeries(TransferDataBetweenNodeAndParserDto searchingSeriesToParseDto) {
        rabbitTemplate.convertAndSend(PARSING_FOLLOW_SERIES_RELEASE, searchingSeriesToParseDto);
    }
}
