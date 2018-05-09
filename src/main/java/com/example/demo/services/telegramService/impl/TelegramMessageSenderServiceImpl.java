package com.example.demo.services.telegramService.impl;

import com.example.demo.entities.SpeakingMessage;
import com.example.demo.enums.Platform;
import com.example.demo.enums.telegramEnums.CallBackData;
import com.example.demo.models.telegram.Message;
import com.example.demo.models.telegram.TelegramRequest;
import com.example.demo.models.telegram.buttons.*;
import com.example.demo.services.repositoryService.SpeakingMessagesRepositoryService;
import com.example.demo.services.telegramService.TelegramMessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static com.example.demo.enums.Platform.TELEGRAM_ADMIN_PANEL_BOT;
import static com.example.demo.enums.messengerEnums.PayloadCases.QUESTION_NO;
import static com.example.demo.enums.messengerEnums.PayloadCases.QUESTION_YES;
import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.*;
import static com.example.demo.enums.telegramEnums.CallBackData.CREATE_OWN_CROISSANT_DATA;
import static com.example.demo.enums.telegramEnums.CallBackData.MENU_DATA;

@Service
public class TelegramMessageSenderServiceImpl implements TelegramMessageSenderService {
    @Autowired
    private SpeakingMessagesRepositoryService speakingMessagesRepositoryService;
    @Value("${telegran.url}")
    private String TELEGRAM_URL;
    @Value("${server.url}")
    private String SERVER_URL;
    @Value("${telegran.admins.url}")
    private String TELEGRAM_ADMIN_PANEL_URL;
    @Override
    public void sendMessage(TelegramRequest telegramRequest, Platform platform) {
        try {
            String url = TELEGRAM_URL;
            if(platform == TELEGRAM_ADMIN_PANEL_BOT)
                url = TELEGRAM_ADMIN_PANEL_URL;
            new RestTemplate().postForEntity(url+"/sendMessage",telegramRequest,Void.class);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    @Override
    public void helloMessage(Message message) {
        String messange = ResourceBundle.getBundle("dictionary").getString(HELLO_MESSAGE.name());
        int chatId  = message.getChat().getId();
        sendMessage(new TelegramRequest(messange,chatId),message.getPlatform());
    }

    @Override
    public void simpleMessage(String message,Message m) {
        sendMessage(new TelegramRequest(message,m.getChat().getId()),m.getPlatform());
    }

    @Override
    public void errorMessage(Message message) {
        String text = "men, i don`t understand this command, try again)";
        sendMessage(new TelegramRequest(text,message.getChat().getId()),message.getPlatform());
    }

    @Override
    public void sendButtons(Markup markup,String text, Message message) {
        TelegramRequest telegramRequest = new TelegramRequest();
        telegramRequest.setChatId(message.getChat().getId());
        telegramRequest.setText(text);
        telegramRequest.setMarkup(markup);
        sendMessage(telegramRequest,message.getPlatform());
    }

    @Override
    public void sendKeyBoardButtons(Message message) {
        List<KeyboardButton>keyboardButtons = new ArrayList<>(Arrays.asList(new KeyboardButton("Menu"),new KeyboardButton("Create own croissant")));
        Markup markup = new KeyboardMarkup(new ArrayList<>(Arrays.asList(keyboardButtons)));
        SpeakingMessage helloMessage = speakingMessagesRepositoryService.findByKey(HELLO_MESSAGE.name());

        sendButtons(markup,helloMessage.getMessage(),message);
    }

    @Override
    public void sendInlineButtons(List<List<InlineKeyboardButton>>buttons,String text, Message message) {
        Markup markup = new InlineKeyboardMarkup(buttons);
        sendButtons(markup,text,message);
    }

    @Override
    public void sendPhoto( String photo, String caption,Markup markup, Message message) {
        String url = TELEGRAM_URL;
        if(message.getPlatform() == TELEGRAM_ADMIN_PANEL_BOT   )
            url = TELEGRAM_ADMIN_PANEL_URL;
        new RestTemplate().postForEntity(url+"/sendPhoto",new TelegramRequest(message.getChat().getId(),markup,photo,caption),Void.class);
    }

    @Override
    public void sendActions(Message message) {
        List<List<InlineKeyboardButton>>inlineKeyboardButtons = new ArrayList<>();
        InlineKeyboardButton reference = new InlineKeyboardButton();
        reference.setText("Reference");
        reference.setUrl(SERVER_URL+"/reference");
        inlineKeyboardButtons.add(new ArrayList<>(Arrays.asList(new InlineKeyboardButton(ResourceBundle.getBundle("dictionary").getString(MENU_OF_CROISSANTS.name()),MENU_DATA.name()))));
        inlineKeyboardButtons.add(new ArrayList<>(Arrays.asList(new InlineKeyboardButton(ResourceBundle.getBundle("dictionary").getString(CREATE_OWN_CROISSANT.name()), CREATE_OWN_CROISSANT_DATA.name()))));
        inlineKeyboardButtons.add(new ArrayList<>(Arrays.asList(reference)));
        String text = ResourceBundle.getBundle("dictionary").getString(CHOOSE_ACtIONS.name());
        sendInlineButtons(inlineKeyboardButtons,text,message);
    }

    @Override
    public void simpleQuestion(CallBackData data, String splitter,String text, Message message ) {
        List<InlineKeyboardButton>inlineKeyboardButtons = new ArrayList<>();
        String yes = ResourceBundle.getBundle("dictionary").getString(YES.name());
        String no = ResourceBundle.getBundle("dictionary").getString(NO.name());
        inlineKeyboardButtons.add(new InlineKeyboardButton(yes,data.name()+splitter+QUESTION_YES.name()));
        inlineKeyboardButtons.add(new InlineKeyboardButton(no,data.name()+splitter+QUESTION_NO.name()));
        sendInlineButtons(new ArrayList<>(Arrays.asList(inlineKeyboardButtons)),text,message);
    }
}
