//package com.example.restservice.controller;
//
//
//import com.example.restservice.entity.AppDocument;
//import com.example.restservice.entity.AppPhoto;
//import com.example.restservice.entity.BinaryContent;
//import com.example.restservice.service.FileService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.OutputStream;
//
//@RestController
//@RequestMapping("/file")
//@RequiredArgsConstructor
//@Log4j
//public class FileController {
//
//    private final FileService fileService;
//
//    @RequestMapping(method = RequestMethod.GET, value = "/get-doc")
//    public void downloadDocument(@RequestParam("id") String id, HttpServletResponse response){
//        //TODO для формирования badRequest добавить ControllerAdvise
//        AppDocument appDocument = fileService.getAppDocument(id);
//        if(appDocument == null){
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return;
//        }
//
//        response.setContentType(MediaType.parseMediaType(appDocument.getMimeType()).toString());
//        response.setHeader("Content-disposition", "attachment; fileName="  + appDocument.getDocName());
//        response.setStatus(HttpServletResponse.SC_OK);
//
//        BinaryContent binaryContent = appDocument.getBinaryContent();
//
//        try{
//            OutputStream out = response.getOutputStream();
//            out.write(binaryContent.getFileAsArrayOfBytes());
//            out.close();
//        } catch (IOException e) {
//            log.error(e);
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//        }
//
//    }
//
//    @RequestMapping(method = RequestMethod.GET, value = "/get-photo")
//    public void downloadPhoto(@RequestParam("id") String id, HttpServletResponse response){
//        //TODO для формирования badRequest добавить ControllerAdvise
//        AppPhoto appPhoto = fileService.getAppPhoto(id);
//        if(appPhoto == null){
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return;
//        }
//
//        response.setContentType(MediaType.IMAGE_JPEG.toString());
//        response.setHeader("Content-disposition", "attachment;");
//        response.setStatus(HttpServletResponse.SC_OK);
//
//        BinaryContent binaryContent = appPhoto.getBinaryContent();
//
//        try{
//            OutputStream out = response.getOutputStream();
//            out.write(binaryContent.getFileAsArrayOfBytes());
//            out.close();
//        } catch (IOException e) {
//            log.error(e);
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//        }
//    }
//
//}
