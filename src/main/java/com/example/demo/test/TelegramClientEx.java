package com.example.demo.test;

import com.example.demo.constcomponent.telegramEnums.CallBackData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import telegram.Markup;
import telegram.Message;
import telegram.button.Button;
import telegram.button.InlineKeyboardButton;
import telegram.button.KeyboardButton;
import telegram.client.TelegramClient;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class TelegramClientEx extends TelegramClient {

	public TelegramClientEx(@Value("${server.url}") String serverUrl, @Value("${telegram.webhooks}") String webhooks,
							@Value("${telegram.urlmap}") String urlMap) {
		super(serverUrl, webhooks, urlMap);
	}

	public void sendActions(Message message) {
		//////
	}

	public void simpleQuestion(CallBackData data, String splitter, String text, Message message) {
		List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
		String yes = ResourceBundle.getBundle("dictionary").getString("YES");
		String no = ResourceBundle.getBundle("dictionary").getString("NO");
		inlineKeyboardButtons.add(new InlineKeyboardButton(yes, data.name() + splitter + "QUESTION_YES"));
		inlineKeyboardButtons.add(new InlineKeyboardButton(no, data.name() + splitter + "QUESTION_NO"));
		sendButtons(createButtonListMarkup(true,
				inlineKeyboardButtons.toArray(new InlineKeyboardButton[0])), text, message);
	}

	public void sendInlineButtons(String text, Message message, Button... buttons) {
		Markup buttonListMarkup = createButtonListMarkup(true, buttons);
		sendButtons(buttonListMarkup, text, message);
	}

	public void sendKeyboardButtons(Message message, List<List<KeyboardButton>> keyboardButtons, String s) {
		sendButtons(createButtonListMarkup(true, keyboardButtons.toArray(new KeyboardButton[0])), s, message);
	}
}
