package com.example.parser.service.impl;

import com.example.parser.dto.TransferDataBetweenNodeAndParserDto;
import com.example.parser.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.example.commonrabbitmq.RabbitQueue.SEARCHED_SERIES_RELEASE_TO_PARSE_RESPONSE;

@RequiredArgsConstructor
@Service
public class ProducerServiceImpl implements ProducerService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void produceSearchedSeriesResponse(TransferDataBetweenNodeAndParserDto dto) {
        System.out.println("from producer: " + dto.getChatId());
        rabbitTemplate.convertAndSend(SEARCHED_SERIES_RELEASE_TO_PARSE_RESPONSE, dto);
    }
}
