package com.example.node.service;

import com.example.node.dto.TransferDataBetweenNodeAndParserDto;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ProducerService {
    void producerService(SendMessage sendMessage);
    void produceSearchingSeries(TransferDataBetweenNodeAndParserDto searchingSeriesToParseDto);
    void produceParsingFollowSeries(TransferDataBetweenNodeAndParserDto searchingSeriesToParseDto);
}
