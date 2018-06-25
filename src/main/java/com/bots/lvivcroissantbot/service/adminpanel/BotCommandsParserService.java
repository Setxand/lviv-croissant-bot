package com.bots.lvivcroissantbot.service.adminpanel;

import com.bots.lvivcroissantbot.dto.telegram.Message;

public interface BotCommandsParserService {
    public void parseBotCommand(Message message);
}
