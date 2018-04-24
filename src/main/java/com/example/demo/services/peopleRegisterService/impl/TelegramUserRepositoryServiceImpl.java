package com.example.demo.services.peopleRegisterService.impl;

import com.example.demo.entities.peopleRegister.TUser;
import com.example.demo.enums.telegramEnums.TelegramUserStatus;
import com.example.demo.repository.TelegramUserRepository;
import com.example.demo.services.peopleRegisterService.TelegramUserRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TelegramUserRepositoryServiceImpl implements TelegramUserRepositoryService {
    @Autowired
    private TelegramUserRepository telegramUserRepository;
    @Override
    public void delete(TUser tUser) {
        telegramUserRepository.delete(tUser);
    }

    @Override
    public TUser saveAndFlush(TUser tUser) {
       return telegramUserRepository.saveAndFlush(tUser);
    }

    @Override
    public TUser findByChatId(Integer chatId) {
        return telegramUserRepository.findByChatId(chatId);
    }

    @Override
    public void changeStatus(TUser user, TelegramUserStatus status) {
        user.setStatus(status);
        telegramUserRepository.saveAndFlush(user);
    }
}
