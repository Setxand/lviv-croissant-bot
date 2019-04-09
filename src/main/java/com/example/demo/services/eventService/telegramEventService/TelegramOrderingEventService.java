package com.example.demo.services.eventService.telegramEventService;

import com.example.demo.model.telegram.Message;

public interface TelegramOrderingEventService {
	public void makeOrder(Message message);

	void ifNoMore(Message message);
}
