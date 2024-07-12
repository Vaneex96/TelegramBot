package com.example.restservice.controller;

import com.example.restservice.service.UserActivationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class ActivationController {

    private final UserActivationService userActivationService;

    @RequestMapping(method = RequestMethod.GET, value = "/activation")
    public ResponseEntity<?> activation(@RequestParam String id){
        boolean res = userActivationService.activation(id);
        if(res){
            return ResponseEntity.ok().body("Регистрация прошла успешно!");
        }
        return ResponseEntity.internalServerError().build();
    }

}