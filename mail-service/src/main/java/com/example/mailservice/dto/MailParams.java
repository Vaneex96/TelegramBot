package com.example.mailservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailParams {
    private String id;
    private long chatId;
    private String emailTo;
}
