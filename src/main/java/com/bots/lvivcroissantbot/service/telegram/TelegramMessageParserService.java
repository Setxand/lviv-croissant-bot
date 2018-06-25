package com.bots.lvivcroissantbot.service.telegram;

import com.bots.lvivcroissantbot.dto.telegram.Message;

public interface TelegramMessageParserService {
    public void parseMessage(Message message);
}
