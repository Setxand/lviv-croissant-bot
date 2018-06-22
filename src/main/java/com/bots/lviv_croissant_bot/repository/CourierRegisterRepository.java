package com.bots.lviv_croissant_bot.repository;

import com.bots.lviv_croissant_bot.entity.register.Courier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourierRegisterRepository extends JpaRepository<Courier,Long>{
    public Courier findByRecipientId(Long recipientId);
    public Courier findTopByOrderByIdDesc();

}
