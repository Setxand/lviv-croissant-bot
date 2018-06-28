package com.bots.lvivcroissantbot.service.messenger.event.impl;

import com.bots.lvivcroissantbot.dto.messanger.Message;
import com.bots.lvivcroissantbot.dto.messanger.Messaging;
import com.bots.lvivcroissantbot.dto.messanger.Recipient;
import com.bots.lvivcroissantbot.entity.lvivcroissants.CroissantEntity;
import com.bots.lvivcroissantbot.entity.lvivcroissants.CroissantsFilling;
import com.bots.lvivcroissantbot.entity.register.MUser;
import com.bots.lvivcroissantbot.exception.ElementNoFoundException;
import com.bots.lvivcroissantbot.repository.CroisantsFillingEntityRepository;
import com.bots.lvivcroissantbot.repository.CustomerOrderingRepository;
import com.bots.lvivcroissantbot.repository.MenuOfFillingRepository;
import com.bots.lvivcroissantbot.repository.SupportEntityRepository;
import com.bots.lvivcroissantbot.service.messenger.MessageParserService;
import com.bots.lvivcroissantbot.service.messenger.MessageSenderService;
import com.bots.lvivcroissantbot.service.messenger.QuickReplyParserService;
import com.bots.lvivcroissantbot.service.messenger.event.CroissantSavingService;
import com.bots.lvivcroissantbot.service.messenger.event.MenuOfFillingService;
import com.bots.lvivcroissantbot.service.peopleregister.CourierRegisterService;
import com.bots.lvivcroissantbot.service.peopleregister.MUserRepositoryService;
import com.bots.lvivcroissantbot.service.support.RecognizeService;
import com.bots.lvivcroissantbot.service.support.TextFormatter;
import com.bots.lvivcroissantbot.service.uni.CroissantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.bots.lvivcroissantbot.constantenum.messenger.Cases.ADD;
import static com.bots.lvivcroissantbot.constantenum.messenger.payload.QuickReplyPayloads.TYPE_PAYLOAD;
import static com.bots.lvivcroissantbot.constantenum.messenger.speaking.ServerSideSpeaker.*;

@Service
public class CroissantSavingServiceImpl implements CroissantSavingService {
    private final static Logger logger = LoggerFactory.getLogger(CroissantSavingServiceImpl.class);
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private MenuOfFillingRepository menuOfFillingRepositoryService;
    @Autowired
    private CroissantService croissantRepositoryService;
    @Autowired
    private QuickReplyParserService quickReplyParserService;
    @Autowired
    private CroisantsFillingEntityRepository croissantsFillingEntityRepositoryService;
    @Autowired
    private CustomerOrderingRepository customerOrderingRepositoryService;
    @Autowired
    private MUserRepositoryService MUserRepositoryService;
    @Autowired
    private CourierRegisterService courierRegisterService;
    @Autowired
    private MenuOfFillingService menuOfFillingService;
    @Autowired
    private MessageParserService messageParserServiceService;
    @Autowired
    private RecognizeService recognizeService;
    @Autowired
    private SupportEntityRepository supportEntityRepositoryService;

    @Override
    public void saveCroissant(Messaging messaging) {

        try {
            if (messaging.getMessage().getText().toUpperCase().equals(ADD.name()))
                messageSenderService.askTypeOfCroissants(messaging.getSender().getId(), TYPE_PAYLOAD.name() + "?");
            else {
                finalizeSavingCroissant(messaging);
            }

        } catch (Exception ex) {

            CroissantEntity croissantEntity = croissantRepositoryService.findTopByOrderByIdDesc();
            if (croissantEntity.getCroissantsFillings().isEmpty())
                croissantRepositoryService.delete(croissantEntity);
            messageSenderService.errorMessage(messaging.getSender().getId());
            logger.error("Error", ex);
        }
    }


    private void finalizeSavingCroissant(Messaging messaging) {
        MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());
        CroissantEntity croissantEntity = croissantRepositoryService.findTopByCreatorIdOrderByIdDesc(messaging.getSender().getId());


        if (croissantEntity.getName() == null) {
            parseName(messaging, croissantEntity);

        } else if (croissantEntity.getImageUrl() == null) {
            parseImageUrl(messaging, croissantEntity);


        } else if (croissantEntity.getCroissantsFillings().isEmpty()) {
            parseCroissantsFillings(messaging, croissantEntity);
        } else if (croissantEntity.getPrice() == 0) {
            parsePrice(messaging, croissantEntity);

        } else {
            MUser.setStatus(null);
            MUserRepositoryService.saveAndFlush(MUser);
            errorAction(messaging, croissantEntity);

        }
    }

    private void parsePrice(Messaging messaging, CroissantEntity croissantEntity) {
        try {
            croissantEntity.setPrice(Integer.parseInt(messaging.getMessage().getText()));
            croissantRepositoryService.saveAndFlush(croissantEntity);
            messageSenderService.sendMessage(new Messaging(new Message(recognizeService.recognize(CROISSANT_SUCCESSFULLY_ADDED.name(), messaging.getSender().getId())), new Recipient(messaging.getSender().getId())));
            MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());
            MUser.setStatus(null);
            MUserRepositoryService.saveAndFlush(MUser);

        } catch (Exception ex) {
            logger.error("Error", ex);
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_PRICE.name(), messaging.getSender().getId()), messaging.getSender().getId());
        }
    }

    private void errorAction(Messaging messaging, CroissantEntity croissantEntity) {
        messageSenderService.errorMessage(messaging.getSender().getId());
        if (croissantEntity.getCroissantsFillings().isEmpty())
            croissantRepositoryService.delete(croissantEntity);
    }

    private void parseCroissantsFillings(Messaging messaging, CroissantEntity croissantEntity) {
        String[] str = messaging.getMessage().getText().split(",");
        List<CroissantsFilling> croissantsFillingEntities = new ArrayList<>();
        try {
            croissantsFillingEntities.add(new CroissantsFilling(menuOfFillingRepositoryService.findById(Long.parseLong(supportEntityRepositoryService.findByUserId(messaging.getSender().getId()).getType())).orElseThrow(ElementNoFoundException::new)));

            for (String s : str) {
                croissantsFillingEntities.add(new CroissantsFilling(menuOfFillingRepositoryService.findById(Long.parseLong(s)).orElseThrow(ElementNoFoundException::new)));
            }

            croissantEntity.setCroissantsFillings(croissantsFillingEntities);
            croissantRepositoryService.saveAndFlush(croissantEntity);

            for (CroissantsFilling croissantsFilling : croissantsFillingEntities) {
                croissantsFillingEntityRepositoryService.saveAndFlush(croissantsFilling);
            }

            messageSenderService.sendSimpleMessage(recognizeService.recognize(ASK_PRICE.name(), messaging.getSender().getId()), messaging.getSender().getId());
        } catch (Exception ex) {
            logger.error("Error", ex);
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_FILLING.name(), messaging.getSender().getId()), messaging.getSender().getId());
            menuOfFillingService.getMenuOfFilling(messaging.getSender().getId());
        }

    }

    private void parseImageUrl(Messaging messaging, CroissantEntity croissantEntity) {
        croissantEntity.setImageUrl(messaging.getMessage().getText());
        croissantRepositoryService.saveAndFlush(croissantEntity);
        messageSenderService.sendMessage(new Messaging(new Message(recognizeService.recognize(ID_OF_FILLING.name(), messaging.getSender().getId())), new Recipient(messaging.getSender().getId())));
        menuOfFillingService.getMenuOfFilling(messaging.getSender().getId());
    }

    private void parseName(Messaging messaging, CroissantEntity croissantEntity) {
        croissantEntity.setName(TextFormatter.toNormalFormat(messaging.getMessage().getText()));
        croissantRepositoryService.saveAndFlush(croissantEntity);
        messageSenderService.sendMessage(new Messaging(new Message(recognizeService.recognize(IMAGE_URL.name(), messaging.getSender().getId())), new Recipient(messaging.getSender().getId())));

    }
}
