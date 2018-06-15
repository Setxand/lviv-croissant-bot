package com.example.demo.service.eventService.messengerEventService;

import com.example.demo.dto.messanger.Messaging;

public interface CourierEventService {
    public void parseCourier(Messaging messaging);
    public void getOrderingList(Messaging messaging);
    public void orderingFilling(Messaging messaging, Long orderId);
    public void completeOrderingsFinalize(Messaging messaging,Long orderId);
}
