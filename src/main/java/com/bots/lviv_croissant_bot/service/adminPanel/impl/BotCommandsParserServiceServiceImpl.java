package com.bots.lviv_croissant_bot.service.adminPanel.impl;

import com.bots.lviv_croissant_bot.entity.register.TUser;
import com.bots.lviv_croissant_bot.constantEnum.BotCommands;
import com.bots.lviv_croissant_bot.constantEnum.messengerEnum.Role;
import com.bots.lviv_croissant_bot.constantEnum.telegramEnum.TelegramUserStatus;
import com.bots.lviv_croissant_bot.dto.telegram.Message;
import com.bots.lviv_croissant_bot.dto.telegram.button.InlineKeyboardButton;
import com.bots.lviv_croissant_bot.service.adminPanel.BotCommandsParserService;
import com.bots.lviv_croissant_bot.service.adminPanel.BotCommandParseHelperService;
import com.bots.lviv_croissant_bot.service.adminPanel.event.TelegramAddingRecordingsEventService;
import com.bots.lviv_croissant_bot.service.telegram.event.TelegramGetMenuService;
import com.bots.lviv_croissant_bot.service.peopleRegister.TelegramUserRepositoryService;
import com.bots.lviv_croissant_bot.service.telegram.TelegramMessageParserHelperService;
import com.bots.lviv_croissant_bot.service.telegram.TelegramMessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static com.bots.lviv_croissant_bot.constantEnum.messengerEnum.Role.ADMIN;
import static com.bots.lviv_croissant_bot.constantEnum.messengerEnum.Role.COURIER;
import static com.bots.lviv_croissant_bot.constantEnum.messengerEnum.speaking.ServerSideSpeaker.*;
import static com.bots.lviv_croissant_bot.constantEnum.telegramEnum.CallBackData.*;
import static com.bots.lviv_croissant_bot.constantEnum.telegramEnum.TelegramUserStatus.ASKING_TYPE_STATUS;

@Service
public class BotCommandsParserServiceServiceImpl implements BotCommandsParserService {
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private TelegramAddingRecordingsEventService telegramAddingRecordingsEventService;
    @Autowired
    private BotCommandParseHelperService botCommandParseHelperService;
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Autowired
    private TelegramGetMenuService telegramGetMenuService;
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
        if (tUser.getUser().getRole() == ADMIN || tUser.getUser().getRole() == COURIER) {
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
        if (tUser.getUser().getRole() != ADMIN) {
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
        if (tUser.getUser().getRole() != ADMIN && tUser.getUser().getRole() != Role.PERSONAL) {
            telegramMessageSenderService.noEnoughPermissions(message);
            return;
        }
        telegramUserRepositoryService.changeStatus(tUser, ASKING_TYPE_STATUS);
        telegramGetMenuService.getMenu(message);
    }

    private void setUpMessenger(Message message) {

        botCommandParseHelperService.helpSetUpMessenger(message);
    }

    private void help(Message message) {
        botCommandParseHelperService.helpInvokeBotHelpCommand(message);
    }

    private void add(Message message) {
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        if (tUser.getUser().getRole() != Role.COURIER) {
            telegramUserRepositoryService.changeStatus(tUser, TelegramUserStatus.ADDING_CROISSANT_STATUS);
            telegramAddingRecordingsEventService.addCroissant(message);
        } else
            telegramMessageSenderService.noEnoughPermissions(message);

    }

    private void filling(Message message) {

        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        if (tUser.getUser().getRole() != Role.COURIER) {
            telegramUserRepositoryService.changeStatus(tUser, TelegramUserStatus.ADDING_FILLING_STATUS);
            telegramAddingRecordingsEventService.addFilling(message);
        } else
            telegramMessageSenderService.noEnoughPermissions(message);
    }
}
