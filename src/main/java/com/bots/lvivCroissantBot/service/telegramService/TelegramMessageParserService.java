package com.bots.lvivCroissantBot.service.telegramService;

import com.bots.lvivCroissantBot.dto.telegram.Message;

public interface TelegramMessageParserService {
    public void parseMessage(Message message);
}
