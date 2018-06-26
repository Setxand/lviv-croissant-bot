package com.bots.lvivcroissantbot.service.telegram.impl;

import com.bots.lvivcroissantbot.constantenum.Platform;
import com.bots.lvivcroissantbot.constantenum.messenger.speaking.ServerSideSpeaker;
import com.bots.lvivcroissantbot.constantenum.telegram.CallBackData;
import com.bots.lvivcroissantbot.dto.telegram.Message;
import com.bots.lvivcroissantbot.dto.telegram.ReplyKeyboardRemove;
import com.bots.lvivcroissantbot.dto.telegram.TelegramRequest;
import com.bots.lvivcroissantbot.dto.telegram.button.*;
import com.bots.lvivcroissantbot.repository.SpeakingMessagesRepository;
import com.bots.lvivcroissantbot.service.telegram.TelegramMessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static com.bots.lvivcroissantbot.constantenum.Platform.TELEGRAM_ADMIN_PANEL_BOT;
import static com.bots.lvivcroissantbot.constantenum.messenger.PayloadCases.QUESTION_NO;
import static com.bots.lvivcroissantbot.constantenum.messenger.PayloadCases.QUESTION_YES;
import static com.bots.lvivcroissantbot.constantenum.messenger.speaking.ServerSideSpeaker.*;
import static com.bots.lvivcroissantbot.constantenum.telegram.CallBackData.CREATE_OWN_CROISSANT_DATA;
import static com.bots.lvivcroissantbot.constantenum.telegram.CallBackData.MENU_DATA;

@Service
public class TelegramMessageSenderServiceImpl implements TelegramMessageSenderService {
    @Autowired
    private SpeakingMessagesRepository speakingMessagesRepositoryService;
    @Value("${url.telegram}")
    private String TELEGRAM_URL;
    @Value("${url.server}")
    private String SERVER_URL;
    @Value("${url.telegram.admins}")
    private String TELEGRAM_ADMIN_PANEL_URL;

    @Override
    public void sendMessage(TelegramRequest telegramRequest, Platform platform) {
        try {
            String url = TELEGRAM_URL;
            if (platform == TELEGRAM_ADMIN_PANEL_BOT)
                url = TELEGRAM_ADMIN_PANEL_URL;
            new RestTemplate().postForEntity(url + "/sendMessage", telegramRequest, Void.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void helloMessage(Message message) {
        String messange = ResourceBundle.getBundle("dictionary").getString(HELLO_MESSAGE.name());
        int chatId = message.getChat().getId();
        sendMessage(new TelegramRequest(messange, chatId), message.getPlatform());
    }

    @Override
    public void simpleMessage(String message, Message m) {
        sendMessage(new TelegramRequest(message, m.getChat().getId()), m.getPlatform());
    }

    @Override
    public void errorMessage(Message message) {
        String text = "men, i don`t understand this command, try again)";
        sendMessage(new TelegramRequest(text, message.getChat().getId()), message.getPlatform());
    }

    @Override
    public void sendButtons(Markup markup, String text, Message message) {
        TelegramRequest telegramRequest = new TelegramRequest();
        telegramRequest.setChatId(message.getChat().getId());
        telegramRequest.setText(text);
        telegramRequest.setMarkup(markup);
        sendMessage(telegramRequest, message.getPlatform());
    }

    @Override
    public void sendInlineButtons(List<List<InlineKeyboardButton>> buttons, String text, Message message) {
        Markup markup = new InlineKeyboardMarkup(buttons);
        sendButtons(markup, text, message);
    }

    @Override
    public void sendPhoto(String photo, String caption, Markup markup, Message message) {
        String url = TELEGRAM_URL;
        if (message.getPlatform() == TELEGRAM_ADMIN_PANEL_BOT)
            url = TELEGRAM_ADMIN_PANEL_URL;
        new RestTemplate().postForEntity(url + "/sendPhoto", new TelegramRequest(message.getChat().getId(), markup, photo, caption), Void.class);
    }

    @Override
    public void sendActions(Message message) {
        List<List<InlineKeyboardButton>> inlineKeyboardButtons = new ArrayList<>();
        InlineKeyboardButton reference = new InlineKeyboardButton();
        reference.setText("Reference");
        reference.setUrl(SERVER_URL + "/reference");
        inlineKeyboardButtons.add(new ArrayList<>(Arrays.asList(new InlineKeyboardButton(ResourceBundle.getBundle("dictionary").getString(MENU_OF_CROISSANTS.name()), MENU_DATA.name()))));
        inlineKeyboardButtons.add(new ArrayList<>(Arrays.asList(new InlineKeyboardButton(ResourceBundle.getBundle("dictionary").getString(CREATE_OWN_CROISSANT.name()), CREATE_OWN_CROISSANT_DATA.name()))));
        inlineKeyboardButtons.add(new ArrayList<>(Arrays.asList(reference)));
        String text = ResourceBundle.getBundle("dictionary").getString(CHOOSE_ACtIONS.name());
        sendInlineButtons(inlineKeyboardButtons, text, message);
    }

    @Override
    public void simpleQuestion(CallBackData data, String splitter, String text, Message message) {
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        String yes = ResourceBundle.getBundle("dictionary").getString(YES.name());
        String no = ResourceBundle.getBundle("dictionary").getString(NO.name());
        inlineKeyboardButtons.add(new InlineKeyboardButton(yes, data.name() + splitter + QUESTION_YES.name()));
        inlineKeyboardButtons.add(new InlineKeyboardButton(no, data.name() + splitter + QUESTION_NO.name()));
        sendInlineButtons(new ArrayList<>(Arrays.asList(inlineKeyboardButtons)), text, message);
    }

    @Override
    public void noEnoughPermissions(Message message) {
        String text = "You have not enough permissions to make it!";
        simpleMessage(text, message);
    }

    @Override
    public void sendKeyboardButtons(Message message, List<List<KeyboardButton>> buttons, String text) {
        sendButtons(new KeyboardMarkup(buttons), text, message);
    }

    @Override
    public void removeKeyboardButtons(Message message) {
        TelegramRequest telegramRequest = new TelegramRequest();
        telegramRequest.setMarkup(new ReplyKeyboardRemove(true));
        String text = ResourceBundle.getBundle("dictionary").getString(ServerSideSpeaker.ACCEPTED.name());
        telegramRequest.setText(text);
        telegramRequest.setChatId(message.getChat().getId());
        sendMessage(telegramRequest, message.getPlatform());
    }
}
