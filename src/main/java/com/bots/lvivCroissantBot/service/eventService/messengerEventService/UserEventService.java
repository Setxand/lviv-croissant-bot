package com.bots.lvivCroissantBot.service.eventService.messengerEventService;

import com.bots.lvivCroissantBot.entity.peopleRegister.MUser;
import com.bots.lvivCroissantBot.dto.messanger.Messaging;

public interface UserEventService {
    public void customerRegistration(Messaging messaging);
    public void changeStatus(Messaging messaging, String nextCommand);
    public boolean isUser(MUser MUser);
}
