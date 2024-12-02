package com.example.node.configuration;

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
    public Queue textMessageQueue(){
        return new Queue(RabbitQueue.TEXT_MESSAGE_UPDATE);
    }
    @Bean
    public Queue docMessageQueue(){
        return new Queue(RabbitQueue.DOC_MESSAGE_UPDATE);
    }
    @Bean
    public Queue photoMessageQueue(){
        return new Queue(RabbitQueue.PHOTO_MESSAGE_UPDATE);
    }
    @Bean
    public Queue answerMessageQueue(){
        return new Queue(RabbitQueue.ANSWER_MESSAGE);
    }

    @Bean
    public Queue findLastSeriesResultQueue(){
        return new Queue(RabbitQueue.REGISTRATION_MAIL_SEND_RESULT);
    }
    @Bean
    public Queue searchingSeriesQueue(){
        return new Queue(RabbitQueue.FIND_SERIES_TO_SUBSCRIBE);
    }
    @Bean
    public Queue searchingSeriesResponseQueue(){
        return new Queue(RabbitQueue.FIND_SERIES_TO_SUBSCRIBE_RESULT);
    }

    @Bean
    public Queue parsingSeriesQueue(){
        return new Queue(RabbitQueue.FIND_SERIES_VOICE_ACTS);
    }
    @Bean
    public Queue parsingSeriesResponseQueue(){
        return new Queue(RabbitQueue.FIND_SERIES_VOICE_ACTS_RESULT);
    }

    @Bean
    public Queue findLstSeriesQueue(){
        return new Queue(RabbitQueue.FIND_LAST_SERIES);
    }

    @Bean
    public Queue registrationMailSendResultMessageQueue(){
        return new Queue(RabbitQueue.REGISTRATION_MAIL_SEND_RESULT);
    }
    @Bean
    public Queue findSeriesToSubscribeQueue(){
        return new Queue(RabbitQueue.FIND_SERIES_TO_SUBSCRIBE);
    }
    @Bean
    public Queue findSeriesToSubscribeResultQueue(){
        return new Queue(RabbitQueue.FIND_SERIES_TO_SUBSCRIBE_RESULT);
    }
    @Bean
    public Queue findSeriesVoiceActsQueue(){
        return new Queue(RabbitQueue.FIND_SERIES_VOICE_ACTS);
    }
    @Bean
    public Queue findSeriesVoiceActsResultQueue(){
        return new Queue(RabbitQueue.FIND_SERIES_VOICE_ACTS_RESULT);
    }
    @Bean
    public Queue findLastSeriesQueue(){
        return new Queue(RabbitQueue.FIND_LAST_SERIES);
    }
    @Bean
    public Queue findLastSeriesResult(){
        return new Queue(RabbitQueue.FIND_LAST_SERIES_RESULT);
    }

}
