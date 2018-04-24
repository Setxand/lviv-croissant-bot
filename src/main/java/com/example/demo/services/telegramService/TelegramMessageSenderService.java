package com.example.demo.services.telegramService;

import com.example.demo.enums.telegramEnums.CallBackData;
import com.example.demo.models.telegram.TelegramRequest;
import com.example.demo.models.telegram.buttons.InlineKeyboardButton;
import com.example.demo.models.telegram.buttons.Markup;

import java.util.List;

public interface TelegramMessageSenderService {
    public void sendMessage(TelegramRequest telegramRequest);
    public void helloMessage(Integer chatId);
    public void simpleMessage(Integer chatId, String message);
    public void errorMessage(Integer chatId);
    public void sendButtons(Markup markup,String text, Integer chatId);
    public void sendKeyBoardButtons(Integer chatId);
    public void sendInlineButtons(Integer chatId, List<List<InlineKeyboardButton>>buttons,String text);
    public void sendPhoto(Integer chatId, String photo, String caption,Markup markup);
    public void sendActions(Integer chatId);
    public void simpleQuestion(Integer chatId, CallBackData data, String splitter,String text);
}
