package com.bots.lvivcroissantbot.service.messenger.event.impl;

import com.bots.lvivcroissantbot.constantenum.messenger.Role;
import com.bots.lvivcroissantbot.dto.messanger.Messaging;
import com.bots.lvivcroissantbot.dto.messanger.UserData;
import com.bots.lvivcroissantbot.entity.register.MUser;
import com.bots.lvivcroissantbot.service.messenger.MessageParserService;
import com.bots.lvivcroissantbot.service.messenger.MessageSenderService;
import com.bots.lvivcroissantbot.service.messenger.PayloadParserService;
import com.bots.lvivcroissantbot.service.messenger.event.UserService;
import com.bots.lvivcroissantbot.service.peopleregister.MUserRepositoryService;
import com.bots.lvivcroissantbot.service.support.RecognizeService;
import com.bots.lvivcroissantbot.service.support.TextFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.bots.lvivcroissantbot.constantenum.messenger.Cases.CUSTOMER_REGISTER;
import static com.bots.lvivcroissantbot.constantenum.messenger.Cases.CUSTOMER_REGISTER_FINALIZE;
import static com.bots.lvivcroissantbot.constantenum.messenger.payload.Payloads.GET_STARTED_PAYLOAD;
import static com.bots.lvivcroissantbot.constantenum.messenger.speaking.ServerSideSpeaker.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private RecognizeService recognizeService;
    @Autowired
    private MUserRepositoryService MUserRepositoryService;

    @Autowired
    private MessageParserService messageParserService;
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
        return MUser.getAddress() == null || MUser.getUser().getPhoneNumber() == null;

    }


    private void parseName(Messaging messaging) {
        MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());


        MUser.setName(messaging.getMessage().getText());
        MUser.getUser().setRole(Role.CUSTOMER);
        MUserRepositoryService.saveAndFlush(MUser);
        messageSenderService.sendSimpleMessage(recognizeService.recognize(ADDRESS_OF_CUSTOMER.name(), messaging.getSender().getId()), messaging.getSender().getId());

    }

    private void secondStep(Messaging messaging) {
        MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());

        if (MUser.getName() == null) {
            parseName(messaging);
        } else if (MUser.getAddress() == null) {
            parseAddress(messaging, MUser);
        } else if (MUser.getEmail() == null) {
            parseEmail(messaging, MUser);
        } else if (MUser.getUser().getPhoneNumber() == null) {
            parsePhoneNumber(messaging, MUser);
        }
    }

    private void parseEmail(Messaging messaging, MUser MUser) {
        if (TextFormatter.isEmail(messaging)) {
            MUser.setEmail(messaging.getMessage().getText());
            MUserRepositoryService.saveAndFlush(MUser);
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NUMBER_OF_PHONE.name(), messaging.getSender().getId()), messaging.getSender().getId());
        } else {
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_EMAIL.name(), messaging.getSender().getId()), messaging.getSender().getId());
            messageSenderService.sendSimpleMessage(recognizeService.recognize(ENTER_EMAIL.name(), messaging.getSender().getId()), messaging.getSender().getId());
        }
    }

    private void parsePhoneNumber(Messaging messaging, MUser MUser) {
        if (TextFormatter.isPhoneNumber(messaging.getMessage().getText())) {
            MUser.getUser().setPhoneNumber(messaging.getMessage().getText());
            MUserRepositoryService.saveAndFlush(MUser);
            messageSenderService.sendSimpleMessage(recognizeService.recognize(CUSTOMER_ADDED_TO_DB.name(), messaging.getSender().getId()), messaging.getSender().getId());
            messageSenderService.sendSimpleMessage(recognizeService.recognize(SUCCESS_REGISTER.name(), messaging.getSender().getId()), messaging.getSender().getId());
            MUser.setStatus(null);
            MUserRepositoryService.saveAndFlush(MUser);
        } else {
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_NUMBER_OF_TELEPHONE.name(), messaging.getSender().getId()), messaging.getSender().getId());
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NUMBER_OF_PHONE.name(), messaging.getSender().getId()), messaging.getSender().getId());
        }


    }

    private void parseAddress(Messaging messaging, MUser MUser) {
        if (TextFormatter.isCorrectAddress(messaging.getMessage().getText())) {
            MUser.setAddress(messaging.getMessage().getText());
            MUserRepositoryService.saveAndFlush(MUser);
            messageSenderService.sendSimpleMessage(recognizeService.recognize(ENTER_EMAIL.name(), messaging.getSender().getId()), messaging.getSender().getId());

        } else {
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_ADDRESS.name(), messaging.getSender().getId()), messaging.getSender().getId());
            messageSenderService.sendSimpleMessage(recognizeService.recognize(ADDRESS_OF_CUSTOMER.name(), messaging.getSender().getId()), messaging.getSender().getId());
        }

    }


}
