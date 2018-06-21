package com.bots.lvivCroissantBot.service.messangerService.impl;

import com.bots.lvivCroissantBot.dto.messanger.Entry;
import com.bots.lvivCroissantBot.dto.messanger.Event;
import com.bots.lvivCroissantBot.dto.messanger.Messaging;
import com.bots.lvivCroissantBot.service.messangerService.*;
import com.bots.lvivCroissantBot.service.peopleRegisterService.MUserRepositoryService;
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
    MUserRepositoryService MUserRepositoryService;
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
