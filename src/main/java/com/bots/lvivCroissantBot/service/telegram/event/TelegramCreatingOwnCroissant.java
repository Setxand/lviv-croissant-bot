package com.bots.lvivCroissantBot.service.telegram.event;

import com.bots.lvivCroissantBot.dto.telegram.Message;

public interface TelegramCreatingOwnCroissant {
    public void createOwn(Message message);
}
