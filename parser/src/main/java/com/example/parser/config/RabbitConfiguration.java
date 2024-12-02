package com.example.parser.config;

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
    public Queue findLastSeriesResultQueue(){
        return new Queue(RabbitQueue.FIND_LAST_SERIES_RESULT);
    }
}
