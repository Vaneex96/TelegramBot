package com.example.restservice.service;

import com.example.restservice.entity.AppDocument;
import com.example.restservice.entity.AppPhoto;
import com.example.restservice.entity.BinaryContent;
import org.springframework.core.io.FileSystemResource;

public interface FileService {
    AppDocument getAppDocument(String id);
    AppPhoto getAppPhoto(String id);
    FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
