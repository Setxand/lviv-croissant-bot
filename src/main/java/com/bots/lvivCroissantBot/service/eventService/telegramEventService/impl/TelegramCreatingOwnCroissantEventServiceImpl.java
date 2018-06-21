package com.bots.lvivCroissantBot.service.eventService.telegramEventService.impl;

import com.bots.lvivCroissantBot.entity.lvivCroissants.CroissantEntity;
import com.bots.lvivCroissantBot.entity.lvivCroissants.CroissantsFilling;
import com.bots.lvivCroissantBot.entity.register.TUser;
import com.bots.lvivCroissantBot.constantEnum.messengerEnum.type.CroissantsTypes;
import com.bots.lvivCroissantBot.dto.telegram.Message;
import com.bots.lvivCroissantBot.service.eventService.telegramEventService.TelegramCreatingOwnCroissantEventService;
import com.bots.lvivCroissantBot.service.eventService.telegramEventService.TelegramGetMenuEventService;
import com.bots.lvivCroissantBot.service.repositoryService.CroissantRepositoryService;
import com.bots.lvivCroissantBot.service.repositoryService.CroissantsFillingEntityRepositoryService;
import com.bots.lvivCroissantBot.service.repositoryService.MenuOfFillingRepositoryService;
import com.bots.lvivCroissantBot.service.peopleRegisterService.TelegramUserRepositoryService;
import com.bots.lvivCroissantBot.service.telegramService.TelegramMessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ResourceBundle;

import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.speaking.ServerSideSpeaker.*;
import static com.bots.lvivCroissantBot.constantEnum.telegramEnum.TelegramUserStatus.GETTING_MENU_STATUS;
import static com.bots.lvivCroissantBot.constantEnum.telegramEnum.TelegramUserStatus.INPUTTING_FILLINGS_IN_OWN_CROISSANT_STATUS;

@Service
public class TelegramCreatingOwnCroissantEventServiceImpl implements TelegramCreatingOwnCroissantEventService {
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Autowired
    private TelegramGetMenuEventService telegramGetMenuEventService;
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private MenuOfFillingRepositoryService menuOfFillingRepositoryService;
    @Autowired
    private CroissantRepositoryService croissantRepositoryService;
    @Autowired
    private CroissantsFillingEntityRepositoryService croissantsFillingEntityRepositoryService;
    @Override
    public void createOwn(Message message) {
        TUser tUser = telegramUserRepositoryService.findByChatId(message.getChat().getId());
        switch (tUser.getStatus()){
            case OWN_MENU_STATUS:
                startCreatingOwnCroissant(message,tUser);
                break;
            case INPUTTING_FILLINGS_IN_OWN_CROISSANT_STATUS:
                inputtingFillingsIfOnwCroissant(message,tUser);
                break;
            default:
                telegramMessageSenderService.errorMessage(message);                break;
        }


    }

    private void inputtingFillingsIfOnwCroissant(Message message, TUser tUser) {
        String text = message.getText();
        CroissantEntity croissantEntity = new CroissantEntity();
        croissantEntity.setImageUrl("http://www.mlinar.hr/wp-content/uploads/2013/06/mlinar-proizvodi-croissantEntity-600x380.png");
        croissantEntity.setName(ResourceBundle.getBundle("dictionary").getString(OWN_CROISSANT.name()));
        croissantEntity.setType(CroissantsTypes.OWN.name());
        croissantEntity = croissantRepositoryService.saveAndFlush(croissantEntity);
        try {
            String[] fillings = text.split(",");
            for(String filling:fillings) {
                CroissantsFilling croissantsFilling = new CroissantsFilling(menuOfFillingRepositoryService.findOne(Long.parseLong(filling)));
                croissantEntity.addSingleFilling(croissantsFilling);
                croissantEntity.setPrice(croissantEntity.getPrice()+ croissantsFilling.getPrice());
                croissantsFillingEntityRepositoryService.saveAndFlush(croissantsFilling);
            }
            tUser.addCroissant(croissantEntity);
            telegramUserRepositoryService.saveAndFlush(tUser);
            croissantRepositoryService.saveAndFlush(croissantEntity);
            finalCreating(message, tUser);

        }
        catch (HttpClientErrorException hEx){
            hEx.printStackTrace();
            telegramUserRepositoryService.changeStatus(tUser,null);
        }
        catch (Exception ex){
            ex.printStackTrace();
            telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(NON_CORRECT_FORMAT_OF_FILLING.name()),message);
            telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(ID_OF_FILLING.name()),message);
        }


    }

    private void finalCreating(Message message, TUser tUser) {
        telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(CREATED_CROISSANT.name()),message);
        telegramUserRepositoryService.changeStatus(tUser,GETTING_MENU_STATUS);
        message.setText(CroissantsTypes.OWN.name());
        telegramGetMenuEventService.getMenu(message);
    }

    private void startCreatingOwnCroissant(Message message, TUser tUser) {
        telegramMessageSenderService.simpleMessage("Here you can to create your own croissantEntity!!!",message);
        telegramGetMenuEventService.getMenuOfFillings(message);
        telegramMessageSenderService.simpleMessage( ResourceBundle.getBundle("dictionary").getString(ID_OF_FILLING.name()),message);
        telegramUserRepositoryService.changeStatus(tUser, INPUTTING_FILLINGS_IN_OWN_CROISSANT_STATUS);
    }
}
