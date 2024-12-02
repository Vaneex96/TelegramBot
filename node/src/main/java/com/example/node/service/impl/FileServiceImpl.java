package com.example.node.service.impl;

import com.example.node.utils.CryptoTool;
import com.example.node.dao.AppDocumentDAO;
import com.example.node.dao.AppPhotoDAO;
import com.example.node.dao.BinaryContentDAO;
import com.example.node.entity.AppDocument;
import com.example.node.entity.AppPhoto;
import com.example.node.entity.BinaryContent;
import com.example.node.exceptions.UploadFileException;
import com.example.node.service.FileService;
import com.example.node.service.enums.LinkType;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    @Value("${token}")
    private String token;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;
    private final BinaryContentDAO binaryContentDAO;
    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;
    private final CryptoTool cryptoTool;
    @Value("${link.address}")
    private String linkAddress;


    @Override
    public AppDocument processDoc(Message telegramMessage) {
        String fileId = telegramMessage.getDocument().getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if(response.getStatusCode() == HttpStatus.OK){
            BinaryContent persistenceBinaryContent = getPersistanceBinaryContent(response);
            AppDocument transientAppDocument = buildTransientAppDoc(telegramMessage.getDocument(), persistenceBinaryContent);
            return appDocumentDAO.save(transientAppDocument);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage) {
        //TODO пока что обрабатываем только одно фото в сообщении
        int arrSize = telegramMessage.getPhoto().size();
        int photoIndex = arrSize > 1? arrSize - 1: 0;
        PhotoSize telegramPhoto = telegramMessage.getPhoto().get(photoIndex);
        String fileId = telegramPhoto.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if(response.getStatusCode() == HttpStatus.OK){
            BinaryContent persistenceBinaryContent = getPersistanceBinaryContent(response);
            AppPhoto transientAppPhoto = buildTransientAppPhoto(telegramPhoto, persistenceBinaryContent);
            return appPhotoDAO.save(transientAppPhoto);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    @Override
    public String generatedLink(Long docId, LinkType linkType) {
        String hashId = cryptoTool.hashOf(docId);
        return "http://" + linkAddress + linkType + "?id=" + hashId;
    }

    private AppPhoto buildTransientAppPhoto(PhotoSize photo, BinaryContent persistenceBinaryContent) {
        return AppPhoto.builder()
                .binaryContent(persistenceBinaryContent)
                .telegramFileId(photo.getFileId())
                .fileSize(photo.getFileSize())
                .build();
    }

    private BinaryContent getPersistanceBinaryContent(ResponseEntity<String> response) {
        String filePath = getFilePath(response);
        byte[] fileInByte = downLoadFile(filePath);
        BinaryContent transientBinaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(fileInByte)
                .build();
        BinaryContent persistenceBinaryContent = binaryContentDAO.save(transientBinaryContent);
        return persistenceBinaryContent;
    }

    private String getFilePath(ResponseEntity<String> response) {
        JSONObject jsonObject = new JSONObject(response.getBody());
        String filePath = String.valueOf(jsonObject
                .getJSONObject("result")
                .get("file_path")
        );
        return filePath;
    }


    private AppDocument buildTransientAppDoc(Document document, BinaryContent persistentBinaryContent){
        return AppDocument.builder()
                .telegramFileId(document.getFileId())
                .docName(document.getFileName())
                .binaryContent(persistentBinaryContent)
                .mimeType(document.getMimeType())
                .fileSize(document.getFileSize())
                .build();
    }

    private ResponseEntity<String> getFilePath(String fileId){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token, fileId
        );
    }

    private byte[] downLoadFile(String filePath){
        String fullUri = fileStorageUri.replace("{token}", token).replace("{filePath}", filePath);
        URL urlObj = null;

        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        //TODO подумать над оптимизацией
        try(InputStream is = urlObj.openStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new UploadFileException(urlObj.toExternalForm(), e);
        }

    }
}
