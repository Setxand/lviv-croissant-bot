package com.bots.lvivcroissantbot.service.telegram.event;

import com.bots.lvivcroissantbot.dto.telegram.Message;

public interface TelegramGetMenuService {
    public void getMenu(Message message);

    public void getMenuOfFillings(Message message);
}
