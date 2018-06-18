package com.example.demo.repository;

import com.example.demo.entity.peopleRegister.MUser;
import com.example.demo.constantEnum.messengerEnums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MUserRepository extends JpaRepository<MUser,Long> {
    public MUser findTopByOrderByIdDesc();
    public MUser findByRecipientId(Long recipientId);
}
