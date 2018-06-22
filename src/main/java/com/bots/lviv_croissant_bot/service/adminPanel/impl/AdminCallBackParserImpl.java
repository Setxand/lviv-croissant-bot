package com.bots.lviv_croissant_bot.service.adminPanel.impl;

import com.bots.lviv_croissant_bot.entity.lvivCroissants.CroissantEntity;
import com.bots.lviv_croissant_bot.entity.lvivCroissants.CustomerOrdering;
import com.bots.lviv_croissant_bot.entity.register.TUser;
import com.bots.lviv_croissant_bot.constantEnum.messengerEnum.Role;
import com.bots.lviv_croissant_bot.constantEnum.telegramEnum.CallBackData;
import com.bots.lviv_croissant_bot.constantEnum.telegramEnum.TelegramUserStatus;
import com.bots.lviv_croissant_bot.dto.telegram.CallBackQuery;
import com.bots.lviv_croissant_bot.dto.telegram.Chat;
import com.bots.lviv_croissant_bot.dto.telegram.Message;
import com.bots.lviv_croissant_bot.dto.telegram.button.KeyboardButton;
import com.bots.lviv_croissant_bot.exception.ElementNoFoundException;
import com.bots.lviv_croissant_bot.repository.CustomerOrderingRepository;
import com.bots.lviv_croissant_bot.repository.SpeakingMessagesRepository;
import com.bots.lviv_croissant_bot.service.adminPanel.AdminCallBackParser;
import com.bots.lviv_croissant_bot.service.adminPanel.AdminTelegramMessageParserHelper;
import com.bots.lviv_croissant_bot.service.adminPanel.BotCommandParseHelper;
import com.bots.lviv_croissant_bot.service.adminPanel.event.TelegramAddingRecordingsEventService;
import com.bots.lviv_croissant_bot.service.telegram.event.TelegramGetMenu;
import com.bots.lviv_croissant_bot.service.peopleRegister.TelegramUserRepositoryService;
import com.bots.lviv_croissant_bot.service.support.TextFormatter;
import com.bots.lviv_croissant_bot.service.telegram.CallBackParser;
import com.bots.lviv_croissant_bot.service.telegram.TelegramMessageSender;
import com.bots.lviv_croissant_bot.service.uni.CroissantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.bots.lviv_croissant_bot.constantEnum.Platform.TELEGRAM_ADMIN_PANEL_BOT;
import static com.bots.lviv_croissant_bot.constantEnum.messengerEnum.PayloadCases.QUESTION_YES;
import static com.bots.lviv_croissant_bot.constantEnum.messengerEnum.speaking.ServerSideSpeaker.*;
import static com.bots.lviv_croissant_bot.constantEnum.telegramEnum.CallBackData.SURE_TO_DELETE_DATA;
import static com.bots.lviv_croissant_bot.constantEnum.telegramEnum.TelegramUserStatus.*;

@Service
public class AdminCallBackParserImpl implements AdminCallBackParser {
    @Autowired
    private TelegramMessageSender telegramMessageSender;
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private TelegramAddingRecordingsEventService telegramAddingRecordingsEventService;
    @Autowired
    private TelegramGetMenu telegramGetMenu;
    @Autowired
    private CallBackParser callBackParserService;
    @Autowired
    private CroissantService croissantRepositoryService;
    @Autowired
    private AdminTelegramMessageParserHelper adminTelegramMessageParserHelper;
    @Autowired
    private SpeakingMessagesRepository speakingMessagesRepositoryService;
    @Autowired
    private BotCommandParseHelper botCommandParseHelperService;
    @Autowired
    private CustomerOrderingRepository customerOrderingRepositoryService;
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
                    telegramMessageSender.errorMessage(callBackQuery.getMessage());
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
        CustomerOrdering customerOrdering = customerOrderingRepositoryService.findById(orderId).orElseThrow(ElementNoFoundException::new);
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
        tUser.addCourierOrdering(customerOrdering);
        telegramUserRepositoryService.saveAndFlush(tUser);
        String text = ResourceBundle.getBundle("dictionary").getString(DONE.name());
        telegramMessageSender.simpleMessage(text,callBackQuery.getMessage());

    }

    private void listOfOrderingsData(CallBackQuery callBackQuery) {
        botCommandParseHelperService.helpGetListOfOrdering(callBackQuery);
    }

    private void setHelloMessageData(CallBackQuery callBackQuery) {
        String text = ResourceBundle.getBundle("dictionary").getString(NAME_OF_NEW_TEXT.name());
        telegramMessageSender.simpleMessage(text,callBackQuery.getMessage());
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
                        telegramMessageSender.errorMessage(callBackQuery.getMessage());
                        break;
            }
            telegramUserRepositoryService.saveAndFlush(tUser);
            Message message1 = new Message();
            message1.setChat(new Chat(tUser.getChatId()));
            message1.setPlatform(TELEGRAM_ADMIN_PANEL_BOT);
            telegramMessageSender.simpleMessage(String.format(ResourceBundle.getBundle("dictionary").getString(ROLE_SET.name()),tUser.getUser().getRole().name()),message1);
            telegramMessageSender.simpleMessage(ResourceBundle.getBundle("dictionary").getString(DONE.name()),callBackQuery.getMessage());
        }
        else{
            telegramMessageSender.simpleMessage(ResourceBundle.getBundle("dictionary").getString(THANKS.name()),callBackQuery.getMessage());
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


        telegramMessageSender.sendKeyboardButtons(callBackQuery.getMessage(),keyboardButtons,"Enter username:");
    }

    private void sureDeleteData(CallBackQuery callBackQuery) {
        String answer = TextFormatter.ejectVariableWithContext(callBackQuery.getData());
        if(answer.equals(QUESTION_YES.name())){
            deleteCroissant(callBackQuery);
        }
        else {
            String text = ResourceBundle.getBundle("dictionary").getString(THANKS.name());
            telegramMessageSender.simpleMessage(text+" /help",callBackQuery.getMessage());
        }
    }

    private void deleteCroissant(CallBackQuery callBackQuery) {
        CroissantEntity croissantEntity = croissantRepositoryService.findOne(Long.parseLong(TextFormatter.ejectContext(callBackQuery.getData())));
        croissantRepositoryService.remove(croissantEntity);
        telegramMessageSender.simpleMessage(ResourceBundle.getBundle("dictionary").getString(DONE.name())+ "/help",callBackQuery.getMessage());
    }


    private void deleteButtonData(CallBackQuery callBackQuery) {
        Long id = Long.parseLong(TextFormatter.ejectSingleVariable(callBackQuery.getData()));
        CroissantEntity croissantEntity = croissantRepositoryService.findOne(id);
        String text = String.format(ResourceBundle.getBundle("dictionary").getString(SURE_DELETE_CROISSANT.name()), croissantEntity.getName());
        telegramMessageSender.simpleQuestion(SURE_TO_DELETE_DATA,"?"+id+"&",text,callBackQuery.getMessage());
        }

    private void croissantTypeData(CallBackQuery callBackQuery) {
        String croissantType = TextFormatter.ejectSingleVariable(callBackQuery.getData());
        Message message = new Message();
        message.setText(croissantType);
        message.setChat(callBackQuery.getMessage().getChat());
        message.setPlatform(TELEGRAM_ADMIN_PANEL_BOT);
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
        telegramUserRepositoryService.changeStatus(tUser, TelegramUserStatus.GETTING_MENU_STATUS);
        telegramGetMenu.getMenu(message);
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
        adminTelegramMessageParserHelper.helpSetRole(callBackQuery.getMessage());
    }
}
