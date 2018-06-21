package com.bots.lvivCroissantBot.service.peopleRegisterService.impl;

import com.bots.lvivCroissantBot.entity.peopleRegister.CourierRegister;
import com.bots.lvivCroissantBot.repository.CourierRegisterRepository;
import com.bots.lvivCroissantBot.service.peopleRegisterService.CourierRegisterService;
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
