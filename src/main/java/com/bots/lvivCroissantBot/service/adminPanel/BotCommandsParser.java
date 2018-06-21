package com.bots.lvivCroissantBot.service.adminPanel;

import com.bots.lvivCroissantBot.dto.telegram.Message;

public interface BotCommandsParser {
    public void parseBotCommand(Message message);
}
