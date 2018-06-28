package com.bots.lvivcroissantbot.config.client;

import com.bots.lvivcroissantbot.config.AppConfig.MesProps;
import lombok.Getter;

@Getter
public class MessengerClient {
    private MesProps mesProps;

    public MessengerClient(MesProps mesProps) {
        this.mesProps = mesProps;
    }
}
