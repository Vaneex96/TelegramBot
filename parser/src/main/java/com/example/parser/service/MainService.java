package com.example.parser.service;

import com.example.parser.dto.TransferDataBetweenNodeAndParserDto;

import java.util.List;

public interface MainService {
    String parseFollowSeriesRelease(String url, String voiceActing);
    List<String> parseSeriesToFollow(String seriesTitle);
    void processFollowingToSeries(TransferDataBetweenNodeAndParserDto dto);
}
