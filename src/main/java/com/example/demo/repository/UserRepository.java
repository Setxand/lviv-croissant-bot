package com.example.demo.repository;

import com.example.demo.entity.peopleRegister.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByTelegramId(Integer id);

    Optional<User> findByMessengerId(Long id);

    Optional<User> findByPhoneNumber(String phoneNumber);
}
