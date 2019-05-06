package com.example.demo.services.telegramService.impl;

import com.example.demo.client.TelegramClientEx;
import com.example.demo.constcomponent.Platform;
import com.example.demo.services.peopleRegisterService.TelegramUserRepositoryService;
import com.example.demo.services.telegramService.CallBackParserService;
import com.example.demo.services.telegramService.TelegramMessageParserService;
import com.example.demo.services.telegramService.UpdateParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import telegram.Update;

@Service
public class UpdateParserServiceImpl implements UpdateParserService {
	@Autowired
	private TelegramMessageParserService telegramMessageParserService;
	@Autowired
	private TelegramClientEx telegramClient;
	@Autowired
	private CallBackParserService callBackParserService;
	@Autowired
	private TelegramUserRepositoryService telegramUserRepositoryService;

	@Override
	public void parseUpdate(Update update) {
		try {
			if (update.getCallBackQuery() != null) {
				update.getCallBackQuery().getMessage().setPlatform(Platform.COMMON_BOT);
				callBackParserService.parseCallBackQuery(update.getCallBackQuery());
			} else if (update.getMessage() != null) {
				update.getMessage().setPlatform(Platform.COMMON_BOT);
				telegramMessageParserService.parseMessage(update.getMessage());
			}
		} catch (Exception ex) {
			try {
				telegramClient.errorMessage(update.getMessage());
				telegramUserRepositoryService.changeStatus(telegramUserRepositoryService.findByChatId(update.getMessage().getChat().getId()), null);
			} catch (Exception e) {
				e.printStackTrace();
			}

			ex.printStackTrace();
		}

	}
}
