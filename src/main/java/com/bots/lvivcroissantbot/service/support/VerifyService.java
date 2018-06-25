package com.bots.lvivcroissantbot.service.support;

import com.bots.lvivcroissantbot.dto.messanger.Messaging;

public interface VerifyService {
    public boolean verify(String verifyToken);

    public boolean isCustomer(Messaging messaging);

}
