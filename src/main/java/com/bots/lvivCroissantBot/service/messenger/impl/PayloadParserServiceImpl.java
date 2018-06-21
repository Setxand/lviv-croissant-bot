package com.bots.lvivCroissantBot.service.messenger.impl;

import com.bots.lvivCroissantBot.entity.Support;
import com.bots.lvivCroissantBot.entity.register.MUser;
import com.bots.lvivCroissantBot.constantEnum.messengerEnum.payload.Payloads;
import com.bots.lvivCroissantBot.dto.messanger.Message;
import com.bots.lvivCroissantBot.dto.messanger.Messaging;
import com.bots.lvivCroissantBot.dto.messanger.QuickReply;
import com.bots.lvivCroissantBot.service.messenger.event.CourierService;
import com.bots.lvivCroissantBot.service.messenger.event.GetMenuService;
import com.bots.lvivCroissantBot.service.messenger.event.UserService;
import com.bots.lvivCroissantBot.service.repository.CroissantRepositoryService;
import com.bots.lvivCroissantBot.service.repository.SupportEntityRepositoryService;
import com.bots.lvivCroissantBot.service.messenger.MessageParserService;
import com.bots.lvivCroissantBot.service.messenger.MessageSenderService;
import com.bots.lvivCroissantBot.service.messenger.PayloadParserService;
import com.bots.lvivCroissantBot.service.peopleRegister.MUserRepositoryService;
import com.bots.lvivCroissantBot.service.support.RecognizeService;
import com.bots.lvivCroissantBot.service.support.TextFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.Cases.MENU;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.Cases.NAVI;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.Cases.ORDERING;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.payload.Payloads.FOR_GETTING_MENU;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.payload.Payloads.OWN_CROISSANT_MENU_PAYLOAD;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.payload.QuickReplyPayloads.CREATING_OWN_CROISSANT_PAYLOAD;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.speaking.ServerSideSpeaker.DONE;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.type.CroissantsTypes.OWN;

@Service
public class PayloadParserServiceImpl implements PayloadParserService {
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private MessageParserService messageParserServiceService;
    @Autowired
    private RecognizeService recognizeService;
    @Autowired
    private CroissantRepositoryService croissantRepositoryService;
    @Autowired
    private MUserRepositoryService MUserRepositoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private GetMenuService getMenuService;
    @Autowired
    private CourierService courierService;
    @Autowired
    private SupportEntityRepositoryService supportEntityRepositoryService;
    @Value("${server.url}")
    private String SERVER_URL;


    @Override
    public void parsePayload(Messaging messaging) {
        String fullPayload = messaging.getPostback().getPayload();
        switch (Payloads.valueOf(TextFormatter.ejectPaySinglePayload(fullPayload))) {

            case ORDER_PAYLOAD:
                orderCroissant(messaging);
                break;
            case GET_STARTED_PAYLOAD:
                parseGetStarted(messaging);
                break;
            case MENU_PAYLOAD:
                parseMenuPayload(messaging);
                break;
            case CREATE_OWN_CROISSANT_PAYLOAD:
                parseOwnCroissant(messaging);
                break;
            case DELETE_BUTTON_PAYLOAD:
                parseDelButtonPayload(messaging);
                break;
            case OWN_CROISSANT_MENU_PAYLOAD:
                parseOwnCroissantMenuPayload(messaging);
                break;
            case SHOW_MORE_PAYLOAD:
                parseShowMorePayload(messaging);
                break;
            case GET_ORDER:
                parseGetOrder(messaging);
                break;
            case COMPLETE_ORDER:
                parseCompleteOrder(messaging);
                break;
            case NAVIGATION_MENU:
                navigationMenu(messaging);
                break;
            default:
                messageSenderService.errorMessage(messaging.getSender().getId());
                break;
        }
    }



    private void navigationMenu(Messaging messaging) {
        userService.changeStatus(messaging,NAVI.name());
        messaging.setMessage(new Message(""));
        messageParserServiceService.parseMessage(messaging);
    }

    private void parseCompleteOrder(Messaging messaging) {
        String var = TextFormatter.ejectSingleVariable(messaging.getPostback().getPayload());
        courierService.completeOrderingsFinalize(messaging,Long.parseLong(var));
    }

    private void parseGetOrder(Messaging messaging) {
        String var = TextFormatter.ejectSingleVariable(messaging.getPostback().getPayload());
        courierService.orderingFilling(messaging,Long.parseLong(var));
    }

    private void parseShowMorePayload(Messaging messaging) {
        String payload = messaging.getPostback().getPayload();
        if (TextFormatter.ejectContext(payload).equals(FOR_GETTING_MENU.name())) {
            getMenuService.getMenu(messaging);
        } else {
            messaging.setMessage(new Message());
            messaging.getMessage().setQuickReply(new QuickReply());
            messaging.getMessage().getQuickReply().setPayload(TextFormatter.ejectContext(payload));
            courierService.parseCourier(messaging);
        }
    }


    private void parseOwnCroissantMenuPayload(Messaging messaging) {
        Support support = supportEntityRepositoryService.getByUserId(messaging.getSender().getId());
        support.setType(OWN.name());
        QuickReply quickReply = new QuickReply();
        quickReply.setPayload(OWN_CROISSANT_MENU_PAYLOAD.name() + "?" + OWN.name());
        Message message = new Message(MENU.name());
        message.setQuickReply(quickReply);
        messaging.setMessage(message);
        getMenuService.getMenu(messaging);

    }

    private void orderCroissant(Messaging messaging) {
        messaging.setMessage(new Message(ORDERING.name()));
        messageParserServiceService.parseMessage(messaging);
    }

    private void parseDelButtonPayload(Messaging messaging) {
        String varPayload = TextFormatter.ejectSingleVariable(messaging.getPostback().getPayload());
        croissantRepositoryService.remove(croissantRepositoryService.findOne(Long.parseLong(varPayload)));
        MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());
        MUser.getOwnCroissantsId().remove(Long.parseLong(varPayload));
        MUserRepositoryService.saveAndFlush(MUser);
        messageSenderService.sendSimpleMessage(recognizeService.recognize(DONE.name(), messaging.getSender().getId()), messaging.getSender().getId());
    }

    private void parseOwnCroissant(Messaging messaging) {
        messageSenderService.askTypeOfCroissants(messaging.getSender().getId(),CREATING_OWN_CROISSANT_PAYLOAD.name()+"?");

    }


    private void parseMenuPayload(Messaging messaging) {
        messaging.setPostback(null);
        messaging.setMessage(new Message(MENU.name()));
        messageParserServiceService.parseMessage(messaging);
    }

    private void parseGetStarted(Messaging messaging) {
        if(MUserRepositoryService.findOnebyRId(messaging.getSender().getId())==null)
            userService.customerRegistration(messaging);
        else {
            messageSenderService.askSelectLanguage(messaging.getSender().getId());

        }
    }


}
