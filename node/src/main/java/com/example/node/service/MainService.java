package com.example.node.service;

import com.example.node.dto.FindLastSeriesResultDto;
import com.example.node.dto.FindSeriesToSubscribeResultDto;
import com.example.node.dto.FindSeriesVoiceActsResultDto;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface MainService {
    void processTextMessage(Update update);
    void processPhotoMessage(Update update);
    void processDocMessage(Update update);

    void processResultOfFindSeriesToSubscribe(FindSeriesToSubscribeResultDto findSeriesToSubscribeResultDto);

    void processResultFindSeriesVoiceActs(FindSeriesVoiceActsResultDto findSeriesVoiceActsResultDto);

    void processResultFindLastSeries(FindLastSeriesResultDto findLastSeriesResultDto);
}
