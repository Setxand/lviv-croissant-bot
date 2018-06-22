package com.bots.lviv_croissant_bot.service.messenger.event.impl;

import com.bots.lviv_croissant_bot.entity.register.MUser;
import com.bots.lviv_croissant_bot.constantEnum.messengerEnum.Role;
import com.bots.lviv_croissant_bot.dto.messanger.Messaging;
import com.bots.lviv_croissant_bot.dto.messanger.UserData;
import com.bots.lviv_croissant_bot.service.messenger.event.UserService;
import com.bots.lviv_croissant_bot.service.messenger.MessageParserService;
import com.bots.lviv_croissant_bot.service.messenger.MessageSenderService;
import com.bots.lviv_croissant_bot.service.messenger.PayloadParserService;
import com.bots.lviv_croissant_bot.service.peopleRegister.MUserRepositoryService;
import com.bots.lviv_croissant_bot.service.support.RecognizeService;
import com.bots.lviv_croissant_bot.service.support.TextFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.bots.lviv_croissant_bot.constantEnum.messengerEnum.Cases.CUSTOMER_REGISTER;
import static com.bots.lviv_croissant_bot.constantEnum.messengerEnum.Cases.CUSTOMER_REGISTER_FINALIZE;
import static com.bots.lviv_croissant_bot.constantEnum.messengerEnum.payload.Payloads.GET_STARTED_PAYLOAD;
import static com.bots.lviv_croissant_bot.constantEnum.messengerEnum.speaking.ServerSideSpeaker.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private RecognizeService recognizeService;
    @Autowired
    private MUserRepositoryService MUserRepositoryService;

    @Autowired
    private MessageParserService messageParserServiceService;
    @Autowired
    private PayloadParserService payloadParserService;


    @Override
    public void customerRegistration(Messaging messaging) {

        if (messaging.getPostback().getPayload().equals(GET_STARTED_PAYLOAD.name())) {
            userReg(messaging);
        } else {


            MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());
            if (MUser.getStatus() == null) {
                messaging.getMessage().setText(CUSTOMER_REGISTER_FINALIZE.name());
                messageParserServiceService.parseMessage(messaging);
            } else if (messaging.getMessage().getText().equals(CUSTOMER_REGISTER.name())) {
                messageSenderService.sendSimpleMessage(recognizeService.recognize(NAME_LASTNAME.name(), messaging.getSender().getId()), messaging.getSender().getId());
            } else {
                secondStep(messaging);
            }
        }
    }
    private void userReg(Messaging messaging) {

            UserData userData = messageSenderService.sendFacebookRequest(messaging.getSender().getId());
            MUser MUser = new MUser();
            MUser.setRecipientId(messaging.getSender().getId());
            MUser.setName(userData.getFirstName());
            MUser.setLastName(userData.getLastName());
            MUser.setPicture(userData.getPicture());
            MUser.getUser().setRole(Role.CUSTOMER);
            MUserRepositoryService.saveAndFlush(MUser);
            payloadParserService.parsePayload(messaging);
    }

    @Override
    public void changeStatus(Messaging messaging, String nextCommand) {
        MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());
        MUser.setStatus(nextCommand);
        MUserRepositoryService.saveAndFlush(MUser);
    }

    @Override
    public boolean isUser(MUser MUser) {
        return MUser.getAddress() == null || MUser.getUser().getPhoneNumber()==null ;

    }


    private void parseName(Messaging messaging){
        MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());


            MUser.setName(messaging.getMessage().getText());
            MUser.getUser().setRole(Role.CUSTOMER);
            MUserRepositoryService.saveAndFlush(MUser);
            messageSenderService.sendSimpleMessage(recognizeService.recognize(ADDRESS_OF_CUSTOMER.name(),messaging.getSender().getId()), messaging.getSender().getId());

    }
    private void secondStep(Messaging messaging) {
        MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());

        if(MUser.getName()==null) {
            parseName(messaging);
        }
        else if(MUser.getAddress()== null){
            parseAddress(messaging, MUser);
        }
        else if(MUser.getEmail()==null){
            parseEmail(messaging, MUser);
        }
        else if (MUser.getUser().getPhoneNumber() == null){
             parsePhoneNumber(messaging, MUser);
        }
    }

    private void parseEmail(Messaging messaging, MUser MUser) {
        if(TextFormatter.isEmail(messaging)){
            MUser.setEmail(messaging.getMessage().getText());
            MUserRepositoryService.saveAndFlush(MUser);
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NUMBER_OF_PHONE.name(),messaging.getSender().getId()), messaging.getSender().getId());
        }
        else {
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_EMAIL.name(),messaging.getSender().getId()),messaging.getSender().getId());
            messageSenderService.sendSimpleMessage(recognizeService.recognize(ENTER_EMAIL.name(),messaging.getSender().getId()),messaging.getSender().getId());
        }
    }

    private void parsePhoneNumber(Messaging messaging, MUser MUser) {
        if(TextFormatter.isPhoneNumber(messaging.getMessage().getText())){
            MUser.getUser().setPhoneNumber(messaging.getMessage().getText());
            MUserRepositoryService.saveAndFlush(MUser);
            messageSenderService.sendSimpleMessage(recognizeService.recognize(CUSTOMER_ADDED_TO_DB.name(),messaging.getSender().getId()), messaging.getSender().getId());
            messageSenderService.sendSimpleMessage(recognizeService.recognize(SUCCESS_REGISTER.name(),messaging.getSender().getId()),messaging.getSender().getId());
            MUser.setStatus(null);
            MUserRepositoryService.saveAndFlush(MUser);
        }
        else {
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_NUMBER_OF_TELEPHONE.name(),messaging.getSender().getId()), messaging.getSender().getId());
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NUMBER_OF_PHONE.name(),messaging.getSender().getId()), messaging.getSender().getId());
        }


    }

    private void parseAddress(Messaging messaging, MUser MUser) {
        if(TextFormatter.isCorrectAddress(messaging.getMessage().getText())){
            MUser.setAddress(messaging.getMessage().getText());
            MUserRepositoryService.saveAndFlush(MUser);
            messageSenderService.sendSimpleMessage(recognizeService.recognize(ENTER_EMAIL.name(),messaging.getSender().getId()),messaging.getSender().getId());

        }
        else {
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_ADDRESS.name(),messaging.getSender().getId()), messaging.getSender().getId());
            messageSenderService.sendSimpleMessage(recognizeService.recognize(ADDRESS_OF_CUSTOMER.name(),messaging.getSender().getId()), messaging.getSender().getId());
        }

    }


}
