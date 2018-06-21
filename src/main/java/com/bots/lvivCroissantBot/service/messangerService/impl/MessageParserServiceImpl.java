package com.bots.lvivCroissantBot.service.messangerService.impl;

import com.bots.lvivCroissantBot.entity.SupportEntity;
import com.bots.lvivCroissantBot.entity.register.MUser;
import com.bots.lvivCroissantBot.constantEnum.messengerEnum.Cases;
import com.bots.lvivCroissantBot.dto.messanger.Messaging;
import com.bots.lvivCroissantBot.service.eventService.messengerEventService.*;
import com.bots.lvivCroissantBot.service.repositoryService.CustomerOrderingRepositoryService;
import com.bots.lvivCroissantBot.service.repositoryService.SupportEntityRepositoryService;
import com.bots.lvivCroissantBot.service.messangerService.*;
import com.bots.lvivCroissantBot.service.peopleRegisterService.CourierRegisterService;
import com.bots.lvivCroissantBot.service.peopleRegisterService.MUserRepositoryService;
import com.bots.lvivCroissantBot.service.supportService.RecognizeService;
import com.bots.lvivCroissantBot.service.supportService.TextFormatter;
import com.bots.lvivCroissantBot.service.supportService.VerifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.Cases.*;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.speaking.ServerSideSpeaker.ABORTED;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.speaking.ServerSideSpeaker.U_ARE_NOT_A_COURIER;

@Service
public class MessageParserServiceImpl implements MessageParserService {
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private MUserRepositoryService MUserRepositoryService;
    @Autowired
    private CourierRegisterService courierRegisterService;
    @Autowired
    private OrderingEventService orderingEventService;
    @Autowired
    private MenuOfFillingEventService menuOfFillingEventService;
    @Autowired
    private CroissantSavingEventService croissantSavingEventService;
    @Autowired
    private CourierEventService courierEventService;
    @Autowired
    private RecognizeService recognizeService;
    @Autowired
    private CreatingOwnCroissantEventService creatingOwnCroissantEventService;
    @Autowired
    private GetMenuEventService getMenuEventService;
    @Autowired
    private UserEventService userEventService;
    @Autowired
    private QuickReplyParserService quickReplyParserService;
    @Autowired
    private VerifyService verifyService;
    @Autowired
    private BroadcastService broadcastService;
    @Autowired
    private CustomerOrderingRepositoryService customerOrderingRepositoryService;
    @Autowired
    private MessageProcessorHelperService messageProcessorHelperService;
    @Autowired
    private SupportEntityRepositoryService supportEntityRepositoryService;

    private   final static Logger logger = LoggerFactory.getLogger(MessageParserServiceImpl.class);

    private  static Map<String,Method> methodsHashMap;

    @Override
    public void parseMessage(Messaging messaging) {
        String message = messaging.getMessage().getText().toUpperCase();

        if (messaging.getMessage().getQuickReply() != null) {
            quickReplyParserService.parseQuickReply(messaging);
        } else {


            MUser mUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());


            if (mUser.getStatus() != null && !(message.equals(ABORT.name()))) {
                message = mUser.getStatus();
            }

            String userCommand = TextFormatter.toCamelCase(Cases.valueOf(message.toUpperCase()).toString());

            try {
                methodsHashMap.get(userCommand).invoke(this,messaging);
            } catch (IllegalAccessException e) {
                logger.error("Error",e);
            } catch (InvocationTargetException e) {
                logger.error("Error",e);
                e.printStackTrace();
            } catch (Exception ex) {
                logger.error("Error",ex);
                ex.printStackTrace();
            }

        }

    }

    @PostConstruct
    private void methodsHashMapInit() {
        Method[] methods = MessageParserServiceImpl.class.getDeclaredMethods();
        methodsHashMap = new HashMap<>();
        for(Method method: methods){
            methodsHashMap.put(method.getName(),method);
        }
    }
    private void deleteOrderings(Messaging messaging) {
        messageProcessorHelperService.helpDeleteOrderings(messaging);
        messageSenderService.sendSimpleMessage("Deleted!", messaging.getSender().getId());
    }

    private void navi(Messaging messaging) {
        messageSenderService.sendUserActions(messaging.getSender().getId());
        userEventService.changeStatus(messaging,null);
    }

    private void askTime(Messaging messaging) {
        orderingEventService.parseOrdering(messaging);
    }

    private void askPhoneNumber(Messaging messaging) {
        orderingEventService.parseOrdering(messaging);
    }

    private void askAddress(Messaging messaging) {
        orderingEventService.parseOrdering(messaging);
    }

    private void languageChange(Messaging messaging) {
        messageSenderService.askSelectLanguage(messaging.getSender().getId());
    }

    private void customerRegisterFinalize(Messaging messaging) {
        userEventService.changeStatus(messaging, CUSTOMER_REGISTER_FINALIZE.name());
        userEventService.customerRegistration(messaging);
    }

    private void personalRequest(Messaging messaging) {
        parseRoleRequest(messaging);
    }

    private void adminRequest(Messaging messaging) {
        parseRoleRequest(messaging);

    }

    private void courierRegistrationFinal(Messaging messaging) {
        userEventService.changeStatus(messaging, COURIER_REGISTRATION_FINAL.name());
        courierEventService.parseCourier(messaging);
    }

    private void courierRequest(Messaging messaging) {
        parseRoleRequest(messaging);
    }

    private void ordering(Messaging messaging) {
        userEventService.changeStatus(messaging, ORDERING.name());
        orderingEventService.parseOrdering(messaging);
    }

    private void fillingMenu(Messaging messaging) {
        menuOfFillingEventService.getMenuOfFilling(messaging.getSender().getId());

    }

    private void addFilling(Messaging messaging) {
        userEventService.changeStatus(messaging, ADD_FILLING.name());
        menuOfFillingEventService.saveNewFilling(messaging);
    }

    private void add(Messaging messaging) {
        userEventService.changeStatus(messaging, ADD.name());
        croissantSavingEventService.saveCroissant(messaging);
    }


    private void menu(Messaging messaging) {
        getMenuEventService.getMenu(messaging);

    }

    private void abort(Messaging messaging) {
        userEventService.changeStatus(messaging, null);
        if(supportEntityRepositoryService.getByUserId(messaging.getSender().getId())!=null) {
            SupportEntity supportEntity = supportEntityRepositoryService.getByUserId(messaging.getSender().getId());
            supportEntity.setOneMore(null);
            supportEntityRepositoryService.saveAndFlush(supportEntity);
        }
        messageSenderService.sendSimpleMessage(recognizeService.recognize(ABORTED.name(), messaging.getSender().getId()), messaging.getSender().getId());
    }


    private void completeCroissantSecondStep(Messaging messaging) {
        messageProcessorHelperService.helpCompleteCroissantSecondStep(messaging);
        creatingOwnCroissantEventService.CreateOwnCroissant(messaging);
    }

    private void createOwnCroissant(Messaging messaging) {
        creatingOwnCroissantEventService.CreateOwnCroissant(messaging);

    }

    private void completeOrderingsList(Messaging messaging) {
        messageProcessorHelperService.helpCompleteOrderingList(messaging);
        courierEventService.parseCourier(messaging);
    }

    private void completingOrderings(Messaging messaging) {
        messageProcessorHelperService.helpCompletingOrderings(messaging);
        courierEventService.parseCourier(messaging);
    }



    private void orderingListFilling(Messaging messaging) {
        messageProcessorHelperService.helpOrderingListFilling(messaging);
        courierEventService.parseCourier(messaging);
    }

    private void getListOfOrdering(Messaging messaging) {
        messageProcessorHelperService.helpGetListOfOrdering(messaging);
        courierEventService.parseCourier(messaging);
    }

    private void courierRegistration(Messaging messaging) {
        messageProcessorHelperService.helpCourierRegistration(messaging);
        courierEventService.parseCourier(messaging);
    }

    private void parseRoleRequest(Messaging messaging) {
        messageProcessorHelperService.helpParseRoleRequest(messaging);
    }

    private void courierActions(Messaging messaging) {
        if (courierRegisterService.findByRecipientId(messaging.getSender().getId()) != null) {
            messageSenderService.askForCourierActions(messaging.getSender().getId());
        } else
            messageSenderService.sendSimpleMessage(recognizeService.recognize(U_ARE_NOT_A_COURIER.name(), messaging.getSender().getId()), messaging.getSender().getId());
    }


}



