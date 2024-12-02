package com.example.dispatcher.configuration;

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

    @Value("${spring.rabbitmq.queues.text-message-update}")
    private String textMessageUpdateQueue;

    @Value("${spring.rabbitmq.queues.doc-message-update}")
    private String docMessageUpdateQueue;

    @Value("${spring.rabbitmq.queues.photo-message-update}")
    private String photoMessageUpdateQueue;

    @Value("${spring.rabbitmq.queues.answer-message}")
    private String answerMessageQueue;


    @Value("${spring.rabbitmq.queues.registration-mail-message}")
    private String registrationMailMessageQueue;

    @Value("${spring.rabbitmq.queues.registration-mail-send-result}")
    private String registrationMailSendResultMessageQueue;

    @Value("${spring.rabbitmq.queues.find-series-to-subscribe}")
    private String findSeriesToSubscribeQueue;

    @Value("${spring.rabbitmq.queues.find-series-to-subscribe-result}")
    private String findSeriesToSubscribeResultQueue;

    @Value("${spring.rabbitmq.queues.find-series-voice-acts}")
    private String findSeriesVoiceActsQueue;

    @Value("${spring.rabbitmq.queues.find-series-voice-acts-result}")
    private String findSeriesVoiceActsResultQueue;

    @Value("${spring.rabbitmq.queues.find-last-series}")
    private String findLastSeriesQueue;

    @Value("${spring.rabbitmq.queues.find-last-series-result}")
    private String findLastSeriesResult;



    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public Queue textMessageQueue(){
        return new Queue(textMessageUpdateQueue);
    }
    @Bean
    public Queue docMessageQueue(){
        return new Queue(docMessageUpdateQueue);
    }
    @Bean
    public Queue photoMessageQueue(){
        return new Queue(photoMessageUpdateQueue);
    }
    @Bean
    public Queue answerMessageQueue(){
        return new Queue(answerMessageQueue);
    }


    @Bean
    public Queue registrationMailSendResultMessageQueue(){
        return new Queue(registrationMailSendResultMessageQueue);
    }
    @Bean
    public Queue findSeriesToSubscribeQueue(){
        return new Queue(findSeriesToSubscribeQueue);
    }
    @Bean
    public Queue findSeriesToSubscribeResultQueue(){
        return new Queue(findSeriesToSubscribeResultQueue);
    }
    @Bean
    public Queue findSeriesVoiceActsQueue(){
        return new Queue(findSeriesVoiceActsQueue);
    }
    @Bean
    public Queue findSeriesVoiceActsResultQueue(){
        return new Queue(findSeriesVoiceActsResultQueue);
    }
    @Bean
    public Queue findLastSeriesQueue(){
        return new Queue(findLastSeriesQueue);
    }
    @Bean
    public Queue findLastSeriesResult(){
        return new Queue(findLastSeriesResult);
    }

}
