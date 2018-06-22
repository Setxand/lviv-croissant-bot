package com.bots.lviv_croissant_bot.service.adminPanel.impl;

import com.bots.lviv_croissant_bot.entity.register.TUser;

import com.bots.lviv_croissant_bot.dto.telegram.Message;
import com.bots.lviv_croissant_bot.service.adminPanel.AdminTelegramMessageParserHelperService;
import com.bots.lviv_croissant_bot.service.adminPanel.AdminTelegramMessageParserService;
import com.bots.lviv_croissant_bot.service.adminPanel.BotCommandsParserService;
import com.bots.lviv_croissant_bot.service.adminPanel.event.TelegramAddingRecordingsEventService;
import com.bots.lviv_croissant_bot.service.peopleRegister.TelegramUserRepositoryService;
import com.bots.lviv_croissant_bot.service.telegram.TelegramMessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bots.lviv_croissant_bot.dto.telegram.TelegramEntity;
import java.util.List;

import static com.bots.lviv_croissant_bot.constantEnum.telegramEnum.TelegramUserStatus.ADDING_FILLING_STATUS_1;

@Service
public class AdminTelegramMessageParserServiceImpl implements AdminTelegramMessageParserService {
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private BotCommandsParserService botCommandsParserServiceService;
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Autowired
    private TelegramAddingRecordingsEventService telegramAddingRecordingsEventService;
    @Autowired
    private AdminTelegramMessageParserHelperService adminTelegramMessageParserHelperService;
    @Override
    public void parseMessage(Message message) {
        if (message.getEntities() != null) {
            List<TelegramEntity> entities = message.getEntities();
            for (TelegramEntity telegramEntity : entities) {
                if(telegramEntity.getType().equals("url")){
                    checkingByStatus(message);
                    break;
                }
                botCommandsParserServiceService.parseBotCommand(message);
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
                adminTelegramMessageParserHelperService.helpRoleQuestion(message);
                break;
            case SETTING_ADMIN_STATUS:
                adminTelegramMessageParserHelperService.helpSetRole(message);
                break;
            case NAME_OF_NEW_TEXT_STATUS:
                adminTelegramMessageParserHelperService.helpChangeHelloMessage(message);
                break;
            default:
                telegramMessageSenderService.errorMessage(message);
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
