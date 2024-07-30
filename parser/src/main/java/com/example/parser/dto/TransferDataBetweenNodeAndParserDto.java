package com.example.parser.dto;

import com.example.parser.entity.enums.UserState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferDataBetweenNodeAndParserDto {
    private long chatId;
    private UserState userState;
    private String title;
    private String url;
    private List<String> voiceActing;
    private List<String> urlSeriesList;
    private String resultUrl;
}