package com.bots.lvivCroissantBot.repository;

import com.bots.lvivCroissantBot.entity.lvivCroissants.CustomerOrdering;
import com.bots.lvivCroissantBot.entity.peopleRegister.MUser;
import com.bots.lvivCroissantBot.entity.peopleRegister.TUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerOrderingRepository extends JpaRepository<CustomerOrdering,Long> {
    public CustomerOrdering findByPhoneNumber(String phoneNumber);
    public CustomerOrdering findTopByOrderByIdDesc();
    public CustomerOrdering findTopByMUserOrderByIdDesc(MUser MUser);
    public CustomerOrdering findTopByTUserOrderByIdDesc(TUser tUser);


}
