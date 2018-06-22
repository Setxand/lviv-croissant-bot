package com.bots.lviv_croissant_bot.service.peopleRegister;

import com.bots.lviv_croissant_bot.entity.register.MUser;

import java.util.List;

public interface MUserRepositoryService {
    public List<MUser> findAll();
    public MUser findOnebyRId(Long id);
    public MUser findTop();
    public void saveAndFlush(MUser MUser);
    public void remove(MUser MUser);
}
