package com.example.node.service;

import com.example.node.dto.FindLastSeriesDto;
import com.example.node.dto.FindSeriesToSubscribeDto;
import com.example.node.dto.FindSeriesVoiceActsDto;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ProducerService {
    void producerService(SendMessage sendMessage);
    void produceFindSeriesToSubscribe(FindSeriesToSubscribeDto findSeriesToSubscribeDto);
    void produceFindSeriesVoiceActs(FindSeriesVoiceActsDto findSeriesVoiceActsDto);

    void produceFindLastSeries(FindLastSeriesDto findLastSeriesDto);
}
