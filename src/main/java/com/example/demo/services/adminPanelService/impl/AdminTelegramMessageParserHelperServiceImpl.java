package com.example.demo.services.adminPanelService.impl;

import com.example.demo.entities.SpeakingMessage;
import com.example.demo.entities.peopleRegister.TUser;
import com.example.demo.enums.messengerEnums.Roles;
import com.example.demo.models.telegram.Message;
import com.example.demo.models.telegram.buttons.InlineKeyboardButton;
import com.example.demo.services.adminPanelService.AdminTelegramMessageParserHelperService;
import com.example.demo.services.peopleRegisterService.TelegramUserRepositoryService;
import com.example.demo.services.repositoryService.SpeakingMessagesRepositoryService;
import com.example.demo.services.telegramService.TelegramMessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.HELLO_MESSAGE;
import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.NEW_TEXT_HAS_SET;
import static com.example.demo.enums.telegramEnums.CallBackData.SETTING_ROLE_DATA_1;
import static com.example.demo.enums.telegramEnums.CallBackData.SETTING_ROLE_DATA_2;

@Service
public class AdminTelegramMessageParserHelperServiceImpl implements AdminTelegramMessageParserHelperService {
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
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
            String text = "User with username "+message.getText()+" is not exists in our database! He needs to enter command /start!!!";
            telegramMessageSenderService.simpleMessage(text,message);
        }
        telegramMessageSenderService.removeKeyboardButtons(message);
        List<InlineKeyboardButton>buttons = Arrays.asList(new InlineKeyboardButton("Admin", SETTING_ROLE_DATA_1.name()+"?"+Roles.ADMIN.name()+"&"+message.getText()),
                new InlineKeyboardButton("Personal",SETTING_ROLE_DATA_1.name()+"?"+Roles.PERSONAL.name()+"&"+message.getText()),
                new InlineKeyboardButton("Courier",SETTING_ROLE_DATA_1.name()+"?"+Roles.COURIER.name()+"&"+message.getText()),
                new InlineKeyboardButton("Customer",SETTING_ROLE_DATA_1.name()+"?"+Roles.CUSTOMER.name()+"&"+message.getText()));
        telegramMessageSenderService.sendInlineButtons(Arrays.asList(buttons),"Choose role for " + message.getText()+":",message);
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
        telegramMessageSenderService.simpleMessage(text,message);
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        telegramUserRepositoryService.changeStatus(tUser,null);
    }


    private void settingStatus(Message message, TUser tUserAdm) {

            TUser tUser = telegramUserRepositoryService.findByUserName(message.getText());
            String text = "Are you sure you want to set this role for"+tUser.getName()+" "+tUser.getLastName()+" with username \""+tUser.getUserName()+"\"?";
            telegramMessageSenderService.simpleQuestion(SETTING_ROLE_DATA_2,"?"+tUser.getUserName()+"&",text,message);



    }
}
