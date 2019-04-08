package com.example.demo.services.telegramService;

import com.example.demo.models.telegram.Message;

public interface TelegramMessageParserService {
	public void parseMessage(Message message);
}
