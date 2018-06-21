package com.bots.lvivCroissantBot.service.support;

import com.bots.lvivCroissantBot.dto.messanger.Messaging;

public interface VerifyService{
    public boolean verify(String verifyToken);
    public  boolean isCustomer(Messaging messaging);

}
