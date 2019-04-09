package com.example.demo.services.messangerService.impl;

import com.example.demo.DemoApplicationTests;
import com.example.demo.model.messanger.*;
import com.example.demo.services.messangerService.PayloadParserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.example.demo.constcomponent.messengerEnums.payloads.Payloads.NAVIGATION_MENU;

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