package com.example.demo.services.telegramService;

import telegram.Message;

public interface TelegramMessageParserService {
	public void parseMessage(Message message);
}
