package com.bots.lvivcroissantbot.config.props;

import com.bots.lvivcroissantbot.config.props.client.MessengerClient;
import com.bots.lvivcroissantbot.config.props.client.UrlClient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties(PropertiesConfig.MesProps.class)
@PropertySource("classpath:add.properties")
public class PropertiesConfig {

    @Bean
    public MessengerClient getProps(MesProps props) {
        return new MessengerClient(props);
    }

    @Bean
    public UrlClient getUrlProps(UrlProps urlProps) {
        return new UrlClient(urlProps);
    }

    @ConfigurationProperties("messenger")
    @Getter
    @Setter
    @NoArgsConstructor
    public static class MesProps {
        private String token;
        private String name;
        private String lastName;
    }

    @ConfigurationProperties("url")
    @Getter
    @Setter
    @NoArgsConstructor
    public static class UrlProps {
        private String server;
        private Profile profile;

        @Bean
        public Profile getProfile() {
            return profile;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Profile {
            private String messenger;
            private String telegramCommon;
            private String telegramService;
        }
    }
}
