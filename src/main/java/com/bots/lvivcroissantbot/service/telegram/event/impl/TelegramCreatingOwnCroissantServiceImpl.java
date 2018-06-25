package com.bots.lvivcroissantbot.service.telegram.event.impl;

import com.bots.lvivcroissantbot.entity.lvivcroissants.CroissantEntity;
import com.bots.lvivcroissantbot.entity.lvivcroissants.CroissantsFilling;
import com.bots.lvivcroissantbot.entity.register.TUser;
import com.bots.lvivcroissantbot.constantenum.messenger.type.CroissantsTypes;
import com.bots.lvivcroissantbot.dto.telegram.Message;
import com.bots.lvivcroissantbot.exception.ElementNoFoundException;
import com.bots.lvivcroissantbot.repository.CroisantsFillingEntityRepository;
import com.bots.lvivcroissantbot.repository.MenuOfFillingRepository;
import com.bots.lvivcroissantbot.service.telegram.event.TelegramCreatingOwnCroissantService;
import com.bots.lvivcroissantbot.service.telegram.event.TelegramGetMenuService;
import com.bots.lvivcroissantbot.service.peopleregister.TelegramUserRepositoryService;
import com.bots.lvivcroissantbot.service.telegram.TelegramMessageSenderService;
import com.bots.lvivcroissantbot.service.uni.CroissantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ResourceBundle;

import static com.bots.lvivcroissantbot.constantenum.messenger.speaking.ServerSideSpeaker.*;
import static com.bots.lvivcroissantbot.constantenum.telegram.TelegramUserStatus.GETTING_MENU_STATUS;
import static com.bots.lvivcroissantbot.constantenum.telegram.TelegramUserStatus.INPUTTING_FILLINGS_IN_OWN_CROISSANT_STATUS;

@Service
public class TelegramCreatingOwnCroissantServiceImpl implements TelegramCreatingOwnCroissantService {
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    @Autowired
    private TelegramGetMenuService telegramGetMenuService;
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private MenuOfFillingRepository menuOfFillingRepositoryService;
    @Autowired
    private CroissantService croissantRepositoryService;
    @Autowired
    private CroisantsFillingEntityRepository croissantsFillingEntityRepositoryService;
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
                CroissantsFilling croissantsFilling = new CroissantsFilling(menuOfFillingRepositoryService.findById(Long.parseLong(filling)).orElseThrow(ElementNoFoundException::new));
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
        telegramGetMenuService.getMenu(message);
    }

    private void startCreatingOwnCroissant(Message message, TUser tUser) {
        telegramMessageSenderService.simpleMessage("Here you can to create your own croissantEntity!!!",message);
        telegramGetMenuService.getMenuOfFillings(message);
        telegramMessageSenderService.simpleMessage( ResourceBundle.getBundle("dictionary").getString(ID_OF_FILLING.name()),message);
        telegramUserRepositoryService.changeStatus(tUser, INPUTTING_FILLINGS_IN_OWN_CROISSANT_STATUS);
    }
}
