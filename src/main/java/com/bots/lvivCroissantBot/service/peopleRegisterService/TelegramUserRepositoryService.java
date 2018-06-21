package com.bots.lvivCroissantBot.service.peopleRegisterService;

import com.bots.lvivCroissantBot.entity.peopleRegister.TUser;
import com.bots.lvivCroissantBot.constantEnum.telegramEnums.TelegramUserStatus;

import java.util.List;

public interface TelegramUserRepositoryService {
    public void delete(TUser tUser);
    public TUser saveAndFlush(TUser tUser);
    public TUser findByChatId(Integer chatId);
    public void changeStatus(TUser user,TelegramUserStatus status);
    public TUser findByUserName(String userName);
    public List<String> findTopUserNames();
}