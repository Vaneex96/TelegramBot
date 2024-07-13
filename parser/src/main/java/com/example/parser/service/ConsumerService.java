package com.example.parser.service;

import com.example.parser.dto.SearchingSeriesToParseDto;

public interface ConsumerService {
    void consumeSearchingSeriesToParse(SearchingSeriesToParseDto searchingSeriesToParseDto);
}
