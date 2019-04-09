package com.example.demo.services.eventService.telegramEventService;

import telegram.Message;

public interface TelegramGetMenuEventService {
	public void getMenu(Message message);

	public void getMenuOfFillings(Message message);
}
