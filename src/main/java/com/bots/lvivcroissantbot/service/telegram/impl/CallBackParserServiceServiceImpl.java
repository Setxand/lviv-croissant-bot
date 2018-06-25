package com.bots.lvivcroissantbot.service.telegram.impl;

import com.bots.lvivcroissantbot.constantenum.AccountStatus;
import com.bots.lvivcroissantbot.constantenum.messenger.Role;
import com.bots.lvivcroissantbot.entity.lvivcroissants.CroissantEntity;
import com.bots.lvivcroissantbot.entity.lvivcroissants.CustomerOrdering;
import com.bots.lvivcroissantbot.entity.register.TUser;
import com.bots.lvivcroissantbot.constantenum.messenger.PayloadCases;
import com.bots.lvivcroissantbot.constantenum.telegram.CallBackData;
import com.bots.lvivcroissantbot.constantenum.telegram.TelegramUserStatus;
import com.bots.lvivcroissantbot.dto.telegram.CallBackQuery;
import com.bots.lvivcroissantbot.dto.telegram.Chat;
import com.bots.lvivcroissantbot.dto.telegram.Message;
import com.bots.lvivcroissantbot.entity.register.User;
import com.bots.lvivcroissantbot.exception.ElementNoFoundException;
import com.bots.lvivcroissantbot.repository.CustomerOrderingRepository;
import com.bots.lvivcroissantbot.repository.UserRepository;
import com.bots.lvivcroissantbot.service.telegram.event.TelegramCreatingOwnCroissantService;
import com.bots.lvivcroissantbot.service.telegram.event.TelegramGetMenuService;
import com.bots.lvivcroissantbot.service.telegram.event.TelegramOrderingService;
import com.bots.lvivcroissantbot.service.peopleregister.TelegramUserRepositoryService;
import com.bots.lvivcroissantbot.service.support.TextFormatter;
import com.bots.lvivcroissantbot.service.telegram.CallBackParserService;
import com.bots.lvivcroissantbot.service.telegram.TelegramMessageParserHelperService;
import com.bots.lvivcroissantbot.service.telegram.TelegramMessageParserService;
import com.bots.lvivcroissantbot.service.telegram.TelegramMessageSenderService;
import com.bots.lvivcroissantbot.service.uni.CroissantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ResourceBundle;

import static com.bots.lvivcroissantbot.constantenum.Platform.TELEGRAM_ADMIN_PANEL_BOT;
import static com.bots.lvivcroissantbot.constantenum.messenger.PayloadCases.QUESTION_YES;
import static com.bots.lvivcroissantbot.constantenum.messenger.speaking.ServerSideSpeaker.*;
import static com.bots.lvivcroissantbot.constantenum.telegram.TelegramUserStatus.*;

@Service
public class CallBackParserServiceServiceImpl implements CallBackParserService {
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Autowired
    private CroissantService croissantRepositoryService;
    @Autowired
    private TelegramGetMenuService telegramGetMenuService;
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private TelegramOrderingService telegramOrderingService;
    @Autowired
    private TelegramMessageParserService telegramMessageParserService;
    @Autowired
    private TelegramCreatingOwnCroissantService telegramCreatingOwnCroissant;
    @Autowired
    private CustomerOrderingRepository customerOrderingRepositoryService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TelegramMessageParserHelperService telegramMessageParserHelperService;
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
            case QUESTION_HAVING_MESSENGER_DATA:
                questionHavingMessengerData(callBackQuery);
                break;
            case CANCEL_INPUT_NUMBER_DATA:
                cancelInpudNumberData(callBackQuery);
                break;
            case RERINPUT_NUMBER_DATA:
                telegramMessageParserHelperService.helpReinputData(callBackQuery);
                break;
            default:
                telegramMessageSenderService.errorMessage(callBackQuery.getMessage());
                break;
        }
    }



    private void cancelInpudNumberData(CallBackQuery callBackQuery) {
        TUser tUser = telegramUserRepositoryService.findByChatId(Integer.parseInt(TextFormatter.ejectSingleVariable(callBackQuery.getData())));
        User user = new User();
        user.setTUser(tUser);
        user.setRole(Role.CUSTOMER);
        user.setStatus(AccountStatus.ACTIVE);
        userRepository.saveAndFlush(user);
        telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(NEW_USER.name()),callBackQuery.getMessage());
        telegramMessageSenderService.sendActions(callBackQuery.getMessage());
    }

    private void questionHavingMessengerData(CallBackQuery callBackQuery) {
        String ans = TextFormatter.ejectSingleVariable(callBackQuery.getData());
        if(ans.equalsIgnoreCase(QUESTION_YES.name())){
            telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(NUMBER_OF_PHONE.name()),callBackQuery.getMessage());
            telegramUserRepositoryService.changeStatus(telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId()),PHONE_ENTERING_IN_START_STATUS);
        }
        else {
            telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(NEW_USER.name()),callBackQuery.getMessage());
            userCreating(callBackQuery);
            telegramMessageSenderService.sendActions(callBackQuery.getMessage());
        }
    }

    private void userCreating(CallBackQuery callBackQuery) {
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
        User user = new User();
        user.setTUser(tUser);
        tUser.setUser(user);
        user.setRole(Role.CUSTOMER);
        user.setStatus(AccountStatus.ACTIVE);
        userRepository.saveAndFlush(user);
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
        CustomerOrdering customerOrdering = customerOrderingRepositoryService.findById(orderId).orElseThrow(ElementNoFoundException::new);
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
        CustomerOrdering customerOrdering = customerOrderingRepositoryService.findById(idL).orElseThrow(ElementNoFoundException::new);
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
            telegramGetMenuService.getMenu(callBackQuery.getMessage());
        }
        else {
            telegramOrderingService.ifNoMore(callBackQuery.getMessage());
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
       telegramCreatingOwnCroissant.createOwn(callBackQuery.getMessage());

    }

    private void menuData(CallBackQuery callBackQuery) {
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
        telegramUserRepositoryService.changeStatus(tUser, TelegramUserStatus.ASKING_TYPE_STATUS);
        telegramGetMenuService.getMenu(callBackQuery.getMessage());
    }

    private void orderingData(CallBackQuery callBackQuery) {
        TUser tUser = telegramUserRepositoryService.findByChatId(callBackQuery.getMessage().getChat().getId());
        Message message = new Message();
        message.setText(TextFormatter.ejectSingleVariable(callBackQuery.getData()));
        message.setChat(callBackQuery.getMessage().getChat());

        if(tUser.getStatus()!=ONE_MORE_ORDERING_GETTING_MENU_STATUS)
        telegramUserRepositoryService.changeStatus(tUser,TEL_NUMBER_ORDERING_STATUS);
        telegramOrderingService.makeOrder(message);
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
        telegramGetMenuService.getMenu(message);
    }

    
}
