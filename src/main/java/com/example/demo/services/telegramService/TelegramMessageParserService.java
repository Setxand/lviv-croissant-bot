package com.example.demo.services.telegramService;

import com.example.demo.model.telegram.Message;

public interface TelegramMessageParserService {
	public void parseMessage(Message message);
}
