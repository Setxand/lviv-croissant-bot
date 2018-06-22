package com.bots.lviv_croissant_bot.repository;

import com.bots.lviv_croissant_bot.constantEnum.messengerEnum.Role;
import com.bots.lviv_croissant_bot.entity.register.MUser;
import com.bots.lviv_croissant_bot.entity.register.TUser;
import com.bots.lviv_croissant_bot.entity.register.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByTUser(TUser tUser);

    User findByMUser(MUser muser);

    User findByPhoneNumber(String phoneNumber);

    List<User> findAllByRole(Role role);
}
