package com.bots.lvivcroissantbot.service.messenger.event.impl;

import com.bots.lvivcroissantbot.constantenum.messenger.Cases;
import com.bots.lvivcroissantbot.dto.messanger.Message;
import com.bots.lvivcroissantbot.dto.messanger.Messaging;
import com.bots.lvivcroissantbot.dto.messanger.QuickReply;
import com.bots.lvivcroissantbot.entity.Support;
import com.bots.lvivcroissantbot.entity.lvivcroissants.CroissantEntity;
import com.bots.lvivcroissantbot.entity.lvivcroissants.CroissantsFilling;
import com.bots.lvivcroissantbot.entity.register.MUser;
import com.bots.lvivcroissantbot.exception.ElementNoFoundException;
import com.bots.lvivcroissantbot.repository.CroisantsFillingEntityRepository;
import com.bots.lvivcroissantbot.repository.MenuOfFillingRepository;
import com.bots.lvivcroissantbot.repository.SupportEntityRepository;
import com.bots.lvivcroissantbot.service.messenger.MessageParserService;
import com.bots.lvivcroissantbot.service.messenger.MessageSenderService;
import com.bots.lvivcroissantbot.service.messenger.event.CreatingOwnCroissantService;
import com.bots.lvivcroissantbot.service.messenger.event.GetMenuService;
import com.bots.lvivcroissantbot.service.messenger.event.MenuOfFillingService;
import com.bots.lvivcroissantbot.service.messenger.event.UserService;
import com.bots.lvivcroissantbot.service.peopleregister.MUserRepositoryService;
import com.bots.lvivcroissantbot.service.support.RecognizeService;
import com.bots.lvivcroissantbot.service.support.TextFormatter;
import com.bots.lvivcroissantbot.service.uni.CroissantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.bots.lvivcroissantbot.constantenum.messenger.Cases.COMPLETE_CROISSANT_SECOND_STEP;
import static com.bots.lvivcroissantbot.constantenum.messenger.Cases.MENU;
import static com.bots.lvivcroissantbot.constantenum.messenger.speaking.ServerSideSpeaker.*;
import static com.bots.lvivcroissantbot.constantenum.messenger.type.CroissantsTypes.OWN;

@Service
public class CreatingOwnCroissantServiceImpl implements CreatingOwnCroissantService {

    private final static Logger logger = LoggerFactory.getLogger(CreatingOwnCroissantServiceImpl.class);
    @Autowired
    private CroissantService croissantRepositoryService;
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private MessageParserService messageParserServiceService;
    @Autowired
    private RecognizeService recognizeService;
    @Autowired
    private MenuOfFillingService menuOfFillingService;
    @Autowired
    private MenuOfFillingRepository menuOfFillingRepositoryService;
    @Autowired
    private CroisantsFillingEntityRepository croissantsFillingEntityRepositoryService;
    @Autowired
    private GetMenuService getMenuService;
    @Autowired
    private MUserRepositoryService MUserRepositoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private SupportEntityRepository supportEntityRepositoryService;

    @Override
    public void CreateOwnCroissant(Messaging messaging) {
        String message = messaging.getMessage().getText();
        String var = TextFormatter.ejectSingleVariable(message);
        String payload = TextFormatter.ejectPaySinglePayload(message);

        if (payload.equals(COMPLETE_CROISSANT_SECOND_STEP.name())) {
            finalStepCreating(messaging, var);
        } else {
            firstStepCreating(messaging);
        }


    }


    private void firstStepCreating(Messaging messaging) {
        CroissantEntity croissantEntity = new CroissantEntity("Власний круасан", OWN.name());
        Support support = supportEntityRepositoryService.findByUserId(messaging.getSender().getId());
        support.setType(OWN.name());
        supportEntityRepositoryService.saveAndFlush(support);
        croissantEntity.setImageUrl("http://www.mlinar.hr/wp-content/uploads/2013/06/mlinar-proizvodi-croissantEntity-600x380.png");
        String payload = messaging.getPostback().getPayload();
        croissantEntity.setCroissantsFillings(new ArrayList<CroissantsFilling>());
        croissantEntity.addSingleFilling(new CroissantsFilling(menuOfFillingRepositoryService.findById(Long.parseLong(payload)).orElseThrow(ElementNoFoundException::new)));
        croissantRepositoryService.saveAndFlush(croissantEntity);

        MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());

        MUser.getOwnCroissantsId().add(croissantEntity.getId());
        MUserRepositoryService.saveAndFlush(MUser);
        menuOfFillingService.getMenuOfFilling(messaging.getSender().getId());
        userService.changeStatus(messaging, COMPLETE_CROISSANT_SECOND_STEP.name());
        messageSenderService.sendSimpleMessage(recognizeService.recognize(ID_OF_FILLING.name(), messaging.getSender().getId()), messaging.getSender().getId());
    }

    private void finalStepCreating(Messaging messaging, String var) {
        try {
            MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());
            String[] fillings = var.split(",");
            CroissantEntity croissantEntity = croissantRepositoryService.findOne(MUser.getOwnCroissantsId().get(MUser.getOwnCroissantsId().size() - 1));
            int price = 0;
            for (String filling : fillings) {
                CroissantsFilling croissantsFilling = new CroissantsFilling(menuOfFillingRepositoryService.findById(Long.parseLong(filling)).orElseThrow(ElementNoFoundException::new));
                croissantEntity.addSingleFilling(croissantsFilling);
                price += croissantsFilling.getPrice();
            }
            croissantEntity.setPrice(price);
            croissantRepositoryService.saveAndFlush(croissantEntity);
            for (CroissantsFilling croissantsFilling : croissantEntity.getCroissantsFillings()) {
                croissantsFillingEntityRepositoryService.saveAndFlush(croissantsFilling);
            }

            //here maby must be change
            QuickReply quickReply = new QuickReply();
            quickReply.setPayload(Cases.CREATE_OWN_CROISSANT.name() + "?" + OWN.name());
            Message message = new Message(MENU.name());
            message.setQuickReply(quickReply);
            messaging.setMessage(message);
            messageSenderService.sendSimpleMessage(recognizeService.recognize(CREATED_CROISSANT.name(), messaging.getSender().getId()), messaging.getSender().getId());
            //here


            userService.changeStatus(messaging, null);

            getMenuService.getMenu(messaging);

        } catch (Exception ex) {
            logger.error("Error", ex);
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_FILLING.name(), messaging.getSender().getId()), messaging.getSender().getId());
            menuOfFillingService.getMenuOfFilling(messaging.getSender().getId());
            messageSenderService.sendSimpleMessage(recognizeService.recognize(ID_OF_FILLING.name(), messaging.getSender().getId()), messaging.getSender().getId());
        }
    }


}
