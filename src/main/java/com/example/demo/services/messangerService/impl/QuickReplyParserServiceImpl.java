package com.example.demo.services.messangerService.impl;

import com.example.demo.entities.lvivCroissants.Croissant;
import com.example.demo.entities.lvivCroissants.CustomerOrdering;
import com.example.demo.entities.SupportEntity;
import com.example.demo.entities.peopleRegister.User;
import com.example.demo.models.messanger.*;
import com.example.demo.services.eventService.messengerEventService.*;
import com.example.demo.services.repositoryService.CroissantRepositoryService;
import com.example.demo.services.repositoryService.CustomerOrderingRepositoryService;
import com.example.demo.services.repositoryService.MenuOfFillingRepositoryService;
import com.example.demo.services.repositoryService.SupportEntityRepositoryService;
import com.example.demo.services.messangerService.MessageParserService;
import com.example.demo.services.messangerService.MessageSenderService;
import com.example.demo.services.messangerService.QuickReplyParserService;
import com.example.demo.services.peopleRegisterService.UserRepositoryService;
import com.example.demo.services.supportService.RecognizeService;
import com.example.demo.services.supportService.TextFormatter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static com.example.demo.enums.messengerEnums.Cases.*;
import static com.example.demo.enums.messengerEnums.PayloadCases.QUESTION_YES;
import static com.example.demo.enums.messengerEnums.PayloadCases.UA;
import static com.example.demo.enums.messengerEnums.PaymentWay.CARD;
import static com.example.demo.enums.messengerEnums.PaymentWay.CASH;
import static com.example.demo.enums.messengerEnums.Roles.ADMIN;
import static com.example.demo.enums.messengerEnums.Roles.PERSONAL;
import static com.example.demo.enums.messengerEnums.payloads.Payloads.CREATE_OWN_CROISSANT_PAYLOAD;
import static com.example.demo.enums.messengerEnums.payloads.QuickReplyPayloads.*;
import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.*;
import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.CREATE_OWN_CROISSANT;
import static com.example.demo.enums.messengerEnums.types.ButtonType.web_url;
import static com.example.demo.enums.messengerEnums.types.ContentType.text;
import static com.example.demo.enums.messengerEnums.types.CroissantsTypes.SWEET;

@Service
public class QuickReplyParserServiceImpl implements QuickReplyParserService {
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private CroissantRepositoryService croissantRepositoryService;
    @Autowired
    private UserRepositoryService userRepositoryService;
    @Autowired
    private CustomerOrderingRepositoryService customerOrderingRepositoryService;
    @Autowired
    private MessageParserService messageParserService;
    @Autowired
    private CourierEventService courierEventService;
    @Autowired
    private RecognizeService recognizeService;
    @Autowired
    private MenuOfFillingRepositoryService menuOfFillingRepositoryService;
    @Autowired
    private CreatingOwnCroissantEventService creatingOwnCroissantEventService;
    @Autowired
    private GetMenuEventService getMenuEventService;
    @Autowired
    OrderingEventService orderingEventService;
    @Autowired
    private SupportEntityRepositoryService supportEntityRepositoryService;
    @Autowired
    private UserEventService userEventService;
    @Autowired
    private CroissantSavingEventService croissantSavingEventService;
    private static Logger logger = Logger.getLogger(QuickReplyParserServiceImpl.class);
    private static Map<String,Method> methodsMap;

    @Value("${server.url}")
    private String SERVER_URL;


    @Override
    public void parseQuickReply(Messaging messaging) {
        String payload = messaging.getMessage().getQuickReply().getPayload();
        String singlePayload = TextFormatter.ejectPaySinglePayload(payload);
        if (singlePayload.equals(CREATE_OWN_CROISSANT_PAYLOAD.name())) {
            creatingOwnCroissantEventService.CreateOwnCroissant(messaging);
        } else {
            invokeByRef(messaging,singlePayload);

        }



    }

    private void invokeByRef(Messaging messaging, String singlePayload) {
        try {
            String invoking = TextFormatter.toCamelCase(singlePayload);
            if(methodsMap.get(invoking)!=null) {
                methodsMap.get(invoking).invoke(this, messaging);
            }
            else
            {
                logger.info("Can`t find method...");
                messaging.getMessage().setText(singlePayload);
                messaging.getMessage().setQuickReply(null);
                messageParserService.parseMessage(messaging);
            }
        } catch (IllegalAccessException e) {
            logger.warn(e);
        } catch (InvocationTargetException e) {
            logger.warn(e);
            e.printStackTrace();
        }
    }

    @PostConstruct
    private void methodsMapInit(){
        Method[] methods = QuickReplyParserServiceImpl.class.getDeclaredMethods();
        methodsMap = new HashMap<String,Method>();
        for(Method method: methods){
            methodsMap.put(method.getName(),method);
        }

    }

    private void typePayload(Messaging messaging) {
        String payload = messaging.getMessage().getQuickReply().getPayload();
        String type = TextFormatter.ejectSingleVariable(payload);
        Croissant croissant = new Croissant();
        croissant.setType(type);
        croissant.setCreatorId(messaging.getSender().getId());
        croissantRepositoryService.saveAndFlush(croissant);

        SupportEntity supportEntity = null;
        logicTypePayload(messaging,supportEntity,type);

        messageSenderService.askCroissantName(messaging);
    }
    private void paymentWay(Messaging messaging){
        String payload = messaging.getMessage().getQuickReply().getPayload();
        String var = TextFormatter.ejectSingleVariable(payload);
        CustomerOrdering customerOrdering = customerOrderingRepositoryService.findTopByUser(userRepositoryService.findOnebyRId(messaging.getSender().getId()));

        if(!var.equalsIgnoreCase(CASH.name())){
            paymentWayCard(messaging,customerOrdering);

        }
        else {
            paymentWayCash(messaging,customerOrdering);
        }
    }

    private void paymentWayCard(Messaging messaging, CustomerOrdering customerOrdering) {
        customerOrdering.setPaymentWay(CARD);
        customerOrderingRepositoryService.saveAndFlush(customerOrdering);
        Button button = new Button(web_url.name(),recognizeService.recognize(PAYMENT.name(),messaging.getSender().getId()));
        button.setMesExtentions(true);
        button.setUrl(SERVER_URL+"/payment/"+messaging.getSender().getId());
        messageSenderService.sendButtons(new ArrayList<Button>(Arrays.asList(button)),recognizeService.recognize(TAP_CREATE_CHARGE.name(),messaging.getSender().getId()),messaging.getSender().getId());
    }

    private void paymentWayCash(Messaging messaging, CustomerOrdering customerOrdering) {
        customerOrdering.setPaymentWay(CASH);
        customerOrderingRepositoryService.saveAndFlush(customerOrdering);
        messageSenderService.sendSimpleMessage(recognizeService.recognize(ORDERING_WAS_DONE.name(), messaging.getSender().getId()) + "\n" + customerOrdering.getCroissants() + "\nPrice: " + customerOrdering.getPrice() + recognizeService.recognize(CURRENCY.name(), messaging.getSender().getId()), messaging.getSender().getId());
        Button button = new Button(web_url.name(), recognizeService.recognize(RATING_BUTTON.name(), messaging.getSender().getId()));
        button.setMesExtentions(true);
        button.setUrl(SERVER_URL + "/req/" + messaging.getSender().getId());
        messageSenderService.sendButtons(new ArrayList<Button>(Arrays.asList(button)), recognizeService.recognize(RATE_US.name(), messaging.getSender().getId()), messaging.getSender().getId());
        messageSenderService.sendUserActions(messaging.getSender().getId());
    }

    private void personalRequestPayload(Messaging messaging) {
        parseRoleRequest(messaging);
    }

    private void courierReqAdminSidePayload(Messaging messaging) {
        parseRoleRequest(messaging);

    }
//    private void typePayload(Messaging messaging){
//        Message message = messaging.getMessage();
//        String payload = TextFormatter.ejectSingleVariable(message.getQuickReply().getPayload());
//        Croissant croissant = new Croissant();
//        croissant.setType(payload);
//        croissant.setCreatorId(messaging.getSender().getId());
//        croissantRepositoryService.saveAndFlush(croissant);
//        messageSenderService.sendSimpleMessage(recognizeService.recognize(NAMING_CROISSANT.name(),messaging.getSender().getId()),messaging.getSender().getId());
//    }
    private void adminRequestPayload(Messaging messaging) {
        parseRoleRequest(messaging);
    }

    private void acceptOrderingPayload(Messaging messaging) {
        String context = TextFormatter.ejectContext(messaging.getMessage().getQuickReply().getPayload());
        String var = TextFormatter.ejectVariableWithContext(messaging.getMessage().getQuickReply().getPayload());
        User user = userRepositoryService.findOnebyRId(messaging.getSender().getId());
        if (var.equals(QUESTION_YES.name())) {
            acceptOrderingYes(messaging,context,user);

        } else {
            messageSenderService.sendSimpleMessage(recognizeService.recognize(DONE.name(), messaging.getSender().getId()), messaging.getSender().getId());
            messageSenderService.sendSimpleQuestion(messaging.getSender().getId(), recognizeService.recognize(ORDER_SOMETHING_YET.name(), messaging.getSender().getId()), ONE_MORE_ORDERING.name(), "?");

        }
    }

    private void acceptOrderingYes(Messaging messaging, String context, User user) {
        Croissant croissant = croissantRepositoryService.findOne(Long.parseLong(context));
        CustomerOrdering ordering = customerOrderingRepositoryService.findTopByUser(user);
        ordering.getCroissants().add(croissant.toString());
        int price = ordering.getPrice() + croissant.getPrice();
        ordering.setPrice(price);
        customerOrderingRepositoryService.saveAndFlush(ordering);
        messageSenderService.sendSimpleMessage(recognizeService.recognize(DONE.name(), messaging.getSender().getId()), messaging.getSender().getId());
        messageSenderService.sendSimpleQuestion(messaging.getSender().getId(), recognizeService.recognize(ORDER_SOMETHING_YET.name(), messaging.getSender().getId()), ONE_MORE_ORDERING.name(), "?");
    }

    private void oneMoreOrdering(Messaging messaging) {
        String var = TextFormatter.ejectSingleVariable(messaging.getMessage().getQuickReply().getPayload());
        SupportEntity supportEntity = supportEntityRepositoryService.getByUserId(messaging.getSender().getId());
        if (var.equals(QUESTION_YES.name())) {
            oneMoreOrderingYes(messaging,supportEntity);
        } else {
            oneMoreOrderingNo(messaging,supportEntity);

        }

    }

    private void oneMoreOrderingYes(Messaging messaging, SupportEntity supportEntity) {
        supportEntity.setOneMore(true);
        messaging.getMessage().setQuickReply(null);
        supportEntityRepositoryService.saveAndFlush(supportEntity);
        getMenuEventService.getMenu(messaging);
    }

    private void oneMoreOrderingNo(Messaging messaging, SupportEntity supportEntity) {
        User user = userRepositoryService.findOnebyRId(messaging.getSender().getId());
        CustomerOrdering customerOrdering = customerOrderingRepositoryService.findTopByUser(user);

        supportEntity.setOneMore(null);
        userEventService.changeStatus(messaging, null);
        supportEntityRepositoryService.saveAndFlush(supportEntity);

        List<QuickReply>quickReplies = Arrays.asList(new QuickReply(text.name(),recognizeService.recognize(CASH_BUTTON.name(),messaging.getSender().getId()),PAYMENT_WAY.name()+"?"+CASH.name())
                ,new QuickReply(text.name(),recognizeService.recognize(CARD_BUTTON.name(),messaging.getSender().getId()),PAYMENT_WAY.name()+"?"+ CARD.name()));
        messageSenderService.sendQuickReplies(quickReplies,recognizeService.recognize(PAYMENT_WAY_CHOICE.name(),messaging.getSender().getId()),messaging.getSender().getId());

    }

    private void croissantTypePayload(Messaging messaging) {
        SupportEntity supportEntity;
        if (supportEntityRepositoryService.getByUserId(messaging.getSender().getId()) == null) {
            supportEntity = new SupportEntity();
            supportEntity.setUserId(messaging.getSender().getId());
        } else
            supportEntity = supportEntityRepositoryService.getByUserId(messaging.getSender().getId());

        supportEntity.setType(TextFormatter.ejectSingleVariable(messaging.getMessage().getQuickReply().getPayload()));
        supportEntityRepositoryService.saveAndFlush(supportEntity);

        getMenuEventService.getMenu(messaging);
    }

    private void creatingOwnCroissantPayload(Messaging messaging) {
        String singleVar = TextFormatter.ejectSingleVariable(messaging.getMessage().getQuickReply().getPayload());
        String postBackPayload = "";
        if (singleVar.equals(SWEET.name())) {
            postBackPayload = "4";
        } else {
            postBackPayload = "3";
        }


        messaging.setPostback(new PostBack(postBackPayload));
        messaging.setMessage(new Message(CREATE_OWN_CROISSANT.name()));
        messaging.getMessage().setQuickReply(null);
        messageParserService.parseMessage(messaging);


    }

    private void oneMoreActionPayload(Messaging messaging) {
        String answer = TextFormatter.ejectSingleVariable(messaging.getMessage().getQuickReply().getPayload());
        if (answer.equals(QUESTION_YES.name())) {
            Messaging messaging1 = new Messaging(new Message(NAVI.name()), new Recipient(messaging.getSender().getId()));
            messaging1.setSender(new Sender(messaging.getSender().getId()));
            messageParserService.parseMessage(messaging1);
        } else {
            messageSenderService.sendSimpleMessage(recognizeService.recognize(THANKS.name(), messaging.getSender().getId()), messaging.getSender().getId());
        }
    }

    private void languagePayload(Messaging messaging) {
        String singleVariable = TextFormatter.ejectSingleVariable(messaging.getMessage().getQuickReply().getPayload());
        User user = userRepositoryService.findOnebyRId(messaging.getSender().getId());

        if (singleVariable.equals(UA.name()))
            user.setLocale(new Locale("ua", "UA"));
        else
            user.setLocale(new Locale("en", "US"));

        languagePayloadLogic(messaging,user);
    }

    private void languagePayloadLogic(Messaging messaging, User user) {
        user.setRecipientId(messaging.getSender().getId());
        userRepositoryService.saveAndFlush(user);
        messageSenderService.sendSimpleMessage(recognizeService.recognize(HELLO_MESSAGE.name(), messaging.getSender().getId()), messaging.getSender().getId());
        messageSenderService.sendSimpleMessage(recognizeService.recognize(IF_CHANGING_LANGUAGE.name(), messaging.getSender().getId()), messaging.getSender().getId());
        messageSenderService.sendUserActions(messaging.getSender().getId());
    }

    private boolean isAdminReq(String payload) {
        return payload.equals(ADMIN_REQUEST_PAYLOAD.name());
    }

    private void parseRoleRequest(Messaging messaging) {
        String fullPayload = messaging.getMessage().getQuickReply().getPayload();
        String singlePayload = TextFormatter.ejectPaySinglePayload(fullPayload);
        String context = TextFormatter.ejectContext(fullPayload);
        String singleVariable = TextFormatter.ejectVariableWithContext(fullPayload);
        User user = userRepositoryService.findOnebyRId(Long.parseLong(context));
        List<User> admins = userRepositoryService.getByRole(ADMIN);

        if (singleVariable.equals(QUESTION_YES.name())) {
            roleRequestQuestionYes(messaging, user, singlePayload, admins);
        } else {
            roleReqNo(messaging,user);
        }
    }

    private void roleReqNo(Messaging messaging, User user) {
        user.setRole(ADMIN);
        userRepositoryService.saveAndFlush(user);
        messageSenderService.sendSimpleMessage(recognizeService.recognize(ADMIN_ADDED.name(), messaging.getSender().getId()), user.getRecipientId());
    }

    private void roleRequestQuestionYes(Messaging messaging, User user, String singlePayload, List<User> admins) {
        if (isAdminReq(singlePayload)) {
            adminQuestionYes(messaging,user);
        } else if (singlePayload.equalsIgnoreCase(PERSONAL_REQUEST_PAYLOAD.name())) {
            personalRequestYes(messaging,user);
        } else {
            courierQuestionYes(messaging,user);
        }


        for (User admin : admins) {
            messageSenderService.sendSimpleMessage(recognizeService.recognize(ACCEPTED_BY_ONE_OF_ADMINS.name(), messaging.getSender().getId()) + userRepositoryService.findOnebyRId(messaging.getSender().getId()).getName(), admin.getRecipientId());
        }
    }

    private void adminQuestionYes(Messaging messaging, User user) {
        user.setRole(ADMIN);
        userRepositoryService.saveAndFlush(user);
        messageSenderService.sendSimpleMessage(recognizeService.recognize(ADMIN_ADDED.name(), messaging.getSender().getId()), user.getRecipientId());
    }

    private void personalRequestYes(Messaging messaging, User user) {
        user.setRole(PERSONAL);
        userRepositoryService.saveAndFlush(user);
        messageSenderService.sendSimpleMessage(recognizeService.recognize(PERSONAL_ADDED.name(), messaging.getSender().getId()), user.getRecipientId());
    }

    private void courierQuestionYes(Messaging messaging, User user) {
        Messaging messaging1 = new Messaging(new Message(), new Recipient());
        messaging1.setSender(new Sender(user.getRecipientId()));
        userEventService.changeStatus(messaging1, COURIER_REGISTRATION_FINAL.name());
        messageSenderService.sendSimpleQuestion(user.getRecipientId(), recognizeService.recognize(ACCEPT_THIS_DEADLY_AS_COURIER.name(), messaging.getSender().getId()), COURIER_QUESTION_PAYLOAD.name(), "?");

    }




    private void logicTypePayload(Messaging messaging, SupportEntity supportEntity, String type) {
        if (supportEntityRepositoryService.getByUserId(messaging.getSender().getId()) != null)
            supportEntity = supportEntityRepositoryService.getByUserId(messaging.getSender().getId());
        else {
            supportEntity = new SupportEntity();
            supportEntity.setUserId(messaging.getSender().getId());
        }
        if (type.equals(SWEET.name()))
            supportEntity.setType("4");
        else
            supportEntity.setType("3");
        supportEntityRepositoryService.saveAndFlush(supportEntity);
    }


    private void oneMoreActionCourierPayload(Messaging messaging) {
        String simpleVariable = TextFormatter.ejectSingleVariable(messaging.getMessage().getQuickReply().getPayload());
        if (simpleVariable.equals(QUESTION_YES.name())) {
            messaging.getMessage().setText(COURIER_ACTIONS.name());
            messaging.getMessage().setQuickReply(null);
            messageParserService.parseMessage(messaging);
        } else {
            messageSenderService.sendSimpleMessage(recognizeService.recognize(THANKS.name(), messaging.getSender().getId()), messaging.getSender().getId());
        }
    }


    private void courierQuestionPayload(Messaging messaging) {
        if (TextFormatter.ejectSingleVariable(messaging.getMessage().getQuickReply().getPayload()).equals(QUESTION_YES.name())) {
            messaging.getMessage().setQuickReply(null);
            messageParserService.parseMessage(messaging);

        } else {
            messageSenderService.sendSimpleMessage(recognizeService.recognize(GOOD_BYE.name(), messaging.getSender().getId()), messaging.getSender().getId());
            userEventService.changeStatus(messaging, null);
        }

    }

    private void addressPayload(Messaging messaging) {
        User user = userRepositoryService.findOnebyRId(messaging.getSender().getId());

        String singleVariable = TextFormatter.ejectSingleVariable(messaging.getMessage().getQuickReply().getPayload());
        if (singleVariable.equals(QUESTION_YES.name())) {
            orderingEventService.parseOrdering(messaging);
        } else {
            CustomerOrdering ordering = customerOrderingRepositoryService.findTopByUser(user);
            ordering.setAddress(null);
            customerOrderingRepositoryService.saveAndFlush(ordering);
            orderingEventService.parseOrdering(messaging);
        }
    }


}
