package com.example.node.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FindSeriesVoiceActsResultDto {
    private long chatId;
    private long seriesUrlId;
    private Map<Long, String> mapVoiceActing;
}
