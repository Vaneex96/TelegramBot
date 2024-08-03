package com.example.parser.service;

import com.example.parser.dto.TransferDataBetweenNodeAndParserDto;
import com.example.parser.entity.AppSeriesUrlDto;

import java.util.List;

public interface MainService {
    AppSeriesUrlDto parseFollowSeriesRelease(String url, String voiceActing);
    List<String> parseSeriesToFollow(String seriesTitle);
    void processFollowingToSeries(TransferDataBetweenNodeAndParserDto dto);
}
