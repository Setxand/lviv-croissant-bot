package com.example.demo.services.adminPanelService;

import com.example.demo.model.telegram.Message;

public interface AdminTelegramMessageParserService {
	public void parseMessage(Message message);
}
