package com.example.parser.service;

import com.example.parser.dto.FindLastSeriesResultDto;
import com.example.parser.dto.FindSeriesToSubscribeResultDto;
import com.example.parser.dto.FindSeriesVoiceActsResultDto;

public interface ProducerService {
    void produceResultFindSeriesToSubscribe(FindSeriesToSubscribeResultDto findSeriesToSubscribeResultDto);

    void produceResultFindSeriesVoiceActs(FindSeriesVoiceActsResultDto findSeriesVoiceActsResultDto);

    void produceResultFindLastSeries(FindLastSeriesResultDto findLastSeriesResultDto);
}
