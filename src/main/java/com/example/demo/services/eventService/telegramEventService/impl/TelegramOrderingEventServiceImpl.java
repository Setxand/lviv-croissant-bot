package com.example.demo.services.eventService.telegramEventService.impl;

import com.example.demo.entities.lvivCroissants.Croissant;
import com.example.demo.entities.lvivCroissants.CustomerOrdering;
import com.example.demo.entities.peopleRegister.TUser;
import com.example.demo.models.telegram.Message;
import com.example.demo.services.eventService.telegramEventService.TelegramGetMenuEventService;
import com.example.demo.services.eventService.telegramEventService.TelegramOrderingEventService;
import com.example.demo.services.lvivCroissantRepositoryService.CroissantRepositoryService;
import com.example.demo.services.lvivCroissantRepositoryService.CustomerOrderingRepositoryService;
import com.example.demo.services.peopleRegisterService.TelegramUserRepositoryService;
import com.example.demo.services.supportService.TextFormatter;
import com.example.demo.services.telegramService.TelegramMessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ResourceBundle;

import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.*;
import static com.example.demo.enums.telegramEnums.CallBackData.ONE_MORE_ORDERING_DATA;
import static com.example.demo.enums.telegramEnums.TelegramUserStatus.*;

@Service
public class TelegramOrderingEventServiceImpl implements TelegramOrderingEventService {
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Autowired
    private CroissantRepositoryService croissantRepositoryService;
    @Autowired
    private CustomerOrderingRepositoryService customerOrderingRepositoryService;
    @Autowired
    private TelegramGetMenuEventService telegramGetMenuEventService;
    @Override
    public void makeOrder(Message message) {
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        switch (tUser.getStatus()) {
            case TEL_NUMBER_ORDERING_STATUS:
                telNumberOrderingStatus(message, tUser);
                break;
            case FILLING_PHONE_NUMBER_STATUS:
                fillingPhoneNumberStatus(message,tUser);
                break;
            case ADDRESS_STATUS:
                addressStatus(message,tUser);
                break;
            case TIME_STATUS:
                timeStatus(message,tUser);
                break;

            case ONE_MORE_ORDERING_GETTING_MENU_STATUS:
                oneMoreOrderingGettingMenuStatus(message,tUser);
                break;
            default:
                telegramMessageSenderService.errorMessage(message.getChat().getId());
                break;
        }
    }

    private void oneMoreOrderingGettingMenuStatus(Message message, TUser tUser) {
        telNumberOrderingStatus(message,tUser);
    }




    private void timeStatus(Message message, TUser tUser) {
        if(TextFormatter.isCorrectTime(message.getText())){
            CustomerOrdering customerOrdering = customerOrderingRepositoryService.findTopByTUser(tUser);
            customerOrdering.setTime(message.getText());
            telegramUserRepositoryService.saveAndFlush(tUser);
            nullChecking(message);
        }
        else {
            String nonCorrect = ResourceBundle.getBundle("dictionary").getString(NON_CORRECT_FORMAT_OF_TIME.name());
            String enterAddress = ResourceBundle.getBundle("dictionary").getString(TIME_OF_ORDERING.name());
            telegramMessageSenderService.simpleMessage(message.getChat().getId(),nonCorrect);
            telegramMessageSenderService.simpleMessage(message.getChat().getId(),enterAddress);
        }
    }

    private void addressStatus(Message message, TUser tUser) {
        if(TextFormatter.isCorrectAddress(message.getText())){
            CustomerOrdering customerOrdering = customerOrderingRepositoryService.findTopByTUser(tUser);
            customerOrdering.setAddress(message.getText());
            telegramUserRepositoryService.saveAndFlush(tUser);
            nullChecking(message);
        }
        else
        {
            String nonCorrect = ResourceBundle.getBundle("dictionary").getString(NON_CORRECT_FORMAT_OF_ADDRESS.name());
            String enterAddress = ResourceBundle.getBundle("dictionary").getString(ADDRESS_OF_CUSTOMER.name());
            telegramMessageSenderService.simpleMessage(message.getChat().getId(),nonCorrect);
            telegramMessageSenderService.simpleMessage(message.getChat().getId(),enterAddress);
        }
    }

    private void fillingPhoneNumberStatus(Message message, TUser tUser) {
        if(TextFormatter.isPhoneNumber(message.getText())){
            tUser.setPhoneNumber(message.getText());
            CustomerOrdering customerOrdering = customerOrderingRepositoryService.findTopByTUser(tUser);
            customerOrdering.setPhoneNumber(message.getText());
            telegramUserRepositoryService.saveAndFlush(tUser);
            customerOrderingRepositoryService.saveAndFlush(customerOrdering);
            nullChecking(message);
        }
        else {
            String nonCorrect = ResourceBundle.getBundle("dictionary").getString(NON_CORRECT_FORMAT_OF_NUMBER_OF_TELEPHONE.name());
            String enterNumber = ResourceBundle.getBundle("dictionary").getString(NUMBER_OF_PHONE.name());
            telegramMessageSenderService.simpleMessage(message.getChat().getId(),nonCorrect);
            telegramMessageSenderService.simpleMessage(message.getChat().getId(),enterNumber);
        }
    }

    private void telNumberOrderingStatus(Message message, TUser tUser) {
        if(tUser.getStatus()==ONE_MORE_ORDERING_GETTING_MENU_STATUS){
            oneMoreAddingCroissant(message,tUser);
            return;
        }
        CustomerOrdering customerOrdering = new CustomerOrdering();
        Croissant croissant = croissantRepositoryService.findOne(Long.parseLong(message.getText()));
        customerOrdering.setPrice(croissant.getPrice());
        customerOrdering.setName(tUser.getName() + " " + tUser.getLastName());

        customerOrdering.getCroissants().add(croissant.getId().toString());
        tUser.addCustomerOrdering(customerOrdering);
        if (tUser.getPhoneNumber() == null) {
            tUser = telegramUserRepositoryService.saveAndFlush(tUser);
            telegramMessageSenderService.simpleMessage(message.getChat().getId(), ResourceBundle.getBundle("dictionary").getString(NUMBER_OF_PHONE.name()));
            telegramUserRepositoryService.changeStatus(tUser,FILLING_PHONE_NUMBER_STATUS);
        } else {
            customerOrdering.setPhoneNumber(tUser.getPhoneNumber());
            tUser.addCustomerOrdering(customerOrdering);
            telegramUserRepositoryService.saveAndFlush(tUser);
            nullChecking(message);

        }

    }

    private void oneMoreAddingCroissant(Message message, TUser tUser) {
        CustomerOrdering customerOrdering = customerOrderingRepositoryService.findTopByTUser(tUser);
        Croissant croissant = croissantRepositoryService.findOne(Long.parseLong(message.getText()));
        customerOrdering.getCroissants().add(croissant.toString());
        customerOrdering.setPrice(customerOrdering.getPrice()+croissant.getPrice());
        telegramUserRepositoryService.saveAndFlush(tUser);
        nullChecking(message);
    }


    private void nullChecking(Message message) {
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        CustomerOrdering customerOrdering = customerOrderingRepositoryService.findTopByTUser(tUser);

        if(customerOrdering.getAddress()==null){
            telegramUserRepositoryService.changeStatus(tUser,ADDRESS_STATUS);
            addressReq(message);
        }
       else if(customerOrdering.getTime()==null){
            telegramUserRepositoryService.changeStatus(tUser,TIME_STATUS);
            timeReq(message);
        }
        else{
           orderingFinishing(message,customerOrdering,tUser);
        }
    }

    private void orderingFinishing(Message message, CustomerOrdering customerOrdering, TUser tUser) {
        String oneMoreOrderingText = ResourceBundle.getBundle("dictionary").getString(ORDER_SOMETHING_YET.name());
        telegramMessageSenderService.simpleQuestion(message.getChat().getId(),ONE_MORE_ORDERING_DATA,"?",oneMoreOrderingText);

    }
    @Override
    public void ifNoMore(Message message){
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId()) ;
        CustomerOrdering customerOrdering = customerOrderingRepositoryService.findTopByTUser(tUser);
        telegramUserRepositoryService.changeStatus(tUser,null);
        String done = ResourceBundle.getBundle("dictionary").getString(ORDERING_WAS_DONE.name());
        telegramMessageSenderService.simpleMessage(message.getChat().getId(),done);
        for(String i: customerOrdering.getCroissants()){
            Croissant croissant = croissantRepositoryService.findOne(Long.parseLong(i));
            telegramMessageSenderService.sendPhoto(message.getChat().getId(),croissant.getImageUrl(),croissant.getName()+"\n"+croissant.getCroissantsFillings().toString(),null);

        }
        telegramMessageSenderService.simpleMessage(message.getChat().getId(),"price:"+customerOrdering.getPrice());
        telegramMessageSenderService.sendActions(message.getChat().getId());
    }

    private void timeReq(Message message) {
        telegramMessageSenderService.simpleMessage(message.getChat().getId(),ResourceBundle.getBundle("dictionary").getString(TIME_OF_ORDERING.name()));
    }

    private void addressReq(Message message) {
        telegramMessageSenderService.simpleMessage(message.getChat().getId(), ResourceBundle.getBundle("dictionary").getString(ADDRESS_OF_CUSTOMER.name()));
    }
}
