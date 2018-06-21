package com.bots.lvivCroissantBot.service.telegram.event;

import com.bots.lvivCroissantBot.dto.telegram.Message;

public interface TelegramOrdering {
    public void makeOrder(Message message);

    void ifNoMore(Message message);
}
