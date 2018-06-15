package com.example.demo.repository;

import com.example.demo.entity.lvivCroissants.CustomerOrdering;
import com.example.demo.entity.peopleRegister.MUser;
import com.example.demo.entity.peopleRegister.TUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerOrderingRepository extends JpaRepository<CustomerOrdering,Long> {
    public CustomerOrdering findByPhoneNumber(String phoneNumber);
    public CustomerOrdering findTopByOrderByIdDesc();
    public CustomerOrdering findTopByMUserOrderByIdDesc(MUser MUser);
    public CustomerOrdering findTopByTUserOrderByIdDesc(TUser tUser);


}
