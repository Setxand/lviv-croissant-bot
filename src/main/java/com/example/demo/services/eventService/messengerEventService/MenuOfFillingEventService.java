package com.example.demo.services.eventService.messengerEventService;

import com.example.demo.model.messanger.Messaging;

public interface MenuOfFillingEventService {
	public void getMenuOfFilling(Long recipient);

	public void saveNewFilling(Messaging messaging);
}
