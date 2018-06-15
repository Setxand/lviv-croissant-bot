package com.example.demo.service.messangerService.impl;

import com.example.demo.entity.lvivCroissants.CustomerOrdering;
import com.example.demo.entity.peopleRegister.MUser;
import com.example.demo.dto.messanger.Messaging;
import com.example.demo.dto.messanger.QuickReply;
import com.example.demo.service.eventService.messengerEventService.UserEventService;
import com.example.demo.service.repositoryService.CustomerOrderingRepositoryService;
import com.example.demo.service.messangerService.MessageProcessorHelperService;
import com.example.demo.service.messangerService.MessageSenderService;
import com.example.demo.service.peopleRegisterService.UserRepositoryService;
import com.example.demo.service.supportService.RecognizeService;
import com.example.demo.service.supportService.VerifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.constantEnum.messengerEnums.Cases.*;
import static com.example.demo.constantEnum.messengerEnums.Cases.PERSONAL_REQUEST;
import static com.example.demo.constantEnum.messengerEnums.Roles.ADMIN;
import static com.example.demo.constantEnum.messengerEnums.payloads.QuickReplyPayloads.*;
import static com.example.demo.constantEnum.messengerEnums.payloads.QuickReplyPayloads.COMPLETING_ORDERINGS;
import static com.example.demo.constantEnum.messengerEnums.payloads.QuickReplyPayloads.GET_LIST_OF_ORDERING;
import static com.example.demo.constantEnum.messengerEnums.speaking.ServerSideSpeaker.*;

@Service
public class MessageProcessorHelperServiceImpl implements MessageProcessorHelperService {
    @Autowired
    private UserEventService userEventService;
    @Autowired
    private VerifyService verifyService;
    @Autowired
    private UserRepositoryService userRepositoryService;
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private RecognizeService recognizeService;
    @Autowired
    private CustomerOrderingRepositoryService customerOrderingRepositoryService;
    @Override
    public void helpCompleteCroissantSecondStep(Messaging messaging) {
        userEventService.changeStatus(messaging,COMPLETE_CROISSANT_SECOND_STEP.name());
        String var = messaging.getMessage().getText();
        messaging.getMessage().setText(COMPLETE_CROISSANT_SECOND_STEP.name()+"?"+var);
    }

    @Override
    public void helpCompleteOrderingList(Messaging messaging) {
        userEventService.changeStatus(messaging,COMPLETE_ORDERINGS_LIST.name());

        messaging.getMessage().setQuickReply(new QuickReply());
        messaging.getMessage().getQuickReply().setPayload(COMPLETE_ORDERINGS_LIST.name());
    }

    @Override
    public void helpCompletingOrderings(Messaging messaging) {
        userEventService.changeStatus(messaging,COMPLETE_ORDERINGS_LIST.name());
        messaging.getMessage().setQuickReply(new QuickReply());
        messaging.getMessage().getQuickReply().setPayload(COMPLETING_ORDERINGS.name());
    }



    @Override
    public void helpOrderingListFilling(Messaging messaging) {
        userEventService.changeStatus(messaging,ORDERING_LIST_FILLING.name());
        messaging.getMessage().setQuickReply(new QuickReply());
        messaging.getMessage().getQuickReply().setPayload(ORDERING_LIST_FILLING.name());
    }

    @Override
    public void helpParseRoleRequest(Messaging messaging) {
        String text = messaging.getMessage().getText();

        if(verifyService.isCustomer(messaging) || text.equalsIgnoreCase(COURIER_REQUEST.name())) {

            List<MUser> admins = userRepositoryService.getByRole(ADMIN);
            MUser customer = userRepositoryService.findOnebyRId(messaging.getSender().getId());
            for (MUser admin : admins) {
                if (text.toUpperCase().equals(ADMIN_REQUEST.name())) {
                    messageSenderService.sendSimpleQuestion(admin.getRecipientId(), recognizeService.recognize(ADMIN_REQ.name(),messaging.getSender().getId()) + customer.getName(), ADMIN_REQUEST_PAYLOAD.name() + "?" + messaging.getSender().getId(), "&");
                } else if (text.toUpperCase().equals(PERSONAL_REQUEST.name())) {
                    messageSenderService.sendSimpleQuestion(admin.getRecipientId(), recognizeService.recognize(PERSONAL_REQ.name(),messaging.getSender().getId()) + customer.getName(), PERSONAL_REQUEST_PAYLOAD.name() + "?" + messaging.getSender().getId(), "&");

                }
                else if(text.equalsIgnoreCase(COURIER_REQUEST.name())){
                    messageSenderService.sendSimpleQuestion(admin.getRecipientId(),recognizeService.recognize(COURIER_REQ.name(),messaging.getSender().getId())+customer.getName(),COURIER_REQ_ADMIN_SIDE_PAYLOAD.name()+"?"+messaging.getSender().getId(),"&");

                }
            }
        }
        else {
            messageSenderService.sendSimpleMessage(recognizeService.recognize(NEED_TO_REGISTER.name(),messaging.getSender().getId()),messaging.getSender().getId());
            userEventService.customerRegistration(messaging);
        }
    }

    @Override
    public void helpCourierRegistration(Messaging messaging) {
        userEventService.changeStatus(messaging,COURIER_REGISTRATION_FINAL.name());
        QuickReply quickReply =  new QuickReply();
        quickReply.setPayload(COURIER_QUESTION_PAYLOAD.name());
        messaging.getMessage().setQuickReply(quickReply);
    }

    @Override
    public void helpGetListOfOrdering(Messaging messaging) {
        userEventService.changeStatus(messaging,ORDERING_LIST_FILLING.name());
        messaging.getMessage().setQuickReply(new QuickReply());
        messaging.getMessage().getQuickReply().setPayload(GET_LIST_OF_ORDERING.name());
    }

    @Override
    public void helpDeleteOrderings(Messaging messaging) {
        List<CustomerOrdering> customerOrderings = customerOrderingRepositoryService.findAll();
        for(CustomerOrdering customerOrdering: customerOrderings){
            if(customerOrdering.getMUser()!=null){
                customerOrdering.getMUser().getCustomerOrderings().remove(customerOrdering);
                userRepositoryService.saveAndFlush(customerOrdering.getMUser());
                customerOrderingRepositoryService.delete(customerOrdering);
            }
        }
    }

}
