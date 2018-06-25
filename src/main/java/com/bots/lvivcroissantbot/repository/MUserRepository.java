package com.bots.lvivcroissantbot.repository;

import com.bots.lvivcroissantbot.entity.register.MUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MUserRepository extends JpaRepository<MUser, Long> {
    public MUser findTopByOrderByIdDesc();

    public MUser findByRecipientId(Long recipientId);
}
