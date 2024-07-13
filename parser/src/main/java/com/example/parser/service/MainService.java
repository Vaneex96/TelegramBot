package com.example.parser.service;

import com.example.parser.dto.SearchedSeriesDto;
import com.example.parser.dto.SearchingSeriesToParseDto;

import java.util.List;

public interface MainService {
    SearchedSeriesDto searchingSeriesToFollow(SearchingSeriesToParseDto searchingSeriesToParseDto);
    String parseFollowSeriesRelease(String url, String voiceActing);
    List<String> parseSeriesToFollow(String seriesTitle);
}
