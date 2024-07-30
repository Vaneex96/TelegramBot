package com.example.parser.service;

import com.example.parser.dto.TransferDataBetweenNodeAndParserDto;

public interface ConsumerService {
    void consumeSearchingSeriesToParse(TransferDataBetweenNodeAndParserDto dto);
}
