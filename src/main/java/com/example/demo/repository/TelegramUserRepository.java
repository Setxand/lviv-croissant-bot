package com.example.demo.repository;

import com.example.demo.entities.peopleRegister.TUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramUserRepository extends JpaRepository<TUser,Long>{
    public TUser findByChatId(Integer chatId);
}
