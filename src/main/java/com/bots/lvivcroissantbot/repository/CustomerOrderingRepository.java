package com.bots.lvivcroissantbot.repository;

import com.bots.lvivcroissantbot.entity.lvivcroissants.CustomerOrdering;
import com.bots.lvivcroissantbot.entity.register.MUser;
import com.bots.lvivcroissantbot.entity.register.TUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerOrderingRepository extends JpaRepository<CustomerOrdering,Long> {
    public CustomerOrdering findByPhoneNumber(String phoneNumber);
    public CustomerOrdering findTopByOrderByIdDesc();
    public CustomerOrdering findTopByMUserOrderByIdDesc(MUser MUser);
    public CustomerOrdering findTopByTUserOrderByIdDesc(TUser tUser);


}
