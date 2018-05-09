package com.example.demo.services.adminPanelService.impl;

import com.example.demo.entities.peopleRegister.TUser;
import com.example.demo.enums.BotCommands;
import com.example.demo.enums.telegramEnums.TelegramUserStatus;
import com.example.demo.models.telegram.Message;
import com.example.demo.models.telegram.buttons.InlineKeyboardButton;
import com.example.demo.services.adminPanelService.BotCommandsParserService;
import com.example.demo.services.adminPanelService.BotCommandParseHelperService;
import com.example.demo.services.eventService.servicePanel.TelegramAddingRecordingsEventService;
import com.example.demo.services.eventService.telegramEventService.TelegramGetMenuEventService;
import com.example.demo.services.peopleRegisterService.TelegramUserRepositoryService;
import com.example.demo.services.telegramService.TelegramMessageParserHelperService;
import com.example.demo.services.telegramService.TelegramMessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.CHOOSE_ACTIONS;
import static com.example.demo.enums.telegramEnums.CallBackData.*;
import static com.example.demo.enums.telegramEnums.TelegramUserStatus.ASKING_TYPE_STATUS;

@Service
public class BotCommandsParserServiceImpl implements BotCommandsParserService {
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private TelegramAddingRecordingsEventService telegramAddingRecordingsEventService;
    @Autowired
    private BotCommandParseHelperService botCommandParseHelperService;
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Autowired
    private TelegramGetMenuEventService telegramGetMenuEventService;
    @Autowired
    private TelegramMessageParserHelperService telegramMessageParserHelperService;
    @Override
    public void parseBotCommand(Message message) {
        StringBuilder command = new StringBuilder(message.getText()).deleteCharAt(0);
        switch (BotCommands.valueOf(command.toString().toUpperCase())){
            case FILLING:
                filling(message);
                break;
            case ADD:
                add(message);
                break;
            case HELP:
                help(message);
                break;
            case SETUPMESSENGER:
                setUpMessenger(message);
                break;
            case DELETECROISSANT:
                deleteCroissant(message);
                break;
            case ADMINPANEL:
                adminPanel(message);
                break;
            case START:
                telegramMessageParserHelperService.helpStart(message);
                break;
                default:
                    telegramMessageSenderService.errorMessage(message);
                    break;
        }

    }


    private void adminPanel(Message message) {
        List<InlineKeyboardButton>buttons = new ArrayList<>(Arrays.asList(new InlineKeyboardButton("Set role",SET_ROLE_DATA.name()),
                new InlineKeyboardButton("Change hello message",SET_HELLO_MESSAGE_DATA.name())));
        String text = ResourceBundle.getBundle("dictionary").getString(CHOOSE_ACTIONS.name());
        telegramMessageSenderService.sendInlineButtons(new ArrayList<>(Arrays.asList(buttons)),text,message);
    }

    private void deleteCroissant(Message message) {
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        telegramUserRepositoryService.changeStatus(tUser,ASKING_TYPE_STATUS);
        telegramGetMenuEventService.getMenu(message);
    }

    private void setUpMessenger(Message message) {
        botCommandParseHelperService.helpSetUpMessenger(message);
    }

    private void help(Message message) {
        botCommandParseHelperService.helpInvokeBotHelpCommand(message);
    }

    private void add(Message message) {
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        telegramUserRepositoryService.changeStatus(tUser,TelegramUserStatus.ADDING_CROISSANT_STATUS);
        telegramAddingRecordingsEventService.addCroissant(message);
    }

    private void filling(Message message) {
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        telegramUserRepositoryService.changeStatus(tUser,TelegramUserStatus.ADDING_FILLING_STATUS);
        telegramAddingRecordingsEventService.addFilling(message);
    }
}
