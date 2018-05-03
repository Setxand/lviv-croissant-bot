package com.example.demo.services.adminPanelService.impl;

import com.example.demo.entities.lvivCroissants.Croissant;
import com.example.demo.entities.peopleRegister.TUser;
import com.example.demo.enums.messengerEnums.Roles;
import com.example.demo.enums.telegramEnums.CallBackData;
import com.example.demo.enums.telegramEnums.TelegramUserStatus;
import com.example.demo.models.telegram.CallBackQuery;
import com.example.demo.models.telegram.Message;
import com.example.demo.services.adminPanelService.AdminCallBackParserService;
import com.example.demo.services.adminPanelService.AdminTelegramMessageParserHelperService;
import com.example.demo.services.eventService.servicePanel.TelegramAddingRecordingsEventService;
import com.example.demo.services.eventService.telegramEventService.TelegramGetMenuEventService;
import com.example.demo.services.lvivCroissantRepositoryService.CroissantRepositoryService;
import com.example.demo.services.peopleRegisterService.TelegramUserRepositoryService;
import com.example.demo.services.supportService.TextFormatter;
import com.example.demo.services.telegramService.CallBackParserService;
import com.example.demo.services.telegramService.TelegramMessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ResourceBundle;

import static com.example.demo.enums.Platform.TELEGRAM_ADMIN_PANEL_BOT;
import static com.example.demo.enums.messengerEnums.PayloadCases.QUESTION_YES;
import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.DONE;
import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.SURE_DELETE_CROISSANT;
import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.THANKS;
import static com.example.demo.enums.telegramEnums.CallBackData.SURE_TO_DELETE_DATA;
import static com.example.demo.enums.telegramEnums.TelegramUserStatus.*;

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
                default:
                    telegramMessageSenderService.errorMessage(callBackQuery.getMessage());
                    break;
        }
    }

    private void settingRoleData2(CallBackQuery callBackQuery) {
        String answer = TextFormatter.ejectVariableWithContext(callBackQuery.getData());
        String userName = TextFormatter.ejectContext(callBackQuery.getData());
        TUser tUser = telegramUserRepositoryService.findByUserName(userName);
        if(answer.equals(QUESTION_YES.name())){
            switch (telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId()).getStatus()){
                case SETTING_ADMIN_STATUS:
                    tUser.setRole(Roles.ADMIN);
                    break;
                case SETTING_COURIER_STATUS:
                    tUser.setRole(Roles.COURIER);
                    break;
                case SETTING_PERSONAL_STATUS:
                    tUser.setRole(Roles.PERSONAL);
                    break;
                case SETTING_CUSTOMER_STATUS:
                    tUser.setRole(Roles.CUSTOMER);
                    break;
                    default:
                        telegramMessageSenderService.errorMessage(callBackQuery.getMessage());
                        break;
            }
            telegramUserRepositoryService.saveAndFlush(tUser);
            telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(DONE.name()),callBackQuery.getMessage());
        }
        else{
            telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(THANKS.name()),callBackQuery.getMessage());
        }
    }

    private void setRoleData(CallBackQuery callBackQuery) {
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
        telegramUserRepositoryService.changeStatus(tUser,SETTING_ROLE_STATUS);
        telegramMessageSenderService.simpleMessage("Enter username:",callBackQuery.getMessage());
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
        Croissant croissant = croissantRepositoryService.findOne(Long.parseLong(TextFormatter.ejectContext(callBackQuery.getData())));
        croissantRepositoryService.remove(croissant);
        telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(DONE.name())+ "/help",callBackQuery.getMessage());
    }


    private void deleteButtonData(CallBackQuery callBackQuery) {
        Long id = Long.parseLong(TextFormatter.ejectSingleVariable(callBackQuery.getData()));
        Croissant croissant = croissantRepositoryService.findOne(id);
        String text = String.format(ResourceBundle.getBundle("dictionary").getString(SURE_DELETE_CROISSANT.name()),croissant.getName());
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
        switch (Roles.valueOf(context)){
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
