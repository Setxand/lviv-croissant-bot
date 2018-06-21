package com.bots.lvivCroissantBot.service.eventService.messengerEventService;

import com.bots.lvivCroissantBot.dto.messanger.Messaging;

public interface OrderingEventService {
    public void parseOrdering(Messaging messaging);
}
