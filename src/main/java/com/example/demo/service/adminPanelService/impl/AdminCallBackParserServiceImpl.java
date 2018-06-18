package com.example.demo.service.adminPanelService.impl;

import com.example.demo.entity.lvivCroissants.CroissantEntity;
import com.example.demo.entity.lvivCroissants.CustomerOrdering;
import com.example.demo.entity.peopleRegister.TUser;
import com.example.demo.constantEnum.messengerEnums.Role;
import com.example.demo.constantEnum.telegramEnums.CallBackData;
import com.example.demo.constantEnum.telegramEnums.TelegramUserStatus;
import com.example.demo.dto.telegram.CallBackQuery;
import com.example.demo.dto.telegram.Chat;
import com.example.demo.dto.telegram.Message;
import com.example.demo.dto.telegram.button.KeyboardButton;
import com.example.demo.service.adminPanelService.AdminCallBackParserService;
import com.example.demo.service.adminPanelService.AdminTelegramMessageParserHelperService;
import com.example.demo.service.adminPanelService.BotCommandParseHelperService;
import com.example.demo.service.eventService.servicePanel.TelegramAddingRecordingsEventService;
import com.example.demo.service.eventService.telegramEventService.TelegramGetMenuEventService;
import com.example.demo.service.repositoryService.CroissantRepositoryService;
import com.example.demo.service.peopleRegisterService.TelegramUserRepositoryService;
import com.example.demo.service.repositoryService.CustomerOrderingRepositoryService;
import com.example.demo.service.repositoryService.SpeakingMessagesRepositoryService;
import com.example.demo.service.supportService.TextFormatter;
import com.example.demo.service.telegramService.CallBackParserService;
import com.example.demo.service.telegramService.TelegramMessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.example.demo.constantEnum.Platform.TELEGRAM_ADMIN_PANEL_BOT;
import static com.example.demo.constantEnum.messengerEnums.PayloadCases.QUESTION_YES;
import static com.example.demo.constantEnum.messengerEnums.speaking.ServerSideSpeaker.*;
import static com.example.demo.constantEnum.telegramEnums.CallBackData.SURE_TO_DELETE_DATA;
import static com.example.demo.constantEnum.telegramEnums.TelegramUserStatus.*;

@Service
public class AdminCallBackParserServiceImpl implements AdminCallBackParserService {
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private TelegramAddingRecordingsEventService telegramAddingRecordingsEventService;
    @Autowired
    private TelegramGetMenuEventService telegramGetMenuEventService;
    @Autowired
    private CallBackParserService callBackParserService;
    @Autowired
    private CroissantRepositoryService croissantRepositoryService;
    @Autowired
    private AdminTelegramMessageParserHelperService adminTelegramMessageParserHelperService;
    @Autowired
    private SpeakingMessagesRepositoryService speakingMessagesRepositoryService;
    @Autowired
    private BotCommandParseHelperService botCommandParseHelperService;
    @Autowired
    private CustomerOrderingRepositoryService customerOrderingRepositoryService;
    @Override
    public void parseAdminCallBackQuery(CallBackQuery callBackQuery) {

        switch (CallBackData.valueOf(TextFormatter.ejectPaySinglePayload(callBackQuery.getData()))){
            case CROISSANT_TYPE_ADDING_DATA:
                croissantTypeAddingData(callBackQuery);
                break;
            case CROISSANT_TYPE_DATA:
                croissantTypeData(callBackQuery);
                break;
            case DELETE_BUTTON_DATA:
                deleteButtonData(callBackQuery);
                break;
            case SURE_TO_DELETE_DATA:
                sureDeleteData(callBackQuery);
                break;
            case SET_ROLE_DATA:
                setRoleData(callBackQuery);
                break;
            case SETTING_ROLE_DATA_1:
                    settingRoleData1(callBackQuery);
                break;
            case SETTING_ROLE_DATA_2:
                settingRoleData2(callBackQuery);
                break;
            case SET_HELLO_MESSAGE_DATA:
                setHelloMessageData(callBackQuery);
                break;
            case LIST_OF_ORDERING_DATA:
                listOfOrderingsData(callBackQuery);
                break;
            case GET_ORDER_DATA:
                getOrderData(callBackQuery);
                break;
            case LIST_OF_COMPLETE_ORDERING_DATA:
                completeOrderingData(callBackQuery);
                break;
            case COMPLETE_ORDER_DATA:
                completeOrderData(callBackQuery);
                break;
                default:
                    telegramMessageSenderService.errorMessage(callBackQuery.getMessage());
                    break;
        }
    }

    private void completeOrderData(CallBackQuery callBackQuery) {
        botCommandParseHelperService.helpCompleteOrderData(callBackQuery) ;
    }

    private void completeOrderingData(CallBackQuery callBackQuery) {
        botCommandParseHelperService.helpGetListOfOrdering(callBackQuery);
    }

    private void getOrderData(CallBackQuery callBackQuery) {
        long orderId = Long.parseLong(TextFormatter.ejectSingleVariable(callBackQuery.getData()));
        CustomerOrdering customerOrdering = customerOrderingRepositoryService.findOne(orderId);
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
        tUser.addCourierOrdering(customerOrdering);
        telegramUserRepositoryService.saveAndFlush(tUser);
        String text = ResourceBundle.getBundle("dictionary").getString(DONE.name());
        telegramMessageSenderService.simpleMessage(text,callBackQuery.getMessage());

    }

    private void listOfOrderingsData(CallBackQuery callBackQuery) {
        botCommandParseHelperService.helpGetListOfOrdering(callBackQuery);
    }

    private void setHelloMessageData(CallBackQuery callBackQuery) {
        String text = ResourceBundle.getBundle("dictionary").getString(NAME_OF_NEW_TEXT.name());
        telegramMessageSenderService.simpleMessage(text,callBackQuery.getMessage());
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
        telegramUserRepositoryService.changeStatus(tUser,NAME_OF_NEW_TEXT_STATUS);
    }

    private void settingRoleData2(CallBackQuery callBackQuery) {
        String answer = TextFormatter.ejectVariableWithContext(callBackQuery.getData());
        String userName = TextFormatter.ejectContext(callBackQuery.getData());
        TUser tUser = telegramUserRepositoryService.findByUserName(userName);
        if(answer.equals(QUESTION_YES.name())){
            switch (telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId()).getStatus()){
                case SETTING_ADMIN_STATUS:
                    tUser.getUser().setRole(Role.ADMIN);
                    break;
                case SETTING_COURIER_STATUS:
                    tUser.getUser().setRole(Role.COURIER);
                    break;
                case SETTING_PERSONAL_STATUS:
                    tUser.getUser().setRole(Role.PERSONAL);
                    break;
                case SETTING_CUSTOMER_STATUS:
                    tUser.getUser().setRole(Role.CUSTOMER);
                    break;
                    default:
                        telegramMessageSenderService.errorMessage(callBackQuery.getMessage());
                        break;
            }
            telegramUserRepositoryService.saveAndFlush(tUser);
            Message message1 = new Message();
            message1.setChat(new Chat(tUser.getChatId()));
            message1.setPlatform(TELEGRAM_ADMIN_PANEL_BOT);
            telegramMessageSenderService.simpleMessage(String.format(ResourceBundle.getBundle("dictionary").getString(ROLE_SET.name()),tUser.getUser().getRole().name()),message1);
            telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(DONE.name()),callBackQuery.getMessage());
        }
        else{
            telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(THANKS.name()),callBackQuery.getMessage());
        }
    }

    private void setRoleData(CallBackQuery callBackQuery) {
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
        telegramUserRepositoryService.changeStatus(tUser,SETTING_ROLE_STATUS);
        userNameEntering(callBackQuery);
    }

    private void userNameEntering(CallBackQuery callBackQuery) {
        List<String>list = telegramUserRepositoryService.findTopUserNames();

        List<List<KeyboardButton>>keyboardButtons = new ArrayList<>();
        for(int i=0;i<10;i++){
            if(list.isEmpty())break;
            List<KeyboardButton>list1 = new ArrayList<>();
            keyboardButtons.add(list1);
            for(int j=0;j<2;j++){
                if(list.isEmpty())break;
                list1.add(new KeyboardButton(list.get(0)));
                list.remove(0);
            }
        }


        telegramMessageSenderService.sendKeyboardButtons(callBackQuery.getMessage(),keyboardButtons,"Enter username:");
    }

    private void sureDeleteData(CallBackQuery callBackQuery) {
        String answer = TextFormatter.ejectVariableWithContext(callBackQuery.getData());
        if(answer.equals(QUESTION_YES.name())){
            deleteCroissant(callBackQuery);
        }
        else {
            String text = ResourceBundle.getBundle("dictionary").getString(THANKS.name());
            telegramMessageSenderService.simpleMessage(text+" /help",callBackQuery.getMessage());
        }
    }

    private void deleteCroissant(CallBackQuery callBackQuery) {
        CroissantEntity croissantEntity = croissantRepositoryService.findOne(Long.parseLong(TextFormatter.ejectContext(callBackQuery.getData())));
        croissantRepositoryService.remove(croissantEntity);
        telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(DONE.name())+ "/help",callBackQuery.getMessage());
    }


    private void deleteButtonData(CallBackQuery callBackQuery) {
        Long id = Long.parseLong(TextFormatter.ejectSingleVariable(callBackQuery.getData()));
        CroissantEntity croissantEntity = croissantRepositoryService.findOne(id);
        String text = String.format(ResourceBundle.getBundle("dictionary").getString(SURE_DELETE_CROISSANT.name()), croissantEntity.getName());
        telegramMessageSenderService.simpleQuestion(SURE_TO_DELETE_DATA,"?"+id+"&",text,callBackQuery.getMessage());
        }

    private void croissantTypeData(CallBackQuery callBackQuery) {
        String croissantType = TextFormatter.ejectSingleVariable(callBackQuery.getData());
        Message message = new Message();
        message.setText(croissantType);
        message.setChat(callBackQuery.getMessage().getChat());
        message.setPlatform(TELEGRAM_ADMIN_PANEL_BOT);
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
        telegramUserRepositoryService.changeStatus(tUser, TelegramUserStatus.GETTING_MENU_STATUS);
        telegramGetMenuEventService.getMenu(message);
    }

    private void croissantTypeAddingData(CallBackQuery callBackQuery) {
        String data = TextFormatter.ejectSingleVariable(callBackQuery.getData());
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
        telegramUserRepositoryService.changeStatus(tUser,TelegramUserStatus.ADDING_CROISSANT_STATUS_1);
        callBackQuery.getMessage().setText(data);
        telegramAddingRecordingsEventService.addCroissant(callBackQuery.getMessage());
    }

    public void settingRoleData1(CallBackQuery callBackQuery) {
        String context = TextFormatter.ejectContext(callBackQuery.getData());
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
        switch (Role.valueOf(context)){
            case ADMIN:
                settingRole(callBackQuery,tUser,SETTING_ADMIN_STATUS);
                break;
            case PERSONAL:
                settingRole(callBackQuery,tUser,SETTING_PERSONAL_STATUS);

                break;
            case COURIER:
                settingRole(callBackQuery,tUser,SETTING_COURIER_STATUS);
                break;
            case CUSTOMER:
                      settingRole(callBackQuery,tUser,SETTING_CUSTOMER_STATUS);
                break;
        }
    }


    private void settingRole(CallBackQuery callBackQuery, TUser tUser, TelegramUserStatus telegramUserStatus){
        telegramUserRepositoryService.changeStatus(tUser,telegramUserStatus);
        callBackQuery.getMessage().setText(TextFormatter.ejectVariableWithContext(callBackQuery.getData()));
        adminTelegramMessageParserHelperService.helpSetRole(callBackQuery.getMessage());
    }
}
