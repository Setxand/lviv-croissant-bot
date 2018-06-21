package com.bots.lvivCroissantBot.service.peopleRegister.impl;

import com.bots.lvivCroissantBot.entity.register.MUser;
import com.bots.lvivCroissantBot.repository.MUserRepository;
import com.bots.lvivCroissantBot.service.peopleRegister.MUserRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class MUserRepositoryServiceImpl implements MUserRepositoryService {
    @Autowired
    private MUserRepository MUserRepository;
    @Override
    public List<MUser> findAll() {
        return MUserRepository.findAll();
    }

    @Override
    public MUser findOnebyRId(Long id) {
        return MUserRepository.findByRecipientId(id);
    }



    @Override
    public MUser findTop() {
        return MUserRepository.findTopByOrderByIdDesc();
    }

    @Override
    public void saveAndFlush(MUser MUser) {
        MUserRepository.saveAndFlush(MUser);
    }


    @Override
    public void remove(MUser MUser) {
        MUserRepository.delete(MUser);
    }


}
