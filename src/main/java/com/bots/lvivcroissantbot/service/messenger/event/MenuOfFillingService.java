package com.bots.lvivcroissantbot.service.messenger.event;

import com.bots.lvivcroissantbot.dto.messanger.Messaging;

public interface MenuOfFillingService {
    public void getMenuOfFilling(Long recipient);

    public void saveNewFilling(Messaging messaging);
}
