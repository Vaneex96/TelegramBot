package com.example.dispatcher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@RequiredArgsConstructor
public class WebHookController {
    private final UpdateProcessor updateProcessor;

    @RequestMapping(value = "/callback/update", method = RequestMethod.POST)
    public ResponseEntity<?> onUpdateReceived(@RequestBody Update update){
//        if(update.hasCallbackQuery()){
//            System.out.println(update.getCallbackQuery().getMessage().getText());
//            System.out.println(update.getCallbackQuery().getData());
//            return ResponseEntity.ok().build();
//        }
        updateProcessor.processUpdate(update);
        return ResponseEntity.ok().build();
    }

}
