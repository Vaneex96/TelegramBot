package com.example.mailservice.service.impl;


import com.example.mailservice.dto.MailParams;
import com.example.mailservice.dto.MailSentResponse;
import com.example.mailservice.service.MailSenderService;
import com.example.mailservice.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class MailSenderServiceImpl implements MailSenderService {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String mailFrom;
    @Value("${service.activation.uri}")
    private String activationServiceUri;
    private final ProducerService producerService;

    @Override
    public boolean send(MailParams mailParams) {
        String subject = "Активация учетной записи";
        String text = getActivationMailBody(mailParams.getId());
        String emailTo = mailParams.getEmailTo();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject(subject);
        mailMessage.setFrom(mailFrom);
        mailMessage.setTo(emailTo);
        mailMessage.setText(text);


        try{
            mailSender.send(mailMessage);
        } catch (MailSendException exc){
            log.error(exc);

            MailSentResponse response = MailSentResponse.builder()
                    .chatId(mailParams.getChatId())
                    .response("Произошла ошибка при отправке эл. письма для потдверждения регистрации! " +
                            "Проверьте корректность указаной эл. почты и повторите попытку еще раз.")
                    .build();
            producerService.produceMailSendResult(response);

            return false;
        }

        return true;
    }

    private String getActivationMailBody(String id) {
        String msg = String.format("Для завершения регистрации перейдите по ссылке:\n%s", activationServiceUri);
        return msg.replace("{id}", id);
    }
}
