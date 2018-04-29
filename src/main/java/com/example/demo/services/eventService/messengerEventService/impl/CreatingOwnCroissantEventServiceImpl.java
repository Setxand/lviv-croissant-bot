package com.example.demo.services.eventService.messengerEventService.impl;

import com.example.demo.entities.lvivCroissants.Croissant;
import com.example.demo.entities.lvivCroissants.CroissantsFilling;
import com.example.demo.entities.SupportEntity;
import com.example.demo.entities.peopleRegister.User;
import com.example.demo.enums.messengerEnums.Cases;
import com.example.demo.models.messanger.Message;
import com.example.demo.models.messanger.Messaging;
import com.example.demo.models.messanger.QuickReply;
import com.example.demo.services.eventService.messengerEventService.CreatingOwnCroissantEventService;
import com.example.demo.services.eventService.messengerEventService.GetMenuEventService;
import com.example.demo.services.eventService.messengerEventService.MenuOfFillingEventService;
import com.example.demo.services.eventService.messengerEventService.UserEventService;
import com.example.demo.services.lvivCroissantRepositoryService.CroissantRepositoryService;
import com.example.demo.services.lvivCroissantRepositoryService.CroissantsFillingRepositoryService;
import com.example.demo.services.lvivCroissantRepositoryService.MenuOfFillingRepositoryService;
import com.example.demo.services.lvivCroissantRepositoryService.SupportEntityRepositoryService;
import com.example.demo.services.messangerService.MessageParserService;
import com.example.demo.services.messangerService.MessageSenderService;
import com.example.demo.services.messangerService.QuickReplyParserService;
import com.example.demo.services.peopleRegisterService.UserRepositoryService;
import com.example.demo.services.supportService.RecognizeService;
import com.example.demo.services.supportService.TextFormatter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.example.demo.enums.messengerEnums.Cases.COMPLETE_CROISSANT_SECOND_STEP;
import static com.example.demo.enums.messengerEnums.Cases.MENU;
import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.*;
import static com.example.demo.enums.messengerEnums.types.CroissantsTypes.OWN;

@Service
public class CreatingOwnCroissantEventServiceImpl implements CreatingOwnCroissantEventService {

    @Autowired
    private CroissantRepositoryService croissantRepositoryService;
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private MessageParserService messageParserService;
    @Autowired
    private RecognizeService recognizeService;
    @Autowired
    private MenuOfFillingEventService menuOfFillingEventService;
    @Autowired
    private MenuOfFillingRepositoryService menuOfFillingRepositoryService;
    @Autowired
    private CroissantsFillingRepositoryService croissantsFillingRepositoryService;
    @Autowired
    private GetMenuEventService getMenuEventService;
    @Autowired
    private UserRepositoryService userRepositoryService;
    @Autowired
    private UserEventService userEventService;
    @Autowired
    private SupportEntityRepositoryService supportEntityRepositoryService;
    private static final Logger logger = Logger.getLogger(QuickReplyParserService.class);
    @Override
    public void CreateOwnCroissant(Messaging messaging) {
        String message = messaging.getMessage().getText();
        String var = TextFormatter.ejectSingleVariable(message);
        String payload = TextFormatter.ejectPaySinglePayload(message);

         if(payload.equals(COMPLETE_CROISSANT_SECOND_STEP.name())){
            finalStepCreating(messaging,var);
        }
        else {
            firstStepCreating(messaging);
        }


    }




    private void firstStepCreating(Messaging messaging) {
        Croissant croissant = new Croissant("Власний круасан", OWN.name());
        SupportEntity supportEntity = supportEntityRepositoryService.getByUserId(messaging.getSender().getId());
        supportEntity.setType(OWN.name());
        supportEntityRepositoryService.saveAndFlush(supportEntity);
        croissant.setImageUrl("http://www.mlinar.hr/wp-content/uploads/2013/06/mlinar-proizvodi-croissant-600x380.png");
        String payload = messaging.getPostback().getPayload();
        croissant.setCroissantsFillings(new ArrayList<CroissantsFilling>());
        croissant.addSingleFilling(new CroissantsFilling(menuOfFillingRepositoryService.findOne(Long.parseLong(payload))));
        croissantRepositoryService.saveAndFlush(croissant);

        User user = userRepositoryService.findOnebyRId(messaging.getSender().getId());

        user.getOwnCroissantsId().add(croissant.getId());
        userRepositoryService.saveAndFlush(user);
        menuOfFillingEventService.getMenuOfFilling(messaging.getSender().getId());
        userEventService.changeStatus(messaging,COMPLETE_CROISSANT_SECOND_STEP.name());
        messageSenderService.sendSimpleMessage(recognizeService.recognize(ID_OF_FILLING.name(),messaging.getSender().getId()),messaging.getSender().getId());
    }

    private void finalStepCreating(Messaging messaging, String var) {
        try {
            User user = userRepositoryService.findOnebyRId(messaging.getSender().getId());
            String[]fillings = var.split(",");
            Croissant croissant = croissantRepositoryService.findOne(user.getOwnCroissantsId().get(user.getOwnCroissantsId().size()-1));
            int price = 0;
            for(String filling:fillings){
                CroissantsFilling croissantsFilling = new CroissantsFilling(menuOfFillingRepositoryService.findOne(Long.parseLong(filling)));
                croissant.addSingleFilling(croissantsFilling);
                price+=croissantsFilling.getPrice();
            }
            croissant.setPrice(price);
            croissantRepositoryService.saveAndFlush(croissant);
            for(CroissantsFilling croissantsFilling:croissant.getCroissantsFillings()){
                croissantsFillingRepositoryService.saveAndFlush(croissantsFilling);
            }

            //here maby must be change
            QuickReply quickReply = new QuickReply();
            quickReply.setPayload(Cases.CREATE_OWN_CROISSANT.name()+"?"+OWN.name());
            Message message = new Message(MENU.name());
            message.setQuickReply(quickReply);
            messaging.setMessage(message);
            messageSenderService.sendSimpleMessage(recognizeService.recognize(CREATED_CROISSANT.name(),messaging.getSender().getId()),messaging.getSender().getId());
            //here


            userEventService.changeStatus(messaging,null);

            getMenuEventService.getMenu(messaging);

        }catch (Exception ex){
            logger.warn(ex);
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_FILLING.name(),messaging.getSender().getId()),messaging.getSender().getId());
            menuOfFillingEventService.getMenuOfFilling(messaging.getSender().getId());
            messageSenderService.sendSimpleMessage(recognizeService.recognize(ID_OF_FILLING.name(),messaging.getSender().getId()),messaging.getSender().getId());
        }
    }




}
