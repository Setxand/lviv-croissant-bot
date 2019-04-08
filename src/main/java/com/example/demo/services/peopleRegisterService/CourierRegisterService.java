package com.example.demo.services.peopleRegisterService;

import com.example.demo.entities.peopleRegister.CourierRegister;

public interface CourierRegisterService {
	public CourierRegister findByRecipientId(Long recipientId);

	public CourierRegister findTop();

	public void saveAndFlush(CourierRegister courierRegister);

	public void remove(CourierRegister courierRegister);
}
