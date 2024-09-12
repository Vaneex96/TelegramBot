package com.example.node.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FindLastSeriesDto {
    private long chatId;
    private long telegramUserId;
    private String voiceActing;
    private String url;
    private long urlSeriesId;
}
