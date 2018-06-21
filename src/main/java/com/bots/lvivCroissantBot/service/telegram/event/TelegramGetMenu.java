package com.bots.lvivCroissantBot.service.telegram.event;

import com.bots.lvivCroissantBot.dto.telegram.Message;

public interface TelegramGetMenu {
    public void getMenu(Message message);
    public void getMenuOfFillings(Message message);
}
