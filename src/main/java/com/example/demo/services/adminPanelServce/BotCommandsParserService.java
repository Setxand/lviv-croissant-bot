package com.example.demo.services.adminPanelServce;

import com.example.demo.models.telegram.Message;

public interface BotCommandsParserService {
    public void parseBotCommand(Message message);
}
