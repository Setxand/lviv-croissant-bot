package com.bots.lviv_croissant_bot.service.adminPanel;

import com.bots.lviv_croissant_bot.dto.telegram.Message;

public interface BotCommandsParserService {
    public void parseBotCommand(Message message);
}