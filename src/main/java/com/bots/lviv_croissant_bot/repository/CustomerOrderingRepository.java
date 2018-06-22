package com.bots.lviv_croissant_bot.repository;

import com.bots.lviv_croissant_bot.entity.lvivCroissants.CustomerOrdering;
import com.bots.lviv_croissant_bot.entity.register.MUser;
import com.bots.lviv_croissant_bot.entity.register.TUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerOrderingRepository extends JpaRepository<CustomerOrdering,Long> {
    public CustomerOrdering findByPhoneNumber(String phoneNumber);
    public CustomerOrdering findTopByOrderByIdDesc();
    public CustomerOrdering findTopByMUserOrderByIdDesc(MUser MUser);
    public CustomerOrdering findTopByTUserOrderByIdDesc(TUser tUser);


}
