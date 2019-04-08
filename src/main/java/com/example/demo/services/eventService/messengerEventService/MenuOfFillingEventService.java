package com.example.demo.services.eventService.messengerEventService;

import com.example.demo.models.messanger.Messaging;

public interface MenuOfFillingEventService {
	public void getMenuOfFilling(Long recipient);

	public void saveNewFilling(Messaging messaging);
}
