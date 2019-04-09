package com.example.demo.services.adminPanelService;

import com.example.demo.model.telegram.Message;

public interface BotCommandsParserService {
	public void parseBotCommand(Message message);
}
