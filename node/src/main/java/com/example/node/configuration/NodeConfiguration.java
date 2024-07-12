package com.example.node.configuration;

import com.example.commonutils.utils.CryptoTool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NodeConfiguration {

    @Value("${salt}")
    private String salt;

    @Bean
    public CryptoTool cryptoTool(){
        return new CryptoTool(salt);
    }

}
