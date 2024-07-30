package com.example.parser.service.impl;

import com.example.parser.dto.TransferDataBetweenNodeAndParserDto;

public interface ProducerService {
    void produceSearchedSeriesResponse(TransferDataBetweenNodeAndParserDto dto);
}
