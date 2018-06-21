package com.bots.lvivCroissantBot.service.eventService.telegramEventService;

import com.bots.lvivCroissantBot.dto.telegram.Message;

public interface TelegramGetMenuEventService {
    public void getMenu(Message message);
    public void getMenuOfFillings(Message message);
}
