package com.bots.lvivCroissantBot.service.uniService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

//@Configuration
//@EnableConfigurationProperties(RestConfig.MessengerProperties.class)
public class RestConfig {
    @Bean
    public RestTemplate restTemplate(@Value(("config.connect-timeout")) int timeout) {
        return  new RestTemplateBuilder().setConnectTimeout(timeout).build();

    }
  /*  @Bean
    public MessengerClient messengerClient(MessengerProperties properties) {
        return new MessengerClient(properties);
    }

    @ConfigurationProperties(prefix = "messenger")
    public static class MessengerProperties {
        private String token;
        private String estimation;


    }*/
}
