package com.example.restservice.service.impl;


import com.example.restservice.dao.AppDocumentDAO;
import com.example.restservice.dao.AppPhotoDAO;
import com.example.restservice.entity.AppDocument;
import com.example.restservice.entity.AppPhoto;
import com.example.restservice.entity.BinaryContent;
import com.example.restservice.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Log4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;

    @Override
    public AppDocument getAppDocument(String docId) {
        //TODO добавить генерацию имени временного файла
        Long id = Long.parseLong(docId);
        return appDocumentDAO.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getAppPhoto(String photoId) {
        //TODO добавить генерацию имени временного файла
        Long id = Long.parseLong(photoId);
        return appPhotoDAO.findById(id).orElse(null);
    }

    @Override
    public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
        try {
            File temp = File.createTempFile("tempFile", ".bin");
            temp.deleteOnExit();
            FileUtils.writeByteArrayToFile(temp, binaryContent.getFileAsArrayOfBytes());
            return new FileSystemResource(temp);
        } catch (IOException e) {
            log.error(e);
            return null;
        }
    }
}
