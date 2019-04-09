package com.example.demo.test;

import com.example.demo.constcomponent.telegramEnums.CallBackData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import telegram.Message;
import telegram.TelegramTextCommands;
import telegram.button.InlineKeyboardButton;
import telegram.client.TelegramClient;

import java.util.*;

@Component
public class TelegramClientEx extends TelegramClient {

	public TelegramClientEx(@Value("${server.url}") String serverUrl, @Value("${telegram.webhooks}") String webhooks,
							@Value("${telegram.urlmap}") String urlMap) {
		super(serverUrl, webhooks, urlMap);
	}

	@Override
	public void sendActions(Message message) {
		//////
	}

	public void simpleQuestion(CallBackData data, String splitter, String text, Message message) {
		List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
		String yes = ResourceBundle.getBundle("dictionary").getString("YES");
		String no = ResourceBundle.getBundle("dictionary").getString("NO");
		inlineKeyboardButtons.add(new InlineKeyboardButton(yes, data.name() + splitter + "QUESTION_YES"));
		inlineKeyboardButtons.add(new InlineKeyboardButton(no, data.name() + splitter + "QUESTION_NO"));
		sendInlineButtons(new ArrayList<>(Collections.singletonList(inlineKeyboardButtons)), text, message);
	}
}
