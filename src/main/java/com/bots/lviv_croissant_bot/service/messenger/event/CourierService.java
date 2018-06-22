package com.bots.lviv_croissant_bot.service.messenger.event;

import com.bots.lviv_croissant_bot.dto.messanger.Messaging;

public interface CourierService {
    public void parseCourier(Messaging messaging);
    public void getOrderingList(Messaging messaging);
    public void orderingFilling(Messaging messaging, Long orderId);
    public void completeOrderingsFinalize(Messaging messaging,Long orderId);
}
