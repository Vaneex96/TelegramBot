package com.example.node.service;

import com.example.node.entity.AppDocument;
import com.example.node.entity.AppPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
}
