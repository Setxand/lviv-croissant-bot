package com.bots.lvivcroissantbot.repository;

import com.bots.lvivcroissantbot.constantenum.messenger.Role;
import com.bots.lvivcroissantbot.entity.register.MUser;
import com.bots.lvivcroissantbot.entity.register.TUser;
import com.bots.lvivcroissantbot.entity.register.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByTUser(TUser tUser);

    User findByMUser(MUser muser);

    User findByPhoneNumber(String phoneNumber);

    List<User> findAllByRole(Role role);
}
