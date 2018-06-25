package com.bots.lvivcroissantbot.service.peopleregister.impl;

import com.bots.lvivcroissantbot.entity.register.Courier;
import com.bots.lvivcroissantbot.repository.CourierRegisterRepository;
import com.bots.lvivcroissantbot.service.peopleregister.CourierRegisterService;
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
