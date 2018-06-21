package com.bots.lvivCroissantBot.repository;

import com.bots.lvivCroissantBot.entity.peopleRegister.TUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TUserRepository extends JpaRepository<TUser,Long>{
    @Query(nativeQuery = true,
            value = "select user_name from tuser where user_name is not NULL order by id desc limit 0, 20")
    List<String> findTopw();

    TUser findByChatId(Integer chatId);

    TUser findByUserName(String userName);




}
