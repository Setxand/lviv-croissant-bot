package com.example.demo.service.eventService.messengerEventService.impl;

import com.example.demo.entity.lvivCroissants.CroissantEntity;
import com.example.demo.entity.lvivCroissants.CroissantsFilling;
import com.example.demo.entity.SupportEntity;
import com.example.demo.entity.peopleRegister.User;
import com.example.demo.constantEnum.messengerEnums.Cases;
import com.example.demo.dto.messanger.Message;
import com.example.demo.dto.messanger.Messaging;
import com.example.demo.dto.messanger.QuickReply;
import com.example.demo.service.eventService.messengerEventService.CreatingOwnCroissantEventService;
import com.example.demo.service.eventService.messengerEventService.GetMenuEventService;
import com.example.demo.service.eventService.messengerEventService.MenuOfFillingEventService;
import com.example.demo.service.eventService.messengerEventService.UserEventService;
import com.example.demo.service.repositoryService.CroissantRepositoryService;
import com.example.demo.service.repositoryService.CroissantsFillingEntityRepositoryService;
import com.example.demo.service.repositoryService.MenuOfFillingRepositoryService;
import com.example.demo.service.repositoryService.SupportEntityRepositoryService;
import com.example.demo.service.messangerService.MessageParserService;
import com.example.demo.service.messangerService.MessageSenderService;
import com.example.demo.service.messangerService.QuickReplyParserService;
import com.example.demo.service.peopleRegisterService.UserRepositoryService;
import com.example.demo.service.supportService.RecognizeService;
import com.example.demo.service.supportService.TextFormatter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.example.demo.constantEnum.messengerEnums.Cases.COMPLETE_CROISSANT_SECOND_STEP;
import static com.example.demo.constantEnum.messengerEnums.Cases.MENU;
import static com.example.demo.constantEnum.messengerEnums.speaking.ServerSideSpeaker.*;
import static com.example.demo.constantEnum.messengerEnums.types.CroissantsTypes.OWN;

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
    private CroissantsFillingEntityRepositoryService croissantsFillingEntityRepositoryService;
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
        CroissantEntity croissantEntity = new CroissantEntity("Власний круасан", OWN.name());
        SupportEntity supportEntity = supportEntityRepositoryService.getByUserId(messaging.getSender().getId());
        supportEntity.setType(OWN.name());
        supportEntityRepositoryService.saveAndFlush(supportEntity);
        croissantEntity.setImageUrl("http://www.mlinar.hr/wp-content/uploads/2013/06/mlinar-proizvodi-croissantEntity-600x380.png");
        String payload = messaging.getPostback().getPayload();
        croissantEntity.setCroissantsFillings(new ArrayList<CroissantsFilling>());
        croissantEntity.addSingleFilling(new CroissantsFilling(menuOfFillingRepositoryService.findOne(Long.parseLong(payload))));
        croissantRepositoryService.saveAndFlush(croissantEntity);

        User user = userRepositoryService.findOnebyRId(messaging.getSender().getId());

        user.getOwnCroissantsId().add(croissantEntity.getId());
        userRepositoryService.saveAndFlush(user);
        menuOfFillingEventService.getMenuOfFilling(messaging.getSender().getId());
        userEventService.changeStatus(messaging,COMPLETE_CROISSANT_SECOND_STEP.name());
        messageSenderService.sendSimpleMessage(recognizeService.recognize(ID_OF_FILLING.name(),messaging.getSender().getId()),messaging.getSender().getId());
    }

    private void finalStepCreating(Messaging messaging, String var) {
        try {
            User user = userRepositoryService.findOnebyRId(messaging.getSender().getId());
            String[]fillings = var.split(",");
            CroissantEntity croissantEntity = croissantRepositoryService.findOne(user.getOwnCroissantsId().get(user.getOwnCroissantsId().size()-1));
            int price = 0;
            for(String filling:fillings){
                CroissantsFilling croissantsFilling = new CroissantsFilling(menuOfFillingRepositoryService.findOne(Long.parseLong(filling)));
                croissantEntity.addSingleFilling(croissantsFilling);
                price+= croissantsFilling.getPrice();
            }
            croissantEntity.setPrice(price);
            croissantRepositoryService.saveAndFlush(croissantEntity);
            for(CroissantsFilling croissantsFilling : croissantEntity.getCroissantsFillings()){
                croissantsFillingEntityRepositoryService.saveAndFlush(croissantsFilling);
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
