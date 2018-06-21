package com.bots.lvivCroissantBot.service.adminPanel.impl;

import com.bots.lvivCroissantBot.entity.register.TUser;

import com.bots.lvivCroissantBot.dto.telegram.Message;
import com.bots.lvivCroissantBot.service.adminPanel.AdminTelegramMessageParserHelper;
import com.bots.lvivCroissantBot.service.adminPanel.AdminTelegramMessageParser;
import com.bots.lvivCroissantBot.service.adminPanel.BotCommandsParser;
import com.bots.lvivCroissantBot.service.adminPanel.event.TelegramAddingRecordingsEventService;
import com.bots.lvivCroissantBot.service.peopleRegister.TelegramUserRepositoryService;
import com.bots.lvivCroissantBot.service.telegram.TelegramMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bots.lvivCroissantBot.dto.telegram.TelegramEntity;
import java.util.List;

import static com.bots.lvivCroissantBot.constantEnum.telegramEnum.TelegramUserStatus.ADDING_FILLING_STATUS_1;

@Service
public class AdminTelegramMessageParserImpl implements AdminTelegramMessageParser {
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private BotCommandsParser botCommandsParserService;
    @Autowired
    private TelegramMessageSender telegramMessageSender;
    @Autowired
    private TelegramAddingRecordingsEventService telegramAddingRecordingsEventService;
    @Autowired
    private AdminTelegramMessageParserHelper adminTelegramMessageParserHelper;
    @Override
    public void parseMessage(Message message) {
        if (message.getEntities() != null) {
            List<TelegramEntity> entities = message.getEntities();
            for (TelegramEntity telegramEntity : entities) {
                if(telegramEntity.getType().equals("url")){
                    checkingByStatus(message);
                    break;
                }
                botCommandsParserService.parseBotCommand(message);
            }
            return;
        }
        checkingByStatus(message);

    }

    private void checkingByStatus(Message message) {
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        switch (tUser.getStatus()) {
            case ADDING_FILLING_STATUS:
                addingFillingStatus(message, tUser);
                break;
            case ADDING_FILLING_STATUS_1:
                addingFillingStatus1(message, tUser);
                break;
            case NULL_CHECKING_ADDING_CROISSANT_STATUS_1:
                telegramAddingRecordingsEventService.addCroissant(message);
                break;
            case NULL_CHECKING_ADDING_CROISSANT_STATUS:
                telegramAddingRecordingsEventService.addCroissant(message);
                break;
            case SETTING_ROLE_STATUS:
                adminTelegramMessageParserHelper.helpRoleQuestion(message);
                break;
            case SETTING_ADMIN_STATUS:
                adminTelegramMessageParserHelper.helpSetRole(message);
                break;
            case NAME_OF_NEW_TEXT_STATUS:
                adminTelegramMessageParserHelper.helpChangeHelloMessage(message);
                break;
            default:
                telegramMessageSender.errorMessage(message);
                break;

        }
    }

    private void addingFillingStatus1(Message message, TUser tUser) {
        telegramAddingRecordingsEventService.addFilling(message);
    }

    private void addingFillingStatus(Message message, TUser tUser) {
        telegramUserRepositoryService.changeStatus(tUser, ADDING_FILLING_STATUS_1);
        telegramAddingRecordingsEventService.addFilling(message);

    }
}