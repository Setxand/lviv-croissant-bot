package com.bots.lviv_croissant_bot.repository;

import com.bots.lviv_croissant_bot.entity.register.MUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MUserRepository extends JpaRepository<MUser,Long> {
    public MUser findTopByOrderByIdDesc();
    public MUser findByRecipientId(Long recipientId);
}
