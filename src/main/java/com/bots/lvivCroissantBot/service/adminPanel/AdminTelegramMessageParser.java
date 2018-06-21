package com.bots.lvivCroissantBot.service.adminPanel;

import com.bots.lvivCroissantBot.dto.telegram.Message;

public interface AdminTelegramMessageParser {
    public void parseMessage(Message message);
}
