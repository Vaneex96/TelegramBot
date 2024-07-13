package com.example.node.service;

import com.example.node.dto.SearchedSeriesDto;
import com.example.node.dto.SearchingSeriesToParseDto;
import com.example.node.entity.MailSentResponse;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface ConsumerService {
    void consumeTextMessagesUpdate(Update update);
    void consumePhotoMessagesUpdate(Update update);
    void consumeDocMessagesUpdate(Update update);

    void consumeRegistrationMailSendResult(MailSentResponse response);

    void consumeSearchedSeriesReleaseToParseResponse(SearchedSeriesDto searchedSeriesDto);
}
