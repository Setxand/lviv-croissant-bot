package com.bots.lvivCroissantBot.service.eventService.messengerEventService;

import com.bots.lvivCroissantBot.dto.messanger.Messaging;

public interface CourierEventService {
    public void parseCourier(Messaging messaging);
    public void getOrderingList(Messaging messaging);
    public void orderingFilling(Messaging messaging, Long orderId);
    public void completeOrderingsFinalize(Messaging messaging,Long orderId);
}
