package com.bots.lvivcroissantbot.service.messenger.impl;

import com.bots.lvivcroissantbot.DemoApplicationTests;
import com.bots.lvivcroissantbot.dto.messanger.*;
import com.bots.lvivcroissantbot.service.messenger.PayloadParserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bots.lvivcroissantbot.constantenum.messenger.payload.Payloads.NAVIGATION_MENU;

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