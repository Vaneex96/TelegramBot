package com.example.node.service;

import com.example.node.dto.SearchingSeriesToParseDto;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ProducerService {
    void producerService(SendMessage sendMessage);
    void produceSearchingSeries(SearchingSeriesToParseDto searchingSeriesToParseDto);
}
