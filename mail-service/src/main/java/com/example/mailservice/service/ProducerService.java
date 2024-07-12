package com.example.mailservice.service;

import com.example.mailservice.dto.MailSentResponse;

public interface ProducerService {
    void produceMailSendResult(MailSentResponse response);
}
