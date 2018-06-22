package com.bots.lviv_croissant_bot.service.peopleRegister;

import com.bots.lviv_croissant_bot.entity.register.Courier;

public interface CourierRegisterService {
    public Courier findByRecipientId(Long recipientId);
    public Courier findTop();

    public void saveAndFlush(Courier courier);
    public void remove(Courier courier);
}
