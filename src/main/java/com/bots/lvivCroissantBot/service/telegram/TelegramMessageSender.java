package com.bots.lvivCroissantBot.service.telegram;

import com.bots.lvivCroissantBot.constantEnum.Platform;
import com.bots.lvivCroissantBot.constantEnum.telegramEnum.CallBackData;
import com.bots.lvivCroissantBot.dto.telegram.Message;
import com.bots.lvivCroissantBot.dto.telegram.TelegramRequest;
import com.bots.lvivCroissantBot.dto.telegram.button.InlineKeyboardButton;
import com.bots.lvivCroissantBot.dto.telegram.button.KeyboardButton;
import com.bots.lvivCroissantBot.dto.telegram.button.Markup;

import java.util.List;

public interface TelegramMessageSender {
    public void sendMessage(TelegramRequest telegramRequest,Platform platform);
    public void helloMessage(Message message);
    public void simpleMessage( String message,Message m);
    public void errorMessage(Message message);
    public void sendButtons(Markup markup,String text, Message message);
    public void sendInlineButtons(List<List<InlineKeyboardButton>>buttons,String text, Message message);
    public void sendPhoto( String photo, String caption,Markup markup, Message message);
    public void sendActions(Message message);
    public void simpleQuestion(CallBackData data, String splitter,String text, Message message );
    public void noEnoughPermissions(Message message);
    public void sendKeyboardButtons(Message message, List<List<KeyboardButton>> buttons, String text);
    public void removeKeyboardButtons(Message message);
}
