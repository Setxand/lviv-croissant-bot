package com.bots.lvivCroissantBot.service.peopleRegisterService;

import com.bots.lvivCroissantBot.entity.peopleRegister.CourierRegister;

public interface CourierRegisterService {
    public CourierRegister findByRecipientId(Long recipientId);
    public CourierRegister findTop();

    public void saveAndFlush(CourierRegister courierRegister);
    public void remove(CourierRegister courierRegister);
}
