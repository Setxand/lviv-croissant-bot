package com.bots.lvivcroissantbot.service.peopleregister;

import com.bots.lvivcroissantbot.entity.register.Courier;

public interface CourierRegisterService {
    public Courier findByRecipientId(Long recipientId);
    public Courier findTop();

    public void saveAndFlush(Courier courier);
    public void remove(Courier courier);
}
