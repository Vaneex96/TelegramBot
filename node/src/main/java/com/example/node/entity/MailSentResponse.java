package com.example.node.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
public class MailSentResponse {
    private long chatId;
    private String response;

    public MailSentResponse() {
    }
}
