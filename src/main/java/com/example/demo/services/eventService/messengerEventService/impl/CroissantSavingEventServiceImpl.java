package com.example.demo.services.eventService.messengerEventService.impl;

import com.example.demo.entities.lvivCroissants.Croissant;
import com.example.demo.entities.lvivCroissants.CroissantsFilling;
import com.example.demo.entities.peopleRegister.User;
import com.example.demo.models.messanger.Message;
import com.example.demo.models.messanger.Messaging;
import com.example.demo.models.messanger.Recipient;
import com.example.demo.services.eventService.messengerEventService.CroissantSavingEventService;
import com.example.demo.services.eventService.messengerEventService.MenuOfFillingEventService;
import com.example.demo.services.repositoryService.*;
import com.example.demo.services.messangerService.MessageParserService;
import com.example.demo.services.messangerService.MessageSenderService;
import com.example.demo.services.messangerService.QuickReplyParserService;
import com.example.demo.services.peopleRegisterService.CourierRegisterService;
import com.example.demo.services.peopleRegisterService.UserRepositoryService;
import com.example.demo.services.supportService.RecognizeService;
import com.example.demo.services.supportService.TextFormatter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.enums.messengerEnums.Cases.ADD;
import static com.example.demo.enums.messengerEnums.payloads.QuickReplyPayloads.TYPE_PAYLOAD;
import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.*;

@Service
public class CroissantSavingEventServiceImpl implements CroissantSavingEventService {
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private MenuOfFillingRepositoryService menuOfFillingRepositoryService;
    @Autowired
    private CroissantRepositoryService croissantRepositoryService;
    @Autowired
    private QuickReplyParserService quickReplyParserService;
    @Autowired
    private CroissantsFillingRepositoryService croissantsFillingRepositoryService;

    @Autowired
    private CustomerOrderingRepositoryService customerOrderingRepositoryService;
    @Autowired
    private UserRepositoryService userRepositoryService;
    @Autowired
    private CourierRegisterService courierRegisterService;
    @Autowired
    private MenuOfFillingEventService menuOfFillingEventService;
    @Autowired
    private MessageParserService messageParserService;
    @Autowired
    private RecognizeService recognizeService;
    @Autowired
    private SupportEntityRepositoryService supportEntityRepositoryService;

    private static final Logger logger = Logger.getLogger(CroissantSavingEventServiceImpl.class);
    @Override
    public void saveCroissant(Messaging messaging) {

        try {
            if (messaging.getMessage().getText().toUpperCase().equals(ADD.name()))
                messageSenderService.askTypeOfCroissants(messaging.getSender().getId(),TYPE_PAYLOAD.name()+"?");
            else {
                 finalizeSavingCroissant(messaging);
            }

        }catch (Exception ex){

            Croissant croissant = croissantRepositoryService.findLastRecord();
            if(croissant.getCroissantsFillings().isEmpty())
                croissantRepositoryService.remove(croissant);
            messageSenderService.errorMessage(messaging.getSender().getId());
            logger.warn(ex);
        }
    }



    private void finalizeSavingCroissant(Messaging messaging) {
        User user = userRepositoryService.findOnebyRId(messaging.getSender().getId());
        Croissant croissant = croissantRepositoryService.findLastByCreatorId(messaging.getSender().getId());


        if (croissant.getName() == null) {
            parseName(messaging,croissant);

        } else if (croissant.getImageUrl() == null) {
            parseImageUrl(messaging,croissant);


        } else if (croissant.getCroissantsFillings().isEmpty()) {
             parseCroissantsFillings(messaging,croissant);
        }
        else if(croissant.getPrice() == 0){
             parsePrice(messaging,croissant);

        }else {
            user.setStatus(null);
            userRepositoryService.saveAndFlush(user);
             errorAction(messaging,croissant);

        }
    }

    private void parsePrice(Messaging messaging, Croissant croissant) {
        try {
            croissant.setPrice(Integer.parseInt(messaging.getMessage().getText()));
            croissantRepositoryService.saveAndFlush(croissant);
            messageSenderService.sendMessage(new Messaging(new Message(recognizeService.recognize(CROISSANT_SUCCESSFULLY_ADDED.name(),messaging.getSender().getId())), new Recipient(messaging.getSender().getId())));
            User user = userRepositoryService.findOnebyRId(messaging.getSender().getId());
            user.setStatus(null);
            userRepositoryService.saveAndFlush(user);

        }catch (Exception ex){
            logger.warn(ex);
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_PRICE.name(),messaging.getSender().getId()),messaging.getSender().getId());
        }
    }

    private void errorAction(Messaging messaging, Croissant croissant) {
        messageSenderService.errorMessage(messaging.getSender().getId());
        if (croissant.getCroissantsFillings().isEmpty())
            croissantRepositoryService.remove(croissant);
    }

    private void parseCroissantsFillings(Messaging messaging, Croissant croissant) {
        String[] str = messaging.getMessage().getText().split(",");
        List<CroissantsFilling> croissantsFillings = new ArrayList<>();
            try {
                croissantsFillings.add(new CroissantsFilling(menuOfFillingRepositoryService.findOne(Long.parseLong(supportEntityRepositoryService.getByUserId(messaging.getSender().getId()).getType()))));

                for (String s : str) {
                    croissantsFillings.add(new CroissantsFilling(menuOfFillingRepositoryService.findOne(Long.parseLong(s))));
            }

                croissant.setCroissantsFillings(croissantsFillings);
            croissantRepositoryService.saveAndFlush(croissant);

            for (CroissantsFilling croissantsFilling : croissantsFillings) {
                croissantsFillingRepositoryService.saveAndFlush(croissantsFilling);
            }

            messageSenderService.sendSimpleMessage(recognizeService.recognize(ASK_PRICE.name(),messaging.getSender().getId()),messaging.getSender().getId());
        }
        catch (Exception ex){
            logger.warn(ex);
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_FILLING.name(),messaging.getSender().getId()), messaging.getSender().getId());
            menuOfFillingEventService.getMenuOfFilling(messaging.getSender().getId());
        }

    }

    private void parseImageUrl(Messaging messaging, Croissant croissant) {
        croissant.setImageUrl(messaging.getMessage().getText());
        croissantRepositoryService.saveAndFlush(croissant);
        messageSenderService.sendMessage(new Messaging(new Message(recognizeService.recognize(ID_OF_FILLING.name(),messaging.getSender().getId())), new Recipient(messaging.getSender().getId())));
        menuOfFillingEventService.getMenuOfFilling(messaging.getSender().getId());
    }

    private void parseName(Messaging messaging, Croissant croissant) {
        croissant.setName(TextFormatter.toNormalFormat(messaging.getMessage().getText()));
        croissantRepositoryService.saveAndFlush(croissant);
        messageSenderService.sendMessage(new Messaging(new Message(recognizeService.recognize(IMAGE_URL .name(),messaging.getSender().getId()) ), new Recipient(messaging.getSender().getId())));

    }
}
