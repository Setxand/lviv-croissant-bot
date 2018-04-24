package com.example.demo.services.telegramService.impl;

import com.example.demo.entities.lvivCroissants.Croissant;
import com.example.demo.entities.peopleRegister.TUser;
import com.example.demo.enums.messengerEnums.PayloadCases;
import com.example.demo.enums.telegramEnums.CallBackData;
import com.example.demo.enums.telegramEnums.TelegramUserStatus;
import com.example.demo.models.telegram.CallBackQuery;
import com.example.demo.models.telegram.Message;
import com.example.demo.services.eventService.telegramEventService.TelegramCreatingOwnCroissantEventService;
import com.example.demo.services.eventService.telegramEventService.TelegramGetMenuEventService;
import com.example.demo.services.eventService.telegramEventService.TelegramOrderingEventService;
import com.example.demo.services.lvivCroissantRepositoryService.CroissantRepositoryService;
import com.example.demo.services.peopleRegisterService.TelegramUserRepositoryService;
import com.example.demo.services.supportService.TextFormatter;
import com.example.demo.services.telegramService.CallBackParserService;
import com.example.demo.services.telegramService.TelegramMessageParserService;
import com.example.demo.services.telegramService.TelegramMessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ResourceBundle;

import static com.example.demo.enums.messengerEnums.PayloadCases.QUESTION_YES;
import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.DONE;
import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.THANKS;
import static com.example.demo.enums.telegramEnums.TelegramUserStatus.*;

@Service
public class CallBackParserServiceImpl implements CallBackParserService {
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Autowired
    private CroissantRepositoryService croissantRepositoryService;
    @Autowired
    private TelegramGetMenuEventService telegramGetMenuEventService;
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private TelegramOrderingEventService telegramOrderingEventService;
    @Autowired
    private TelegramMessageParserService telegramMessageParserService;
    @Autowired
    private TelegramCreatingOwnCroissantEventService telegramCreatingOwnCroissantEventService;
    @Override
    public void parseCallBackQuery(CallBackQuery callBackQuery) {
        switch (CallBackData.valueOf(TextFormatter.ejectPaySinglePayload(callBackQuery.getData()))){
            case CROISSANT_TYPE_DATA:
                croissantTypeData(callBackQuery);
                break;
            case ORDERING_DATA:
                orderingData(callBackQuery);
                break;
            case MENU_DATA:
                menuData(callBackQuery);
                break;
            case CREATE_OWN_CROISSANT_DATA:
                createOwnCroissantData(callBackQuery);
                break;
            case DELETE_BUTTON_DATA:
                deleteButtonData(callBackQuery);
                break;
            case CREATE_OWN_QUESTION_DATA:
                createOwnQuestionData(callBackQuery);
                break;
            case ONE_MORE_ORDERING_DATA:
                oneMoreOrderingData(callBackQuery);
                break;
            default:
                telegramMessageSenderService.errorMessage(callBackQuery.getMessage().getChat().getId());
                break;
        }
    }

    private void oneMoreOrderingData(CallBackQuery callBackQuery) {
        String answer = TextFormatter.ejectSingleVariable(callBackQuery.getData());
        if(answer.equals(QUESTION_YES.name())){
            TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
            telegramUserRepositoryService.changeStatus(tUser,ONE_MORE_ORDERING_STATUS);
            telegramGetMenuEventService.getMenu(callBackQuery.getMessage());
        }
        else {
            telegramOrderingEventService.ifNoMore(callBackQuery.getMessage());
        }
    }

    private void createOwnQuestionData(CallBackQuery callBackQuery) {
        String answer = TextFormatter.ejectSingleVariable(callBackQuery.getData());
        if(PayloadCases.valueOf(answer) == QUESTION_YES){
            createOwnCroissantData(callBackQuery);
        }
        else {
            telegramMessageSenderService.simpleMessage(callBackQuery.getMessage().getChat().getId(),ResourceBundle.getBundle("dictionary").getString(THANKS.name()));
            telegramMessageSenderService.sendActions(callBackQuery.getMessage().getChat().getId());

        }
    }

    private void deleteButtonData(CallBackQuery callBackQuery) {
        Long id = Long.parseLong(TextFormatter.ejectSingleVariable(callBackQuery.getData()));
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());

        Croissant croissant = croissantRepositoryService.findOne(id);
        tUser.getOwnCroissants().remove(croissant);
        croissant.setTUser(null);
        croissantRepositoryService.remove(croissant);
        telegramUserRepositoryService.saveAndFlush(tUser);
        telegramMessageSenderService.simpleMessage(callBackQuery.getMessage().getChat().getId(), ResourceBundle.getBundle("dictionary").getString(DONE.name()));
    }

    private void createOwnCroissantData(CallBackQuery callBackQuery) {
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
        telegramUserRepositoryService.changeStatus(tUser,OWN_MENU_STATUS);
       telegramCreatingOwnCroissantEventService.createOwn(callBackQuery.getMessage());

    }

    private void menuData(CallBackQuery callBackQuery) {
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
        telegramUserRepositoryService.changeStatus(tUser, TelegramUserStatus.ASKING_TYPE_STATUS);
        telegramGetMenuEventService.getMenu(callBackQuery.getMessage());
    }

    private void orderingData(CallBackQuery callBackQuery) {
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
        Message message = new Message();
        message.setText(TextFormatter.ejectSingleVariable(callBackQuery.getData()));
        message.setChat(callBackQuery.getMessage().getChat());

        if(tUser.getStatus()!=ONE_MORE_ORDERING_GETTING_MENU_STATUS)
        telegramUserRepositoryService.changeStatus(tUser,TEL_NUMBER_ORDERING_STATUS);
        telegramOrderingEventService.makeOrder(message);
    }

    private void croissantTypeData(CallBackQuery callBackQuery) {
        String croissantType = TextFormatter.ejectSingleVariable(callBackQuery.getData());
        Message message = new Message();
        message.setText(croissantType);
        message.setChat(callBackQuery.getMessage().getChat());
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
        if(tUser.getStatus()==ONE_MORE_ORDERING_STATUS)
            telegramUserRepositoryService.changeStatus(tUser,ONE_MORE_ORDERING_GETTING_MENU_STATUS);
        else
            telegramUserRepositoryService.changeStatus(tUser, TelegramUserStatus.GETTING_MENU_STATUS);
        telegramGetMenuEventService.getMenu(message);
    }

    
}
