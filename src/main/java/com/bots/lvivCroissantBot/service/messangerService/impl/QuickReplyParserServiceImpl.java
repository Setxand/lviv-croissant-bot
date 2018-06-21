package com.bots.lvivCroissantBot.service.messangerService.impl;

import com.bots.lvivCroissantBot.dto.telegram.Chat;
import com.bots.lvivCroissantBot.entity.lvivCroissants.CroissantEntity;
import com.bots.lvivCroissantBot.entity.lvivCroissants.CustomerOrdering;
import com.bots.lvivCroissantBot.entity.SupportEntity;
import com.bots.lvivCroissantBot.entity.register.MUser;
import com.bots.lvivCroissantBot.dto.messanger.*;
import com.bots.lvivCroissantBot.entity.register.TUser;
import com.bots.lvivCroissantBot.entity.register.User;
import com.bots.lvivCroissantBot.repository.MUserRepository;
import com.bots.lvivCroissantBot.repository.UserRepository;
import com.bots.lvivCroissantBot.service.eventService.messengerEventService.*;
import com.bots.lvivCroissantBot.service.peopleRegisterService.TelegramUserRepositoryService;
import com.bots.lvivCroissantBot.service.repositoryService.CroissantRepositoryService;
import com.bots.lvivCroissantBot.service.repositoryService.CustomerOrderingRepositoryService;
import com.bots.lvivCroissantBot.service.repositoryService.MenuOfFillingRepositoryService;
import com.bots.lvivCroissantBot.service.repositoryService.SupportEntityRepositoryService;
import com.bots.lvivCroissantBot.service.messangerService.MessageParserService;
import com.bots.lvivCroissantBot.service.messangerService.MessageSenderService;
import com.bots.lvivCroissantBot.service.messangerService.QuickReplyParserService;
import com.bots.lvivCroissantBot.service.peopleRegisterService.MUserRepositoryService;
import com.bots.lvivCroissantBot.service.supportService.RecognizeService;
import com.bots.lvivCroissantBot.service.supportService.TextFormatter;
import com.bots.lvivCroissantBot.service.telegramService.TelegramMessageSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.Cases.*;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.PayloadCases.QUESTION_YES;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.PayloadCases.UA;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.PaymentWay.CARD;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.PaymentWay.CASH;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.Role.ADMIN;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.Role.PERSONAL;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.payload.Payloads.CREATE_OWN_CROISSANT_PAYLOAD;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.payload.QuickReplyPayloads.*;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.speaking.ServerSideSpeaker.*;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.speaking.ServerSideSpeaker.CREATE_OWN_CROISSANT;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.type.ButtonType.web_url;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.type.ContentType.text;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.type.CroissantsTypes.SWEET;
import static com.bots.lvivCroissantBot.constantEnum.telegramEnum.TelegramUserStatus.PHONE_ENTERING_IN_START_STATUS;

@Service
public class QuickReplyParserServiceImpl implements QuickReplyParserService {
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private CroissantRepositoryService croissantRepositoryService;
    @Autowired
    private MUserRepositoryService MUserRepositoryService;
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
    @Autowired
    private TelegramUserRepositoryService telegramUserRepositoryService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MUserRepository mUserRepository;
    @Autowired
    private TelegramMessageSenderService telegramMessageSenderService;
    private   final static Logger logger = LoggerFactory.getLogger(QuickReplyParserServiceImpl.class);

    private static Map<String, Method> methodsMap;

    @Value("${server.url}")
    private String SERVER_URL;


    @Override
    public void parseQuickReply(Messaging messaging) {
        String payload = messaging.getMessage().getQuickReply().getPayload();
        String singlePayload = TextFormatter.ejectPaySinglePayload(payload);
        if (singlePayload.equals(CREATE_OWN_CROISSANT_PAYLOAD.name())) {
            creatingOwnCroissantEventService.CreateOwnCroissant(messaging);
        } else {
            invokeByRef(messaging, singlePayload);

        }


    }

    private void invokeByRef(Messaging messaging, String singlePayload) {
        try {
            String invoking = TextFormatter.toCamelCase(singlePayload);
            if (methodsMap.get(invoking) != null) {
                methodsMap.get(invoking).invoke(this, messaging);
            } else {
                logger.info("Can`t find method...");
                messaging.getMessage().setText(singlePayload);
                messaging.getMessage().setQuickReply(null);
                messageParserService.parseMessage(messaging);
            }
        } catch (IllegalAccessException e) {
            logger.error("Error",e);
        } catch (InvocationTargetException e) {
            logger.error("Error",e);
            e.printStackTrace();
        }
    }

    @PostConstruct
    private void methodsMapInit() {
        Method[] methods = QuickReplyParserServiceImpl.class.getDeclaredMethods();
        methodsMap = new HashMap<String, Method>();
        for (Method method : methods) {
            methodsMap.put(method.getName(), method);
        }

    }

    private void questionApproving(Messaging messaging) {
        String context = TextFormatter.ejectContext(messaging.getMessage().getQuickReply().getPayload());
        String answer = TextFormatter.ejectVariableWithContext(messaging.getMessage().getQuickReply().getPayload());
        if (answer.equals(QUESTION_YES.name())) {
            synchWithTelegram(messaging, context, answer);
        } else {
            notApproved(context);
        }
    }

    private void notApproved(String context) {
        com.bots.lvivCroissantBot.dto.telegram.Message message = new com.bots.lvivCroissantBot.dto.telegram.Message();
        message.setChat(new Chat(Integer.parseInt(context)));
        telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(NOT_APPROVED.name()), message);
        telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(NUMBER_OF_PHONE.name()),message);
        telegramUserRepositoryService.changeStatus(telegramUserRepositoryService.findByChatId(message.getChat().getId()),PHONE_ENTERING_IN_START_STATUS);
    }

    private void synchWithTelegram(Messaging messaging, String context, String answer) {
        TUser tUser = telegramUserRepositoryService.findByChatId(Integer.parseInt(context));
        User user = mUserRepository.findByRecipientId(messaging.getSender().getId()).getUser();
        user.setTUser(tUser);
        tUser.setUser(user);
        userRepository.saveAndFlush(user);
        messageSenderService.sendSimpleMessage(ResourceBundle.getBundle("dictionary").getString(SYNCH.name()), messaging.getSender().getId());
        com.bots.lvivCroissantBot.dto.telegram.Message message = new com.bots.lvivCroissantBot.dto.telegram.Message();
        message.setChat(new Chat(tUser.getChatId()));
        telegramMessageSenderService.simpleMessage(ResourceBundle.getBundle("dictionary").getString(SYNCH.name()), message);
        telegramMessageSenderService.sendActions(message);
    }


    private void typePayload(Messaging messaging) {
        String payload = messaging.getMessage().getQuickReply().getPayload();
        String type = TextFormatter.ejectSingleVariable(payload);
        CroissantEntity croissantEntity = new CroissantEntity();
        croissantEntity.setType(type);
        croissantEntity.setCreatorId(messaging.getSender().getId());
        croissantRepositoryService.saveAndFlush(croissantEntity);

        SupportEntity supportEntity = null;
        logicTypePayload(messaging, supportEntity, type);

        messageSenderService.askCroissantName(messaging);
    }

    private void paymentWay(Messaging messaging) {
        String payload = messaging.getMessage().getQuickReply().getPayload();
        String var = TextFormatter.ejectSingleVariable(payload);
        CustomerOrdering customerOrdering = customerOrderingRepositoryService.findTopByUser(MUserRepositoryService.findOnebyRId(messaging.getSender().getId()));

        if (!var.equalsIgnoreCase(CASH.name())) {
            paymentWayCard(messaging, customerOrdering);

        } else {
            paymentWayCash(messaging, customerOrdering);
        }
    }

    private void paymentWayCard(Messaging messaging, CustomerOrdering customerOrdering) {
        customerOrdering.setPaymentWay(CARD);
        customerOrderingRepositoryService.saveAndFlush(customerOrdering);
        Button button = new Button(web_url.name(), recognizeService.recognize(PAYMENT.name(), messaging.getSender().getId()));
        button.setMesExtentions(true);
        button.setUrl(SERVER_URL + "/payment/" + messaging.getSender().getId());
        messageSenderService.sendButtons(new ArrayList<Button>(Arrays.asList(button)), recognizeService.recognize(TAP_CREATE_CHARGE.name(), messaging.getSender().getId()), messaging.getSender().getId());
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

    private void adminRequestPayload(Messaging messaging) {
        parseRoleRequest(messaging);
    }

    private void acceptOrderingPayload(Messaging messaging) {
        String context = TextFormatter.ejectContext(messaging.getMessage().getQuickReply().getPayload());
        String var = TextFormatter.ejectVariableWithContext(messaging.getMessage().getQuickReply().getPayload());
        MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());
        if (var.equals(QUESTION_YES.name())) {
            acceptOrderingYes(messaging, context, MUser);

        } else {
            messageSenderService.sendSimpleMessage(recognizeService.recognize(DONE.name(), messaging.getSender().getId()), messaging.getSender().getId());
            messageSenderService.sendSimpleQuestion(messaging.getSender().getId(), recognizeService.recognize(ORDER_SOMETHING_YET.name(), messaging.getSender().getId()), ONE_MORE_ORDERING.name(), "?");

        }
    }

    private void acceptOrderingYes(Messaging messaging, String context, MUser MUser) {
        CroissantEntity croissantEntity = croissantRepositoryService.findOne(Long.parseLong(context));
        CustomerOrdering ordering = customerOrderingRepositoryService.findTopByUser(MUser);
        ordering.getCroissants().add(croissantEntity.toString());
        int price = ordering.getPrice() + croissantEntity.getPrice();
        ordering.setPrice(price);
        customerOrderingRepositoryService.saveAndFlush(ordering);
        messageSenderService.sendSimpleMessage(recognizeService.recognize(DONE.name(), messaging.getSender().getId()), messaging.getSender().getId());
        messageSenderService.sendSimpleQuestion(messaging.getSender().getId(), recognizeService.recognize(ORDER_SOMETHING_YET.name(), messaging.getSender().getId()), ONE_MORE_ORDERING.name(), "?");
    }

    private void oneMoreOrdering(Messaging messaging) {
        String var = TextFormatter.ejectSingleVariable(messaging.getMessage().getQuickReply().getPayload());
        SupportEntity supportEntity = supportEntityRepositoryService.getByUserId(messaging.getSender().getId());
        if (var.equals(QUESTION_YES.name())) {
            oneMoreOrderingYes(messaging, supportEntity);
        } else {
            oneMoreOrderingNo(messaging, supportEntity);

        }

    }

    private void oneMoreOrderingYes(Messaging messaging, SupportEntity supportEntity) {
        supportEntity.setOneMore(true);
        messaging.getMessage().setQuickReply(null);
        supportEntityRepositoryService.saveAndFlush(supportEntity);
        getMenuEventService.getMenu(messaging);
    }

    private void oneMoreOrderingNo(Messaging messaging, SupportEntity supportEntity) {
        MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());
        CustomerOrdering customerOrdering = customerOrderingRepositoryService.findTopByUser(MUser);

        supportEntity.setOneMore(null);
        userEventService.changeStatus(messaging, null);
        supportEntityRepositoryService.saveAndFlush(supportEntity);

        List<QuickReply> quickReplies = Arrays.asList(new QuickReply(text.name(), recognizeService.recognize(CASH_BUTTON.name(), messaging.getSender().getId()), PAYMENT_WAY.name() + "?" + CASH.name())
                , new QuickReply(text.name(), recognizeService.recognize(CARD_BUTTON.name(), messaging.getSender().getId()), PAYMENT_WAY.name() + "?" + CARD.name()));
        messageSenderService.sendQuickReplies(quickReplies, recognizeService.recognize(PAYMENT_WAY_CHOICE.name(), messaging.getSender().getId()), messaging.getSender().getId());

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
        MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());

        if (singleVariable.equals(UA.name()))
            MUser.setLocale(new Locale("ua", "UA"));
        else
            MUser.setLocale(new Locale("en", "US"));

        languagePayloadLogic(messaging, MUser);
    }

    private void languagePayloadLogic(Messaging messaging, MUser MUser) {
        MUser.setRecipientId(messaging.getSender().getId());
        MUserRepositoryService.saveAndFlush(MUser);
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
        MUser MUser = MUserRepositoryService.findOnebyRId(Long.parseLong(context));
        List<MUser> admins = userRepository.findAllByRole(ADMIN).stream().map(User::getMUser).collect(Collectors.toList());

        if (singleVariable.equals(QUESTION_YES.name())) {
            roleRequestQuestionYes(messaging, MUser, singlePayload, admins);
        } else {
            roleReqNo(messaging, MUser);
        }
    }

    private void roleReqNo(Messaging messaging, MUser MUser) {
        MUser.getUser().setRole(ADMIN);
        MUserRepositoryService.saveAndFlush(MUser);
        messageSenderService.sendSimpleMessage(recognizeService.recognize(ADMIN_ADDED.name(), messaging.getSender().getId()), MUser.getRecipientId());
    }

    private void roleRequestQuestionYes(Messaging messaging, MUser MUser, String singlePayload, List<MUser> admins) {
        if (isAdminReq(singlePayload)) {
            adminQuestionYes(messaging, MUser);
        } else if (singlePayload.equalsIgnoreCase(PERSONAL_REQUEST_PAYLOAD.name())) {
            personalRequestYes(messaging, MUser);
        } else {
            courierQuestionYes(messaging, MUser);
        }


        for (MUser admin : admins) {
            messageSenderService.sendSimpleMessage(recognizeService.recognize(ACCEPTED_BY_ONE_OF_ADMINS.name(), messaging.getSender().getId()) + MUserRepositoryService.findOnebyRId(messaging.getSender().getId()).getName(), admin.getRecipientId());
        }
    }

    private void adminQuestionYes(Messaging messaging, MUser MUser) {
        MUser.getUser().setRole(ADMIN);
        MUserRepositoryService.saveAndFlush(MUser);
        messageSenderService.sendSimpleMessage(recognizeService.recognize(ADMIN_ADDED.name(), messaging.getSender().getId()), MUser.getRecipientId());
    }

    private void personalRequestYes(Messaging messaging, MUser MUser) {
        MUser.getUser().setRole(PERSONAL);
        MUserRepositoryService.saveAndFlush(MUser);
        messageSenderService.sendSimpleMessage(recognizeService.recognize(PERSONAL_ADDED.name(), messaging.getSender().getId()), MUser.getRecipientId());
    }

    private void courierQuestionYes(Messaging messaging, MUser MUser) {
        Messaging messaging1 = new Messaging(new Message(), new Recipient());
        messaging1.setSender(new Sender(MUser.getRecipientId()));
        userEventService.changeStatus(messaging1, COURIER_REGISTRATION_FINAL.name());
        messageSenderService.sendSimpleQuestion(MUser.getRecipientId(), recognizeService.recognize(ACCEPT_THIS_DEADLY_AS_COURIER.name(), messaging.getSender().getId()), COURIER_QUESTION_PAYLOAD.name(), "?");

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
        MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());

        String singleVariable = TextFormatter.ejectSingleVariable(messaging.getMessage().getQuickReply().getPayload());
        if (singleVariable.equals(QUESTION_YES.name())) {
            orderingEventService.parseOrdering(messaging);
        } else {
            CustomerOrdering ordering = customerOrderingRepositoryService.findTopByUser(MUser);
            ordering.setAddress(null);
            customerOrderingRepositoryService.saveAndFlush(ordering);
            orderingEventService.parseOrdering(messaging);
        }
    }


}
