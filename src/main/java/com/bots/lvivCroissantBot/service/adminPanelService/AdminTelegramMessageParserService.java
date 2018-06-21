package com.bots.lvivCroissantBot.service.adminPanelService;

import com.bots.lvivCroissantBot.dto.telegram.Message;

public interface AdminTelegramMessageParserService {
    public void parseMessage(Message message);
}
