package com.bots.lviv_croissant_bot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test {
    private Object object;


    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public Object getObj(){
        return object;
    }
    public void setObject(Object object) {
        this.object = object;
    }
}
