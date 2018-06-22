package com.bots.lviv_croissant_bot.service.messenger.event.impl;

import com.bots.lviv_croissant_bot.entity.lvivCroissants.CustomerOrdering;
import com.bots.lviv_croissant_bot.entity.register.MUser;
import com.bots.lviv_croissant_bot.dto.messanger.*;
import com.bots.lviv_croissant_bot.exception.ElementNoFoundException;
import com.bots.lviv_croissant_bot.repository.CustomerOrderingRepository;
import com.bots.lviv_croissant_bot.service.messenger.event.UserService;
import com.bots.lviv_croissant_bot.service.messenger.MessageSenderService;
import com.bots.lviv_croissant_bot.service.peopleRegister.CourierRegisterService;
import com.bots.lviv_croissant_bot.service.peopleRegister.MUserRepositoryService;
import com.bots.lviv_croissant_bot.service.support.RecognizeService;
import com.bots.lviv_croissant_bot.service.support.TextFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.bots.lviv_croissant_bot.constantEnum.messengerEnum.Cases.COMPLETE_ORDERINGS_LIST;
import static com.bots.lviv_croissant_bot.constantEnum.messengerEnum.Cases.ORDERING_LIST_FILLING;
import static com.bots.lviv_croissant_bot.constantEnum.messengerEnum.CasesCourierActions.*;
import static com.bots.lviv_croissant_bot.constantEnum.messengerEnum.payload.Payloads.COMPLETE_ORDER;
import static com.bots.lviv_croissant_bot.constantEnum.messengerEnum.payload.Payloads.GET_ORDER;
import static com.bots.lviv_croissant_bot.constantEnum.messengerEnum.payload.Payloads.SHOW_MORE_PAYLOAD;
import static com.bots.lviv_croissant_bot.constantEnum.messengerEnum.payload.QuickReplyPayloads.COURIER_QUESTION_PAYLOAD;
import static com.bots.lviv_croissant_bot.constantEnum.messengerEnum.payload.QuickReplyPayloads.ONE_MORE_ACTION_COURIER_PAYLOAD;
import static com.bots.lviv_croissant_bot.constantEnum.messengerEnum.speaking.ServerSideSpeaker.*;
import static com.bots.lviv_croissant_bot.constantEnum.messengerEnum.type.AttachmentType.template;
import static com.bots.lviv_croissant_bot.constantEnum.messengerEnum.type.ButtonType.postback;
import static com.bots.lviv_croissant_bot.constantEnum.messengerEnum.type.ButtonType.web_url;
import static com.bots.lviv_croissant_bot.constantEnum.messengerEnum.type.TemplateType.generic;

@Service
public class CourierService implements com.bots.lviv_croissant_bot.service.messenger.event.CourierService {

    @Autowired
    private CourierRegisterService courierRegisterService;
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private CustomerOrderingRepository customerOrderingRepositoryService;
    @Autowired
    private RecognizeService recognizeService;
    @Autowired
    private MUserRepositoryService MUserRepositoryService;
    @Autowired
    private UserService userService;
    private   final static Logger logger = LoggerFactory.getLogger(com.bots.lviv_croissant_bot.service.messenger.event.impl.CourierService.class);

    @Value("${server.url}")
    private String SERVER_URL;
    @Override
    public void parseCourier(Messaging messaging) {
        try {


            if (messaging.getMessage().getQuickReply() != null) {
                QuickReply quickReply = messaging.getMessage().getQuickReply();
                String payload = quickReply.getPayload().toUpperCase();

                if (payload.equals(GET_LIST_OF_ORDERING.name()) ) {
                    getOrderingList(messaging);
                } else if (payload.equals(COMPLETING_ORDERINGS.name()) ) {
                    completeOrderings(messaging);

                } else if (TextFormatter.ejectPaySinglePayload(quickReply.getPayload()).equals(COURIER_QUESTION_PAYLOAD.name())) {
                    courierRegisterCreator(messaging);
                }


            }
            else if(messaging.getPostback()!=null){
                String postBackPayload = messaging.getPostback().getPayload();
                String payload = TextFormatter.ejectContext(postBackPayload);
                if(payload.equals(GET_LIST_OF_ORDERING.name()) || payload.equals(ORDERING_LIST_FILLING.name())){
                    messaging.getMessage().setQuickReply(new QuickReply());
                    messaging.getMessage().getQuickReply().setPayload(" ");
                    getOrderingList(messaging);
                }
            }
            else {
                com.bots.lviv_croissant_bot.entity.register.Courier courier = courierRegisterService.findByRecipientId(messaging.getSender().getId());
                if (courier.getName() == null) {
                    parseName(messaging, courier);
                } else if (courier.getPhoneNumber() == null) {
                    parsePhoneNumber(messaging, courier);
                }


            }
        } catch (Exception ex) {
            logger.error("Error",ex);
            MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());
            MUser.setStatus(null);
            MUserRepositoryService.saveAndFlush(MUser);
            messageSenderService.errorMessage(messaging.getSender().getId());
            if (courierRegisterService.findTop().getPhoneNumber() == null)
                courierRegisterService.remove(courierRegisterService.findTop());
        }
    }



    private void completeOrderings(Messaging messaging) {
        com.bots.lviv_croissant_bot.entity.register.Courier courier = courierRegisterService.findByRecipientId(messaging.getSender().getId());

        if (!courier.getCustomerOrderings().isEmpty()) {

            if (!messaging.getMessage().getQuickReply().getPayload().equals(COMPLETE_ORDERINGS_LIST.name())) {
                getOwnOrderingList(messaging);
            }
        } else {
            messageSenderService.sendSimpleMessage(recognizeService.recognize(EMPTY_LIST.name(),messaging.getSender().getId()), messaging.getSender().getId());
            userService.changeStatus(messaging,null);

            askOneMoreAction(messaging.getSender().getId());
        }

    }
    @Override
    public void completeOrderingsFinalize(Messaging messaging,Long orderId) {
        try {
            com.bots.lviv_croissant_bot.entity.register.Courier courier = courierRegisterService.findByRecipientId(messaging.getSender().getId());


            CustomerOrdering customerOrdering = customerOrderingRepositoryService.findById(orderId).orElseThrow(ElementNoFoundException::new);
            courier.getCustomerOrderings().remove(customerOrdering);
            customerOrdering.setCourier(null);
            courierRegisterService.saveAndFlush(courier);
            MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());
            MUser.getCustomerOrderings().remove(customerOrdering);
            MUserRepositoryService.saveAndFlush(MUser);
            customerOrderingRepositoryService.delete(customerOrdering);


            messageSenderService.sendSimpleMessage(recognizeService.recognize(DONE.name(), messaging.getSender().getId()), messaging.getSender().getId());

            askOneMoreAction(messaging.getSender().getId());
        } catch (Exception ex) {
            logger.error("Error",ex);
        }
    }


    private void getOwnOrderingList(Messaging messaging) {
        com.bots.lviv_croissant_bot.entity.register.Courier courier = courierRegisterService.findByRecipientId(messaging.getSender().getId());
        if (!courier.getCustomerOrderings().isEmpty()) {
            messageSenderService.sendMessage(makeGeneric(messaging, courier.getCustomerOrderings()));

        } else {
            messageSenderService.sendSimpleMessage(recognizeService.recognize(EMPTY_LIST.name(),messaging.getSender().getId()), messaging.getSender().getId());

        }
        userService.changeStatus(messaging,null);
    }


    @Override
    public void getOrderingList(Messaging messaging) {


        if (!customerOrderingRepositoryService.findAll().isEmpty()) {
            getOrderingListFirstStepIsNotEmpty(messaging);

        } else {
            userService.changeStatus(messaging,null);
            messageSenderService.sendSimpleMessage(recognizeService.recognize(EMPTY_LIST.name(),messaging.getSender().getId()), messaging.getSender().getId());
            askOneMoreAction(messaging.getSender().getId());
        }

    }


    private void getOrderingListFirstStepIsNotEmpty(Messaging messaging) {
        List<CustomerOrdering> customerOrderings = customerOrderingRepositoryService.findAll();
        List<CustomerOrdering> customerOrderings1 = new ArrayList<>();
        for (CustomerOrdering customerOrdering : customerOrderings) {
            if (customerOrdering.getCourier() == null) {
                customerOrderings1.add(customerOrdering);
            }
        }

        if (customerOrderings1.isEmpty()) {
            getOrderingListFirstStepIsEmpty(messaging);

        } else if (!messaging.getMessage().getQuickReply().getPayload().equals(ORDERING_LIST_FILLING.name())) {
            messageSenderService.sendMessage(makeGeneric(messaging,customerOrderings1));
            userService.changeStatus(messaging,null);
        }
    }

    private Messaging makeGeneric(Messaging messaging, List<CustomerOrdering> customerOrderings1) {
        Attachment attachment = new Attachment(template.name(),new Payload());
        List<Element>elements = new ArrayList<>();
        attachment.getPayload().setElements(elements);
        attachment.getPayload().setTemplateType(generic.name());
        List<CustomerOrdering> subList ;
        int index =0;
        if(messaging.getPostback()!=null){
            index = Integer.parseInt(TextFormatter.ejectVariableWithContext(messaging.getPostback().getPayload()));
        }
        try {
            subList = customerOrderings1.subList(index,index+10);
            index+=9;
        }
        catch (Exception ex){
            subList = customerOrderings1.subList(index,customerOrderings1.size());
            index+=(customerOrderings1.size()-index-1);
        }

        for(CustomerOrdering customerOrdering: subList){
            Element element = new Element();
            if(customerOrdering == subList.get(subList.size()-1) && customerOrdering!=customerOrderings1.get(customerOrderings1.size()-1)){
                Button button = new Button(postback.name(),"Show more",SHOW_MORE_PAYLOAD.name()+"?"+messaging.getMessage().getQuickReply().getPayload()+"&"+index);
                element.getButtons().add(button);
                element.setTitle("End of the list");
                elements.add(element);

                break;
            }
            element.setTitle("Замовлення №"+customerOrdering.getId()+", Ім'я:"+customerOrdering.getName());
            element.setSubtitle(" Адреса:"+customerOrdering.getAddress()+"\n номeр: "+customerOrdering.getPhoneNumber());
            Button openViewButton = new Button(web_url.name(),recognizeService.recognize(OPEN_ORDER.name(),messaging.getSender().getId()));
            openViewButton.setMesExtentions(true);
            openViewButton.setUrl(SERVER_URL+"/showMore/"+customerOrdering.getId());
            openViewButton.setHeightRatio("tall");
            element.getButtons().add(openViewButton);
            Button courierButton ;
            if(messaging.getMessage().getQuickReply().getPayload().equals(GET_LIST_OF_ORDERING.name()))
            courierButton = new Button(postback.name(),recognizeService.recognize(GETTING_ORDER.name(),messaging.getSender().getId()), GET_ORDER.name()+"?"+customerOrdering.getId());
            else
            courierButton = new Button(postback.name(),recognizeService.recognize(COMPLETING_ORDER.name(),messaging.getSender().getId()), COMPLETE_ORDER.name()+"?"+customerOrdering.getId());

            element.getButtons().add(courierButton);
            elements.add(element);

        }
            Messaging newMes = new Messaging(new Message(),new Recipient(messaging.getSender().getId()));
        newMes.getMessage().setAttachment(attachment);
        return newMes;
    }

    private void getOrderingListFirstStepIsEmpty(Messaging messaging) {
        messageSenderService.sendSimpleMessage(recognizeService.recognize(EMPTY_LIST.name(),messaging.getSender().getId()), messaging.getSender().getId());
        askOneMoreAction(messaging.getSender().getId());
        MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());
        MUser.setStatus(null);
        MUserRepositoryService.saveAndFlush(MUser);
    }
    @Override
    public void orderingFilling(Messaging messaging,Long orderId) {
        try {


            com.bots.lviv_croissant_bot.entity.register.Courier courier = courierRegisterService.findByRecipientId(messaging.getSender().getId());


            CustomerOrdering customerOrdering = customerOrderingRepositoryService.findById(orderId).orElseThrow(ElementNoFoundException::new);
            courier.addOne(customerOrdering);
            courierRegisterService.saveAndFlush(courier);
            customerOrderingRepositoryService.saveAndFlush(customerOrdering);

            messageSenderService.sendSimpleMessage(recognizeService.recognize(ADDED_TO_DB.name(), messaging.getSender().getId()), messaging.getSender().getId());
            userService.changeStatus(messaging, null);
            askOneMoreAction(messaging.getSender().getId());

        } catch (Exception ex) {
            logger.error("Error",ex);

        }

    }


    private void parsePhoneNumber(Messaging messaging, com.bots.lviv_croissant_bot.entity.register.Courier courier) {
        if (TextFormatter.isPhoneNumber(messaging.getMessage().getText())) {
            courier.setPhoneNumber(messaging.getMessage().getText());
            courierRegisterService.saveAndFlush(courier);
            messageSenderService.sendSimpleMessage(recognizeService.recognize(ADDED_TO_DB.name(),messaging.getSender().getId()), messaging.getSender().getId());
            MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());
            MUser.setStatus(null);
            MUserRepositoryService.saveAndFlush(MUser);
        } else {
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_NUMBER_OF_TELEPHONE.name(),messaging.getSender().getId()), messaging.getSender().getId());
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NUMBER_OF_PHONE.name(),messaging.getSender().getId()), messaging.getSender().getId());

        }
    }


    private void parseName(Messaging messaging, com.bots.lviv_croissant_bot.entity.register.Courier courier) {
        courier.setName(messaging.getMessage().getText());
        courierRegisterService.saveAndFlush(courier);
        messageSenderService.sendSimpleMessage(recognizeService.recognize(NUMBER_OF_PHONE.name(),messaging.getSender().getId()), messaging.getSender().getId());

    }

    private void courierRegisterCreator(Messaging messaging) {
        com.bots.lviv_croissant_bot.entity.register.Courier courier = new com.bots.lviv_croissant_bot.entity.register.Courier();
        courier.setRecipientId(messaging.getSender().getId());
        courierRegisterService.saveAndFlush(courier);
        messageSenderService.sendSimpleMessage(recognizeService.recognize(NAME_LASTNAME.name(),messaging.getSender().getId()), messaging.getSender().getId());
    }


    private void askOneMoreAction(Long recipient) {
        messageSenderService.sendSimpleQuestion(recipient, recognizeService.recognize(ONE_MORE_ACTION_COURIER.name(),recipient), ONE_MORE_ACTION_COURIER_PAYLOAD.name(), "?");
    }
}