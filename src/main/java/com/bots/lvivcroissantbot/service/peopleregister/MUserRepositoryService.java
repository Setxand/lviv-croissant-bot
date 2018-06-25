package com.bots.lvivcroissantbot.service.peopleregister;

import com.bots.lvivcroissantbot.entity.register.MUser;

import java.util.List;

public interface MUserRepositoryService {
    public List<MUser> findAll();

    public MUser findOnebyRId(Long id);

    public MUser findTop();

    public void saveAndFlush(MUser MUser);

    public void remove(MUser MUser);
}
