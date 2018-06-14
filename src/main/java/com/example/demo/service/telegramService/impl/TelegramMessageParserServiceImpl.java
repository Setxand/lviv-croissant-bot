package com.example.demo.service.telegramService.impl;

import com.example.demo.entity.peopleRegister.TUser;
import com.example.demo.constantEnum.telegramEnums.MessageCases;
import com.example.demo.constantEnum.telegramEnums.TelegramUserStatus;
import com.example.demo.dto.telegram.Message;
import com.example.demo.service.eventService.telegramEventService.TelegramCreatingOwnCroissantEventService;
import com.example.demo.service.eventService.telegramEventService.TelegramGetMenuEventService;
import com.example.demo.service.eventService.telegramEventService.TelegramOrderingEventService;
import com.example.demo.service.peopleRegisterService.TelegramUserRepositoryService;
import com.example.demo.service.telegramService.TelegramMessageParserHelperService;
import com.example.demo.service.telegramService.TelegramMessageParserService;
import com.example.demo.service.telegramService.TelegramMessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TelegramMessageParserServiceImpl implements TelegramMessageParserService {
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Autowired
    private TelegramGetMenuEventService telegramGetMenuEventService;
    @Autowired
    private TelegramMessageParserHelperService telegramMessageParserHelperService;
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private TelegramOrderingEventService telegramOrderingEventService;
    @Autowired
    private TelegramCreatingOwnCroissantEventService telegramCreatingOwnCroissantEventService;
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

            default:
                telegramMessageSenderService.errorMessage(message);
                break;
        }
    }
    private void deleteOrderings(Message message, TUser tUser) {
        telegramMessageParserHelperService.helpDeleteOrderings(message);
    }
    private void createOwn(Message message) {
        telegramCreatingOwnCroissantEventService.createOwn(message);
    }

    private void makeOrder(Message message) {
        telegramOrderingEventService.makeOrder(message);
    }

    private void menu(Message message, TUser tUser) {
        telegramUserRepositoryService.changeStatus(tUser, TelegramUserStatus.ASKING_TYPE_STATUS);
        telegramGetMenuEventService.getMenu(message);
    }

    private void start(Message message) {
            telegramMessageParserHelperService.helpStart(message);

        telegramMessageSenderService.sendActions(message);
    }
}
