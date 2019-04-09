package com.example.demo.services.telegramService;

import com.example.demo.constcomponent.Platform;
import com.example.demo.constcomponent.telegramEnums.CallBackData;
import com.example.demo.model.telegram.Message;
import com.example.demo.model.telegram.TelegramRequest;
import com.example.demo.model.telegram.buttons.InlineKeyboardButton;
import com.example.demo.model.telegram.buttons.KeyboardButton;
import com.example.demo.model.telegram.buttons.Markup;

import java.util.List;

public interface TelegramMessageSenderService {
	public void sendMessage(TelegramRequest telegramRequest, Platform platform);

	public void helloMessage(Message message);

	public void simpleMessage(String message, Message m);

	public void errorMessage(Message message);

	public void sendButtons(Markup markup, String text, Message message);

	public void sendInlineButtons(List<List<InlineKeyboardButton>> buttons, String text, Message message);

	public void sendPhoto(String photo, String caption, Markup markup, Message message);

	public void sendActions(Message message);

	public void simpleQuestion(CallBackData data, String splitter, String text, Message message);

	public void noEnoughPermissions(Message message);

	public void sendKeyboardButtons(Message message, List<List<KeyboardButton>> buttons, String text);

	public void removeKeyboardButtons(Message message);
}
