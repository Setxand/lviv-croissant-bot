package com.bots.lviv_croissant_bot.service.messenger;

import com.bots.lviv_croissant_bot.dto.messanger.Messaging;

public interface PayloadParserService {
    public void parsePayload(Messaging messaging);
}
