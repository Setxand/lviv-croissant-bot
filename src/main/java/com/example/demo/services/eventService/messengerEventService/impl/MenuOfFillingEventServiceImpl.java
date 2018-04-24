package com.example.demo.services.eventService.messengerEventService.impl;

import com.example.demo.entities.lvivCroissants.MenuOfFilling;
import com.example.demo.entities.peopleRegister.User;
import com.example.demo.models.messanger.Message;
import com.example.demo.models.messanger.Messaging;
import com.example.demo.models.messanger.Recipient;
import com.example.demo.services.eventService.messengerEventService.MenuOfFillingEventService;
import com.example.demo.services.lvivCroissantRepositoryService.MenuOfFillingRepositoryService;
import com.example.demo.services.messangerService.MessageSenderService;
import com.example.demo.services.peopleRegisterService.UserRepositoryService;
import com.example.demo.services.supportService.RecognizeService;
import com.example.demo.services.supportService.TextFormatter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

import static com.example.demo.enums.messengerEnums.Cases.ADD_FILLING;
import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.*;

@Service
public class MenuOfFillingEventServiceImpl implements MenuOfFillingEventService {
    @Autowired
    private MenuOfFillingRepositoryService menuOfFillingRepositoryService;
    @Autowired
    private MessageSenderService messageSenderService;

    @Autowired
    private RecognizeService recognizeService;
    @Autowired
    private UserRepositoryService userRepositoryService;

    private static final Logger logger = Logger.getLogger(MenuOfFillingEventServiceImpl.class);


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
                User user = userRepositoryService.findOnebyRId(messaging.getSender().getId());
                user.setStatus(null);
                userRepositoryService.saveAndFlush(user);
            }
            catch (Exception ex){
                logger.warn(ex);
                messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_FILLING.name(),messaging.getSender().getId()),messaging.getSender().getId());
                getMenuOfFilling(messaging.getSender().getId());
                messageSenderService.sendMessage(new Messaging(new Message(recognizeService.recognize(NAME_OF_FILLING.name(),messaging.getSender().getId())), new Recipient(messaging.getSender().getId())));
                if(menuOfFilling!=null)
                    menuOfFillingRepositoryService.remove(menuOfFilling);
                    messageSenderService.errorMessage(messaging.getSender().getId());
            }

        }
    }
}
