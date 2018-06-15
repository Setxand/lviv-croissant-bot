package com.example.demo.service.messangerService.impl;

import com.example.demo.DemoApplicationTests;
import com.example.demo.dto.messanger.*;
import com.example.demo.service.messangerService.PayloadParserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.example.demo.constantEnum.messengerEnums.payloads.Payloads.NAVIGATION_MENU;

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