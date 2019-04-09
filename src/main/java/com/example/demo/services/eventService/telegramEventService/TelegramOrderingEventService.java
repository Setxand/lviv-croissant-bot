package com.example.demo.services.eventService.telegramEventService;

import telegram.Message;

public interface TelegramOrderingEventService {
	public void makeOrder(Message message);

	void ifNoMore(Message message);
}
