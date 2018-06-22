package com.bots.lviv_croissant_bot.service.telegram;

import com.bots.lviv_croissant_bot.dto.telegram.Message;

public interface TelegramMessageParser {
    public void parseMessage(Message message);
}
