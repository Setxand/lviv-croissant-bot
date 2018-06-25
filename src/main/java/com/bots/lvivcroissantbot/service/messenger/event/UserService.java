package com.bots.lvivcroissantbot.service.messenger.event;

import com.bots.lvivcroissantbot.entity.register.MUser;
import com.bots.lvivcroissantbot.dto.messanger.Messaging;

public interface UserService {
    public void customerRegistration(Messaging messaging);
    public void changeStatus(Messaging messaging, String nextCommand);
    public boolean isUser(MUser MUser);
}
