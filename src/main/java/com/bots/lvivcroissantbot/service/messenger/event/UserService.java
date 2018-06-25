package com.bots.lvivcroissantbot.service.messenger.event;

import com.bots.lvivcroissantbot.dto.messanger.Messaging;
import com.bots.lvivcroissantbot.entity.register.MUser;

public interface UserService {
    public void customerRegistration(Messaging messaging);

    public void changeStatus(Messaging messaging, String nextCommand);

    public boolean isUser(MUser MUser);
}
