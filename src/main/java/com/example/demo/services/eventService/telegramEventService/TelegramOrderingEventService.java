package com.example.demo.services.eventService.telegramEventService;

import com.example.demo.models.telegram.Message;

public interface TelegramOrderingEventService {
    public void makeOrder(Message message);

    void ifNoMore(Message message);
}
