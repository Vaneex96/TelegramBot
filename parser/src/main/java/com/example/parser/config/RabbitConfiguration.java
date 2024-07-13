package com.example.parser.config;

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
    public Queue searchingSeriesQueue(){
        return new Queue(RabbitQueue.SEARCHING_SERIES_RELEASE_TO_PARSE);
    }
    @Bean
    public Queue searchingSeriesResponseQueue(){
        return new Queue(RabbitQueue.SEARCHED_SERIES_RELEASE_TO_PARSE_RESPONSE);
    }

    @Bean
    public Queue parsingSeriesQueue(){
        return new Queue(RabbitQueue.PARSING_FOLLOW_SERIES_RELEASE);
    }
    @Bean
    public Queue parsingSeriesResponseQueue(){
        return new Queue(RabbitQueue.PARSING_FOLLOW_SERIES_RELEASE_RESPONSE);
    }
}
