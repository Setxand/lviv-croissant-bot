package com.example.demo.service.peopleRegisterService;

import com.example.demo.entity.peopleRegister.CourierRegister;

public interface CourierRegisterService {
    public CourierRegister findByRecipientId(Long recipientId);
    public CourierRegister findTop();

    public void saveAndFlush(CourierRegister courierRegister);
    public void remove(CourierRegister courierRegister);
}
