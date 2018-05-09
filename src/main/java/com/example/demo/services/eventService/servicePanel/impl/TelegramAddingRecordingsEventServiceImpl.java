package com.example.demo.services.eventService.servicePanel.impl;

import com.example.demo.entities.lvivCroissants.Croissant;
import com.example.demo.entities.lvivCroissants.CroissantsFilling;
import com.example.demo.entities.lvivCroissants.MenuOfFilling;
import com.example.demo.entities.peopleRegister.TUser;
import com.example.demo.models.telegram.Message;
import com.example.demo.models.telegram.buttons.InlineKeyboardButton;
import com.example.demo.services.eventService.servicePanel.TelegramAddingRecordingsEventService;
import com.example.demo.services.eventService.telegramEventService.TelegramGetMenuEventService;
import com.example.demo.services.repositoryService.CroissantRepositoryService;
import com.example.demo.services.repositoryService.MenuOfFillingRepositoryService;
import com.example.demo.services.peopleRegisterService.TelegramUserRepositoryService;
import com.example.demo.services.supportService.TextFormatter;
import com.example.demo.services.telegramService.TelegramMessageSenderService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.*;
import static com.example.demo.enums.telegramEnums.CallBackData.CROISSANT_TYPE_ADDING_DATA;
import static com.example.demo.enums.telegramEnums.TelegramUserStatus.NULL_CHECKING_ADDING_CROISSANT_STATUS;
import static com.example.demo.enums.telegramEnums.TelegramUserStatus.NULL_CHECKING_ADDING_CROISSANT_STATUS_1;

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
    @Autowired
    private CroissantRepositoryService croissantRepositoryService;


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
                addingCroissantStatus(message, tUser);
                break;
            case ADDING_CROISSANT_STATUS_1:
                addingCroissantStatus1(message);
                break;
            default:
                nullChecking(message);
                break;
        }
    }

    private void addingCroissantStatus1(Message message) {
        Croissant croissant = new Croissant();
        String type = ResourceBundle.getBundle("dictionary", new Locale("ua")).getString(message.getText());
        croissant.setType(message.getText());
        int id = message.getChat().getId();
        croissant.setCreatorId((long) id);
        croissantRepositoryService.saveAndFlush(croissant);

        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        telegramUserRepositoryService.changeStatus(tUser, NULL_CHECKING_ADDING_CROISSANT_STATUS);
        nullChecking(message);
    }

    private void nullChecking(Message message) {
        Croissant croissant = croissantRepositoryService.findLastByCreatorId((long) message.getChat().getId());
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        if (croissant.getName() == null) {
            createNameForCroissant(message, croissant, tUser);
        } else if (croissant.getImageUrl() == null) {
            addingImageForCroissant(message, croissant, tUser);
        } else if (croissant.getCroissantsFillings().isEmpty()) {
            addingCroissantsFillings(message, croissant, tUser);
        } else if (croissant.getPrice() == 0) {
            settingCroissantPrice(message, croissant, tUser);
        } else {
            finalSavingCroissant(message, tUser);
        }
    }

    private void finalSavingCroissant(Message message, TUser tUser) {
        String text = ResourceBundle.getBundle("dictionary").getString(CROISSANT_SUCCESSFULLY_ADDED.name());
        telegramUserRepositoryService.changeStatus(tUser, null);
        telegramMessageSenderService.simpleMessage(text + " /help", message);
    }

    private void settingCroissantPrice(Message message, Croissant croissant, TUser tUser) {
        if (tUser.getStatus() == NULL_CHECKING_ADDING_CROISSANT_STATUS) {
            nullCheckingStatus(message, tUser, ASK_PRICE.name());
        } else if (tUser.getStatus() == NULL_CHECKING_ADDING_CROISSANT_STATUS_1) {
            try {
                croissant.setPrice(Integer.parseInt(message.getText()));
                croissantRepositoryService.saveAndFlush(croissant);
                telegramUserRepositoryService.changeStatus(tUser, NULL_CHECKING_ADDING_CROISSANT_STATUS);
                nullChecking(message);
            } catch (Exception ex) {
                nonCorrectInputInCroissantSaving(message, NON_CORRECT_FORMAT_OF_PRICE.name(), ASK_PRICE.name());

            }
        }
    }

    private void nullCheckingStatus(Message message, TUser tUser, String rBString) {
        telegramUserRepositoryService.changeStatus(tUser, NULL_CHECKING_ADDING_CROISSANT_STATUS_1);
        String text = ResourceBundle.getBundle("dictionary").getString(rBString);
        telegramMessageSenderService.simpleMessage(text, message);
    }

    private void addingCroissantsFillings(Message message, Croissant croissant, TUser tUser) {
        if (tUser.getStatus() == NULL_CHECKING_ADDING_CROISSANT_STATUS) {
            telegramGetMenuEventService.getMenuOfFillings(message);
            nullCheckingStatus(message, tUser, ID_OF_FILLING.name());
        } else if (tUser.getStatus() == NULL_CHECKING_ADDING_CROISSANT_STATUS_1) {
            addingFillings(message, croissant, tUser);
        }
    }

    private void addingFillings(Message message, Croissant croissant, TUser tUser) {
        try {
            String[] fillings = message.getText().split(",");
            for (String filling : fillings) {
                MenuOfFilling menuOfFilling = menuOfFillingRepositoryService.findOne(Long.parseLong(filling));
                croissant.addSingleFilling(new CroissantsFilling(menuOfFilling));
            }
            croissantRepositoryService.saveAndFlush(croissant);
            telegramUserRepositoryService.changeStatus(tUser, NULL_CHECKING_ADDING_CROISSANT_STATUS);
            nullChecking(message);
        } catch (Exception ex) {
            nonCorrectInputInCroissantSaving(message, NON_CORRECT_FORMAT_OF_FILLING.name(), ID_OF_FILLING.name());
        }
    }

    private void nonCorrectInputInCroissantSaving(Message message, String nonCorrectMessage, String directions) {
        String nonCorrect = ResourceBundle.getBundle("dictionary").getString(nonCorrectMessage);
        String fillingId = ResourceBundle.getBundle("dictionary").getString(directions);
        telegramMessageSenderService.simpleMessage(nonCorrect, message);
        telegramMessageSenderService.simpleMessage(fillingId, message);
    }

    private void addingImageForCroissant(Message message, Croissant croissant, TUser tUser) {
        if (tUser.getStatus() == NULL_CHECKING_ADDING_CROISSANT_STATUS) {
            nullCheckingStatus(message, tUser, IMAGE_URL.name());
        } else if (tUser.getStatus() == NULL_CHECKING_ADDING_CROISSANT_STATUS_1) {
            croissant.setImageUrl(message.getText());
            croissantRepositoryService.saveAndFlush(croissant);
            telegramUserRepositoryService.changeStatus(tUser, NULL_CHECKING_ADDING_CROISSANT_STATUS);
            nullChecking(message);

        }
    }

    private void createNameForCroissant(Message message, Croissant croissant, TUser tUser) {
        if (tUser.getStatus() == NULL_CHECKING_ADDING_CROISSANT_STATUS) {
            nullCheckingStatus(message, tUser, NAMING_CROISSANT.name());
        } else if (tUser.getStatus() == NULL_CHECKING_ADDING_CROISSANT_STATUS_1) {
            croissant.setName(message.getText());
            croissantRepositoryService.saveAndFlush(croissant);
            telegramUserRepositoryService.changeStatus(tUser, NULL_CHECKING_ADDING_CROISSANT_STATUS);
            nullChecking(message);

        }
    }

    private void addingCroissantStatus(Message message, TUser tUser) {
        String text = ResourceBundle.getBundle("dictionary").getString(CHOOSE_TYPE_CROISSANT.name());
        String sweet = ResourceBundle.getBundle("dictionary").getString(SWEET.name());
        String sandwich = ResourceBundle.getBundle("dictionary").getString(SANDWICH.name());
        List<InlineKeyboardButton> buttons = new ArrayList<>(Arrays.asList(new InlineKeyboardButton(sweet, CROISSANT_TYPE_ADDING_DATA.name() + "?" + SWEET.name()),
                new InlineKeyboardButton(sandwich, CROISSANT_TYPE_ADDING_DATA.name() + "?" + SANDWICH.name())));
        telegramMessageSenderService.sendInlineButtons(new ArrayList<>(Arrays.asList(buttons)), text, message);
    }

    private void addingFillingStatus1(Message message, TUser tUser) {
        try {
            inputHandling(message, tUser);
        } catch (Exception ex) {
            nonCorrectInputInCroissantSaving(message, NON_CORRECT_FORMAT_OF_FILLING.name(), NAME_OF_FILLING.name());
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

    private void finalSaving(Message message, TUser tUser) {
        String addingDone = ResourceBundle.getBundle("dictionary").getString(FILLING_WAS_ADDED.name());
        telegramMessageSenderService.simpleMessage(addingDone+" /help", message);
        telegramGetMenuEventService.getMenuOfFillings(message);
        telegramUserRepositoryService.changeStatus(tUser, null);
    }

    private void addingFillingStatus(Message message) {
        String text = ResourceBundle.getBundle("dictionary").getString(NAME_OF_FILLING.name());
        telegramMessageSenderService.simpleMessage(text, message);
    }
}
