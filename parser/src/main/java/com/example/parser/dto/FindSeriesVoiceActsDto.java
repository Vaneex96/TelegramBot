package com.example.parser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FindSeriesVoiceActsDto {
    private long chatId;
    private String url;
    private long urlSeriesId;
}
