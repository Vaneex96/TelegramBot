package com.example.restservice.controller;


import com.example.restservice.entity.AppDocument;
import com.example.restservice.entity.AppPhoto;
import com.example.restservice.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/get-doc/id/{id}")
    public ResponseEntity<?> downloadDocument(@PathVariable String id){
        //TODO для формирования badRequest добавить ControllerAdvise
        AppDocument appDocument = fileService.getAppDocument(id);
        if(appDocument == null){
            return ResponseEntity.badRequest().build();
        }
        FileSystemResource fileSystemResource = fileService.getFileSystemResource(appDocument.getBinaryContent());
        if(fileSystemResource == null){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(appDocument.getMimeType()))
                .header("Content-disposition", "attachment", "fileName=" + appDocument.getDocName())
                .body(fileSystemResource);
    }

    @GetMapping("/get-photo/id/{id}")
    public ResponseEntity<?> downloadPhoto(@PathVariable String id){
        //TODO для формирования badRequest добавить ControllerAdvise
        AppPhoto appPhoto = fileService.getAppPhoto(id);
        if(appPhoto == null){
            return ResponseEntity.badRequest().build();
        }
        FileSystemResource fileSystemResource = fileService.getFileSystemResource(appPhoto.getBinaryContent());
        if(fileSystemResource == null){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header("Content-disposition", "attachment")
                .body(fileSystemResource);
    }

}
