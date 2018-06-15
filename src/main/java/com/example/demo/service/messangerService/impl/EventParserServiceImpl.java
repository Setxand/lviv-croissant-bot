package com.example.demo.service.messangerService.impl;

import com.example.demo.entity.peopleRegister.MUser;
import com.example.demo.dto.messanger.Entry;
import com.example.demo.dto.messanger.Event;
import com.example.demo.dto.messanger.Messaging;
import com.example.demo.service.messangerService.*;
import com.example.demo.service.peopleRegisterService.UserRepositoryService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EventParserServiceImpl implements EventParserService {
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private QuickReplyParserService quickReplyParserService;
    @Autowired
    private MessageParserService messageParserService;
    @Autowired
    UserRepositoryService userRepositoryService;
    @Autowired
    private PayloadParserService payloadParserService;


    private static final Logger logger = Logger.getLogger(EventParserServiceImpl.class);

    @Override
    public boolean parseEvent(Event event) {
        for (Entry e : event.getEntry()) {


            for (Messaging messaging : e.getMessaging()) {
                try {
                    if (messaging.getPostback() != null) {

                        payloadParserService.parsePayload(messaging);
                        return true;
                    } else if (messaging.getMessage() != null) {
                        messageParserService.parseMessage(messaging);

                        return true;


                    }
                } catch (Exception ex) {
                    MUser MUser = userRepositoryService.findOnebyRId(messaging.getSender().getId());
                    if(MUser.getStatus()!=null) {
                        MUser.setStatus(null);
                        userRepositoryService.saveAndFlush(MUser);
                    }
                    ex.printStackTrace();
                    logger.warn(ex);
                    messageSenderService.errorMessage(messaging.getSender().getId());
                    return true;
                }
            }
        }
        return false;

    }
}
