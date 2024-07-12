package com.example.node.service.impl;

import com.example.commonrabbitmq.RabbitQueue;
import com.example.commonutils.utils.CryptoTool;
import com.example.node.dao.AppUserDAO;
import com.example.node.dao.enums.UserState;
import com.example.node.dto.MailParams;
import com.example.node.entity.AppUser;
import com.example.node.service.AppUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j
public class AppUserServiceImpl implements AppUserService {

    private final AppUserDAO appUserDAO;
    private final CryptoTool cryptoTool;
    @Value("${service.mail.uri}")
    private String mailServiceUri;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public String registerUser(AppUser appUser) {
        if(appUser.getIsActive()){
            return "Вы уже зарегистрированы!";
        } else if(appUser.getEmail() != null){
            return "Вам на почту уже было отправленно письмо. "
                    + "Перейдите по ссылке в письме для подтверждения регистрации";
        }
        appUser.setState(UserState.WAIT_FOR_EMAIL_STATE);
        appUserDAO.save(appUser);
        return "Введите, пожалуйста, ваш email: ";
    }

    @Override
    public String setEmail(AppUser appUser, String email, long chatId) {
        try{
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException e) {
            return "Введите, пожалуста, корректный email. Для отмены команды введите /cancel";
        }
        Optional<AppUser> optionalAppUser = appUserDAO.findByEmail(email);
        if(optionalAppUser.isEmpty()){
            appUser.setEmail(email);
            appUser.setState(UserState.BASIC_STATE);
            appUserDAO.save(appUser);

            String cryptoUserId = cryptoTool.hashOf(appUser.getId());
            sendRegistrationMail(cryptoUserId, email, chatId);
//            ResponseEntity<String> response = sendRequestToMailService(cryptoUserId, email);
//            if(response.getStatusCode() != HttpStatus.OK){
//                String msg = String.format("Отправка эл. письма на почту %s не удалась!", email);
//                log.error(msg);
//                appUser.setEmail(null);
//                appUserDAO.save(appUser);
//                return msg;
//            }
            return String.format("На вашу почту - %s было отправленно письмо для подтверждения регистрации", email);
        }
        return "Этот email уже используется. Введите корректный email. " +
                "Для отмены команды введите /cancel";
    }

    private void sendRegistrationMail(String cryptoUserId, String email, long chatId) {


//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
        MailParams mailParams = MailParams.builder()
                .id(cryptoUserId)
                .chatId(chatId)
                .emailTo(email)
                .build();

        rabbitTemplate.convertAndSend(RabbitQueue.REGISTRATION_MAIL_MESSAGE, mailParams);

//        var request = new HttpEntity<MailParams>(mailParams, headers);

//        return restTemplate.exchange(mailServiceUri, HttpMethod.POST, request, String.class);
    }

}

