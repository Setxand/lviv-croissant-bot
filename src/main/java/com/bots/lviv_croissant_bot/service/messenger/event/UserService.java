package com.bots.lviv_croissant_bot.service.messenger.event;

import com.bots.lviv_croissant_bot.entity.register.MUser;
import com.bots.lviv_croissant_bot.dto.messanger.Messaging;

public interface UserService {
    public void customerRegistration(Messaging messaging);
    public void changeStatus(Messaging messaging, String nextCommand);
    public boolean isUser(MUser MUser);
}
