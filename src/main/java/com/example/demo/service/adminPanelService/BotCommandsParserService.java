package com.example.demo.service.adminPanelService;

import com.example.demo.dto.telegram.Message;

public interface BotCommandsParserService {
    public void parseBotCommand(Message message);
}
