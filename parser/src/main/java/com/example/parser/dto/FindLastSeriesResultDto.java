package com.example.parser.dto;

import com.example.parser.entity.AppSeriesUrlDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FindLastSeriesResultDto {
    private long chatId;
    private long telegramUserId;
    private String resultUrl;
    private long urlSeriesId;
    private AppSeriesUrlDto appSeriesUrlDto;
}
