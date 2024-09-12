package com.example.parser.service;

import com.example.parser.dto.FindLastSeriesDto;
import com.example.parser.dto.FindSeriesToSubscribeDto;
import com.example.parser.dto.FindSeriesVoiceActsDto;
import com.example.parser.dto.TransferDataBetweenNodeAndParserDto;

public interface MainService {

    void processCheckingReleases(TransferDataBetweenNodeAndParserDto dto);

    void processFindSeries(FindSeriesToSubscribeDto findSeriesToSubscribeDto);

    void processFindSeriesVoiceActs(FindSeriesVoiceActsDto findSeriesVoiceActsDto);

    void processFindLastSeries(FindLastSeriesDto findLastSeriesDto);
}
