package com.bots.lvivcroissantbot.service.messenger;

import com.bots.lvivcroissantbot.dto.messanger.Event;

public interface EventParserService {
    public boolean parseEvent(Event event);
}
