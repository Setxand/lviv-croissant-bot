package com.bots.lvivCroissantBot.service.peopleRegisterService.impl;

import com.bots.lvivCroissantBot.entity.register.Courier;
import com.bots.lvivCroissantBot.repository.CourierRegisterRepository;
import com.bots.lvivCroissantBot.service.peopleRegisterService.CourierRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourierRegisterServiceImpl implements CourierRegisterService {
    @Autowired
    private CourierRegisterRepository courierRegisterRepository;
    @Override
    public Courier findByRecipientId(Long recipientId) {
        return courierRegisterRepository.findByRecipientId(recipientId);
    }

    @Override
    public Courier findTop() {
        return courierRegisterRepository.findTopByOrderByIdDesc();
    }

    @Override
    public void saveAndFlush(Courier courier) {
        courierRegisterRepository.saveAndFlush(courier);
    }


    @Override
    public void remove(Courier courier) {
        courierRegisterRepository.delete(courier);
    }
}
