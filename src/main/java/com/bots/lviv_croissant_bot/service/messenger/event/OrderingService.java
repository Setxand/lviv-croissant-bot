package com.bots.lviv_croissant_bot.service.messenger.event;

import com.bots.lviv_croissant_bot.dto.messanger.Messaging;

public interface OrderingService {
    public void parseOrdering(Messaging messaging);
}
