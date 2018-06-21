package com.bots.lvivCroissantBot.service.eventService.telegramEventService;

import com.bots.lvivCroissantBot.dto.telegram.Message;

public interface TelegramOrderingEventService {
    public void makeOrder(Message message);

    void ifNoMore(Message message);
}
