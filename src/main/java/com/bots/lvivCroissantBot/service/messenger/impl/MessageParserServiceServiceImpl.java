package com.bots.lvivCroissantBot.service.messenger.impl;

import com.bots.lvivCroissantBot.entity.Support;
import com.bots.lvivCroissantBot.entity.register.MUser;
import com.bots.lvivCroissantBot.constantEnum.messengerEnum.Cases;
import com.bots.lvivCroissantBot.dto.messanger.Messaging;
import com.bots.lvivCroissantBot.repository.CustomerOrderingRepository;
import com.bots.lvivCroissantBot.repository.SupportEntityRepository;
import com.bots.lvivCroissantBot.service.messenger.event.*;
import com.bots.lvivCroissantBot.service.messenger.*;
import com.bots.lvivCroissantBot.service.peopleRegister.CourierRegisterService;
import com.bots.lvivCroissantBot.service.peopleRegister.MUserRepositoryService;
import com.bots.lvivCroissantBot.service.support.RecognizeService;
import com.bots.lvivCroissantBot.service.support.TextFormatter;
import com.bots.lvivCroissantBot.service.support.VerifyService;
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
public class MessageParserServiceServiceImpl implements MessageParserService {
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private MUserRepositoryService MUserRepositoryService;
    @Autowired
    private CourierRegisterService courierRegisterService;
    @Autowired
    private OrderingService orderingService;
    @Autowired
    private MenuOfFillingService menuOfFillingService;
    @Autowired
    private CroissantSavingService croissantSavingService;
    @Autowired
    private CourierService courierService;
    @Autowired
    private RecognizeService recognizeService;
    @Autowired
    private CreatingOwnCroissantService creatingOwnCroissantService;
    @Autowired
    private GetMenuService getMenuService;
    @Autowired
    private UserService userService;
    @Autowired
    private QuickReplyParserService quickReplyParserService;
    @Autowired
    private VerifyService verifyService;
    @Autowired
    private BroadcastService broadcastService;
    @Autowired
    private CustomerOrderingRepository customerOrderingRepositoryService;
    @Autowired
    private MessageProcessorHelperService messageProcessorHelperService;
    @Autowired
    private SupportEntityRepository supportEntityRepositoryService;

    private   final static Logger logger = LoggerFactory.getLogger(MessageParserServiceServiceImpl.class);

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
        Method[] methods = MessageParserServiceServiceImpl.class.getDeclaredMethods();
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
        userService.changeStatus(messaging,null);
    }

    private void askTime(Messaging messaging) {
        orderingService.parseOrdering(messaging);
    }

    private void askPhoneNumber(Messaging messaging) {
        orderingService.parseOrdering(messaging);
    }

    private void askAddress(Messaging messaging) {
        orderingService.parseOrdering(messaging);
    }

    private void languageChange(Messaging messaging) {
        messageSenderService.askSelectLanguage(messaging.getSender().getId());
    }

    private void customerRegisterFinalize(Messaging messaging) {
        userService.changeStatus(messaging, CUSTOMER_REGISTER_FINALIZE.name());
        userService.customerRegistration(messaging);
    }

    private void personalRequest(Messaging messaging) {
        parseRoleRequest(messaging);
    }

    private void adminRequest(Messaging messaging) {
        parseRoleRequest(messaging);

    }

    private void courierRegistrationFinal(Messaging messaging) {
        userService.changeStatus(messaging, COURIER_REGISTRATION_FINAL.name());
        courierService.parseCourier(messaging);
    }

    private void courierRequest(Messaging messaging) {
        parseRoleRequest(messaging);
    }

    private void ordering(Messaging messaging) {
        userService.changeStatus(messaging, ORDERING.name());
        orderingService.parseOrdering(messaging);
    }

    private void fillingMenu(Messaging messaging) {
        menuOfFillingService.getMenuOfFilling(messaging.getSender().getId());

    }

    private void addFilling(Messaging messaging) {
        userService.changeStatus(messaging, ADD_FILLING.name());
        menuOfFillingService.saveNewFilling(messaging);
    }

    private void add(Messaging messaging) {
        userService.changeStatus(messaging, ADD.name());
        croissantSavingService.saveCroissant(messaging);
    }


    private void menu(Messaging messaging) {
        getMenuService.getMenu(messaging);

    }

    private void abort(Messaging messaging) {
        userService.changeStatus(messaging, null);
        if(supportEntityRepositoryService.findByUserId(messaging.getSender().getId())!=null) {
            Support support = supportEntityRepositoryService.findByUserId(messaging.getSender().getId());
            support.setOneMore(null);
            supportEntityRepositoryService.saveAndFlush(support);
        }
        messageSenderService.sendSimpleMessage(recognizeService.recognize(ABORTED.name(), messaging.getSender().getId()), messaging.getSender().getId());
    }


    private void completeCroissantSecondStep(Messaging messaging) {
        messageProcessorHelperService.helpCompleteCroissantSecondStep(messaging);
        creatingOwnCroissantService.CreateOwnCroissant(messaging);
    }

    private void createOwnCroissant(Messaging messaging) {
        creatingOwnCroissantService.CreateOwnCroissant(messaging);

    }

    private void completeOrderingsList(Messaging messaging) {
        messageProcessorHelperService.helpCompleteOrderingList(messaging);
        courierService.parseCourier(messaging);
    }

    private void completingOrderings(Messaging messaging) {
        messageProcessorHelperService.helpCompletingOrderings(messaging);
        courierService.parseCourier(messaging);
    }



    private void orderingListFilling(Messaging messaging) {
        messageProcessorHelperService.helpOrderingListFilling(messaging);
        courierService.parseCourier(messaging);
    }

    private void getListOfOrdering(Messaging messaging) {
        messageProcessorHelperService.helpGetListOfOrdering(messaging);
        courierService.parseCourier(messaging);
    }

    private void courierRegistration(Messaging messaging) {
        messageProcessorHelperService.helpCourierRegistration(messaging);
        courierService.parseCourier(messaging);
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



