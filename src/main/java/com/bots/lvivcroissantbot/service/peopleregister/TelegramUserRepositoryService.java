package com.bots.lvivcroissantbot.service.peopleregister;

import com.bots.lvivcroissantbot.constantenum.telegram.TelegramUserStatus;
import com.bots.lvivcroissantbot.entity.register.TUser;

import java.util.List;

public interface TelegramUserRepositoryService {
    public void delete(TUser tUser);

    public TUser saveAndFlush(TUser tUser);

    public TUser findByChatId(Integer chatId);

    public void changeStatus(TUser user, TelegramUserStatus status);

    public TUser findByUserName(String userName);

    public List<String> findTopUserNames();
}
