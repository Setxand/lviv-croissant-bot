package com.example.demo.services.peopleRegisterService.impl;

import com.example.demo.entities.peopleRegister.CourierRegister;
import com.example.demo.repository.CourierRegisterRepository;
import com.example.demo.services.peopleRegisterService.CourierRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourierRegisterServiceImpl implements CourierRegisterService {
	@Autowired
	private CourierRegisterRepository courierRegisterRepository;

	@Override
	public CourierRegister findByRecipientId(Long recipientId) {
		return courierRegisterRepository.findByRecipientId(recipientId);
	}

	@Override
	public CourierRegister findTop() {
		return courierRegisterRepository.findTopByOrderByIdDesc();
	}

	@Override
	public void saveAndFlush(CourierRegister courierRegister) {
		courierRegisterRepository.saveAndFlush(courierRegister);
	}


	@Override
	public void remove(CourierRegister courierRegister) {
		courierRegisterRepository.delete(courierRegister);
	}
}
