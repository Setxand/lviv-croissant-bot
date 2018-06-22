package com.bots.lviv_croissant_bot.service.telegram.impl;

import com.bots.lviv_croissant_bot.entity.register.TUser;
import com.bots.lviv_croissant_bot.constantEnum.telegramEnum.MessageCases;
import com.bots.lviv_croissant_bot.constantEnum.telegramEnum.TelegramUserStatus;
import com.bots.lviv_croissant_bot.dto.telegram.Message;
import com.bots.lviv_croissant_bot.service.telegram.event.TelegramCreatingOwnCroissantService;
import com.bots.lviv_croissant_bot.service.telegram.event.TelegramGetMenuService;
import com.bots.lviv_croissant_bot.service.telegram.event.TelegramOrderingService;
import com.bots.lviv_croissant_bot.service.peopleRegister.TelegramUserRepositoryService;
import com.bots.lviv_croissant_bot.service.telegram.TelegramMessageParserHelperService;
import com.bots.lviv_croissant_bot.service.telegram.TelegramMessageParserService;
import com.bots.lviv_croissant_bot.service.telegram.TelegramMessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TelegramMessageParserServiceImpl implements TelegramMessageParserService {
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Autowired
    private TelegramGetMenuService telegramGetMenuService;
    @Autowired
    private TelegramMessageParserHelperService telegramMessageParserHelperService;
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private TelegramOrderingService telegramOrderingService;
    @Autowired
    private TelegramCreatingOwnCroissantService telegramCreatingOwnCroissant;
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
                    telegramMessageSenderService.helloMessage(message);
                    break;
                case MENU:
                    menu(message, tUser);
                    break;
                case DELETE_ORDERINGS:
                    deleteOrderings(message,tUser);
                    break;
                case CREATE_OWN_CROISSANT:
                    telegramMessageParserHelperService.helpCreateOwnCroissant(message);
                    break;
                default:
                    telegramMessageSenderService.errorMessage(message);
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
                telegramMessageSenderService.errorMessage(message);
                break;
        }
    }

    private void phoneEnteringInStartStatus(Message message) {
        telegramMessageParserHelperService.helpEnterPhoneInStart(message);
    }

    private void deleteOrderings(Message message, TUser tUser) {
        telegramMessageParserHelperService.helpDeleteOrderings(message);
    }
    private void createOwn(Message message) {
        telegramCreatingOwnCroissant.createOwn(message);
    }

    private void makeOrder(Message message) {
        telegramOrderingService.makeOrder(message);
    }

    private void menu(Message message, TUser tUser) {
        telegramUserRepositoryService.changeStatus(tUser, TelegramUserStatus.ASKING_TYPE_STATUS);
        telegramGetMenuService.getMenu(message);
    }

    private void start(Message message) {
            telegramMessageParserHelperService.helpStart(message);

    }
}
