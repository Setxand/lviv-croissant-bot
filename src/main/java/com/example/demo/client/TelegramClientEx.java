package com.example.demo.client;

import com.example.demo.constcomponent.telegramEnums.CallBackData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import telegram.Message;
import telegram.TelegramRequest;
import telegram.button.Button;
import telegram.button.InlineKeyboardButton;
import telegram.button.KeyboardButton;
import telegram.button.KeyboardMarkup;
import telegram.client.TelegramClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import static com.example.demo.constcomponent.messengerEnums.speaking.ServerSideSpeaker.*;
import static com.example.demo.constcomponent.telegramEnums.CallBackData.CREATE_OWN_CROISSANT_DATA;
import static com.example.demo.constcomponent.telegramEnums.CallBackData.MENU_DATA;

@Component
public class TelegramClientEx extends TelegramClient {

	public TelegramClientEx(@Value("${server.url}") String serverUrl, @Value("${telegram.webhooks}") String webhooks,
							@Value("${telegram.urlmap}") String urlMap) {
		super(serverUrl, webhooks, urlMap);
	}

	public void sendActions(Message message) {
		List<List<InlineKeyboardButton>> inlineKeyboardButtons = new ArrayList<>();
		InlineKeyboardButton reference = new InlineKeyboardButton();
		reference.setText("Reference");
		reference.setUrl(SERVER_URL + "/reference");
		inlineKeyboardButtons.add(Collections.singletonList(new InlineKeyboardButton(
					ResourceBundle.getBundle("dictionary").getString(MENU_OF_CROISSANTS.name()), MENU_DATA.name())));
		inlineKeyboardButtons.add(Collections.singletonList(new InlineKeyboardButton(ResourceBundle
				.getBundle("dictionary").getString(CREATE_OWN_CROISSANT.name()), CREATE_OWN_CROISSANT_DATA.name())));
		inlineKeyboardButtons.add(Collections.singletonList(reference));
		String text = ResourceBundle.getBundle("dictionary").getString(CHOOSE_ACtIONS.name());
		sendButtons(createButtonListMarkup(true,
				inlineKeyboardButtons.toArray(new InlineKeyboardButton[0])), text, message);
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

	public void sendInlineButtons(List<InlineKeyboardButton> asList, String mes, Message message) {
		sendButtons(createButtonListMarkup(false, asList.toArray(new InlineKeyboardButton[0])), mes, message);
	}

	public void sendKeyboardButtons(Message message, List<KeyboardButton> keyboardButtons, String s) {
		sendButtons(createButtonListMarkup(false, keyboardButtons.toArray(new KeyboardButton[0])), s, message);
	}

	public void sendSpecialButtons(List<List<Button>> buttons, String text, Message message) {
		TelegramRequest telegramRequest = new TelegramRequest();
		telegramRequest.setChatId(message.getChat().getId());
		telegramRequest.setText(text);
		telegramRequest.setMarkup(new KeyboardMarkup(buttons));
		telegramRequest.setPlatform(message.getPlatform());
		this.sendMessage(telegramRequest);
	}
}
