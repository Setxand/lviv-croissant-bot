package com.example.demo.repository;

import com.example.demo.constantEnum.messengerEnums.Role;
import com.example.demo.entity.peopleRegister.MUser;
import com.example.demo.entity.peopleRegister.TUser;
import com.example.demo.entity.peopleRegister.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByTUser(TUser tUser);

    User findByMUser(MUser muser);

    User findByPhoneNumber(String phoneNumber);

    List<User> findAllByRole(Role role);
}
