package com.example.demo.services.eventService.messengerEventService;

import com.example.demo.entities.peopleRegister.User;
import com.example.demo.models.messanger.Messaging;

public interface UserEventService {
	public void customerRegistration(Messaging messaging);

	public void changeStatus(Messaging messaging, String nextCommand);

	public boolean isUser(User user);
}
