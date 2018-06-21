package com.bots.lvivCroissantBot.repository;

import com.bots.lvivCroissantBot.constantEnum.messengerEnums.Role;
import com.bots.lvivCroissantBot.entity.peopleRegister.MUser;
import com.bots.lvivCroissantBot.entity.peopleRegister.TUser;
import com.bots.lvivCroissantBot.entity.peopleRegister.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByTUser(TUser tUser);

    User findByMUser(MUser muser);

    User findByPhoneNumber(String phoneNumber);

    List<User> findAllByRole(Role role);
}
