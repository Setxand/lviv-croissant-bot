package com.bots.lvivcroissantbot.service.messenger.impl;

import com.bots.lvivcroissantbot.entity.lvivcroissants.CustomerOrdering;
import com.bots.lvivcroissantbot.entity.register.MUser;
import com.bots.lvivcroissantbot.dto.messanger.Messaging;
import com.bots.lvivcroissantbot.dto.messanger.QuickReply;
import com.bots.lvivcroissantbot.entity.register.User;
import com.bots.lvivcroissantbot.repository.CustomerOrderingRepository;
import com.bots.lvivcroissantbot.repository.UserRepository;
import com.bots.lvivcroissantbot.service.messenger.event.UserService;
import com.bots.lvivcroissantbot.service.messenger.MessageProcessorHelperService;
import com.bots.lvivcroissantbot.service.messenger.MessageSenderService;
import com.bots.lvivcroissantbot.service.peopleregister.MUserRepositoryService;
import com.bots.lvivcroissantbot.service.support.RecognizeService;
import com.bots.lvivcroissantbot.service.support.VerifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.bots.lvivcroissantbot.constantenum.messenger.Cases.*;
import static com.bots.lvivcroissantbot.constantenum.messenger.Cases.PERSONAL_REQUEST;
import static com.bots.lvivcroissantbot.constantenum.messenger.Role.ADMIN;
import static com.bots.lvivcroissantbot.constantenum.messenger.payload.QuickReplyPayloads.*;
import static com.bots.lvivcroissantbot.constantenum.messenger.payload.QuickReplyPayloads.COMPLETING_ORDERINGS;
import static com.bots.lvivcroissantbot.constantenum.messenger.payload.QuickReplyPayloads.GET_LIST_OF_ORDERING;
import static com.bots.lvivcroissantbot.constantenum.messenger.speaking.ServerSideSpeaker.*;

@Service
public class MessageProcessorHelperServiceImpl implements MessageProcessorHelperService {
    @Autowired
    private UserService userService;
    @Autowired
    private VerifyService verifyService;
    @Autowired
    private MUserRepositoryService MUserRepositoryService;
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private RecognizeService recognizeService;
    @Autowired
    private CustomerOrderingRepository customerOrderingRepositoryService;
    @Autowired
    private UserRepository userRepository;
    @Override
    public void helpCompleteCroissantSecondStep(Messaging messaging) {
        userService.changeStatus(messaging,COMPLETE_CROISSANT_SECOND_STEP.name());
        String var = messaging.getMessage().getText();
        messaging.getMessage().setText(COMPLETE_CROISSANT_SECOND_STEP.name()+"?"+var);
    }

    @Override
    public void helpCompleteOrderingList(Messaging messaging) {
        userService.changeStatus(messaging,COMPLETE_ORDERINGS_LIST.name());

        messaging.getMessage().setQuickReply(new QuickReply());
        messaging.getMessage().getQuickReply().setPayload(COMPLETE_ORDERINGS_LIST.name());
    }

    @Override
    public void helpCompletingOrderings(Messaging messaging) {
        userService.changeStatus(messaging,COMPLETE_ORDERINGS_LIST.name());
        messaging.getMessage().setQuickReply(new QuickReply());
        messaging.getMessage().getQuickReply().setPayload(COMPLETING_ORDERINGS.name());
    }



    @Override
    public void helpOrderingListFilling(Messaging messaging) {
        userService.changeStatus(messaging,ORDERING_LIST_FILLING.name());
        messaging.getMessage().setQuickReply(new QuickReply());
        messaging.getMessage().getQuickReply().setPayload(ORDERING_LIST_FILLING.name());
    }

    @Override
    public void helpParseRoleRequest(Messaging messaging) {
        String text = messaging.getMessage().getText();

        if(verifyService.isCustomer(messaging) || text.equalsIgnoreCase(COURIER_REQUEST.name())) {

            List<MUser> admins = userRepository.findAllByRole(ADMIN).stream().map(User::getMUser).collect(Collectors.toList());
            MUser customer = MUserRepositoryService.findOnebyRId(messaging.getSender().getId());
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
            userService.customerRegistration(messaging);
        }
    }

    @Override
    public void helpCourierRegistration(Messaging messaging) {
        userService.changeStatus(messaging,COURIER_REGISTRATION_FINAL.name());
        QuickReply quickReply =  new QuickReply();
        quickReply.setPayload(COURIER_QUESTION_PAYLOAD.name());
        messaging.getMessage().setQuickReply(quickReply);
    }

    @Override
    public void helpGetListOfOrdering(Messaging messaging) {
        userService.changeStatus(messaging,ORDERING_LIST_FILLING.name());
        messaging.getMessage().setQuickReply(new QuickReply());
        messaging.getMessage().getQuickReply().setPayload(GET_LIST_OF_ORDERING.name());
    }

    @Override
    public void helpDeleteOrderings(Messaging messaging) {
        List<CustomerOrdering> customerOrderings = customerOrderingRepositoryService.findAll();
        for(CustomerOrdering customerOrdering: customerOrderings){
            if(customerOrdering.getMUser()!=null){
                customerOrdering.getMUser().getCustomerOrderings().remove(customerOrdering);
                MUserRepositoryService.saveAndFlush(customerOrdering.getMUser());
                customerOrderingRepositoryService.delete(customerOrdering);
            }
        }
    }

}
