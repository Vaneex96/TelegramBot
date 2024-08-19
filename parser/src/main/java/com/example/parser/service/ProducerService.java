package com.example.parser.service;

import com.example.parser.dto.TransferDataBetweenNodeAndParserDto;

public interface ProducerService {
    void produceSearchedSeriesResponse(TransferDataBetweenNodeAndParserDto dto);
}
