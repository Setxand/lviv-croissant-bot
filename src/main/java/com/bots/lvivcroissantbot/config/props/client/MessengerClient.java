package com.bots.lvivcroissantbot.config.props.client;

import com.bots.lvivcroissantbot.config.props.PropertiesConfig.MesProps;
import lombok.Getter;

@Getter
public class MessengerClient {
    private MesProps mesProps;

    public MessengerClient(MesProps mesProps) {
        this.mesProps = mesProps;
    }
}
