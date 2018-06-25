package com.bots.lvivcroissantbot.service.messenger.impl;

import com.bots.lvivcroissantbot.dto.messanger.Entry;
import com.bots.lvivcroissantbot.dto.messanger.Event;
import com.bots.lvivcroissantbot.dto.messanger.Messaging;
import com.bots.lvivcroissantbot.service.messenger.*;
import com.bots.lvivcroissantbot.service.peopleregister.MUserRepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EventParserServiceImpl implements EventParserService {
    private final static Logger logger = LoggerFactory.getLogger(EventParserService.class);
    @Autowired
    MUserRepositoryService MUserRepositoryService;
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private QuickReplyParserService quickReplyParserService;
    @Autowired
    private MessageParserService messageParserServiceService;
    @Autowired
    private PayloadParserService payloadParserService;

    @Override
    public boolean parseEvent(Event event) {
        for (Entry e : event.getEntry()) {


            for (Messaging messaging : e.getMessaging()) {
                try {
                    if (messaging.getPostback() != null) {

                        payloadParserService.parsePayload(messaging);
                        return true;
                    } else if (messaging.getMessage() != null) {
                        messageParserServiceService.parseMessage(messaging);

                        return true;


                    }
                } catch (Exception ex) {

                    ex.printStackTrace();
                    logger.error("Error", ex);
                    messageSenderService.errorMessage(messaging.getSender().getId());
                    return true;
                }
            }
        }
        return false;

    }
}
