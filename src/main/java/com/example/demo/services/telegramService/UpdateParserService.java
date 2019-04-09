package com.example.demo.services.telegramService;


import telegram.Update;

public interface UpdateParserService {
	public void parseUpdate(Update update);
}
