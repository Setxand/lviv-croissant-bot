package com.bots.lvivCroissantBot.service.adminPanel.impl;

import com.bots.lvivCroissantBot.entity.register.TUser;
import com.bots.lvivCroissantBot.constantEnum.BotCommands;
import com.bots.lvivCroissantBot.constantEnum.messengerEnum.Role;
import com.bots.lvivCroissantBot.constantEnum.telegramEnum.TelegramUserStatus;
import com.bots.lvivCroissantBot.dto.telegram.Message;
import com.bots.lvivCroissantBot.dto.telegram.button.InlineKeyboardButton;
import com.bots.lvivCroissantBot.service.adminPanel.BotCommandsParser;
import com.bots.lvivCroissantBot.service.adminPanel.BotCommandParseHelper;
import com.bots.lvivCroissantBot.service.adminPanel.event.TelegramAddingRecordingsEventService;
import com.bots.lvivCroissantBot.service.telegram.event.TelegramGetMenu;
import com.bots.lvivCroissantBot.service.peopleRegister.TelegramUserRepositoryService;
import com.bots.lvivCroissantBot.service.telegram.TelegramMessageParserHelper;
import com.bots.lvivCroissantBot.service.telegram.TelegramMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.Role.ADMIN;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.Role.COURIER;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.speaking.ServerSideSpeaker.*;
import static com.bots.lvivCroissantBot.constantEnum.telegramEnum.CallBackData.*;
import static com.bots.lvivCroissantBot.constantEnum.telegramEnum.TelegramUserStatus.ASKING_TYPE_STATUS;

@Service
public class BotCommandsParserServiceImpl implements BotCommandsParser {
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private TelegramAddingRecordingsEventService telegramAddingRecordingsEventService;
    @Autowired
    private BotCommandParseHelper botCommandParseHelper;
    @Autowired
    private TelegramMessageSender telegramMessageSender;
    @Autowired
    private TelegramGetMenu telegramGetMenu;
    @Autowired
    private TelegramMessageParserHelper telegramMessageParserHelper;

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
                telegramMessageParserHelper.helpStart(message);
                break;
            case COURIERACTIONS:
                courierActions(message);
                break;
            default:
                telegramMessageSender.errorMessage(message);
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
            telegramMessageSender.sendInlineButtons(Arrays.asList(buttons), courierActions, message);

        }
        else {
            telegramMessageSender.noEnoughPermissions(message);
        }
    }


    private void adminPanel(Message message) {
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        if (tUser.getUser().getRole() != ADMIN) {
            telegramMessageSender.noEnoughPermissions(message);
            return;
        }
        List<InlineKeyboardButton> buttons = new ArrayList<>(Arrays.asList(new InlineKeyboardButton("Set role", SET_ROLE_DATA.name()),
                new InlineKeyboardButton("Change hello message", SET_HELLO_MESSAGE_DATA.name())));
        String text = ResourceBundle.getBundle("dictionary").getString(CHOOSE_ACTIONS.name());
        telegramMessageSender.sendInlineButtons(new ArrayList<>(Arrays.asList(buttons)), text, message);
    }

    private void deleteCroissant(Message message) {

        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        if (tUser.getUser().getRole() != ADMIN && tUser.getUser().getRole() != Role.PERSONAL) {
            telegramMessageSender.noEnoughPermissions(message);
            return;
        }
        telegramUserRepositoryService.changeStatus(tUser, ASKING_TYPE_STATUS);
        telegramGetMenu.getMenu(message);
    }

    private void setUpMessenger(Message message) {

        botCommandParseHelper.helpSetUpMessenger(message);
    }

    private void help(Message message) {
        botCommandParseHelper.helpInvokeBotHelpCommand(message);
    }

    private void add(Message message) {
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        if (tUser.getUser().getRole() != Role.COURIER) {
            telegramUserRepositoryService.changeStatus(tUser, TelegramUserStatus.ADDING_CROISSANT_STATUS);
            telegramAddingRecordingsEventService.addCroissant(message);
        } else
            telegramMessageSender.noEnoughPermissions(message);

    }

    private void filling(Message message) {

        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        if (tUser.getUser().getRole() != Role.COURIER) {
            telegramUserRepositoryService.changeStatus(tUser, TelegramUserStatus.ADDING_FILLING_STATUS);
            telegramAddingRecordingsEventService.addFilling(message);
        } else
            telegramMessageSender.noEnoughPermissions(message);
    }
}
