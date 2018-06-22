package com.bots.lviv_croissant_bot.service.messenger;

import com.bots.lviv_croissant_bot.dto.messanger.Event;

public interface EventParserService {
    public boolean parseEvent(Event event);
}
