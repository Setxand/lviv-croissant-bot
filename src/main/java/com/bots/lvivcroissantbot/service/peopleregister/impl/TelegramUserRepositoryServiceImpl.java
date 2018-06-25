package com.bots.lvivcroissantbot.service.peopleregister.impl;

import com.bots.lvivcroissantbot.entity.register.TUser;
import com.bots.lvivcroissantbot.constantenum.telegram.TelegramUserStatus;
import com.bots.lvivcroissantbot.repository.TUserRepository;
import com.bots.lvivcroissantbot.service.peopleregister.TelegramUserRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TelegramUserRepositoryServiceImpl implements TelegramUserRepositoryService {
    @Autowired
    private TUserRepository tUserRepository;
    @Override
    public void delete(TUser tUser) {
        tUserRepository.delete(tUser);
    }

    @Override
    public TUser saveAndFlush(TUser tUser) {
       return tUserRepository.saveAndFlush(tUser);
    }

    @Override
    public TUser findByChatId(Integer chatId) {
        return tUserRepository.findByChatId(chatId);
    }

    @Override
    public void changeStatus(TUser user, TelegramUserStatus status) {
        user.setStatus(status);
        tUserRepository.saveAndFlush(user);
    }

    @Override
    public TUser findByUserName(String userName) {
        return tUserRepository.findByUserName(userName);
    }

    @Override
    public List<String> findTopUserNames() {
        return tUserRepository.findTopw();
    }
}
