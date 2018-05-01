package com.example.demo.services.adminPanelService.impl;

import com.example.demo.entities.peopleRegister.TUser;
import com.example.demo.enums.telegramEnums.CallBackData;
import com.example.demo.enums.telegramEnums.TelegramUserStatus;
import com.example.demo.models.telegram.CallBackQuery;
import com.example.demo.models.telegram.Message;
import com.example.demo.repository.TelegramUserRepository;
import com.example.demo.services.adminPanelService.AdminCallBackParserService;
import com.example.demo.services.eventService.servicePanel.TelegramAddingRecordingsEventService;
import com.example.demo.services.messangerService.MessageSenderService;
import com.example.demo.services.peopleRegisterService.TelegramUserRepositoryService;
import com.example.demo.services.supportService.TextFormatter;
import com.example.demo.services.telegramService.TelegramMessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.enums.messengerEnums.types.CroissantsTypes.SWEET;

@Service
public class AdminCallBackParserServiceImpl implements AdminCallBackParserService {
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private TelegramAddingRecordingsEventService telegramAddingRecordingsEventService;
    @Override
    public void parseAdminCallBackQuery(CallBackQuery callBackQuery) {
        switch (CallBackData.valueOf(TextFormatter.ejectPaySinglePayload(callBackQuery.getData()))){
            case CROISSANT_TYPE_DATA:
                croissantTypeData(callBackQuery);
                break;
                default:
                    telegramMessageSenderService.errorMessage(callBackQuery.getMessage());
                    break;
        }
    }

    private void croissantTypeData(CallBackQuery callBackQuery) {
        String data = TextFormatter.ejectSingleVariable(callBackQuery.getData());
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
        telegramUserRepositoryService.changeStatus(tUser,TelegramUserStatus.ADDING_CROISSANT_STATUS_1);
        callBackQuery.getMessage().setText(data);
        telegramAddingRecordingsEventService.addCroissant(callBackQuery.getMessage());
    }
}
