package com.example.mailservice.service;


import com.example.mailservice.dto.MailParams;

public interface MailSenderService {
    boolean send(MailParams mailParams);
}
