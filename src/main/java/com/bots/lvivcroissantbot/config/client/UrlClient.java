package com.bots.lvivcroissantbot.config.client;

import com.bots.lvivcroissantbot.config.AppConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.transaction.Transactional;


@Getter
@Setter
@NoArgsConstructor
public class UrlClient {
    private AppConfig.UrlProps urlProps;
    public UrlClient(AppConfig.UrlProps urlProps) {
        this.urlProps = urlProps;
    }
}
