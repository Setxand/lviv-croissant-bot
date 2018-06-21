package com.bots.lvivCroissantBot.service.eventService.messengerEventService.impl;

import com.bots.lvivCroissantBot.entity.lvivCroissants.MenuOfFilling;
import com.bots.lvivCroissantBot.entity.register.MUser;
import com.bots.lvivCroissantBot.dto.messanger.Message;
import com.bots.lvivCroissantBot.dto.messanger.Messaging;
import com.bots.lvivCroissantBot.dto.messanger.Recipient;
import com.bots.lvivCroissantBot.service.eventService.messengerEventService.MenuOfFillingEventService;
import com.bots.lvivCroissantBot.service.repositoryService.MenuOfFillingRepositoryService;
import com.bots.lvivCroissantBot.service.messangerService.MessageSenderService;
import com.bots.lvivCroissantBot.service.peopleRegisterService.MUserRepositoryService;
import com.bots.lvivCroissantBot.service.supportService.RecognizeService;
import com.bots.lvivCroissantBot.service.supportService.TextFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.Cases.ADD_FILLING;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.speaking.ServerSideSpeaker.*;

@Service
public class MenuOfFillingEventServiceImpl implements MenuOfFillingEventService {
    @Autowired
    private MenuOfFillingRepositoryService menuOfFillingRepositoryService;
    @Autowired
    private MessageSenderService messageSenderService;

    @Autowired
    private RecognizeService recognizeService;
    @Autowired
    private MUserRepositoryService MUserRepositoryService;

    private final static Logger logger = LoggerFactory.getLogger(MenuOfFillingEventServiceImpl.class);


    @Override
    public void getMenuOfFilling(Long recipient) {
        List<MenuOfFilling> menuOfFillings = menuOfFillingRepositoryService.getAll();
        String listStr = "";
        Iterator<MenuOfFilling> iterator = menuOfFillings.iterator();
        while(iterator.hasNext()){
            MenuOfFilling menuOfFilling = iterator.next();
            listStr+= menuOfFilling+"\n";
        }
        messageSenderService.sendMessage(new Messaging(new Message(listStr),new Recipient(recipient)));
    }

    @Override
    public void saveNewFilling(Messaging messaging) {
        String mesText = messaging.getMessage().getText().toUpperCase();
        if(mesText.equals(ADD_FILLING.name())){
            messageSenderService.sendMessage(new Messaging(new Message(recognizeService.recognize(NAME_OF_FILLING.name(),messaging.getSender().getId())), new Recipient(messaging.getSender().getId())));
        }
        else {
            MenuOfFilling menuOfFilling = null;
            try {
                String[] str = mesText.split(",");
                str[0] = TextFormatter.toNormalFormat(str[0]);
                menuOfFilling = new MenuOfFilling(str[0],Integer.parseInt(str[1]));
                menuOfFillingRepositoryService.saveAndFlush(menuOfFilling);
                messageSenderService.sendMessage(new Messaging(new Message(recognizeService.recognize(FILLING_WAS_ADDED.name(),messaging.getSender().getId())), new Recipient(messaging.getSender().getId())));
                MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());
                MUser.setStatus(null);
                MUserRepositoryService.saveAndFlush(MUser);
            }
            catch (Exception ex){
                logger.error("Error",ex);
                messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_FILLING.name(),messaging.getSender().getId()),messaging.getSender().getId());
                messageSenderService.sendMessage(new Messaging(new Message(recognizeService.recognize(NAME_OF_FILLING.name(),messaging.getSender().getId())), new Recipient(messaging.getSender().getId())));
                if(menuOfFilling!=null)
                    menuOfFillingRepositoryService.remove(menuOfFilling);
                    messageSenderService.errorMessage(messaging.getSender().getId());
            }

        }
    }
}
