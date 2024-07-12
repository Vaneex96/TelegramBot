//package com.example.mailservice.contoller;
//
//import com.example.mailservice.dto.MailParams;
//import com.example.mailservice.service.MailSenderService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/mail")
//@RequiredArgsConstructor
//public class MailController {
//
//    private final MailSenderService mailSenderService;
//
//    @PostMapping("/send")
//    public ResponseEntity<?> sendActivationMail(@RequestBody MailParams mailParams){
//        mailSenderService.send(mailParams);
//        return ResponseEntity.ok().build();
//    }
//
//}
