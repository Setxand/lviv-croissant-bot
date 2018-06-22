package com.bots.lviv_croissant_bot.service.adminPanel.event.impl;

import com.bots.lviv_croissant_bot.entity.lvivCroissants.CroissantEntity;
import com.bots.lviv_croissant_bot.entity.lvivCroissants.CroissantsFilling;
import com.bots.lviv_croissant_bot.entity.lvivCroissants.MenuOfFilling;
import com.bots.lviv_croissant_bot.entity.register.TUser;
import com.bots.lviv_croissant_bot.dto.telegram.Message;
import com.bots.lviv_croissant_bot.dto.telegram.button.InlineKeyboardButton;
import com.bots.lviv_croissant_bot.exception.ElementNoFoundException;
import com.bots.lviv_croissant_bot.repository.MenuOfFillingRepository;
import com.bots.lviv_croissant_bot.service.adminPanel.event.TelegramAddingRecordingsEventService;
import com.bots.lviv_croissant_bot.service.telegram.event.TelegramGetMenu;
import com.bots.lviv_croissant_bot.service.peopleRegister.TelegramUserRepositoryService;
import com.bots.lviv_croissant_bot.service.support.TextFormatter;
import com.bots.lviv_croissant_bot.service.telegram.TelegramMessageSender;
import com.bots.lviv_croissant_bot.service.uni.CroissantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.bots.lviv_croissant_bot.constantEnum.messengerEnum.speaking.ServerSideSpeaker.*;
import static com.bots.lviv_croissant_bot.constantEnum.telegramEnum.CallBackData.CROISSANT_TYPE_ADDING_DATA;
import static com.bots.lviv_croissant_bot.constantEnum.telegramEnum.TelegramUserStatus.NULL_CHECKING_ADDING_CROISSANT_STATUS;
import static com.bots.lviv_croissant_bot.constantEnum.telegramEnum.TelegramUserStatus.NULL_CHECKING_ADDING_CROISSANT_STATUS_1;

@Service
public class TelegramAddingRecordingsEventServiceImpl implements TelegramAddingRecordingsEventService {
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private TelegramMessageSender telegramMessageSender;
    @Autowired
    private MenuOfFillingRepository menuOfFillingRepositoryService;
    @Autowired
    private TelegramGetMenu telegramGetMenu;
    @Autowired
    private CroissantService croissantRepositoryService;


    private   final static Logger logger = LoggerFactory.getLogger(TelegramAddingRecordingsEventServiceImpl.class);


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
                telegramMessageSender.errorMessage(message);
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
        CroissantEntity croissantEntity = new CroissantEntity();
        String type = ResourceBundle.getBundle("dictionary", new Locale("ua")).getString(message.getText());
        croissantEntity.setType(message.getText());
        int id = message.getChat().getId();
        croissantEntity.setCreatorId((long) id);
        croissantRepositoryService.saveAndFlush(croissantEntity);

        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        telegramUserRepositoryService.changeStatus(tUser, NULL_CHECKING_ADDING_CROISSANT_STATUS);
        nullChecking(message);
    }

    private void nullChecking(Message message) {
        CroissantEntity croissantEntity = croissantRepositoryService.findLastByCreatorId((long) message.getChat().getId());
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        if (croissantEntity.getName() == null) {
            createNameForCroissant(message, croissantEntity, tUser);
        } else if (croissantEntity.getImageUrl() == null) {
            addingImageForCroissant(message, croissantEntity, tUser);
        } else if (croissantEntity.getCroissantsFillings().isEmpty()) {
            addingCroissantsFillings(message, croissantEntity, tUser);
        } else if (croissantEntity.getPrice()==0) {
            settingCroissantPrice(message, croissantEntity, tUser);
        } else {
            finalSavingCroissant(message, tUser);
        }
    }

    private void finalSavingCroissant(Message message, TUser tUser) {
        String text = ResourceBundle.getBundle("dictionary").getString(CROISSANT_SUCCESSFULLY_ADDED.name());
        telegramUserRepositoryService.changeStatus(tUser, null);
        telegramMessageSender.simpleMessage(text + " /help", message);
    }

    private void settingCroissantPrice(Message message, CroissantEntity croissantEntity, TUser tUser) {
        if (tUser.getStatus() == NULL_CHECKING_ADDING_CROISSANT_STATUS) {
            nullCheckingStatus(message, tUser, ASK_PRICE.name());
        } else if (tUser.getStatus() == NULL_CHECKING_ADDING_CROISSANT_STATUS_1) {
            try {
                croissantEntity.setPrice(Integer.parseInt(message.getText()));
                croissantRepositoryService.saveAndFlush(croissantEntity);
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
        telegramMessageSender.simpleMessage(text, message);
    }

    private void addingCroissantsFillings(Message message, CroissantEntity croissantEntity, TUser tUser) {
        if (tUser.getStatus() == NULL_CHECKING_ADDING_CROISSANT_STATUS) {
            telegramGetMenu.getMenuOfFillings(message);
            nullCheckingStatus(message, tUser, ID_OF_FILLING.name());
        } else if (tUser.getStatus() == NULL_CHECKING_ADDING_CROISSANT_STATUS_1) {
            addingFillings(message, croissantEntity, tUser);
        }
    }

    private void addingFillings(Message message, CroissantEntity croissantEntity, TUser tUser) {
        try {
            String[] fillings = message.getText().split(",");
            for (String filling : fillings) {
                MenuOfFilling menuOfFilling = menuOfFillingRepositoryService.findById(Long.parseLong(filling)).orElseThrow(ElementNoFoundException::new);
                croissantEntity.addSingleFilling(new CroissantsFilling(menuOfFilling));
            }
            croissantRepositoryService.saveAndFlush(croissantEntity);
            telegramUserRepositoryService.changeStatus(tUser, NULL_CHECKING_ADDING_CROISSANT_STATUS);
            nullChecking(message);
        } catch (Exception ex) {
            logger.error("Error",ex);
            nonCorrectInputInCroissantSaving(message, NON_CORRECT_FORMAT_OF_FILLING.name(), ID_OF_FILLING.name());
        }
    }

    private void nonCorrectInputInCroissantSaving(Message message, String nonCorrectMessage, String directions) {
        String nonCorrect = ResourceBundle.getBundle("dictionary").getString(nonCorrectMessage);
        String fillingId = ResourceBundle.getBundle("dictionary").getString(directions);
        telegramMessageSender.simpleMessage(nonCorrect, message);
        telegramMessageSender.simpleMessage(fillingId, message);
    }

    private void addingImageForCroissant(Message message, CroissantEntity croissantEntity, TUser tUser) {
        if (tUser.getStatus() == NULL_CHECKING_ADDING_CROISSANT_STATUS) {
            nullCheckingStatus(message, tUser, IMAGE_URL.name());
        } else if (tUser.getStatus() == NULL_CHECKING_ADDING_CROISSANT_STATUS_1) {
            croissantEntity.setImageUrl(message.getText());
            croissantRepositoryService.saveAndFlush(croissantEntity);
            telegramUserRepositoryService.changeStatus(tUser, NULL_CHECKING_ADDING_CROISSANT_STATUS);
            nullChecking(message);

        }
    }

    private void createNameForCroissant(Message message, CroissantEntity croissantEntity, TUser tUser) {
        if (tUser.getStatus() == NULL_CHECKING_ADDING_CROISSANT_STATUS) {
            nullCheckingStatus(message, tUser, NAMING_CROISSANT.name());
        } else if (tUser.getStatus() == NULL_CHECKING_ADDING_CROISSANT_STATUS_1) {
            croissantEntity.setName(message.getText());
            croissantRepositoryService.saveAndFlush(croissantEntity);
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
        telegramMessageSender.sendInlineButtons(new ArrayList<>(Arrays.asList(buttons)), text, message);
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
        telegramMessageSender.simpleMessage(addingDone+" /help", message);
        telegramGetMenu.getMenuOfFillings(message);
        telegramUserRepositoryService.changeStatus(tUser, null);
    }

    private void addingFillingStatus(Message message) {
        String text = ResourceBundle.getBundle("dictionary").getString(NAME_OF_FILLING.name());
        telegramMessageSender.simpleMessage(text, message);
    }
}
