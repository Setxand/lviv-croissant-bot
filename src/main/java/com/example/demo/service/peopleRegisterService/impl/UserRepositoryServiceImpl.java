package com.example.demo.service.peopleRegisterService.impl;

import com.example.demo.entity.peopleRegister.User;
import com.example.demo.constantEnum.messengerEnums.Roles;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.peopleRegisterService.UserRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserRepositoryServiceImpl implements UserRepositoryService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findOnebyRId(Long id) {
        return userRepository.findByRecipientId(id);
    }



    @Override
    public User findTop() {
        return userRepository.findTopByOrderByIdDesc();
    }

    @Override
    public void saveAndFlush(User user) {
        userRepository.saveAndFlush(user);
    }


    @Override
    public void remove(User user) {
        userRepository.delete(user);
    }

    @Override
    public List<User> getByRole(Roles roles) {
        return userRepository.findAllByRole(roles);
    }
}
