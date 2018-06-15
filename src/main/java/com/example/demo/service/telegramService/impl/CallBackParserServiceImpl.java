package com.example.demo.service.telegramService.impl;

import com.example.demo.entity.lvivCroissants.CroissantEntity;
import com.example.demo.entity.lvivCroissants.CustomerOrdering;
import com.example.demo.entity.peopleRegister.TUser;
import com.example.demo.constantEnum.messengerEnums.PayloadCases;
import com.example.demo.constantEnum.telegramEnums.CallBackData;
import com.example.demo.constantEnum.telegramEnums.TelegramUserStatus;
import com.example.demo.dto.telegram.CallBackQuery;
import com.example.demo.dto.telegram.Chat;
import com.example.demo.dto.telegram.Message;
import com.example.demo.service.eventService.telegramEventService.TelegramCreatingOwnCroissantEventService;
import com.example.demo.service.eventService.telegramEventService.TelegramGetMenuEventService;
import com.example.demo.service.eventService.telegramEventService.TelegramOrderingEventService;
import com.example.demo.service.repositoryService.CroissantRepositoryService;
import com.example.demo.service.peopleRegisterService.TelegramUserRepositoryService;
import com.example.demo.service.repositoryService.CustomerOrderingRepositoryService;
import com.example.demo.service.supportService.TextFormatter;
import com.example.demo.service.telegramService.CallBackParserService;
import com.example.demo.service.telegramService.TelegramMessageParserService;
import com.example.demo.service.telegramService.TelegramMessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ResourceBundle;

import static com.example.demo.constantEnum.Platform.TELEGRAM_ADMIN_PANEL_BOT;
import static com.example.demo.constantEnum.messengerEnums.PayloadCases.QUESTION_YES;
import static com.example.demo.constantEnum.messengerEnums.speaking.ServerSideSpeaker.DONE;
import static com.example.demo.constantEnum.messengerEnums.speaking.ServerSideSpeaker.TECHNICAL_TROUBLE;
import static com.example.demo.constantEnum.messengerEnums.speaking.ServerSideSpeaker.THANKS;
import static com.example.demo.constantEnum.telegramEnums.TelegramUserStatus.*;

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
    @Autowired
    private CustomerOrderingRepositoryService customerOrderingRepositoryService;
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
            case CANCEL_DATA:
                cancelData(callBackQuery);
                break;
            case QUESTION_COMPLETE_DATA:
                questionCompleteData(callBackQuery);
                break;
            default:
                telegramMessageSenderService.errorMessage(callBackQuery.getMessage());                break;
        }
    }

    private void questionCompleteData(CallBackQuery callBackQuery) {
        String answer = TextFormatter.ejectVariableWithContext(callBackQuery.getData());
        long orderId = Long.parseLong(TextFormatter.ejectContext(callBackQuery.getData()));
        if(answer.equals(QUESTION_YES.name())){
            finishingOrdering(callBackQuery,orderId);
        }
        else {
            String text = ResourceBundle.getBundle("dictionary").getString(TECHNICAL_TROUBLE.name());
            telegramMessageSenderService.simpleMessage(text,callBackQuery.getMessage());
        }
    }

    private void finishingOrdering(CallBackQuery callBackQuery, long orderId) {
        CustomerOrdering customerOrdering = customerOrderingRepositoryService.findOne(orderId);
        customerOrdering.setCompletedTime(new java.util.Date().toString());
        customerOrderingRepositoryService.saveAndFlush(customerOrdering);
        String done = ResourceBundle.getBundle("dictionary").getString(DONE.name());
        String thanks = ResourceBundle.getBundle("dictionary").getString(THANKS.name());
        telegramMessageSenderService.simpleMessage(thanks,callBackQuery.getMessage());
        TUser courier = customerOrdering.getCourier();
        Message message = new Message();
        message.setPlatform(TELEGRAM_ADMIN_PANEL_BOT);
        message.setChat(new Chat(courier.getChatId()));
        telegramMessageSenderService.simpleMessage(done,message);
    }

    private void cancelData(CallBackQuery callBackQuery) {
        String id = TextFormatter.ejectSingleVariable(callBackQuery.getData());
        Long idL = Long.parseLong(id);
        CustomerOrdering customerOrdering = customerOrderingRepositoryService.findOne(idL);
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
        tUser.getCustomerOrderings().remove(customerOrdering);
        customerOrderingRepositoryService.delete(customerOrdering);
        telegramUserRepositoryService.saveAndFlush(tUser);
        String text = ResourceBundle.getBundle("dictionary").getString(DONE.name());
        telegramMessageSenderService.simpleMessage(text,callBackQuery.getMessage());

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
            telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(THANKS.name()),callBackQuery.getMessage());
            telegramMessageSenderService.sendActions(callBackQuery.getMessage());

        }
    }

    private void deleteButtonData(CallBackQuery callBackQuery) {
        Long id = Long.parseLong(TextFormatter.ejectSingleVariable(callBackQuery.getData()));
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());

        CroissantEntity croissantEntity = croissantRepositoryService.findOne(id);
        tUser.getOwnCroissantEntities().remove(croissantEntity);
        croissantEntity.setTUser(null);
        croissantRepositoryService.remove(croissantEntity);
        telegramUserRepositoryService.saveAndFlush(tUser);
        telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(DONE.name()),callBackQuery.getMessage());
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
