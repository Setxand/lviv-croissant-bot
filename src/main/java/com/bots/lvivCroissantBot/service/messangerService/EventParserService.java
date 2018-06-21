package com.bots.lvivCroissantBot.service.messangerService;

import com.bots.lvivCroissantBot.dto.messanger.Event;

public interface EventParserService {
    public boolean parseEvent(Event event);
}
