package com.example.demo.services.adminPanelService;


import telegram.Message;

public interface BotCommandsParserService {
	public void parseBotCommand(Message message);
}
