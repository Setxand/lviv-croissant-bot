package com.example.demo.service.supportService;

import com.example.demo.dto.messanger.Messaging;

public interface VerifyService{
    public boolean verify(String verifyToken);
    public  boolean isCustomer(Messaging messaging);

}
