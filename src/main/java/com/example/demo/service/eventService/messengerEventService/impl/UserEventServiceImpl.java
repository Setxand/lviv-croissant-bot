package com.example.demo.service.eventService.messengerEventService.impl;

import com.example.demo.entity.peopleRegister.MUser;
import com.example.demo.constantEnum.messengerEnums.Role;
import com.example.demo.dto.messanger.Messaging;
import com.example.demo.dto.messanger.UserData;
import com.example.demo.service.eventService.messengerEventService.UserEventService;
import com.example.demo.service.messangerService.MessageParserService;
import com.example.demo.service.messangerService.MessageSenderService;
import com.example.demo.service.messangerService.PayloadParserService;
import com.example.demo.service.peopleRegisterService.UserRepositoryService;
import com.example.demo.service.supportService.RecognizeService;
import com.example.demo.service.supportService.TextFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.constantEnum.messengerEnums.Cases.CUSTOMER_REGISTER;
import static com.example.demo.constantEnum.messengerEnums.Cases.CUSTOMER_REGISTER_FINALIZE;
import static com.example.demo.constantEnum.messengerEnums.payloads.Payloads.GET_STARTED_PAYLOAD;
import static com.example.demo.constantEnum.messengerEnums.speaking.ServerSideSpeaker.*;

@Service
public class UserEventServiceImpl implements UserEventService {
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private RecognizeService recognizeService;
    @Autowired
    private UserRepositoryService userRepositoryService;

    @Autowired
    private MessageParserService messageParserService;
    @Autowired
    private PayloadParserService payloadParserService;


    @Override
    public void customerRegistration(Messaging messaging) {

        if (messaging.getPostback().getPayload().equals(GET_STARTED_PAYLOAD.name())) {
            userReg(messaging);
        } else {


            MUser MUser = userRepositoryService.findOnebyRId(messaging.getSender().getId());
            if (MUser.getStatus() == null) {
                messaging.getMessage().setText(CUSTOMER_REGISTER_FINALIZE.name());
                messageParserService.parseMessage(messaging);
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
            MUser.setRole(Role.CUSTOMER);
            userRepositoryService.saveAndFlush(MUser);
            payloadParserService.parsePayload(messaging);
    }

    @Override
    public void changeStatus(Messaging messaging, String nextCommand) {
        MUser MUser = userRepositoryService.findOnebyRId(messaging.getSender().getId());
        MUser.setStatus(nextCommand);
        userRepositoryService.saveAndFlush(MUser);
    }

    @Override
    public boolean isUser(MUser MUser) {
        return MUser.getAddress() == null || MUser.getPhoneNumber()==null ;

    }


    private void parseName(Messaging messaging){
        MUser MUser = userRepositoryService.findOnebyRId(messaging.getSender().getId());


            MUser.setName(messaging.getMessage().getText());
            MUser.setRole(Role.CUSTOMER);
            userRepositoryService.saveAndFlush(MUser);
            messageSenderService.sendSimpleMessage(recognizeService.recognize(ADDRESS_OF_CUSTOMER.name(),messaging.getSender().getId()), messaging.getSender().getId());

    }
    private void secondStep(Messaging messaging) {
        MUser MUser = userRepositoryService.findOnebyRId(messaging.getSender().getId());

        if(MUser.getName()==null) {
            parseName(messaging);
        }
        else if(MUser.getAddress()== null){
            parseAddress(messaging, MUser);
        }
        else if(MUser.getEmail()==null){
            parseEmail(messaging, MUser);
        }
        else if (MUser.getPhoneNumber() == null){
             parsePhoneNumber(messaging, MUser);
        }
    }

    private void parseEmail(Messaging messaging, MUser MUser) {
        if(TextFormatter.isEmail(messaging)){
            MUser.setEmail(messaging.getMessage().getText());
            userRepositoryService.saveAndFlush(MUser);
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NUMBER_OF_PHONE.name(),messaging.getSender().getId()), messaging.getSender().getId());
        }
        else {
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_EMAIL.name(),messaging.getSender().getId()),messaging.getSender().getId());
            messageSenderService.sendSimpleMessage(recognizeService.recognize(ENTER_EMAIL.name(),messaging.getSender().getId()),messaging.getSender().getId());
        }
    }

    private void parsePhoneNumber(Messaging messaging, MUser MUser) {
        if(TextFormatter.isPhoneNumber(messaging.getMessage().getText())){
            MUser.setPhoneNumber(messaging.getMessage().getText());
            userRepositoryService.saveAndFlush(MUser);
            messageSenderService.sendSimpleMessage(recognizeService.recognize(CUSTOMER_ADDED_TO_DB.name(),messaging.getSender().getId()), messaging.getSender().getId());
            messageSenderService.sendSimpleMessage(recognizeService.recognize(SUCCESS_REGISTER.name(),messaging.getSender().getId()),messaging.getSender().getId());
            MUser.setStatus(null);
            userRepositoryService.saveAndFlush(MUser);
        }
        else {
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_NUMBER_OF_TELEPHONE.name(),messaging.getSender().getId()), messaging.getSender().getId());
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NUMBER_OF_PHONE.name(),messaging.getSender().getId()), messaging.getSender().getId());
        }


    }

    private void parseAddress(Messaging messaging, MUser MUser) {
        if(TextFormatter.isCorrectAddress(messaging.getMessage().getText())){
            MUser.setAddress(messaging.getMessage().getText());
            userRepositoryService.saveAndFlush(MUser);
            messageSenderService.sendSimpleMessage(recognizeService.recognize(ENTER_EMAIL.name(),messaging.getSender().getId()),messaging.getSender().getId());

        }
        else {
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_ADDRESS.name(),messaging.getSender().getId()), messaging.getSender().getId());
            messageSenderService.sendSimpleMessage(recognizeService.recognize(ADDRESS_OF_CUSTOMER.name(),messaging.getSender().getId()), messaging.getSender().getId());
        }

    }


}
