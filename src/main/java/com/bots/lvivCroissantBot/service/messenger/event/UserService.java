package com.bots.lvivCroissantBot.service.messenger.event;

import com.bots.lvivCroissantBot.entity.register.MUser;
import com.bots.lvivCroissantBot.dto.messanger.Messaging;

public interface UserService {
    public void customerRegistration(Messaging messaging);
    public void changeStatus(Messaging messaging, String nextCommand);
    public boolean isUser(MUser MUser);
}
