package com.bots.lvivCroissantBot.service.telegram.impl;

import com.bots.lvivCroissantBot.entity.register.TUser;
import com.bots.lvivCroissantBot.constantEnum.telegramEnum.MessageCases;
import com.bots.lvivCroissantBot.constantEnum.telegramEnum.TelegramUserStatus;
import com.bots.lvivCroissantBot.dto.telegram.Message;
import com.bots.lvivCroissantBot.service.telegram.event.TelegramCreatingOwnCroissant;
import com.bots.lvivCroissantBot.service.telegram.event.TelegramGetMenu;
import com.bots.lvivCroissantBot.service.telegram.event.TelegramOrdering;
import com.bots.lvivCroissantBot.service.peopleRegister.TelegramUserRepositoryService;
import com.bots.lvivCroissantBot.service.telegram.TelegramMessageParserHelper;
import com.bots.lvivCroissantBot.service.telegram.TelegramMessageParser;
import com.bots.lvivCroissantBot.service.telegram.TelegramMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TelegramMessageParserImpl implements TelegramMessageParser {
    @Autowired
    private TelegramMessageSender telegramMessageSender;
    @Autowired
    private TelegramGetMenu telegramGetMenu;
    @Autowired
    private TelegramMessageParserHelper telegramMessageParserHelper;
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private TelegramOrdering telegramOrdering;
    @Autowired
    private TelegramCreatingOwnCroissant telegramCreatingOwnCroissant;
    @Override
    public void parseMessage(Message message) {
        if(message.getText().contains(" "))
            message.setText(message.getText().replaceAll(" ","_"));
        if (message.getText().equals("/start")) {
            start(message);
        } else {
            TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
            if (tUser.getStatus() != null) {
                parseByStatus(message, tUser);
                return;
            }
            switch (MessageCases.valueOf(message.getText().toUpperCase())) {
                case HI:
                    telegramMessageSender.helloMessage(message);
                    break;
                case MENU:
                    menu(message, tUser);
                    break;
                case DELETE_ORDERINGS:
                    deleteOrderings(message,tUser);
                    break;
                case CREATE_OWN_CROISSANT:
                    telegramMessageParserHelper.helpCreateOwnCroissant(message);
                    break;
                default:
                    telegramMessageSender.errorMessage(message);
                    break;
            }
        }
    }



    private void parseByStatus(Message message, TUser tUser) {
        switch (tUser.getStatus()) {
            case TEL_NUMBER_ORDERING_STATUS:
                makeOrder(message);
                break;
            case TIME_STATUS:
                makeOrder(message);
                break;
            case ADDRESS_STATUS:
                makeOrder(message);
                break;
            case FILLING_PHONE_NUMBER_STATUS:
                makeOrder(message);
                break;
            case INPUTTING_FILLINGS_IN_OWN_CROISSANT_STATUS:
                createOwn(message);
                break;
            case PHONE_ENTERING_IN_START_STATUS:
                phoneEnteringInStartStatus(message);
                break;

            default:
                telegramMessageSender.errorMessage(message);
                break;
        }
    }

    private void phoneEnteringInStartStatus(Message message) {
        telegramMessageParserHelper.helpEnterPhoneInStart(message);
    }

    private void deleteOrderings(Message message, TUser tUser) {
        telegramMessageParserHelper.helpDeleteOrderings(message);
    }
    private void createOwn(Message message) {
        telegramCreatingOwnCroissant.createOwn(message);
    }

    private void makeOrder(Message message) {
        telegramOrdering.makeOrder(message);
    }

    private void menu(Message message, TUser tUser) {
        telegramUserRepositoryService.changeStatus(tUser, TelegramUserStatus.ASKING_TYPE_STATUS);
        telegramGetMenu.getMenu(message);
    }

    private void start(Message message) {
            telegramMessageParserHelper.helpStart(message);

    }
}
