package com.example.demo.services.adminPanelServce.impl;

import com.example.demo.entities.peopleRegister.TUser;
import com.example.demo.models.telegram.Entity;
import com.example.demo.models.telegram.Message;
import com.example.demo.services.adminPanelServce.AdminTelegramMessageParserService;
import com.example.demo.services.adminPanelServce.BotCommandsParserService;
import com.example.demo.services.eventService.telegramEventService.TelegramAddingRecordingsEventService;
import com.example.demo.services.peopleRegisterService.TelegramUserRepositoryService;
import com.example.demo.services.telegramService.TelegramMessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.enums.telegramEnums.TelegramUserStatus.ADDING_FILLING_STATUS;
import static com.example.demo.enums.telegramEnums.TelegramUserStatus.ADDING_FILLING_STATUS_1;

@Service
public class AdminTelegramMessageParserServiceImpl implements AdminTelegramMessageParserService {
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private BotCommandsParserService botCommandsParserService;
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Autowired
    private TelegramAddingRecordingsEventService telegramAddingRecordingsEventService;

    @Override
    public void parseMessage(Message message) {
        if (message.getEntities() != null) {
            List<Entity> entities = message.getEntities();
            for (Entity entity : entities) {
                botCommandsParserService.parseBotCommand(message);
            }
            return;
        }
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        switch (tUser.getStatus()) {
            case ADDING_FILLING_STATUS:
                addingFillingStatus(message, tUser);
                break;
            case ADDING_FILLING_STATUS_1:
                addingFillingStatus1(message, tUser);
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
