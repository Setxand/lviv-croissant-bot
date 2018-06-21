package com.bots.lvivCroissantBot.service.adminPanelService;

import com.bots.lvivCroissantBot.dto.telegram.Message;

public interface BotCommandsParserService {
    public void parseBotCommand(Message message);
}
