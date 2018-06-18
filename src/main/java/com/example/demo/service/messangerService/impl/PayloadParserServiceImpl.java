package com.example.demo.service.messangerService.impl;

import com.example.demo.entity.SupportEntity;
import com.example.demo.entity.peopleRegister.MUser;
import com.example.demo.constantEnum.messengerEnums.payloads.Payloads;
import com.example.demo.dto.messanger.Message;
import com.example.demo.dto.messanger.Messaging;
import com.example.demo.dto.messanger.QuickReply;
import com.example.demo.service.eventService.messengerEventService.CourierEventService;
import com.example.demo.service.eventService.messengerEventService.GetMenuEventService;
import com.example.demo.service.eventService.messengerEventService.UserEventService;
import com.example.demo.service.repositoryService.CroissantRepositoryService;
import com.example.demo.service.repositoryService.SupportEntityRepositoryService;
import com.example.demo.service.messangerService.MessageParserService;
import com.example.demo.service.messangerService.MessageSenderService;
import com.example.demo.service.messangerService.PayloadParserService;
import com.example.demo.service.peopleRegisterService.MUserRepositoryService;
import com.example.demo.service.supportService.RecognizeService;
import com.example.demo.service.supportService.TextFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.example.demo.constantEnum.messengerEnums.Cases.MENU;
import static com.example.demo.constantEnum.messengerEnums.Cases.NAVI;
import static com.example.demo.constantEnum.messengerEnums.Cases.ORDERING;
import static com.example.demo.constantEnum.messengerEnums.payloads.Payloads.FOR_GETTING_MENU;
import static com.example.demo.constantEnum.messengerEnums.payloads.Payloads.OWN_CROISSANT_MENU_PAYLOAD;
import static com.example.demo.constantEnum.messengerEnums.payloads.QuickReplyPayloads.CREATING_OWN_CROISSANT_PAYLOAD;
import static com.example.demo.constantEnum.messengerEnums.speaking.ServerSideSpeaker.DONE;
import static com.example.demo.constantEnum.messengerEnums.types.CroissantsTypes.OWN;

@Service
public class PayloadParserServiceImpl implements PayloadParserService {
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private MessageParserService messageParserService;
    @Autowired
    private RecognizeService recognizeService;
    @Autowired
    private CroissantRepositoryService croissantRepositoryService;
    @Autowired
    private MUserRepositoryService MUserRepositoryService;
    @Autowired
    private UserEventService userEventService;
    @Autowired
    private GetMenuEventService getMenuEventService;
    @Autowired
    private CourierEventService courierEventService;
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
        userEventService.changeStatus(messaging,NAVI.name());
        messaging.setMessage(new Message(""));
        messageParserService.parseMessage(messaging);
    }

    private void parseCompleteOrder(Messaging messaging) {
        String var = TextFormatter.ejectSingleVariable(messaging.getPostback().getPayload());
        courierEventService.completeOrderingsFinalize(messaging,Long.parseLong(var));
    }

    private void parseGetOrder(Messaging messaging) {
        String var = TextFormatter.ejectSingleVariable(messaging.getPostback().getPayload());
        courierEventService.orderingFilling(messaging,Long.parseLong(var));
    }

    private void parseShowMorePayload(Messaging messaging) {
        String payload = messaging.getPostback().getPayload();
        if (TextFormatter.ejectContext(payload).equals(FOR_GETTING_MENU.name())) {
            getMenuEventService.getMenu(messaging);
        } else {
            messaging.setMessage(new Message());
            messaging.getMessage().setQuickReply(new QuickReply());
            messaging.getMessage().getQuickReply().setPayload(TextFormatter.ejectContext(payload));
            courierEventService.parseCourier(messaging);
        }
    }


    private void parseOwnCroissantMenuPayload(Messaging messaging) {
        SupportEntity supportEntity = supportEntityRepositoryService.getByUserId(messaging.getSender().getId());
        supportEntity.setType(OWN.name());
        QuickReply quickReply = new QuickReply();
        quickReply.setPayload(OWN_CROISSANT_MENU_PAYLOAD.name() + "?" + OWN.name());
        Message message = new Message(MENU.name());
        message.setQuickReply(quickReply);
        messaging.setMessage(message);
        getMenuEventService.getMenu(messaging);

    }

    private void orderCroissant(Messaging messaging) {
        messaging.setMessage(new Message(ORDERING.name()));
        messageParserService.parseMessage(messaging);
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
        messageParserService.parseMessage(messaging);
    }

    private void parseGetStarted(Messaging messaging) {
        if(MUserRepositoryService.findOnebyRId(messaging.getSender().getId())==null)
            userEventService.customerRegistration(messaging);
        else {
            messageSenderService.askSelectLanguage(messaging.getSender().getId());

        }
    }


}
