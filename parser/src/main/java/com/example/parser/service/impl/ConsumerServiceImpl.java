package com.example.parser.service.impl;

import com.example.parser.dto.FindLastSeriesDto;
import com.example.parser.dto.FindSeriesToSubscribeDto;
import com.example.parser.dto.FindSeriesVoiceActsDto;
import com.example.parser.service.ConsumerService;
import com.example.parser.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.example.commonrabbitmq.RabbitQueue.*;

@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {

    private final MainService mainService;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @RabbitListener(queues = FIND_SERIES_TO_SUBSCRIBE)
    public void consumeFindSeriesToSubscribe(FindSeriesToSubscribeDto findSeriesToSubscribeDto) {
        try{
            mainService.processFindSeries(findSeriesToSubscribeDto);
        }catch(NullPointerException e){
            System.out.println(e);
        }
    }

    @Override
    @RabbitListener(queues = FIND_SERIES_VOICE_ACTS)
    public void consumeFindSeriesVoiceActs(FindSeriesVoiceActsDto findSeriesVoiceActsDto) {
        try{
            mainService.processFindSeriesVoiceActs(findSeriesVoiceActsDto);
        }catch(NullPointerException e){
            System.out.println(e);
        }
    }

    @RabbitListener(queues = FIND_LAST_SERIES)
    public void consumeFindLastSeries(FindLastSeriesDto findLastSeriesDto) {
        try{
            mainService.processFindLastSeries(findLastSeriesDto);
        }catch(NullPointerException e){
            System.out.println(e);
        }
    }
}
