package com.bots.lvivcroissantbot.controller;

import com.bots.lvivcroissantbot.config.AppConfig;
import com.bots.lvivcroissantbot.config.client.MessengerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class Test {
    private Object object;
    @Autowired
    private MessengerClient client;
    @GetMapping
    public Object getObj() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }



    @GetMapping("/props")
    public AppConfig.MesProps getProps(){
        return client.getMesProps();
    }
}
