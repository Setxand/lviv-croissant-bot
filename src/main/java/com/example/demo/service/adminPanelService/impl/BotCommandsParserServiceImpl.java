package com.example.demo.service.adminPanelService.impl;

import com.example.demo.entity.peopleRegister.TUser;
import com.example.demo.constantEnum.BotCommands;
import com.example.demo.constantEnum.messengerEnums.Roles;
import com.example.demo.constantEnum.telegramEnums.TelegramUserStatus;
import com.example.demo.dto.telegram.Message;
import com.example.demo.dto.telegram.button.InlineKeyboardButton;
import com.example.demo.service.adminPanelService.BotCommandsParserService;
import com.example.demo.service.adminPanelService.BotCommandParseHelperService;
import com.example.demo.service.eventService.servicePanel.TelegramAddingRecordingsEventService;
import com.example.demo.service.eventService.telegramEventService.TelegramGetMenuEventService;
import com.example.demo.service.peopleRegisterService.TelegramUserRepositoryService;
import com.example.demo.service.telegramService.TelegramMessageParserHelperService;
import com.example.demo.service.telegramService.TelegramMessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static com.example.demo.constantEnum.messengerEnums.Roles.ADMIN;
import static com.example.demo.constantEnum.messengerEnums.Roles.COURIER;
import static com.example.demo.constantEnum.messengerEnums.speaking.ServerSideSpeaker.*;
import static com.example.demo.constantEnum.telegramEnums.CallBackData.*;
import static com.example.demo.constantEnum.telegramEnums.TelegramUserStatus.ASKING_TYPE_STATUS;

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
        switch (BotCommands.valueOf(command.toString().toUpperCase())) {
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
            case COURIERACTIONS:
                courierActions(message);
                break;
            default:
                telegramMessageSenderService.errorMessage(message);
                break;
        }

    }

    private void courierActions(Message message) {
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        if (tUser.getRole() == ADMIN || tUser.getRole() == COURIER) {
            String listOfOrdering = ResourceBundle.getBundle("dictionary").getString(ORDERING_LIST.name());
            String listOfOwnOrdering = ResourceBundle.getBundle("dictionary").getString(COMPLETE_ORDERING.name().toUpperCase());
            List<InlineKeyboardButton> buttons = Arrays.asList(new InlineKeyboardButton(listOfOrdering, LIST_OF_ORDERING_DATA.name()),
                    new InlineKeyboardButton(listOfOwnOrdering, LIST_OF_COMPLETE_ORDERING_DATA.name()));
            String courierActions = ResourceBundle.getBundle("dictionary").getString(CHOOSE_ACTIONS.name());
            telegramMessageSenderService.sendInlineButtons(Arrays.asList(buttons), courierActions, message);

        }
        else {
            telegramMessageSenderService.noEnoughPermissions(message);
        }
    }


    private void adminPanel(Message message) {
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        if (tUser.getRole() != ADMIN) {
            telegramMessageSenderService.noEnoughPermissions(message);
            return;
        }
        List<InlineKeyboardButton> buttons = new ArrayList<>(Arrays.asList(new InlineKeyboardButton("Set role", SET_ROLE_DATA.name()),
                new InlineKeyboardButton("Change hello message", SET_HELLO_MESSAGE_DATA.name())));
        String text = ResourceBundle.getBundle("dictionary").getString(CHOOSE_ACTIONS.name());
        telegramMessageSenderService.sendInlineButtons(new ArrayList<>(Arrays.asList(buttons)), text, message);
    }

    private void deleteCroissant(Message message) {

        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        if (tUser.getRole() != ADMIN && tUser.getRole() != Roles.PERSONAL) {
            telegramMessageSenderService.noEnoughPermissions(message);
            return;
        }
        telegramUserRepositoryService.changeStatus(tUser, ASKING_TYPE_STATUS);
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
        if (tUser.getRole() != Roles.COURIER) {
            telegramUserRepositoryService.changeStatus(tUser, TelegramUserStatus.ADDING_CROISSANT_STATUS);
            telegramAddingRecordingsEventService.addCroissant(message);
        } else
            telegramMessageSenderService.noEnoughPermissions(message);

    }

    private void filling(Message message) {

        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        if (tUser.getRole() != Roles.COURIER) {
            telegramUserRepositoryService.changeStatus(tUser, TelegramUserStatus.ADDING_FILLING_STATUS);
            telegramAddingRecordingsEventService.addFilling(message);
        } else
            telegramMessageSenderService.noEnoughPermissions(message);
    }
}
