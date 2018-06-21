package com.bots.lvivCroissantBot.service.messenger.event.impl;

import com.bots.lvivCroissantBot.entity.lvivCroissants.CroissantEntity;
import com.bots.lvivCroissantBot.entity.lvivCroissants.CustomerOrdering;
import com.bots.lvivCroissantBot.entity.Support;
import com.bots.lvivCroissantBot.entity.register.MUser;
import com.bots.lvivCroissantBot.dto.messanger.Messaging;
import com.bots.lvivCroissantBot.repository.CustomerOrderingRepository;
import com.bots.lvivCroissantBot.repository.SupportEntityRepository;
import com.bots.lvivCroissantBot.service.messenger.event.OrderingService;
import com.bots.lvivCroissantBot.service.messenger.event.UserService;
import com.bots.lvivCroissantBot.service.messenger.MessageSenderService;
import com.bots.lvivCroissantBot.service.peopleRegister.MUserRepositoryService;
import com.bots.lvivCroissantBot.service.support.RecognizeService;
import com.bots.lvivCroissantBot.service.support.TextFormatter;
import com.bots.lvivCroissantBot.service.uni.CroissantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.Cases.*;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.payload.QuickReplyPayloads.ACCEPT_ORDERING_PAYLOAD;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.payload.QuickReplyPayloads.ADDRESS_PAYLOAD;
import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.speaking.ServerSideSpeaker.*;

@Service
public class OrderingServiceImpl implements OrderingService {
    @Autowired
    private CroissantService croissantRepositoryService;
    @Autowired
    private MUserRepositoryService MUserRepositoryService;
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private CustomerOrderingRepository customerOrderingRepositoryService;
    @Autowired
    private RecognizeService recognizeService;
    @Autowired
    private UserService userService;
    @Autowired
    private SupportEntityRepository supportEntityRepositoryService;
    @Override
    public void parseOrdering(Messaging messaging) {
        if(messaging.getPostback()!=null)
             orderingCreator(messaging);
        else
             orderingFinalist(messaging);
    }


    private void orderingCreator(Messaging messaging) {
        MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());
        String croissantId = TextFormatter.ejectSingleVariable(messaging.getPostback().getPayload());
        CroissantEntity croissantEntity = croissantRepositoryService.findOne(Long.parseLong(croissantId));

        if(supportEntityRepositoryService.findByUserId(messaging.getSender().getId())!=null ){
            if(supportEntityRepositoryService.findByUserId(messaging.getSender().getId()).getOneMore()!=null) {
                messageSenderService.sendSimpleQuestion(messaging.getSender().getId(),recognizeService.recognize(ACCEPTING_ORDERING.name(),messaging.getSender().getId())+ croissantEntity.getName()+"?",ACCEPT_ORDERING_PAYLOAD.name()+"?"+croissantId,"&");
                return;

            }
        }

        createOrdering(MUser, croissantEntity,messaging);
    }

    private void createOrdering(MUser MUser, CroissantEntity croissantEntity, Messaging messaging) {
        CustomerOrdering customerOrdering = new CustomerOrdering();
        customerOrdering.setPrice(0);
        Support support = supportEntityRepositoryService.findByUserId(messaging.getSender().getId());
        support.setCount(Integer.parseInt(croissantEntity.getId().toString()));
        supportEntityRepositoryService.saveAndFlush(support);
        customerOrdering.setName(MUser.getName() + " " + MUser.getLastName());
        MUser.addCustomerOrdering(customerOrdering);
        customerOrderingRepositoryService.saveAndFlush(customerOrdering);
        MUserRepositoryService.saveAndFlush(MUser);
        if (userService.isUser(MUser)) {

            isExistsUser(customerOrdering, messaging);
        } else {
            orderingFinalist(messaging);
        }
    }


    private void orderingFinalist(Messaging messaging) {
        MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());
        CustomerOrdering ordering = customerOrderingRepositoryService.findTopByMUserOrderByIdDesc(MUser);


        if (ordering.getPhoneNumber() == null) {
             parsePhoneNumber(messaging, ordering, MUser);
        }
         else if (ordering.getAddress() == null) {
             parseAddress(messaging, ordering, MUser);


        } else if (ordering.getTime() == null) {
             parseTime(messaging, ordering, MUser);


        } else {
             errorAction(messaging);
        }
    }




    private void isExistsUser(CustomerOrdering customerOrdering, Messaging messaging) {

            MUser MUser = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());
            customerOrdering.setPhoneNumber(MUser.getUser().getPhoneNumber());
            customerOrdering.setAddress(MUser.getAddress());
            customerOrderingRepositoryService.saveAndFlush(customerOrdering);
            String text = MUser.getName()+", "+recognizeService.recognize(ADDRESS_LOCATION_QUESTION.name(),messaging.getSender().getId())+": ("+ MUser.getAddress()+")?";
            messageSenderService.sendSimpleQuestion(messaging.getSender().getId(),text,ADDRESS_PAYLOAD.name(),"?");

    }




    private void errorAction(Messaging messaging) {
        MUser customer1 = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());
        customer1.setStatus(null);
        MUserRepositoryService.saveAndFlush(customer1);
        messageSenderService.errorMessage(messaging.getSender().getId());
        if (customerOrderingRepositoryService.findTopByOrderByIdDesc().getCroissants().isEmpty()) {
            customerOrderingRepositoryService.delete(customerOrderingRepositoryService.findTopByOrderByIdDesc());
        }
    }

    private void parseTime(Messaging messaging, CustomerOrdering ordering, MUser MUser) {

        if(!MUser.getStatus().equals(ASK_TIME.name())){
            messageSenderService.sendSimpleMessage(recognizeService.recognize(TIME_OF_ORDERING.name(),messaging.getSender().getId()), messaging.getSender().getId());
            userService.changeStatus(messaging,ASK_TIME.name());
        }
        else if(TextFormatter.isCorrectTime(messaging.getMessage().getText())) {


            ordering.setTime(messaging.getMessage().getText());
            customerOrderingRepositoryService.saveAndFlush(ordering);
            userService.changeStatus(messaging,null);


            Support support = supportEntityRepositoryService.findByUserId(messaging.getSender().getId());
            CroissantEntity croissantEntity = croissantRepositoryService.findOne(Long.parseLong(support.getCount().toString()));
            messageSenderService.sendSimpleQuestion(messaging.getSender().getId(),recognizeService.recognize(ACCEPTING_ORDERING.name(),messaging.getSender().getId())+ croissantEntity.getName()+"?",ACCEPT_ORDERING_PAYLOAD.name()+"?"+ croissantEntity.getId(),"&");
            support.setCount(null);
            supportEntityRepositoryService.saveAndFlush(support);
        }
        else{
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_TIME.name(),messaging.getSender().getId()),messaging.getSender().getId());
            messageSenderService.sendSimpleMessage(recognizeService.recognize(TIME_OF_ORDERING.name(),messaging.getSender().getId()), messaging.getSender().getId());
        }
    }



    private void parseAddress(Messaging messaging, CustomerOrdering ordering, MUser MUser) {
        if(MUser.getAddress()!=null && messaging.getMessage().getQuickReply()==null && !MUser.getStatus().equals(ASK_ADDRESS.name())){
            ordering.setAddress(MUser.getAddress());
            customerOrderingRepositoryService.saveAndFlush(ordering);
            orderingFinalist(messaging);

        }
        else if(!MUser.getStatus().equals(ASK_ADDRESS.name())){
            messageSenderService.sendSimpleMessage(recognizeService.recognize(ADDRESS_OF_CUSTOMER.name(),messaging.getSender().getId()), messaging.getSender().getId());
            userService.changeStatus(messaging,ASK_ADDRESS.name());
        }
        else if(TextFormatter.isCorrectAddress(messaging.getMessage().getText())) {
            ordering.setAddress(messaging.getMessage().getText());
            customerOrderingRepositoryService.saveAndFlush(ordering);
            userService.changeStatus(messaging,ORDERING.name());
            MUser.setAddress(ordering.getAddress());
            MUserRepositoryService.saveAndFlush(MUser);

            orderingFinalist(messaging);

        }
        else {
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_ADDRESS.name(),messaging.getSender().getId()), messaging.getSender().getId());
            messageSenderService.sendSimpleMessage(recognizeService.recognize(ADDRESS_OF_CUSTOMER.name(),messaging.getSender().getId()), messaging.getSender().getId());
        }

    }


    private void parsePhoneNumber(Messaging messaging, CustomerOrdering ordering,MUser MUser) {
        if(MUser.getUser().getPhoneNumber()!=null){
            ordering.setPhoneNumber(MUser.getUser().getPhoneNumber());
            customerOrderingRepositoryService.saveAndFlush(ordering);
            orderingFinalist(messaging);

        }
        else if(!MUser.getStatus().equals(ASK_PHONE_NUMBER.name())) {
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NUMBER_OF_PHONE.name(), messaging.getSender().getId()), messaging.getSender().getId());
            userService.changeStatus(messaging,ASK_PHONE_NUMBER.name());
        }
       else if(TextFormatter.isPhoneNumber(messaging.getMessage().getText())) {
            ordering.setPhoneNumber(messaging.getMessage().getText());
            customerOrderingRepositoryService.saveAndFlush(ordering);
            userService.changeStatus(messaging,ORDERING.name());
            if(MUser.getUser().getPhoneNumber()==null) {
                MUser.getUser().setPhoneNumber(ordering.getPhoneNumber());
                MUserRepositoryService.saveAndFlush(MUser);
            }
            orderingFinalist(messaging);

        }
        else {
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NON_CORRECT_FORMAT_OF_NUMBER_OF_TELEPHONE.name(),messaging.getSender().getId()),messaging.getSender().getId());
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NUMBER_OF_PHONE.name(),messaging.getSender().getId()),messaging.getSender().getId());
        }

    }





}
