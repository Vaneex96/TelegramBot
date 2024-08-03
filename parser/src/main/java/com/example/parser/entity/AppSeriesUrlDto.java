package com.example.parser.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppSeriesUrlDto {
    private long id;
    private String url;
    private String voiceActingName;
    private int voiceActingValue;
    private int lastSeason;
    private int lastEpisode;
    private String newUrl;
}
