package com.example.demo.services.telegramService.impl;

import com.example.demo.enums.telegramEnums.CallBackData;
import com.example.demo.models.telegram.TelegramRequest;
import com.example.demo.models.telegram.buttons.*;
import com.example.demo.services.telegramService.TelegramMessageSenderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static com.example.demo.enums.messengerEnums.PayloadCases.QUESTION_NO;
import static com.example.demo.enums.messengerEnums.PayloadCases.QUESTION_YES;
import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.*;
import static com.example.demo.enums.telegramEnums.CallBackData.CREATE_OWN_CROISSANT_DATA;
import static com.example.demo.enums.telegramEnums.CallBackData.MENU_DATA;

@Service
public class TelegramMessageSenderServiceImpl implements TelegramMessageSenderService {

    @Value("${telegran.url}")
    private String TELEGRAM_URL;
    @Value("${server.url}")
    private String SERVER_URL;
    @Override
    public void sendMessage(TelegramRequest telegramRequest) {
        try {
            new RestTemplate().postForEntity(TELEGRAM_URL+"/sendMessage",telegramRequest,Void.class);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    @Override
    public void helloMessage(Integer chatId) {

    }

    @Override
    public void simpleMessage(Integer chatId, String message) {
        sendMessage(new TelegramRequest(message,chatId));
    }

    @Override
    public void errorMessage(Integer chatId) {
        String message = "men, i don`t understand this command, try again)";
        sendMessage(new TelegramRequest(message,chatId));
    }

    @Override
    public void sendButtons(Markup markup,String text ,Integer chatId) {
        TelegramRequest telegramRequest = new TelegramRequest();
        telegramRequest.setChatId(chatId);
        telegramRequest.setText(text);
        telegramRequest.setMarkup(markup);
        new RestTemplate().postForEntity(TELEGRAM_URL+"/sendMessage",telegramRequest,Void.class);
    }

    @Override
    public void sendKeyBoardButtons(Integer chatId) {
        List<KeyboardButton>keyboardButtons = new ArrayList<>(Arrays.asList(new KeyboardButton("Menu"),new KeyboardButton("Create own croissant")));
        Markup markup = new KeyboardMarkup(new ArrayList<>(Arrays.asList(keyboardButtons)));
        String text = ResourceBundle.getBundle("dictionary").getString(HELLO_MESSAGE.name());
        sendButtons(markup,text,chatId);
    }

    @Override
    public void sendInlineButtons(Integer chatId, List<List<InlineKeyboardButton>> buttons,String text) {
        Markup markup = new InlineKeyboardMarkup(buttons);
        sendButtons(markup,text,chatId);
    }

    @Override
    public void sendPhoto(Integer chatId, String photo, String caption, Markup markup) {
        new RestTemplate().postForEntity(TELEGRAM_URL+"/sendPhoto",new TelegramRequest(chatId,markup,photo,caption),Void.class);
    }

    @Override
    public void sendActions(Integer chatId) {
        List<List<InlineKeyboardButton>>inlineKeyboardButtons = new ArrayList<>();
        InlineKeyboardButton reference = new InlineKeyboardButton();
        reference.setText("Reference");
        reference.setUrl(SERVER_URL+"/reference");
        inlineKeyboardButtons.add(new ArrayList<>(Arrays.asList(new InlineKeyboardButton(ResourceBundle.getBundle("dictionary").getString(MENU_OF_CROISSANTS.name()),MENU_DATA.name()))));
        inlineKeyboardButtons.add(new ArrayList<>(Arrays.asList(new InlineKeyboardButton(ResourceBundle.getBundle("dictionary").getString(CREATE_OWN_CROISSANT.name()), CREATE_OWN_CROISSANT_DATA.name()))));
        inlineKeyboardButtons.add(new ArrayList<>(Arrays.asList(reference)));
        String text = ResourceBundle.getBundle("dictionary").getString(CHOOSE_ACtIONS.name());
        sendInlineButtons(chatId,inlineKeyboardButtons,text);
    }

    @Override
    public void simpleQuestion(Integer chatId, CallBackData data, String splitter, String text) {
        List<InlineKeyboardButton>inlineKeyboardButtons = new ArrayList<>();
        String yes = ResourceBundle.getBundle("dictionary").getString(YES.name());
        String no = ResourceBundle.getBundle("dictionary").getString(NO.name());
        inlineKeyboardButtons.add(new InlineKeyboardButton(yes,data.name()+splitter+QUESTION_YES.name()));
        inlineKeyboardButtons.add(new InlineKeyboardButton(no,data.name()+splitter+QUESTION_NO.name()));
        sendInlineButtons(chatId,new ArrayList<>(Arrays.asList(inlineKeyboardButtons)),text);
    }
}
