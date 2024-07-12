package com.example.mailservice.configuration;

import com.example.commonrabbitmq.RabbitQueue;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue registrationMessageQueue(){
        return new Queue(RabbitQueue.REGISTRATION_MAIL_MESSAGE);
    }

    @Bean
    public Queue registrationMailSendResult(){
        return new Queue(RabbitQueue.REGISTRATION_MAIL_SEND_RESULT);
    }

}
