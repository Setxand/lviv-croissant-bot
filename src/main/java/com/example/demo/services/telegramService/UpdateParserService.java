package com.example.demo.services.telegramService;

import com.example.demo.model.telegram.Update;

public interface UpdateParserService {
	public void parseUpdate(Update update);
}
