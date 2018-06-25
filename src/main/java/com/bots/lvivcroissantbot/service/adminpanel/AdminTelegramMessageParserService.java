package com.bots.lvivcroissantbot.service.adminpanel;

import com.bots.lvivcroissantbot.dto.telegram.Message;

public interface AdminTelegramMessageParserService {
    public void parseMessage(Message message);
}
