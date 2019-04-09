package com.example.demo.test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import telegram.Message;
import telegram.TelegramTextCommands;
import telegram.client.TelegramClient;

@Component
public class TelegramClientEx extends TelegramClient {

	public TelegramClientEx(@Value("${telegran.url}") String telegramUrl, @Value("${server.url}") String serverUrl,
							@Value("${telegram.webhooks}") String webhooks) {
		super(telegramUrl, serverUrl, webhooks);
	}

	@Override
	public void sendActions(Message message) {
		//////
	}
}
