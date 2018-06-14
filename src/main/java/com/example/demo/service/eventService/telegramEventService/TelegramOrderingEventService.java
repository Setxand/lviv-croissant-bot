package com.example.demo.service.eventService.telegramEventService;

import com.example.demo.dto.telegram.Message;

public interface TelegramOrderingEventService {
    public void makeOrder(Message message);

    void ifNoMore(Message message);
}
