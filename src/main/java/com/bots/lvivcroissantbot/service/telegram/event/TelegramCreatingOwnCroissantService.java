package com.bots.lvivcroissantbot.service.telegram.event;

import com.bots.lvivcroissantbot.dto.telegram.Message;

public interface TelegramCreatingOwnCroissantService {
    public void createOwn(Message message);
}
