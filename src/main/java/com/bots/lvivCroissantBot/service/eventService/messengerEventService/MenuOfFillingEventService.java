package com.bots.lvivCroissantBot.service.eventService.messengerEventService;

import com.bots.lvivCroissantBot.dto.messanger.Messaging;

public interface MenuOfFillingEventService {
    public void getMenuOfFilling(Long recipient);
    public void saveNewFilling(Messaging messaging);
}
