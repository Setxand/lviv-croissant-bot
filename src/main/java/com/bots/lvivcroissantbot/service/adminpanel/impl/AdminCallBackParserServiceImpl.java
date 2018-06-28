package com.bots.lvivcroissantbot.service.adminpanel.impl;

import com.bots.lvivcroissantbot.constantenum.messenger.Role;
import com.bots.lvivcroissantbot.constantenum.telegram.CallBackData;
import com.bots.lvivcroissantbot.constantenum.telegram.TelegramUserStatus;
import com.bots.lvivcroissantbot.dto.telegram.CallBackQuery;
import com.bots.lvivcroissantbot.dto.telegram.Chat;
import com.bots.lvivcroissantbot.dto.telegram.Message;
import com.bots.lvivcroissantbot.dto.telegram.button.KeyboardButton;
import com.bots.lvivcroissantbot.entity.lvivcroissants.CroissantEntity;
import com.bots.lvivcroissantbot.entity.lvivcroissants.CustomerOrdering;
import com.bots.lvivcroissantbot.entity.register.TUser;
import com.bots.lvivcroissantbot.exception.ElementNoFoundException;
import com.bots.lvivcroissantbot.repository.CroissantEntityRepository;
import com.bots.lvivcroissantbot.repository.CustomerOrderingRepository;
import com.bots.lvivcroissantbot.repository.SpeakingMessagesRepository;
import com.bots.lvivcroissantbot.service.adminpanel.AdminCallBackParserService;
import com.bots.lvivcroissantbot.service.adminpanel.AdminTelegramMessageParserHelperService;
import com.bots.lvivcroissantbot.service.adminpanel.BotCommandParseHelperService;
import com.bots.lvivcroissantbot.service.adminpanel.event.TelegramAddingRecordingsEventService;
import com.bots.lvivcroissantbot.service.peopleregister.TelegramUserRepositoryService;
import com.bots.lvivcroissantbot.service.support.TextFormatter;
import com.bots.lvivcroissantbot.service.telegram.CallBackParserService;
import com.bots.lvivcroissantbot.service.telegram.TelegramMessageSenderService;
import com.bots.lvivcroissantbot.service.telegram.event.TelegramGetMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.bots.lvivcroissantbot.constantenum.Platform.TELEGRAM_ADMIN_PANEL_BOT;
import static com.bots.lvivcroissantbot.constantenum.messenger.PayloadCases.QUESTION_YES;
import static com.bots.lvivcroissantbot.constantenum.messenger.speaking.ServerSideSpeaker.*;
import static com.bots.lvivcroissantbot.constantenum.telegram.CallBackData.SURE_TO_DELETE_DATA;
import static com.bots.lvivcroissantbot.constantenum.telegram.TelegramUserStatus.*;

@Service
public class AdminCallBackParserServiceImpl implements AdminCallBackParserService {
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private TelegramAddingRecordingsEventService telegramAddingRecordingsEventService;
    @Autowired
    private TelegramGetMenuService telegramGetMenuService;
    @Autowired
    private CallBackParserService callBackParserServiceService;
    @Autowired
    private CroissantEntityRepository croissantRepository;
    @Autowired
    private AdminTelegramMessageParserHelperService adminTelegramMessageParserHelperService;
    @Autowired
    private SpeakingMessagesRepository speakingMessagesRepositoryService;
    @Autowired
    private BotCommandParseHelperService botCommandParseHelperServiceService;
    @Autowired
    private CustomerOrderingRepository customerOrderingRepositoryService;

    @Override
    public void parseAdminCallBackQuery(CallBackQuery callBackQuery) {

        switch (CallBackData.valueOf(TextFormatter.ejectPaySinglePayload(callBackQuery.getData()))) {
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
        botCommandParseHelperServiceService.helpCompleteOrderData(callBackQuery);
    }

    private void completeOrderingData(CallBackQuery callBackQuery) {
        botCommandParseHelperServiceService.helpGetListOfOrdering(callBackQuery);
    }

    private void getOrderData(CallBackQuery callBackQuery) {
        long orderId = Long.parseLong(TextFormatter.ejectSingleVariable(callBackQuery.getData()));
        CustomerOrdering customerOrdering = customerOrderingRepositoryService.findById(orderId).orElseThrow(ElementNoFoundException::new);
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
        tUser.addCourierOrdering(customerOrdering);
        telegramUserRepositoryService.saveAndFlush(tUser);
        String text = ResourceBundle.getBundle("dictionary").getString(DONE.name());
        telegramMessageSenderService.simpleMessage(text, callBackQuery.getMessage());

    }

    private void listOfOrderingsData(CallBackQuery callBackQuery) {
        botCommandParseHelperServiceService.helpGetListOfOrdering(callBackQuery);
    }

    private void setHelloMessageData(CallBackQuery callBackQuery) {
        String text = ResourceBundle.getBundle("dictionary").getString(NAME_OF_NEW_TEXT.name());
        telegramMessageSenderService.simpleMessage(text, callBackQuery.getMessage());
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
        telegramUserRepositoryService.changeStatus(tUser, NAME_OF_NEW_TEXT_STATUS);
    }

    private void settingRoleData2(CallBackQuery callBackQuery) {
        String answer = TextFormatter.ejectVariableWithContext(callBackQuery.getData());
        String userName = TextFormatter.ejectContext(callBackQuery.getData());
        TUser tUser = telegramUserRepositoryService.findByUserName(userName);
        if (answer.equals(QUESTION_YES.name())) {
            switch (telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId()).getStatus()) {
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
            telegramMessageSenderService.simpleMessage(String.format(ResourceBundle.getBundle("dictionary").getString(ROLE_SET.name()), tUser.getUser().getRole().name()), message1);
            telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(DONE.name()), callBackQuery.getMessage());
        } else {
            telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(THANKS.name()), callBackQuery.getMessage());
        }
    }

    private void setRoleData(CallBackQuery callBackQuery) {
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
        telegramUserRepositoryService.changeStatus(tUser, SETTING_ROLE_STATUS);
        userNameEntering(callBackQuery);
    }

    private void userNameEntering(CallBackQuery callBackQuery) {
        List<String> list = telegramUserRepositoryService.findTopUserNames();

        List<List<KeyboardButton>> keyboardButtons = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (list.isEmpty()) break;
            List<KeyboardButton> list1 = new ArrayList<>();
            keyboardButtons.add(list1);
            for (int j = 0; j < 2; j++) {
                if (list.isEmpty()) break;
                list1.add(new KeyboardButton(list.get(0)));
                list.remove(0);
            }
        }


        telegramMessageSenderService.sendKeyboardButtons(callBackQuery.getMessage(), keyboardButtons, "Enter username:");
    }

    private void sureDeleteData(CallBackQuery callBackQuery) {
        String answer = TextFormatter.ejectVariableWithContext(callBackQuery.getData());
        if (answer.equals(QUESTION_YES.name())) {
            deleteCroissant(callBackQuery);
        } else {
            String text = ResourceBundle.getBundle("dictionary").getString(THANKS.name());
            telegramMessageSenderService.simpleMessage(text + " /help", callBackQuery.getMessage());
        }
    }

    private void deleteCroissant(CallBackQuery callBackQuery) {
        CroissantEntity croissantEntity = croissantRepository.getOne(Long.parseLong(TextFormatter.ejectContext(callBackQuery.getData())));
        croissantRepository.delete(croissantEntity);
        telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(DONE.name()) + "/help", callBackQuery.getMessage());
    }


    private void deleteButtonData(CallBackQuery callBackQuery) {
        Long id = Long.parseLong(TextFormatter.ejectSingleVariable(callBackQuery.getData()));
        CroissantEntity croissantEntity = croissantRepository.getOne(id);
        String text = String.format(ResourceBundle.getBundle("dictionary").getString(SURE_DELETE_CROISSANT.name()), croissantEntity.getName());
        telegramMessageSenderService.simpleQuestion(SURE_TO_DELETE_DATA, "?" + id + "&", text, callBackQuery.getMessage());
    }

    private void croissantTypeData(CallBackQuery callBackQuery) {
        String croissantType = TextFormatter.ejectSingleVariable(callBackQuery.getData());
        Message message = new Message();
        message.setText(croissantType);
        message.setChat(callBackQuery.getMessage().getChat());
        message.setPlatform(TELEGRAM_ADMIN_PANEL_BOT);
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
        telegramUserRepositoryService.changeStatus(tUser, TelegramUserStatus.GETTING_MENU_STATUS);
        telegramGetMenuService.getMenu(message);
    }

    private void croissantTypeAddingData(CallBackQuery callBackQuery) {
        String data = TextFormatter.ejectSingleVariable(callBackQuery.getData());
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
        telegramUserRepositoryService.changeStatus(tUser, TelegramUserStatus.ADDING_CROISSANT_STATUS_1);
        callBackQuery.getMessage().setText(data);
        telegramAddingRecordingsEventService.addCroissant(callBackQuery.getMessage());
    }

    public void settingRoleData1(CallBackQuery callBackQuery) {
        String context = TextFormatter.ejectContext(callBackQuery.getData());
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
        switch (Role.valueOf(context)) {
            case ADMIN:
                settingRole(callBackQuery, tUser, SETTING_ADMIN_STATUS);
                break;
            case PERSONAL:
                settingRole(callBackQuery, tUser, SETTING_PERSONAL_STATUS);

                break;
            case COURIER:
                settingRole(callBackQuery, tUser, SETTING_COURIER_STATUS);
                break;
            case CUSTOMER:
                settingRole(callBackQuery, tUser, SETTING_CUSTOMER_STATUS);
                break;
        }
    }


    private void settingRole(CallBackQuery callBackQuery, TUser tUser, TelegramUserStatus telegramUserStatus) {
        telegramUserRepositoryService.changeStatus(tUser, telegramUserStatus);
        callBackQuery.getMessage().setText(TextFormatter.ejectVariableWithContext(callBackQuery.getData()));
        adminTelegramMessageParserHelperService.helpSetRole(callBackQuery.getMessage());
    }
}
