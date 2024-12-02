//package com.example.restservice.entity;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "app_document")
//public class AppDocument {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long id;
//    private String telegramFileId;
//    private String docName;
//    @OneToOne
//    private BinaryContent binaryContent;
//    private String mimeType;
//    private long fileSize;
//
//}
