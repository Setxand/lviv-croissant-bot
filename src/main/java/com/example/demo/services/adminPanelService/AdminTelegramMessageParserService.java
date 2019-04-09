package com.example.demo.services.adminPanelService;


import telegram.Message;

public interface AdminTelegramMessageParserService {
	public void parseMessage(Message message);
}
