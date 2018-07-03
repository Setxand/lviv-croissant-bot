package com.bots.lvivcroissantbot.controller;

import com.bots.lvivcroissantbot.config.props.PropertiesConfig;
import com.bots.lvivcroissantbot.config.props.client.MessengerClient;
import com.bots.lvivcroissantbot.config.props.client.UrlClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    private Object object;
    @Autowired
    private MessengerClient client;
    @Autowired
    private UrlClient urlClient;

    @GetMapping
    public Object getObj() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }


    @GetMapping("/urlProps")
    private PropertiesConfig.UrlProps getUrlProps() {
        return urlClient.getUrlProps();
    }

    @GetMapping("/props")
    public PropertiesConfig.MesProps getProps() {
        return client.getMesProps();
    }
}
