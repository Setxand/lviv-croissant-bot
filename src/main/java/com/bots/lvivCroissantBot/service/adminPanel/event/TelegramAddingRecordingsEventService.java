package com.bots.lvivCroissantBot.service.adminPanel.event;

import com.bots.lvivCroissantBot.dto.telegram.Message;

public interface TelegramAddingRecordingsEventService {
    public void addFilling(Message message);
    public void addCroissant(Message message);
}
