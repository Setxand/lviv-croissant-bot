package com.bots.lvivCroissantBot.service.telegram;

import com.bots.lvivCroissantBot.dto.telegram.Message;

public interface TelegramMessageParser {
    public void parseMessage(Message message);
}
