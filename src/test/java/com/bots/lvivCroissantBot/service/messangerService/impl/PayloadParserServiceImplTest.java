package com.bots.lvivCroissantBot.service.messangerService.impl;

import com.bots.lvivCroissantBot.DemoApplicationTests;
import com.bots.lvivCroissantBot.dto.messanger.*;
import com.bots.lvivCroissantBot.service.messangerService.PayloadParserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bots.lvivCroissantBot.constantEnum.messengerEnum.payload.Payloads.NAVIGATION_MENU;

public class PayloadParserServiceImplTest extends DemoApplicationTests{
    @Autowired
    private PayloadParserService payloadParserService;
    private Messaging messaging;
    @Before
    public void setUp(){
     messaging = new Messaging();
     messaging.setRecipient(new Recipient(userId));
     messaging.setPostback(new PostBack(NAVIGATION_MENU.name()));
     messaging.setSender(new Sender(userId));
    }
    @Test
    public void parsePayload() throws Exception {
        payloadParserService.parsePayload(messaging);

    }

}