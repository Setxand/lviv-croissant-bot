package com.bots.lviv_croissant_bot.service.telegram.event;

import com.bots.lviv_croissant_bot.dto.telegram.Message;

public interface TelegramCreatingOwnCroissantService {
    public void createOwn(Message message);
}