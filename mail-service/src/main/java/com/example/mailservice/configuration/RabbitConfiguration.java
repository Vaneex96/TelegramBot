package com.example.mailservice.configuration;

import lombok.Getter;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class RabbitConfiguration {

    @Value("${spring.rabbitmq.queues.registration-mail-message}")
    private String registrationMailMessageQueue;

    @Value("${spring.rabbitmq.queues.registration-mail-send-result}")
    private String registrationMailSendResultQueue;

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue registrationMessageQueue(){
        return new Queue(registrationMailMessageQueue);
    }

    @Bean
    public Queue registrationMailSendResult(){
        return new Queue(registrationMailSendResultQueue);
    }

}
