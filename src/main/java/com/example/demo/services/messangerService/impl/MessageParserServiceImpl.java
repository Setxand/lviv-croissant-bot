package com.example.demo.services.messangerService.impl;

import com.example.demo.entities.lvivCroissants.SupportEntity;
import com.example.demo.entities.peopleRegister.User;
import com.example.demo.enums.messengerEnums.Cases;
import com.example.demo.models.messanger.Messaging;
import com.example.demo.services.eventService.messengerEventService.*;
import com.example.demo.services.lvivCroissantRepositoryService.CustomerOrderingRepositoryService;
import com.example.demo.services.lvivCroissantRepositoryService.SupportEntityRepositoryService;
import com.example.demo.services.messangerService.*;
import com.example.demo.services.peopleRegisterService.CourierRegisterService;
import com.example.demo.services.peopleRegisterService.UserRepositoryService;
import com.example.demo.services.supportService.RecognizeService;
import com.example.demo.services.supportService.TextFormatter;
import com.example.demo.services.supportService.VerifyService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.example.demo.enums.messengerEnums.Cases.*;
import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.ABORTED;
import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.U_ARE_NOT_A_COURIER;

@Service
public class MessageParserServiceImpl implements MessageParserService {
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private UserRepositoryService userRepositoryService;
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

    private static final Logger logger = Logger.getLogger(MessageParserServiceImpl.class);
    private  static Map<String,Method> methodsHashMap;

    @Override
    public void parseMessage(Messaging messaging) {
        String message = messaging.getMessage().getText().toUpperCase();

        if (messaging.getMessage().getQuickReply() != null) {
            quickReplyParserService.parseQuickReply(messaging);
        } else {


            User user = userRepositoryService.findOnebyRId(messaging.getSender().getId());


            if (user.getStatus() != null && !(message.equals(ABORT.name()))) {
                message = user.getStatus();
            }

            String userCommand = TextFormatter.toCamelCase(Cases.valueOf(message.toUpperCase()).toString());

            try {
                methodsHashMap.get(userCommand).invoke(this,messaging);
            } catch (IllegalAccessException e) {
                logger.warn(e);
            } catch (InvocationTargetException e) {
                logger.warn(e);
                e.printStackTrace();
            } catch (Exception ex) {
                logger.warn(ex);
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



