package com.bots.lvivCroissantBot.repository;

import com.bots.lvivCroissantBot.constantEnum.messengerEnum.Role;
import com.bots.lvivCroissantBot.entity.register.MUser;
import com.bots.lvivCroissantBot.entity.register.TUser;
import com.bots.lvivCroissantBot.entity.register.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByTUser(TUser tUser);

    User findByMUser(MUser muser);

    User findByPhoneNumber(String phoneNumber);

    List<User> findAllByRole(Role role);
}
