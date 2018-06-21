package com.bots.lvivCroissantBot.service.peopleRegisterService;

import com.bots.lvivCroissantBot.entity.register.Courier;

public interface CourierRegisterService {
    public Courier findByRecipientId(Long recipientId);
    public Courier findTop();

    public void saveAndFlush(Courier courier);
    public void remove(Courier courier);
}
