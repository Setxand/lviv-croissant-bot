package com.bots.lvivCroissantBot.service.messenger;

import com.bots.lvivCroissantBot.dto.messanger.Messaging;

public interface PayloadParserService {
    public void parsePayload(Messaging messaging);
}
