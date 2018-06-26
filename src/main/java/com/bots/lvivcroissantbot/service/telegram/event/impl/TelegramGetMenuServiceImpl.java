package com.bots.lvivcroissantbot.service.telegram.event.impl;

import com.bots.lvivcroissantbot.constantenum.messenger.speaking.ServerSideSpeaker;
import com.bots.lvivcroissantbot.constantenum.messenger.type.CroissantsTypes;
import com.bots.lvivcroissantbot.constantenum.telegram.TelegramUserStatus;
import com.bots.lvivcroissantbot.dto.telegram.Message;
import com.bots.lvivcroissantbot.dto.telegram.button.InlineKeyboardButton;
import com.bots.lvivcroissantbot.dto.telegram.button.InlineKeyboardMarkup;
import com.bots.lvivcroissantbot.dto.telegram.button.Markup;
import com.bots.lvivcroissantbot.entity.lvivcroissants.CroissantEntity;
import com.bots.lvivcroissantbot.entity.lvivcroissants.CroissantsFilling;
import com.bots.lvivcroissantbot.entity.lvivcroissants.MenuOfFilling;
import com.bots.lvivcroissantbot.entity.register.TUser;
import com.bots.lvivcroissantbot.repository.MenuOfFillingRepository;
import com.bots.lvivcroissantbot.service.peopleregister.TelegramUserRepositoryService;
import com.bots.lvivcroissantbot.service.telegram.TelegramMessageSenderService;
import com.bots.lvivcroissantbot.service.telegram.event.TelegramGetMenuService;
import com.bots.lvivcroissantbot.service.uni.CroissantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static com.bots.lvivcroissantbot.constantenum.Platform.TELEGRAM_ADMIN_PANEL_BOT;
import static com.bots.lvivcroissantbot.constantenum.messenger.speaking.ServerSideSpeaker.*;
import static com.bots.lvivcroissantbot.constantenum.telegram.CallBackData.*;

@Service
public class TelegramGetMenuServiceImpl implements TelegramGetMenuService {
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Autowired
    private CroissantService croissantRepositoryService;
    @Autowired
    private MenuOfFillingRepository menuOfFillingRepositoryService;
    @Value("${url.server}")
    private String SERVER_URL;

    @Override
    public void getMenu(Message message) {
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        switch (tUser.getStatus()) {
            case ASKING_TYPE_STATUS:
                askType(message);
                break;
            case GETTING_MENU_STATUS:
                gettingMenu(message);
                break;
            case ONE_MORE_ORDERING_STATUS:
                askType(message);
                break;
            case ONE_MORE_ORDERING_GETTING_MENU_STATUS:
                oMoGeMeStatus(message);
                break;

            default:
                break;
        }
    }

    private void oMoGeMeStatus(Message message) {
        gettingMenu(message);
    }

    @Override
    public void getMenuOfFillings(Message message) {
        List<MenuOfFilling> menuOfFillings = menuOfFillingRepositoryService.findAll();
        String fillings = new String();
        for (MenuOfFilling menuOfFilling : menuOfFillings) {
            fillings += menuOfFilling.getId() + ". " + menuOfFilling.getName() + "\n";
        }
        telegramMessageSenderService.simpleMessage(fillings, message);

    }

    private String getFillings(CroissantEntity croissantEntity) {
        StringBuilder fillings = new StringBuilder("fillings: (");

        for (CroissantsFilling croissantsFilling : croissantEntity.getCroissantsFillings()) {
            fillings.append(croissantsFilling.getName() + ", ");
        }
        fillings.setCharAt(fillings.length() - 2, ')');
        return fillings.toString();
    }

    private void gettingMenu(Message message) {
        telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(CROISSANTS_MENU.name()), message);
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        String text = message.getText();
        if (text.equals(CroissantsTypes.OWN.name())) {
            parseOwn(tUser, message);
            return;
        }
        List<CroissantEntity> croissantEntities = croissantRepositoryService.findAllByType(text);

        for (CroissantEntity croissantEntity : croissantEntities) {
            String caption = croissantEntity.getName() + " \nprice: " + croissantEntity.getPrice() + "\n" + getFillings(croissantEntity);
            String button = MAKE_ORDER.name();
            String buttonData = ORDERING_DATA.name();
            if (message.getPlatform() == TELEGRAM_ADMIN_PANEL_BOT) {
                button = DELETE_BUTTON.name();
                buttonData = DELETE_BUTTON_DATA.name();
            }

            Markup markup = new InlineKeyboardMarkup(Arrays.asList(Arrays.asList(new InlineKeyboardButton(ResourceBundle.getBundle("dictionary").getString(button), buttonData + "?" + croissantEntity.getId()))));

            telegramMessageSenderService.sendPhoto(croissantEntity.getImageUrl(), caption, markup, message);
        }
        if (tUser.getStatus() != TelegramUserStatus.ONE_MORE_ORDERING_GETTING_MENU_STATUS && message.getPlatform() != TELEGRAM_ADMIN_PANEL_BOT) {
            telegramUserRepositoryService.changeStatus(tUser, null);
            telegramMessageSenderService.sendActions(message);
        }
    }

    private void parseOwn(TUser tUser, Message message) {
        List<CroissantEntity> croissantEntities = tUser.getOwnCroissantEntities();
        if (croissantEntities.isEmpty()) {
            telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(EMPTY_LIST.name()), message);
            telegramUserRepositoryService.changeStatus(tUser, null);
            String simpleQuestionText = ResourceBundle.getBundle("dictionary").getString(CREATE_OWN_QUESTION.name());
            telegramMessageSenderService.simpleQuestion(CREATE_OWN_QUESTION_DATA, "?", simpleQuestionText, message);
            return;
        }
        for (CroissantEntity croissantEntity : croissantEntities) {
            String caption = croissantEntity.getName() + " \nprice: " + croissantEntity.getPrice() + "\n" + getFillings(croissantEntity);
            Markup markup = new InlineKeyboardMarkup(Arrays.asList(Arrays.asList(new InlineKeyboardButton(ResourceBundle.getBundle("dictionary").getString(MAKE_ORDER.name()), ORDERING_DATA.name() + "?" + croissantEntity.getId())),
                    Arrays.asList(new InlineKeyboardButton(ResourceBundle.getBundle("dictionary").getString(DELETE_BUTTON.name()), DELETE_BUTTON_DATA.name() + "?" + croissantEntity.getId()))));
            telegramMessageSenderService.sendPhoto(croissantEntity.getImageUrl(), caption, markup, message);
        }
        telegramUserRepositoryService.changeStatus(tUser, null);

        telegramMessageSenderService.sendActions(message);


    }


    private void askType(Message message) {
        String text = ResourceBundle.getBundle("dictionary").getString(CHOOSE_TYPE_CROISSANT.name());
        String sweet = ResourceBundle.getBundle("dictionary").getString(SWEET.name());
        String own = ResourceBundle.getBundle("dictionary").getString(OWN.name());
        String sandwich = ResourceBundle.getBundle("dictionary").getString(ServerSideSpeaker.SANDWICH.name());
        List<InlineKeyboardButton> list = new ArrayList<>(Arrays.asList(new InlineKeyboardButton(sweet, CROISSANT_TYPE_DATA.name() + "?" + CroissantsTypes.SWEET.name()),
                new InlineKeyboardButton(sandwich, CROISSANT_TYPE_DATA.name() + "?" + CroissantsTypes.SANDWICH.name())));
        if (message.getPlatform() != TELEGRAM_ADMIN_PANEL_BOT)
            list.add(new InlineKeyboardButton(own, CROISSANT_TYPE_DATA.name() + "?" + CroissantsTypes.OWN.name()));
        telegramMessageSenderService.sendInlineButtons(new ArrayList<>(Arrays.asList(list)), text, message);
    }
}
