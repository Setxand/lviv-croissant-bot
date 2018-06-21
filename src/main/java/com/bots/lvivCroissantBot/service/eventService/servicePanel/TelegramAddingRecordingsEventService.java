package com.bots.lvivCroissantBot.service.eventService.servicePanel;

import com.bots.lvivCroissantBot.dto.telegram.Message;

public interface TelegramAddingRecordingsEventService {
    public void addFilling(Message message);
    public void addCroissant(Message message);
}
