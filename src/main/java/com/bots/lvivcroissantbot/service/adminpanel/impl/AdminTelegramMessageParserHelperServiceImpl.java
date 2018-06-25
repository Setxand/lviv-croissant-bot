package com.bots.lvivcroissantbot.service.adminpanel.impl;

import com.bots.lvivcroissantbot.constantenum.messenger.Role;
import com.bots.lvivcroissantbot.dto.telegram.Message;
import com.bots.lvivcroissantbot.dto.telegram.button.InlineKeyboardButton;
import com.bots.lvivcroissantbot.entity.SpeakingMessage;
import com.bots.lvivcroissantbot.entity.register.TUser;
import com.bots.lvivcroissantbot.repository.SpeakingMessagesRepository;
import com.bots.lvivcroissantbot.service.adminpanel.AdminTelegramMessageParserHelperService;
import com.bots.lvivcroissantbot.service.peopleregister.TelegramUserRepositoryService;
import com.bots.lvivcroissantbot.service.telegram.TelegramMessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static com.bots.lvivcroissantbot.constantenum.messenger.speaking.ServerSideSpeaker.HELLO_MESSAGE;
import static com.bots.lvivcroissantbot.constantenum.messenger.speaking.ServerSideSpeaker.NEW_TEXT_HAS_SET;
import static com.bots.lvivcroissantbot.constantenum.telegram.CallBackData.SETTING_ROLE_DATA_1;
import static com.bots.lvivcroissantbot.constantenum.telegram.CallBackData.SETTING_ROLE_DATA_2;

@Service
public class AdminTelegramMessageParserHelperServiceImpl implements AdminTelegramMessageParserHelperService {
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Autowired
    private SpeakingMessagesRepository speakingMessagesRepositoryService;

    @Override
    public void helpSetRole(Message message) {
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        settingStatus(message, tUser);


    }

    @Override
    public void helpRoleQuestion(Message message) {
        try {
            TUser tUser = telegramUserRepositoryService.findByUserName(message.getText());
        } catch (Exception ex) {
            telegramUserRepositoryService.changeStatus(telegramUserRepositoryService.findByChatId(message.getChat().getId()), null);
            String text = "MUser with username " + message.getText() + " is not exists in our database! He needs to enter command /start!!!";
            telegramMessageSenderService.simpleMessage(text, message);
        }
        telegramMessageSenderService.removeKeyboardButtons(message);
        List<InlineKeyboardButton> buttons = Arrays.asList(new InlineKeyboardButton("Admin", SETTING_ROLE_DATA_1.name() + "?" + Role.ADMIN.name() + "&" + message.getText()),
                new InlineKeyboardButton("Personal", SETTING_ROLE_DATA_1.name() + "?" + Role.PERSONAL.name() + "&" + message.getText()),
                new InlineKeyboardButton("CourierService", SETTING_ROLE_DATA_1.name() + "?" + Role.COURIER.name() + "&" + message.getText()),
                new InlineKeyboardButton("Customer", SETTING_ROLE_DATA_1.name() + "?" + Role.CUSTOMER.name() + "&" + message.getText()));
        telegramMessageSenderService.sendInlineButtons(Arrays.asList(buttons), "Choose role for " + message.getText() + ":", message);
    }

    @Override
    public void helpChangeHelloMessage(Message message) {
        SpeakingMessage speakingMessage = speakingMessagesRepositoryService.findById(HELLO_MESSAGE.name()).orElse(new SpeakingMessage(HELLO_MESSAGE.name()));
        speakingMessage.setMessage(message.getText());
        speakingMessagesRepositoryService.saveAndFlush(speakingMessage);
        String text = String.format(ResourceBundle.getBundle("dictionary").getString(NEW_TEXT_HAS_SET.name()), message.getText());
        telegramMessageSenderService.simpleMessage(text, message);
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        telegramUserRepositoryService.changeStatus(tUser, null);
    }


    private void settingStatus(Message message, TUser tUserAdm) {

        TUser tUser = telegramUserRepositoryService.findByUserName(message.getText());
        String text = "Are you sure you want to set this role for" + tUser.getName() + " " + tUser.getLastName() + " with username \"" + tUser.getUserName() + "\"?";
        telegramMessageSenderService.simpleQuestion(SETTING_ROLE_DATA_2, "?" + tUser.getUserName() + "&", text, message);


    }
}
