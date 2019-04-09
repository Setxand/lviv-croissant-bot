package com.example.demo.services.eventService.messengerEventService;

import com.example.demo.entity.peopleRegister.User;
import com.example.demo.model.messanger.Messaging;

public interface UserEventService {
	public void customerRegistration(Messaging messaging);

	public void changeStatus(Messaging messaging, String nextCommand);

	public boolean isUser(User user);
}
