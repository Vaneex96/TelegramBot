package com.example.parser.service.impl;

import com.example.parser.dto.FindLastSeriesResultDto;
import com.example.parser.dto.FindSeriesToSubscribeResultDto;
import com.example.parser.dto.FindSeriesVoiceActsResultDto;
import com.example.parser.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.example.commonrabbitmq.RabbitQueue.*;

@RequiredArgsConstructor
@Service
public class ProducerServiceImpl implements ProducerService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void produceResultFindSeriesToSubscribe(FindSeriesToSubscribeResultDto findSeriesToSubscribeResultDto) {
        System.out.println("from producer: " + findSeriesToSubscribeResultDto.getChatId());
        rabbitTemplate.convertAndSend(FIND_SERIES_TO_SUBSCRIBE_RESULT, findSeriesToSubscribeResultDto);
    }

    @Override
    public void produceResultFindSeriesVoiceActs(FindSeriesVoiceActsResultDto findSeriesVoiceActsResultDto) {
        System.out.println("from producer: " + findSeriesVoiceActsResultDto.getChatId());
        rabbitTemplate.convertAndSend(FIND_SERIES_VOICE_ACTS_RESULT, findSeriesVoiceActsResultDto);
    }

    @Override
    public void produceResultFindLastSeries(FindLastSeriesResultDto findLastSeriesResultDto) {
        System.out.println("from producer: " + findLastSeriesResultDto.getChatId());
        rabbitTemplate.convertAndSend(FIND_LAST_SERIES_RESULT, findLastSeriesResultDto);
    }
}
