package com.bots.lvivcroissantbot.service.telegram.event;

import com.bots.lvivcroissantbot.dto.telegram.Message;

public interface TelegramOrderingService {
    public void makeOrder(Message message);

    void ifNoMore(Message message);
}
