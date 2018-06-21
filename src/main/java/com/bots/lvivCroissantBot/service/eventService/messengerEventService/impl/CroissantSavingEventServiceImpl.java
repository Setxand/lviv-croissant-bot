package com.bots.lvivCroissantBot.service.eventService.messengerEventService.impl;

import com.bots.lvivCroissantBot.entity.lvivCroissants.CroissantEntity;
import com.bots.lvivCroissantBot.entity.lvivCroissants.CroissantsFilling;
import com.bots.lvivCroissantBot.entity.peopleRegister.MUser;
import com.bots.lvivCroissantBot.dto.messanger.Message;
import com.bots.lvivCroissantBot.dto.messanger.Messaging;
import com.bots.lvivCroissantBot.dto.messanger.Recipient;
import com.bots.lvivCroissantBot.service.eventService.messengerEventService.CroissantSavingEventService;
import com.bots.lvivCroissantBot.service.eventService.messengerEventService.MenuOfFillingEventService;
import com.bots.lvivCroissantBot.service.repositoryService.*;
import com.bots.lvivCroissantBot.service.messangerService.MessageParserService;
import com.bots.lvivCroissantBot.service.messangerService.MessageSenderService;
import com.bots.lvivCroissantBot.service.messangerService.QuickReplyParserService;
import com.bots.lvivCroissantBot.service.peopleRegisterService.CourierRegisterService;
import com.bots.lvivCroissantBot.service.peopleRegisterService.MUserRepositoryService;
import com.bots.lvivCroissantBot.service.supportService.RecognizeService;
import com.bots.lvivCroissantBot.service.supportService.TextFormatter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.bots.lvivCroissantBot.constantEnum.messengerEnums.Cases.ADD;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnums.payloads.QuickReplyPayloads.TYPE_PAYLOAD;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnums.speaking.ServerSideSpeaker.*;

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
    private CroissantsFillingEntityRepositoryService croissantsFillingEntityRepositoryService;

    @Autowired
    private CustomerOrderingRepositoryService customerOrderingRepositoryService;
    @Autowired
    private MUserRepositoryService MUserRepositoryService;
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

            CroissantEntity croissantEntity = croissantRepositoryService.findLastRecord();
            if(croissantEntity.getCroissantsFillings().isEmpty())
                croissantRepositoryService.remove(croissantEntity);
            messageSenderService.errorMessage(messaging.getSender().getId());
            logger.warn(ex);
        }
    }



    private void finalizeSavingCroissant(Messaging messaging) {
        MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());
        CroissantEntity croissantEntity = croissantRepositoryService.findLastByCreatorId(messaging.getSender().getId());


        if (croissantEntity.getName() == null) {
            parseName(messaging, croissantEntity);

        } else if (croissantEntity.getImageUrl() == null) {
            parseImageUrl(messaging, croissantEntity);


        } else if (croissantEntity.getCroissantsFillings().isEmpty()) {
             parseCroissantsFillings(messaging, croissantEntity);
        }
        else if(croissantEntity.getPrice() == 0){
             parsePrice(messaging, croissantEntity);

        }else {
            MUser.setStatus(null);
            MUserRepositoryService.saveAndFlush(MUser);
             errorAction(messaging, croissantEntity);

        }
    }

    private void parsePrice(Messaging messaging, CroissantEntity croissantEntity) {
        try {
            croissantEntity.setPrice(Integer.parseInt(messaging.getMessage().getText()));
            croissantRepositoryService.saveAndFlush(croissantEntity);
            messageSenderService.sendMessage(new Messaging(new Message(recognizeService.recognize(CROISSANT_SUCCESSFULLY_ADDED.name(),messaging.getSender().getId())), new Recipient(messaging.getSender().getId())));
            MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());
            MUser.setStatus(null);
            MUserRepositoryService.saveAndFlush(MUser);

        }catch (Exception ex){
            logger.warn(ex);
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_PRICE.name(),messaging.getSender().getId()),messaging.getSender().getId());
        }
    }

    private void errorAction(Messaging messaging, CroissantEntity croissantEntity) {
        messageSenderService.errorMessage(messaging.getSender().getId());
        if (croissantEntity.getCroissantsFillings().isEmpty())
            croissantRepositoryService.remove(croissantEntity);
    }

    private void parseCroissantsFillings(Messaging messaging, CroissantEntity croissantEntity) {
        String[] str = messaging.getMessage().getText().split(",");
        List<CroissantsFilling> croissantsFillingEntities = new ArrayList<>();
            try {
                croissantsFillingEntities.add(new CroissantsFilling(menuOfFillingRepositoryService.findOne(Long.parseLong(supportEntityRepositoryService.getByUserId(messaging.getSender().getId()).getType()))));

                for (String s : str) {
                    croissantsFillingEntities.add(new CroissantsFilling(menuOfFillingRepositoryService.findOne(Long.parseLong(s))));
            }

                croissantEntity.setCroissantsFillings(croissantsFillingEntities);
            croissantRepositoryService.saveAndFlush(croissantEntity);

            for (CroissantsFilling croissantsFilling : croissantsFillingEntities) {
                croissantsFillingEntityRepositoryService.saveAndFlush(croissantsFilling);
            }

            messageSenderService.sendSimpleMessage(recognizeService.recognize(ASK_PRICE.name(),messaging.getSender().getId()),messaging.getSender().getId());
        }
        catch (Exception ex){
            logger.warn(ex);
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_FILLING.name(),messaging.getSender().getId()), messaging.getSender().getId());
            menuOfFillingEventService.getMenuOfFilling(messaging.getSender().getId());
        }

    }

    private void parseImageUrl(Messaging messaging, CroissantEntity croissantEntity) {
        croissantEntity.setImageUrl(messaging.getMessage().getText());
        croissantRepositoryService.saveAndFlush(croissantEntity);
        messageSenderService.sendMessage(new Messaging(new Message(recognizeService.recognize(ID_OF_FILLING.name(),messaging.getSender().getId())), new Recipient(messaging.getSender().getId())));
        menuOfFillingEventService.getMenuOfFilling(messaging.getSender().getId());
    }

    private void parseName(Messaging messaging, CroissantEntity croissantEntity) {
        croissantEntity.setName(TextFormatter.toNormalFormat(messaging.getMessage().getText()));
        croissantRepositoryService.saveAndFlush(croissantEntity);
        messageSenderService.sendMessage(new Messaging(new Message(recognizeService.recognize(IMAGE_URL .name(),messaging.getSender().getId()) ), new Recipient(messaging.getSender().getId())));

    }
}
