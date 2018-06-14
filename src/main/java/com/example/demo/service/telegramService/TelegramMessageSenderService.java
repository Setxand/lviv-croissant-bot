package com.example.demo.service.telegramService;

import com.example.demo.constantEnum.Platform;
import com.example.demo.constantEnum.telegramEnums.CallBackData;
import com.example.demo.dto.telegram.Message;
import com.example.demo.dto.telegram.TelegramRequest;
import com.example.demo.dto.telegram.button.InlineKeyboardButton;
import com.example.demo.dto.telegram.button.KeyboardButton;
import com.example.demo.dto.telegram.button.Markup;

import java.util.List;

public interface TelegramMessageSenderService {
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
