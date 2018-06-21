package com.bots.lvivCroissantBot.service.messenger.event;

import com.bots.lvivCroissantBot.dto.messanger.Messaging;

public interface OrderingService {
    public void parseOrdering(Messaging messaging);
}
