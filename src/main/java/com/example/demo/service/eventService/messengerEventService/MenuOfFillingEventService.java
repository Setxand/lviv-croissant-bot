package com.example.demo.service.eventService.messengerEventService;

import com.example.demo.dto.messanger.Messaging;

public interface MenuOfFillingEventService {
    public void getMenuOfFilling(Long recipient);
    public void saveNewFilling(Messaging messaging);
}
