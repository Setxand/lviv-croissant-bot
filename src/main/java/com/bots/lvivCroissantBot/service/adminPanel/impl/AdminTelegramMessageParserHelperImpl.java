package com.bots.lvivCroissantBot.service.adminPanel.impl;

import com.bots.lvivCroissantBot.entity.SpeakingMessage;
import com.bots.lvivCroissantBot.entity.register.TUser;
import com.bots.lvivCroissantBot.constantEnum.messengerEnum.Role;
import com.bots.lvivCroissantBot.dto.telegram.Message;
import com.bots.lvivCroissantBot.dto.telegram.button.InlineKeyboardButton;
import com.bots.lvivCroissantBot.service.adminPanel.AdminTelegramMessageParserHelper;
import com.bots.lvivCroissantBot.service.peopleRegister.TelegramUserRepositoryService;
import com.bots.lvivCroissantBot.service.repository.SpeakingMessagesRepositoryService;
import com.bots.lvivCroissantBot.service.telegram.TelegramMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.speaking.ServerSideSpeaker.HELLO_MESSAGE;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.speaking.ServerSideSpeaker.NEW_TEXT_HAS_SET;
import static com.bots.lvivCroissantBot.constantEnum.telegramEnum.CallBackData.SETTING_ROLE_DATA_1;
import static com.bots.lvivCroissantBot.constantEnum.telegramEnum.CallBackData.SETTING_ROLE_DATA_2;

@Service
public class AdminTelegramMessageParserHelperImpl implements AdminTelegramMessageParserHelper {
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private TelegramMessageSender telegramMessageSender;
    @Autowired
    private SpeakingMessagesRepositoryService speakingMessagesRepositoryService;
    @Override
    public void helpSetRole(Message message) {
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        settingStatus(message,tUser);


    }

    @Override
    public void helpRoleQuestion(Message message) {
        try {
            TUser tUser = telegramUserRepositoryService.findByUserName(message.getText());
        }
        catch (Exception ex){
            telegramUserRepositoryService.changeStatus(telegramUserRepositoryService.findByChatId(message.getChat().getId()),null);
            String text = "MUser with username "+message.getText()+" is not exists in our database! He needs to enter command /start!!!";
            telegramMessageSender.simpleMessage(text,message);
        }
        telegramMessageSender.removeKeyboardButtons(message);
        List<InlineKeyboardButton>buttons = Arrays.asList(new InlineKeyboardButton("Admin", SETTING_ROLE_DATA_1.name()+"?"+Role.ADMIN.name()+"&"+message.getText()),
                new InlineKeyboardButton("Personal",SETTING_ROLE_DATA_1.name()+"?"+Role.PERSONAL.name()+"&"+message.getText()),
                new InlineKeyboardButton("CourierService",SETTING_ROLE_DATA_1.name()+"?"+Role.COURIER.name()+"&"+message.getText()),
                new InlineKeyboardButton("Customer",SETTING_ROLE_DATA_1.name()+"?"+Role.CUSTOMER.name()+"&"+message.getText()));
        telegramMessageSender.sendInlineButtons(Arrays.asList(buttons),"Choose role for " + message.getText()+":",message);
    }

    @Override
    public void helpChangeHelloMessage(Message message) {
        SpeakingMessage speakingMessage;
        if(speakingMessagesRepositoryService.findByKey(HELLO_MESSAGE.name())==null){
            speakingMessage = new SpeakingMessage();
            speakingMessage.setId(HELLO_MESSAGE.name());
        }
        else speakingMessage = speakingMessagesRepositoryService.findByKey(HELLO_MESSAGE.name());
        speakingMessage.setMessage(message.getText());
        speakingMessagesRepositoryService.saveAndFlush(speakingMessage);
        String text = String.format(ResourceBundle.getBundle("dictionary").getString(NEW_TEXT_HAS_SET.name()),message.getText());
        telegramMessageSender.simpleMessage(text,message);
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        telegramUserRepositoryService.changeStatus(tUser,null);
    }


    private void settingStatus(Message message, TUser tUserAdm) {

            TUser tUser = telegramUserRepositoryService.findByUserName(message.getText());
            String text = "Are you sure you want to set this role for"+tUser.getName()+" "+tUser.getLastName()+" with username \""+tUser.getUserName()+"\"?";
            telegramMessageSender.simpleQuestion(SETTING_ROLE_DATA_2,"?"+tUser.getUserName()+"&",text,message);



    }
}
