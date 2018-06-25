package com.bots.lvivcroissantbot.service.adminpanel.event;

import com.bots.lvivcroissantbot.dto.telegram.Message;

public interface TelegramAddingRecordingsEventService {
    public void addFilling(Message message);
    public void addCroissant(Message message);
}
