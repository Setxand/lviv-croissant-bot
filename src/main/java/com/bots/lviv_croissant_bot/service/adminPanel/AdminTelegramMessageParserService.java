package com.bots.lviv_croissant_bot.service.adminPanel;

import com.bots.lviv_croissant_bot.dto.telegram.Message;

public interface AdminTelegramMessageParserService {
    public void parseMessage(Message message);
}
