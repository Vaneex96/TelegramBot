//package com.example.restservice.service.impl;
//
//import com.example.restservice.dao.AppDocumentDAO;
//import com.example.restservice.dao.AppPhotoDAO;
//import com.example.restservice.entity.AppDocument;
//import com.example.restservice.entity.AppPhoto;
//import com.example.restservice.service.FileService;
//import com.example.restservice.utils.CryptoTool;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Log4j
//@Service
//@RequiredArgsConstructor
//public class FileServiceImpl implements FileService {
//
//    private final AppDocumentDAO appDocumentDAO;
//    private final AppPhotoDAO appPhotoDAO;
//    @Value("${salt}")
//    private String salt;
//
//    @Override
//    public AppDocument getAppDocument(String hash) {
//        //TODO добавить генерацию имени временного файла
//        CryptoTool cryptoTool = new CryptoTool(salt);
//        Long id = cryptoTool.idOf(hash);
//        if (id == null) return null;
//        return appDocumentDAO.findById(id).orElse(null);
//    }
//
//    @Override
//    public AppPhoto getAppPhoto(String hash) {
//        //TODO добавить генерацию имени временного файла
//        CryptoTool cryptoTool = new CryptoTool(salt);
//        Long id = cryptoTool.idOf(hash);
//        if (id == null) return null;
//        return appPhotoDAO.findById(id).orElse(null);
//    }
//
//}
