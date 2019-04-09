package com.example.demo.services.eventService.telegramEventService;

import com.example.demo.model.telegram.Message;

public interface TelegramGetMenuEventService {
	public void getMenu(Message message);

	public void getMenuOfFillings(Message message);
}
