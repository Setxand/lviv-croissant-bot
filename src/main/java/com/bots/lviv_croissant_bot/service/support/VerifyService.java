package com.bots.lviv_croissant_bot.service.support;

import com.bots.lviv_croissant_bot.dto.messanger.Messaging;

public interface VerifyService{
    public boolean verify(String verifyToken);
    public  boolean isCustomer(Messaging messaging);

}
