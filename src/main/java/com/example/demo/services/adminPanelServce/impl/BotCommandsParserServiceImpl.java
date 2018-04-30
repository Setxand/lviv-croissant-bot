package com.example.demo.services.adminPanelServce.impl;

import com.example.demo.entities.peopleRegister.TUser;
import com.example.demo.enums.telegramEnums.BotCommands;
import com.example.demo.enums.telegramEnums.TelegramUserStatus;
import com.example.demo.models.telegram.Message;
import com.example.demo.services.adminPanelServce.BotCommandsParserService;
import com.example.demo.services.eventService.telegramEventService.TelegramAddingRecordingsEventService;
import com.example.demo.services.eventService.telegramEventService.TelegramGetMenuEventService;
import com.example.demo.services.peopleRegisterService.TelegramUserRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BotCommandsParserServiceImpl implements BotCommandsParserService {
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private TelegramAddingRecordingsEventService telegramAddingRecordingsEventService;
    @Override
    public void parseBotCommand(Message message) {
        StringBuilder command = new StringBuilder(message.getText()).deleteCharAt(0);
        switch (BotCommands.valueOf(command.toString().toUpperCase())){
            case FILLING:
                filling(message);
                break;
            case ADD:
                break;
        }

    }

    private void filling(Message message) {
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        telegramUserRepositoryService.changeStatus(tUser,TelegramUserStatus.ADDING_FILLING_STATUS);
        telegramAddingRecordingsEventService.addFilling(message);
    }
}
