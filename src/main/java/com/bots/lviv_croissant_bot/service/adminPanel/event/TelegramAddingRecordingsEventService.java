package com.bots.lviv_croissant_bot.service.adminPanel.event;

import com.bots.lviv_croissant_bot.dto.telegram.Message;

public interface TelegramAddingRecordingsEventService {
    public void addFilling(Message message);
    public void addCroissant(Message message);
}
