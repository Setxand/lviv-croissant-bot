package com.bots.lviv_croissant_bot.service.telegram.event;

import com.bots.lviv_croissant_bot.dto.telegram.Message;

public interface TelegramGetMenuService {
    public void getMenu(Message message);
    public void getMenuOfFillings(Message message);
}
