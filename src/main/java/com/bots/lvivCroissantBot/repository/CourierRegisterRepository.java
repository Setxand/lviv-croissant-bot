package com.bots.lvivCroissantBot.repository;

import com.bots.lvivCroissantBot.entity.peopleRegister.CourierRegister;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourierRegisterRepository extends JpaRepository<CourierRegister,Long>{
    public CourierRegister findByRecipientId(Long recipientId);
    public CourierRegister findTopByOrderByIdDesc();

}
