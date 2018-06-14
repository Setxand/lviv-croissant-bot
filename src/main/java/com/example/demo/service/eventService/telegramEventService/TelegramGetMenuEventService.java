package com.example.demo.service.eventService.telegramEventService;

import com.example.demo.dto.telegram.Message;

public interface TelegramGetMenuEventService {
    public void getMenu(Message message);
    public void getMenuOfFillings(Message message);
}
