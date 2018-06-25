package com.bots.lvivcroissantbot.service.messenger;

import com.bots.lvivcroissantbot.dto.messanger.Messaging;

public interface PayloadParserService {
    public void parsePayload(Messaging messaging);
}
