package com.example.parser.service;

import com.example.parser.dto.FindSeriesToSubscribeDto;
import com.example.parser.dto.FindSeriesVoiceActsDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import static com.example.parser.config.RabbitQueue.FIND_SERIES_VOICE_ACTS;

public interface ConsumerService {
    void consumeFindSeriesToSubscribe(FindSeriesToSubscribeDto findSeriesToSubscribeDto);

    @RabbitListener(queues = FIND_SERIES_VOICE_ACTS)
    void consumeFindSeriesVoiceActs(FindSeriesVoiceActsDto findSeriesVoiceActsDto);
}
