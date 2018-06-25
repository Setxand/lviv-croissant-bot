package com.bots.lvivcroissantbot.service.messenger.event;

import com.bots.lvivcroissantbot.dto.messanger.Messaging;

public interface CourierService {
    public void parseCourier(Messaging messaging);
    public void getOrderingList(Messaging messaging);
    public void orderingFilling(Messaging messaging, Long orderId);
    public void completeOrderingsFinalize(Messaging messaging,Long orderId);
}
