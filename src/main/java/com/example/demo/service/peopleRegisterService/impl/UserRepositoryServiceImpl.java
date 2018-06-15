package com.example.demo.service.peopleRegisterService.impl;

import com.example.demo.entity.peopleRegister.MUser;
import com.example.demo.constantEnum.messengerEnums.Roles;
import com.example.demo.repository.MUserRepository;
import com.example.demo.service.peopleRegisterService.UserRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserRepositoryServiceImpl implements UserRepositoryService {
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

    @Override
    public List<MUser> getByRole(Roles roles) {
        return MUserRepository.findAllByRole(roles);
    }
}
