package com.example.restservice.service;

import com.example.restservice.entity.AppDocument;
import com.example.restservice.entity.AppPhoto;

public interface FileService {
    AppDocument getAppDocument(String id);
    AppPhoto getAppPhoto(String id);
}
