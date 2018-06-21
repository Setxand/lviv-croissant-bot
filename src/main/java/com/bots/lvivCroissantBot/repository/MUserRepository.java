package com.bots.lvivCroissantBot.repository;

import com.bots.lvivCroissantBot.entity.peopleRegister.MUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MUserRepository extends JpaRepository<MUser,Long> {
    public MUser findTopByOrderByIdDesc();
    public MUser findByRecipientId(Long recipientId);
}
