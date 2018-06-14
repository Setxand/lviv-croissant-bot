package com.example.demo.service.eventService.messengerEventService;

import com.example.demo.entity.peopleRegister.User;
import com.example.demo.dto.messanger.Messaging;

public interface UserEventService {
    public void customerRegistration(Messaging messaging);
    public void changeStatus(Messaging messaging, String nextCommand);
    public boolean isUser(User user);
}
