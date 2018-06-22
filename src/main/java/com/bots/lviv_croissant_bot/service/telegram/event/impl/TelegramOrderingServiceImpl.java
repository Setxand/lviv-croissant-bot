package com.bots.lviv_croissant_bot.service.telegram.event.impl;

import com.bots.lviv_croissant_bot.entity.lvivCroissants.CroissantEntity;
import com.bots.lviv_croissant_bot.entity.lvivCroissants.CustomerOrdering;
import com.bots.lviv_croissant_bot.entity.register.TUser;
import com.bots.lviv_croissant_bot.constantEnum.messengerEnum.speaking.ServerSideSpeaker;
import com.bots.lviv_croissant_bot.constantEnum.telegramEnum.CallBackData;
import com.bots.lviv_croissant_bot.dto.telegram.Message;
import com.bots.lviv_croissant_bot.dto.telegram.button.InlineKeyboardButton;
import com.bots.lviv_croissant_bot.repository.CustomerOrderingRepository;
import com.bots.lviv_croissant_bot.service.telegram.event.TelegramGetMenuService;
import com.bots.lviv_croissant_bot.service.telegram.event.TelegramOrderingService;
import com.bots.lviv_croissant_bot.service.peopleRegister.TelegramUserRepositoryService;
import com.bots.lviv_croissant_bot.service.support.TextFormatter;
import com.bots.lviv_croissant_bot.service.telegram.TelegramMessageSenderService;
import com.bots.lviv_croissant_bot.service.uni.CroissantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static com.bots.lviv_croissant_bot.constantEnum.messengerEnum.speaking.ServerSideSpeaker.*;
import static com.bots.lviv_croissant_bot.constantEnum.telegramEnum.CallBackData.ONE_MORE_ORDERING_DATA;
import static com.bots.lviv_croissant_bot.constantEnum.telegramEnum.TelegramUserStatus.*;

@Service
public class TelegramOrderingServiceImpl implements TelegramOrderingService {
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Autowired
    private CroissantService croissantRepositoryService;
    @Autowired
    private CustomerOrderingRepository customerOrderingRepositoryService;
    @Autowired
    private TelegramGetMenuService telegramGetMenuService;
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
                telegramMessageSenderService.errorMessage(message);
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
            telegramMessageSenderService.simpleMessage(nonCorrect,message);
            telegramMessageSenderService.simpleMessage(enterAddress,message);
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
            telegramMessageSenderService.simpleMessage(nonCorrect,message);
            telegramMessageSenderService.simpleMessage(enterAddress,message);
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
            telegramMessageSenderService.simpleMessage(nonCorrect,message);
            telegramMessageSenderService.simpleMessage(enterNumber,message);
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
            telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(NUMBER_OF_PHONE.name()),message);
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
        telegramMessageSenderService.simpleQuestion(ONE_MORE_ORDERING_DATA,"?",oneMoreOrderingText,message);

    }
    @Override
    public void ifNoMore(Message message){
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId()) ;
        CustomerOrdering customerOrdering = customerOrderingRepositoryService.findTopByTUserOrderByIdDesc(tUser);
        telegramUserRepositoryService.changeStatus(tUser,null);
        String done = ResourceBundle.getBundle("dictionary").getString(ORDERING_WAS_DONE.name());
        telegramMessageSenderService.simpleMessage(done,message);
        for(String i: customerOrdering.getCroissants()){
            CroissantEntity croissantEntity = croissantRepositoryService.findOne(Long.parseLong(i));
            telegramMessageSenderService.sendPhoto(croissantEntity.getImageUrl(), croissantEntity.getName()+"\n"+ croissantEntity.getCroissantsFillings().toString(),null,message);

        }
        telegramMessageSenderService.simpleMessage("price:"+customerOrdering.getPrice(),message);
        sendCancelButton(message,customerOrdering);
        telegramMessageSenderService.sendActions(message);
    }

    private void sendCancelButton(Message message, CustomerOrdering customerOrdering) {
        String text = ResourceBundle.getBundle("dictionary").getString(ServerSideSpeaker.CANCEL.name());
        List<InlineKeyboardButton> buttons = Arrays.asList(new InlineKeyboardButton(text, CallBackData.CANCEL_DATA.name()+"?"+customerOrdering.getId()));
        String mes = ResourceBundle.getBundle("dictionary").getString(ServerSideSpeaker.CANCEL_TEXT.name());
        telegramMessageSenderService.sendInlineButtons(Arrays.asList(buttons),mes,message);
    }

    private void timeReq(Message message) {
        telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(TIME_OF_ORDERING.name()),message);
    }

    private void addressReq(Message message) {
        telegramMessageSenderService.simpleMessage( ResourceBundle.getBundle("dictionary").getString(ADDRESS_OF_CUSTOMER.name()),message);
    }
}
