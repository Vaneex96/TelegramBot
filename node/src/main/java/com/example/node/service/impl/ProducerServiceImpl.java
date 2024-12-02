package com.example.node.service.impl;

import com.example.node.dto.FindLastSeriesDto;
import com.example.node.dto.FindSeriesToSubscribeDto;
import com.example.node.dto.FindSeriesVoiceActsDto;
import com.example.node.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.example.node.configuration.RabbitQueue.*;

@Service
@RequiredArgsConstructor
public class ProducerServiceImpl implements ProducerService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void producerService(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
    }

    @Override
    public void produceFindSeriesToSubscribe(FindSeriesToSubscribeDto findSeriesToSubscribeDto) {
        rabbitTemplate.convertAndSend(FIND_SERIES_TO_SUBSCRIBE, findSeriesToSubscribeDto);
    }

    @Override
    public void produceFindSeriesVoiceActs(FindSeriesVoiceActsDto findSeriesVoiceActsDto) {
        rabbitTemplate.convertAndSend(FIND_SERIES_VOICE_ACTS, findSeriesVoiceActsDto);
    }

    @Override
    public void produceFindLastSeries(FindLastSeriesDto findLastSeriesDto) {
        rabbitTemplate.convertAndSend(FIND_LAST_SERIES, findLastSeriesDto);
    }

}
