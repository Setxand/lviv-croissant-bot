package com.bots.lvivcroissantbot.service.messenger.event;

import com.bots.lvivcroissantbot.dto.messanger.Messaging;

public interface OrderingService {
    public void parseOrdering(Messaging messaging);
}
