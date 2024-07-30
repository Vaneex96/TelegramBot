package com.example.parser.service.impl;

import com.example.parser.dto.TransferDataBetweenNodeAndParserDto;
import com.example.parser.service.ConsumerService;
import com.example.parser.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.example.commonrabbitmq.RabbitQueue.SEARCHING_SERIES_RELEASE_TO_PARSE;

@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {

    private final MainService mainService;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @RabbitListener(queues = SEARCHING_SERIES_RELEASE_TO_PARSE)
    public void consumeSearchingSeriesToParse(TransferDataBetweenNodeAndParserDto dto) {
        try{
            mainService.processFollowingToSeries(dto);
        }catch(NullPointerException e){
            System.out.println(e);
        }
    }
}
