package com.example.demo.services.eventService.telegramEventService.impl;

import com.example.demo.entities.lvivCroissants.MenuOfFilling;
import com.example.demo.entities.peopleRegister.TUser;
import com.example.demo.enums.telegramEnums.TelegramUserStatus;
import com.example.demo.models.telegram.Message;
import com.example.demo.services.eventService.telegramEventService.TelegramAddingRecordingsEventService;
import com.example.demo.services.eventService.telegramEventService.TelegramGetMenuEventService;
import com.example.demo.services.lvivCroissantRepositoryService.MenuOfFillingRepositoryService;
import com.example.demo.services.peopleRegisterService.TelegramUserRepositoryService;
import com.example.demo.services.supportService.TextFormatter;
import com.example.demo.services.telegramService.TelegramMessageSenderService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ResourceBundle;

import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.FILLING_WAS_ADDED;
import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.NAME_OF_FILLING;
import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.NON_CORRECT_FORMAT_OF_FILLING;

@Service
public class TelegramAddingRecordingsEventServiceImpl implements TelegramAddingRecordingsEventService {
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Autowired
    private MenuOfFillingRepositoryService menuOfFillingRepositoryService;
    @Autowired
    private TelegramGetMenuEventService telegramGetMenuEventService;
    private static final Logger logger = Logger.getLogger(TelegramAddingRecordingsEventServiceImpl.class);

    @Override
    public void addFilling(Message message) {
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        switch (tUser.getStatus()) {
            case ADDING_FILLING_STATUS:
                addingFillingStatus(message);
                break;

            case ADDING_FILLING_STATUS_1:
                addingFillingStatus1(message, tUser);
                break;
            default:
                telegramMessageSenderService.errorMessage(message);
                break;

        }
    }

    @Override
    public void addCroissant(Message message) {
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        switch (tUser.getStatus()) {
            case ADDING_CROISSANT_STATUS:
                addingCroissantStatus(message,tUser);
                break;
                default:
                    telegramMessageSenderService.errorMessage(message);
                    break;
        }
    }

    private void addingCroissantStatus(Message message, TUser tUser) {
    }

    private void addingFillingStatus1(Message message, TUser tUser) {
        try {
            inputHandling(message,tUser);
        }
        catch (Exception ex) {
            repeatInput(message);
        }
    }

    private void inputHandling(Message message, TUser tUser) {
        String text = message.getText();
        String[] filling = text.split(",");
        String nameFilling = TextFormatter.toNormalFormat(filling[0]);
        int price = Integer.parseInt(filling[1]);
        MenuOfFilling menuOfFilling = new MenuOfFilling(nameFilling, price);
        menuOfFillingRepositoryService.saveAndFlush(menuOfFilling);
        finalSaving(message, tUser);
    }

    private void repeatInput(Message message) {
        String nonCorrect = ResourceBundle.getBundle("dictionary").getString(NON_CORRECT_FORMAT_OF_FILLING.name());;
        String enterAgain = ResourceBundle.getBundle("dictionary").getString(NAME_OF_FILLING.name());
        telegramMessageSenderService.simpleMessage(nonCorrect,message);
        telegramMessageSenderService.simpleMessage(enterAgain,message);
    }

    private void finalSaving(Message message, TUser tUser) {
        String addingDone = ResourceBundle.getBundle("dictionary").getString(FILLING_WAS_ADDED.name());
        telegramMessageSenderService.simpleMessage(addingDone, message);
        telegramGetMenuEventService.getMenuOfFillings(message);
        telegramUserRepositoryService.changeStatus(tUser, null);
    }

    private void addingFillingStatus(Message message) {
        String text = ResourceBundle.getBundle("dictionary").getString(NAME_OF_FILLING.name());
        telegramMessageSenderService.simpleMessage(text, message);
    }
}
