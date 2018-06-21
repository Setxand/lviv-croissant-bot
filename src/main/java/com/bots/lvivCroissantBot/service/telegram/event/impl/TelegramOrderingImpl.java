package com.bots.lvivCroissantBot.service.telegram.event.impl;

import com.bots.lvivCroissantBot.entity.lvivCroissants.CroissantEntity;
import com.bots.lvivCroissantBot.entity.lvivCroissants.CustomerOrdering;
import com.bots.lvivCroissantBot.entity.register.TUser;
import com.bots.lvivCroissantBot.constantEnum.messengerEnum.speaking.ServerSideSpeaker;
import com.bots.lvivCroissantBot.constantEnum.telegramEnum.CallBackData;
import com.bots.lvivCroissantBot.dto.telegram.Message;
import com.bots.lvivCroissantBot.dto.telegram.button.InlineKeyboardButton;
import com.bots.lvivCroissantBot.repository.CustomerOrderingRepository;
import com.bots.lvivCroissantBot.service.telegram.event.TelegramGetMenu;
import com.bots.lvivCroissantBot.service.telegram.event.TelegramOrdering;
import com.bots.lvivCroissantBot.service.peopleRegister.TelegramUserRepositoryService;
import com.bots.lvivCroissantBot.service.support.TextFormatter;
import com.bots.lvivCroissantBot.service.telegram.TelegramMessageSender;
import com.bots.lvivCroissantBot.service.uni.CroissantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.speaking.ServerSideSpeaker.*;
import static com.bots.lvivCroissantBot.constantEnum.telegramEnum.CallBackData.ONE_MORE_ORDERING_DATA;
import static com.bots.lvivCroissantBot.constantEnum.telegramEnum.TelegramUserStatus.*;

@Service
public class TelegramOrderingImpl implements TelegramOrdering {
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private TelegramMessageSender telegramMessageSender;
    @Autowired
    private CroissantService croissantRepositoryService;
    @Autowired
    private CustomerOrderingRepository customerOrderingRepositoryService;
    @Autowired
    private TelegramGetMenu telegramGetMenu;
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
                telegramMessageSender.errorMessage(message);
                break;
        }
    }

    private void oneMoreOrderingGettingMenuStatus(Message message, TUser tUser) {
        telNumberOrderingStatus(message,tUser);
    }




    private void timeStatus(Message message, TUser tUser) {
        if(TextFormatter.isCorrectTime(message.getText())){
            CustomerOrdering customerOrdering = customerOrderingRepositoryService.findTopByTUserOrderByIdDesc(tUser);
            customerOrdering.setTime(message.getText());
            telegramUserRepositoryService.saveAndFlush(tUser);
            nullChecking(message);
        }
        else {
            String nonCorrect = ResourceBundle.getBundle("dictionary").getString(NON_CORRECT_FORMAT_OF_TIME.name());
            String enterAddress = ResourceBundle.getBundle("dictionary").getString(TIME_OF_ORDERING.name());
            telegramMessageSender.simpleMessage(nonCorrect,message);
            telegramMessageSender.simpleMessage(enterAddress,message);
        }
    }

    private void addressStatus(Message message, TUser tUser) {
        if(TextFormatter.isCorrectAddress(message.getText())){
            CustomerOrdering customerOrdering = customerOrderingRepositoryService.findTopByTUserOrderByIdDesc(tUser);
            customerOrdering.setAddress(message.getText());
            telegramUserRepositoryService.saveAndFlush(tUser);
            nullChecking(message);
        }
        else
        {
            String nonCorrect = ResourceBundle.getBundle("dictionary").getString(NON_CORRECT_FORMAT_OF_ADDRESS.name());
            String enterAddress = ResourceBundle.getBundle("dictionary").getString(ADDRESS_OF_CUSTOMER.name());
            telegramMessageSender.simpleMessage(nonCorrect,message);
            telegramMessageSender.simpleMessage(enterAddress,message);
        }
    }

    private void fillingPhoneNumberStatus(Message message, TUser tUser) {
        if(TextFormatter.isPhoneNumber(message.getText())){
            tUser.getUser().setPhoneNumber(message.getText());
            CustomerOrdering customerOrdering = customerOrderingRepositoryService.findTopByTUserOrderByIdDesc(tUser);
            customerOrdering.setPhoneNumber(message.getText());
            telegramUserRepositoryService.saveAndFlush(tUser);
            customerOrderingRepositoryService.saveAndFlush(customerOrdering);
            nullChecking(message);
        }
        else {
            String nonCorrect = ResourceBundle.getBundle("dictionary").getString(NON_CORRECT_FORMAT_OF_NUMBER_OF_TELEPHONE.name());
            String enterNumber = ResourceBundle.getBundle("dictionary").getString(NUMBER_OF_PHONE.name());
            telegramMessageSender.simpleMessage(nonCorrect,message);
            telegramMessageSender.simpleMessage(enterNumber,message);
        }
    }

    private void telNumberOrderingStatus(Message message, TUser tUser) {
        if(tUser.getStatus()==ONE_MORE_ORDERING_GETTING_MENU_STATUS){
            oneMoreAddingCroissant(message,tUser);
            return;
        }
        CustomerOrdering customerOrdering = new CustomerOrdering();
        CroissantEntity croissantEntity = croissantRepositoryService.findOne(Long.parseLong(message.getText()));
        customerOrdering.setPrice(croissantEntity.getPrice());
        customerOrdering.setName(tUser.getName() + " " + tUser.getLastName());

        customerOrdering.getCroissants().add(croissantEntity.getId().toString());
        tUser.addCustomerOrdering(customerOrdering);
        if (tUser.getUser().getPhoneNumber() == null) {
            tUser = telegramUserRepositoryService.saveAndFlush(tUser);
            telegramMessageSender.simpleMessage(ResourceBundle.getBundle("dictionary").getString(NUMBER_OF_PHONE.name()),message);
            telegramUserRepositoryService.changeStatus(tUser,FILLING_PHONE_NUMBER_STATUS);
        } else {
            customerOrdering.setPhoneNumber(tUser.getUser().getPhoneNumber());
            tUser.addCustomerOrdering(customerOrdering);
            telegramUserRepositoryService.saveAndFlush(tUser);
            nullChecking(message);

        }

    }

    private void oneMoreAddingCroissant(Message message, TUser tUser) {
        CustomerOrdering customerOrdering = customerOrderingRepositoryService.findTopByTUserOrderByIdDesc(tUser);
        CroissantEntity croissantEntity = croissantRepositoryService.findOne(Long.parseLong(message.getText()));
        customerOrdering.getCroissants().add(croissantEntity.getId().toString());
        customerOrdering.setPrice(customerOrdering.getPrice()+ croissantEntity.getPrice());
        telegramUserRepositoryService.saveAndFlush(tUser);
        nullChecking(message);
    }


    private void nullChecking(Message message) {
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        CustomerOrdering customerOrdering = customerOrderingRepositoryService.findTopByTUserOrderByIdDesc(tUser);

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
        telegramMessageSender.simpleQuestion(ONE_MORE_ORDERING_DATA,"?",oneMoreOrderingText,message);

    }
    @Override
    public void ifNoMore(Message message){
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId()) ;
        CustomerOrdering customerOrdering = customerOrderingRepositoryService.findTopByTUserOrderByIdDesc(tUser);
        telegramUserRepositoryService.changeStatus(tUser,null);
        String done = ResourceBundle.getBundle("dictionary").getString(ORDERING_WAS_DONE.name());
        telegramMessageSender.simpleMessage(done,message);
        for(String i: customerOrdering.getCroissants()){
            CroissantEntity croissantEntity = croissantRepositoryService.findOne(Long.parseLong(i));
            telegramMessageSender.sendPhoto(croissantEntity.getImageUrl(), croissantEntity.getName()+"\n"+ croissantEntity.getCroissantsFillings().toString(),null,message);

        }
        telegramMessageSender.simpleMessage("price:"+customerOrdering.getPrice(),message);
        sendCancelButton(message,customerOrdering);
        telegramMessageSender.sendActions(message);
    }

    private void sendCancelButton(Message message, CustomerOrdering customerOrdering) {
        String text = ResourceBundle.getBundle("dictionary").getString(ServerSideSpeaker.CANCEL.name());
        List<InlineKeyboardButton> buttons = Arrays.asList(new InlineKeyboardButton(text, CallBackData.CANCEL_DATA.name()+"?"+customerOrdering.getId()));
        String mes = ResourceBundle.getBundle("dictionary").getString(ServerSideSpeaker.CANCEL_TEXT.name());
        telegramMessageSender.sendInlineButtons(Arrays.asList(buttons),mes,message);
    }

    private void timeReq(Message message) {
        telegramMessageSender.simpleMessage(ResourceBundle.getBundle("dictionary").getString(TIME_OF_ORDERING.name()),message);
    }

    private void addressReq(Message message) {
        telegramMessageSender.simpleMessage( ResourceBundle.getBundle("dictionary").getString(ADDRESS_OF_CUSTOMER.name()),message);
    }
}
