package com.bots.lvivCroissantBot.repository;

import com.bots.lvivCroissantBot.entity.register.Courier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourierRegisterRepository extends JpaRepository<Courier,Long>{
    public Courier findByRecipientId(Long recipientId);
    public Courier findTopByOrderByIdDesc();

}
