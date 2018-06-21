package com.bots.lvivCroissantBot.service.messenger.event;

import com.bots.lvivCroissantBot.dto.messanger.Messaging;

public interface MenuOfFillingService {
    public void getMenuOfFilling(Long recipient);
    public void saveNewFilling(Messaging messaging);
}
