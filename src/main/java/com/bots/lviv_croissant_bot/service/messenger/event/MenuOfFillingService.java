package com.bots.lviv_croissant_bot.service.messenger.event;

import com.bots.lviv_croissant_bot.dto.messanger.Messaging;

public interface MenuOfFillingService {
    public void getMenuOfFilling(Long recipient);
    public void saveNewFilling(Messaging messaging);
}
