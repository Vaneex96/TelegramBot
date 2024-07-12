package com.example.mailservice.service;

import com.example.mailservice.dto.MailParams;

public interface ConsumerService {
    void consumeRegistrationMail(MailParams mailParams);
}
