package com.example.demo.services.supportService;

import com.example.demo.models.messanger.Messaging;

public interface VerifyService{
    public boolean verify(String verifyToken);
    public  boolean isCustomer(Messaging messaging);

}
