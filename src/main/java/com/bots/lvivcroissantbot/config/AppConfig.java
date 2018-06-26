package com.bots.lvivcroissantbot.config;

import com.bots.lvivcroissantbot.config.client.MessengerClient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AppConfig.MesProps.class)
public class AppConfig {

    @ConfigurationProperties("messenger")
    @Getter
    @Setter
    @NoArgsConstructor
    public static class MesProps{
        private String token;
        private String name;
        private String lastName;
    }

    @ConfigurationProperties("url")
    @Getter
    @Setter
    @NoArgsConstructor
    public static class UrlProps{
        private String server;
        private String telegram;

        private String telegramAdmins;
    }

    @Bean
    public MessengerClient getProps(MesProps props){
        return new MessengerClient(props);
    }
}
