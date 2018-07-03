package com.bots.lvivcroissantbot.config.props.client;

import com.bots.lvivcroissantbot.config.props.PropertiesConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class UrlClient {
    private PropertiesConfig.UrlProps urlProps;

    public UrlClient(PropertiesConfig.UrlProps urlProps) {
        this.urlProps = urlProps;
    }
}
